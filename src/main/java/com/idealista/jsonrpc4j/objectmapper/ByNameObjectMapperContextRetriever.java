package com.idealista.jsonrpc4j.objectmapper;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Retrieves existing {@link ObjectMapper} from application context by bean name
 * 
 * @author dortegau
 */
class ByNameObjectMapperContextRetriever implements ObjectMapperContextRetriever {

	private static final String OBJECT_MAPPER_BEAN_NAME = "objectMapper";	
	
	private ApplicationContext applicationContext;
	
	public ByNameObjectMapperContextRetriever(ApplicationContext applicationContext){
		this.applicationContext = applicationContext;
	}
	
	@Override
	public ObjectMapper retrieve() {
		if(!isObjectMapperBeanNameInContext()) return null;
		return (ObjectMapper) applicationContext.getBean(OBJECT_MAPPER_BEAN_NAME);
	}
	
	private boolean isObjectMapperBeanNameInContext() {
		return applicationContext != null && applicationContext.containsBean(OBJECT_MAPPER_BEAN_NAME);
	}
	
}