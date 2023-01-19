package coms;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.Operation;

import coms.model.Article;
import coms.model.ArticleRepository;
import coms.model.batch.Asset;
import coms.model.batch.AssetBatch;
import coms.model.ArticleAsset;
import coms.model.ArticleAssetRepository;

@RestController
@RequestMapping("/article")
public class ArticleController {
	
	public static String BATCH_TYPE = "ARTICLE";
	public static String STATUS_DRAFT = "RECEIVED";
	public static String STATUS_COMPLETE = "COMPLETE";
	public static String STATUS_PRX_POSTED = "PROCESSED";
	
	public static String NEW_LINE = "\n";
	
	@Value( "${ASSET_IMPORT_URL}" )
	private String assetImportUrl;
		
	@Autowired
	public ArticleRepository articleRepo;

	@Autowired
	public ArticleAssetRepository assetRepo;

	
	@GetMapping("/{articleBatch}")
	@Operation(summary="Get status pf batch")
	public List<Article> getArticleBatchStatus(@PathVariable String articleBatch) throws Exception{
		System.out.println("ArticleController.getArticleBatchStatus() :: "+ articleBatch);		
		
		return articleRepo.findByArticleBatch(articleBatch);
	}
	
	
	@PostMapping("/")
	@Operation(summary="Create batch of new articles")
	public String createArticleBatch(@RequestBody List<Article> art) throws Exception{
		System.out.println("ArticleController.createArticleBatch()");		
		
		String articleBatchId = "ART-"+System.currentTimeMillis();
		
		for (Iterator iterator = art.iterator(); iterator.hasNext();) {
			Article article = (Article) iterator.next();
			article.setArticleBatch(articleBatchId);
			article = this.createArticle(article);
		} 
		
		System.out.println("Initiated processing for batch: "+articleBatchId);		
				
		return articleBatchId;
	}
	
	public Article createArticle(Article art) throws Exception{
		System.out.println("ArticleController.createArticle()");		
		
		
		art.setArticleStatus(STATUS_DRAFT);
		Set<ArticleAsset> assets = art.getAssets();
		for (Iterator iterator = assets.iterator(); iterator.hasNext();) {
			ArticleAsset articleAsset = (ArticleAsset) iterator.next();
			articleAsset.setArticle(art);
		}
		
		art = articleRepo.save(art);
		System.out.println("Article saved to DB: "+art.getId()+", "+art.getCode());	
		
		AssetBatch batch = new AssetBatch(null, BATCH_TYPE, art.getId().toString(), null);
		for (Iterator iterator = assets.iterator(); iterator.hasNext();) {
			ArticleAsset articleAsset = (ArticleAsset) iterator.next();
			batch.addAsset(new Asset(null, articleAsset.getFilename(), articleAsset.getDaUrl(), null, null));
		}
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Long> res = restTemplate.postForEntity(new URI(assetImportUrl), batch, Long.class);
		Long batchId = res.getBody();
		System.out.println("Asset import batch process triggered for Article: "+art.getId()+", "+art.getCode());	
		
		art.setArticleStatus(STATUS_DRAFT);
		art.setBatchId(batchId.toString());
		art.setBatchStart(new Date());
		
		art = articleRepo.save(art);
		
		
		//String json = new Gson().toJson(batch, AssetBatch.class);
		//messageService.sendMessage(input_queue, json);
		
		System.out.println("Asset Import Batch details updated for Article: "+art.getId()+", "+art.getCode());		
				
		return art;
	}
	
	@PostMapping("/assetprocessed")
	@Operation(summary="Asset import completed for an article")
	public String completeArticle(@RequestBody Article payload) throws Exception{
		System.out.println("ArticleController.completeArticle()");		
		
		Set<ArticleAsset> payloadAssets = payload.getAssets();
		
		Article art = articleRepo.findById(payload.getId()).get();
		
		art.setBatchEnd(new Date());
		art.setBatchStatus(STATUS_COMPLETE);
		
		Set<ArticleAsset> assets = art.getAssets();
		for (Iterator iterator = assets.iterator(); iterator.hasNext();) {
			ArticleAsset articleAsset = (ArticleAsset) iterator.next();
			
			for (Iterator it = payloadAssets.iterator(); it.hasNext();) {
				ArticleAsset payloadAsset = (ArticleAsset) it.next();
				
				if(articleAsset.getId().longValue() == payloadAsset.getId().longValue()) {
					articleAsset.setPhilipsUrl(payloadAsset.getPhilipsUrl());
				}
			}			
		}		
		
		art = articleRepo.save(art);
		
		System.out.println("Article record updated. Posting to PRX: size = "+art.getAssets().size());		
		
		this.postToPRX(art);
		
		System.out.println("Article "+ art.getId() + "::"+ art.getCode() + " posted to PRX");		
		
		art.setArticleStatus(STATUS_PRX_POSTED);
		art = articleRepo.save(art);
		
		return "success";
	}
	
	public void postToPRX(Article art) throws Exception{
		String content = "<article>"+NEW_LINE;
		content = content + "<id>"+ art.getId() +  "</id>" + NEW_LINE;
		content = content + "<code>" +art.getCode() + "</code>" + NEW_LINE;
		content = content + "<description>" + art.getDescription() + "</description>" + NEW_LINE;
		
		
		content = content + "<assets>"+NEW_LINE;
		
		for (Iterator iterator = art.getAssets().iterator(); iterator.hasNext();) {
			ArticleAsset asset = (ArticleAsset) iterator.next();
			content = content + "	<asset>"+NEW_LINE;
			content = content + "		<id>"+ asset.getId() +  "</id>" + NEW_LINE;
			content = content + "		<file_name>"+ asset.getFilename() +  "</file_name>" + NEW_LINE;
			content = content + "		<philips_url>"+ asset.getPhilipsUrl() +  "</philips_url>" + NEW_LINE;		
			content = content + "	</asset>"+NEW_LINE;
		}
		
		content = content + "</assets>"+NEW_LINE;
		
		content = content + "</article>"+NEW_LINE;
		
		String file = "C:\\Temp\\da\\prx-article-"+art.getId()+".xml";
		FileOutputStream fos = new FileOutputStream(new File(file));
		fos.write(content.getBytes());
		fos.close();
	}
	
	
	@GetMapping("/clean")
	@Operation(summary="Utility method: Clean all records")
	public String cleanDef() {
		assetRepo.deleteAll();
		articleRepo.deleteAll();
		return "All records removed";
	}
}
