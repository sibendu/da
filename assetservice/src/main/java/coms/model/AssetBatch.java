package coms.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "asset_batch")
@Getter @Setter @NoArgsConstructor
public class AssetBatch implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String type;
	private String referenceId;
	private String status;
	
	@OneToMany(mappedBy = "assetBatch", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private Set<Asset> assets = new HashSet<>();
	
	public AssetBatch(Long id, String type, String referenceId, String status) {
		super();
		this.id = id;
		this.type = type;
		this.referenceId = referenceId;
		this.status = status;
	}
	
	public void addAsset(Asset asset) {
		this.assets.add(asset);
	}
}
