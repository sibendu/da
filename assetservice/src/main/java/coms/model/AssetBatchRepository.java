package coms.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AssetBatchRepository extends CrudRepository<AssetBatch, Long> {
	
	//public List<AssetBatch> findByCode(String code);
}
