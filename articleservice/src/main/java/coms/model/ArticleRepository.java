package coms.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends CrudRepository<Article, Long> {
	
	public List<Article> findByCode(String code);
	public List<Article> findByArticleBatch(String articleBatch);

}
