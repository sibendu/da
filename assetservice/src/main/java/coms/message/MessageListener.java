package coms.message;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.jms.Queue;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import coms.model.AssetBatch;
import coms.model.AssetBatchRepository;
import coms.AssetController;
import coms.model.Asset;
import coms.model.AssetRepository;

@Component
public class MessageListener {

	@Autowired
	private Queue asset_queue;
	
	@Autowired
	private Queue output_queue;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private MessageService messageService;

	@Autowired
	public AssetBatchRepository batchRepo;
	
	@Autowired
	public AssetRepository assetRepo;
	
	/*
	 
	@JmsListener(destination = "${INPUT_QUEUE}", concurrency = "2")
	public void processArticle(TextMessage message) {
		
		AssetBatch art = null;
		try {
			String jsonEvent = message.getText();
			art = new Gson().fromJson(jsonEvent, AssetBatch.class);
			System.out.println("Starting Batch:: Id="+art.getId()+", type="+art.getType()+" , referenceId="+art.getReferenceId());
			
			art = batchRepo.findById(art.getId()).get();
			
			Set<Asset> assets = art.getAssets();
			for (Iterator iterator = assets.iterator(); iterator.hasNext();) {
				Asset asset = (Asset) iterator.next();
				asset = new Asset(asset.getId(), asset.getFilename(), asset.getUrl(), asset.getStatus(), null);
				
				String json = new Gson().toJson(asset, Asset.class);
				messageService.sendMessage(asset_queue, json);
			}
			
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
		}
	}
	
	
	@JmsListener(destination = "${ASSET_QUEUE}", concurrency = "3-5")
	public void processAsset(TextMessage message) {
		
		Asset asset = null;
		AssetBatch art = null;
		try {
			String jsonEvent = message.getText();
			//System.out.println("Received event message: "+jsonEvent);
			asset = new Gson().fromJson(jsonEvent, Asset.class);
			System.out.println("Importing asset. Id="+ asset.getId()+", filename="+asset.getFilename());
			
			asset = assetRepo.findById(asset.getId()).get();
			art = batchRepo.findById(asset.getAssetBatch().getId()).get();
			
			Random rn = new Random();
			int answer = rn.nextInt(20) + 1;
			Thread.sleep(10000 + answer * 1000);
			
			asset.setStatus(AssetController.STATUS_COMPLETE);
			assetRepo.save(asset);
			
			
			System.out.println("Asset published asset to Scene 7. Id="+ asset.getId()+", filename="+asset.getFilename()+", Batch="+art.getId());

			List<Asset> assets = assetRepo.findByAssetBatchAndStatus(art, AssetController.STATUS_DRAFT);
			if(assets.size() > 0) {
				System.out.println("Batch "+art.getId()+" still has more assets to be imported");
			}else {
				System.out.println("All assets in Batch "+art.getId()+" are imported");
				
				art = batchRepo.findById(asset.getAssetBatch().getId()).get();
				art.setStatus(AssetController.STATUS_COMPLETE);
				batchRepo.save(art);
				
				Thread.sleep(5000);
				
				art.setAssets(null);
				String json = new Gson().toJson(art, AssetBatch.class);
				messageService.sendMessage(output_queue, json);
				
				System.out.println("Batch "+art.getId()+" sucessfully completed");
			}
			
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
		}
	}
	
	*/
}
