package com.googlecode.jsonrpc4j.spring.objectmapper;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Retrieves existing Object Mapper in application context or creates new Object
 * Mapper instance otherwise
 * 
 * @author dortega
 */
public class ObjectMapperRetriever {

	private List<ObjectMapperContextRetriever> retrievers;

	public ObjectMapperRetriever(ApplicationContext applicationContext) {
		this.retrievers = buildRetrieversList(applicationContext);
	}

	public ObjectMapper retrieve() {
		for(ObjectMapperContextRetriever retriever : this.retrievers){
			ObjectMapper mapper = retriever.retrieve();
			if(mapper != null) return mapper; 
		}
		
		return new ObjectMapper();
	}

	private List<ObjectMapperContextRetriever> buildRetrieversList(ApplicationContext applicationContext) {
		return Arrays.asList(new ByNameObjectMapperContextRetriever(applicationContext),
								new ByTypeObjectMapperContextRetriever(applicationContext));
	}
}