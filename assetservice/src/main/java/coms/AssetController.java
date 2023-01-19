package coms;

import java.util.Iterator;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import coms.message.MessageService;
import coms.model.Asset;
import coms.model.AssetBatch;
import coms.model.AssetBatchRepository;
import coms.model.AssetRepository;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/asset")
public class AssetController {
	
	public static String STATUS_DRAFT = "DRAFT";
	public static String STATUS_COMPLETE = "COMPLETE";
	
	@Autowired
	private Queue input_queue;
		
	@Autowired
	private MessageService messageService;
	
    @Autowired
    private JmsTemplate jmsTemplate;
	
	@Autowired
	public AssetBatchRepository batchRepo;
	
	@Autowired
	public AssetRepository assetRepo;
		
	@GetMapping("/batch/{id}")
	@Operation(summary="Get all process definition")
	public AssetBatch getBatch(@PathVariable Long id) {
		System.out.println("AssetController.getBatch()");
		return batchRepo.findById(id).get();
	}
	
	@PostMapping("/import")
	@Operation(summary="Import assets")
	public Long importAssets(@RequestBody AssetBatch assetBatch) throws Exception{
		System.out.println("AssetController.importAssets()");		
		
		assetBatch.setStatus(STATUS_DRAFT);
		Set<Asset> assets = assetBatch.getAssets();
		for (Iterator iterator = assets.iterator(); iterator.hasNext();) {
			Asset asset = (Asset) iterator.next();
			asset.setStatus(STATUS_DRAFT);
			
			asset.setAssetBatch(assetBatch);
		}
		
		assetBatch = batchRepo.save(assetBatch);
		System.out.println("Batch saved in DB: "+assetBatch.getId());
		
		// Trigger first event to start processing
		AssetBatch batch = new AssetBatch(assetBatch.getId(), assetBatch.getType(), assetBatch.getReferenceId(), STATUS_DRAFT);
		
		String json = new Gson().toJson(batch, AssetBatch.class);
		
		messageService.sendMessage(input_queue, json);
		System.out.println("Event sent to trigger batch: "+ assetBatch.getId());		
				
		return assetBatch.getId();
	}
	
	@GetMapping("/clean")
	@Operation(summary="Utility method: Clean all records")
	public String cleanDef() {
		assetRepo.deleteAll();
		batchRepo.deleteAll();
		return "All records removed";
	}
}
