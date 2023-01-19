package coms.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AssetRepository extends CrudRepository<Asset, Long> {
	List<Asset> findByAssetBatchAndStatus(AssetBatch article, String status);
}
