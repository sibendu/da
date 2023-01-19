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
@Table(name = "article_asset")
@Getter @Setter @NoArgsConstructor
public class ArticleAsset implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String filename;
	private String daUrl;
	private String philipsUrl;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
    private Article article;
	
	
	public ArticleAsset(Long id, String filename, String daUrl, String philipsUrl, Article article) {
		super();
		this.id = id;
		this.filename = filename;
		this.daUrl = daUrl;
		this.philipsUrl = philipsUrl;
		this.article = article;
	}
	
	
}
