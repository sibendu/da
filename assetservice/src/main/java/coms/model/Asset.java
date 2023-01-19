package coms.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "asset")
@Getter @Setter @NoArgsConstructor
public class Asset implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String filename;
	private String url;
	
	private String status;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_batch_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
    private AssetBatch assetBatch;
	
	
	public Asset(Long id, String filename, String url, String status, AssetBatch assetBatch) {
		super();
		this.id = id;
		this.filename = filename;
		this.url = url;
		this.assetBatch = assetBatch;
	}
	
	
}
