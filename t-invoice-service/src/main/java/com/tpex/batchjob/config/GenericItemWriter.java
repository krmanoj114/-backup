package com.tpex.batchjob.config;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public class GenericItemWriter {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@SuppressWarnings("rawtypes")
	public JpaItemWriter jpaWriter() {
		JpaItemWriter iwriter = new JpaItemWriter();
		iwriter.setEntityManagerFactory(this.entityManagerFactory);
		return iwriter;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RepositoryItemWriter repositoryWriter(CrudRepository repository, String methodName) {
		RepositoryItemWriter iwriter = new RepositoryItemWriter();
		iwriter.setRepository(repository);
		iwriter.setMethodName(methodName);
		return iwriter;
	}
}
