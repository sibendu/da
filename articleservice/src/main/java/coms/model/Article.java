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
@Table(name = "article")
@Getter @Setter @NoArgsConstructor
public class Article implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String articleBatch;
	
	private String code;
	private String description;
	
	private String batchId;
	private Date batchStart;
	private Date batchEnd;
	private String batchStatus;
	private String articleStatus;
	
	@OneToMany(mappedBy = "article", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private Set<ArticleAsset> assets = new HashSet<>();
	
	public void addAsset(ArticleAsset asset) {
		this.assets.add(asset);
	}

	public Article(Long id, String code, String description, String batchId, Date batchStart, Date batchEnd,
			String batchStatus, String articleStatus) {
		super();
		this.id = id;
		this.code = code;
		this.description = description;
		this.batchId = batchId;
		this.batchStart = batchStart;
		this.batchEnd = batchEnd;
		this.batchStatus = batchStatus;
		this.articleStatus = articleStatus;
	}
}
