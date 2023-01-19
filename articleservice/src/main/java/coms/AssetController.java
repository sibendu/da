package coms;

import java.util.Iterator;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import coms.model.batch.AssetBatch;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/asset")
public class AssetController {
	
	@PostMapping("/import")
	@Operation(summary="Import assets")
	public Long importAssets(@RequestBody AssetBatch assetBatch) throws Exception{
		System.out.println("AssetController.importAssets(): I am called");		
		
		return new Long(100001);
	}
}
