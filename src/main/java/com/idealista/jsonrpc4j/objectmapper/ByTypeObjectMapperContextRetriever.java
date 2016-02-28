package com.idealista.jsonrpc4j.objectmapper;

import java.util.logging.Logger;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Retrieves existing {@link ObjectMapper} from application context by bean type
 * 
 * @author dortegau
 */
class ByTypeObjectMapperContextRetriever implements ObjectMapperContextRetriever {

	private static final Logger LOGGER = Logger.getLogger(ObjectMapperRetriever.class.getName());
	
	private static final Class<ObjectMapper> OBJECT_MAPPER_CLASS = ObjectMapper.class;
	
	private ApplicationContext applicationContext;
	
	public ByTypeObjectMapperContextRetriever(ApplicationContext applicationContext){
		this.applicationContext = applicationContext;
	}
	
	@Override
	public ObjectMapper retrieve(){
		try {
			if(applicationContext == null) return null;
			return BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext, OBJECT_MAPPER_CLASS);
		} catch (Exception e) {
			LOGGER.warning("Cannot retrieve object mapper from context by type");
			return null;
		}
	}
}