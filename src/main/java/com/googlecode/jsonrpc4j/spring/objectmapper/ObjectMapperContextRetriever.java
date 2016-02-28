package com.googlecode.jsonrpc4j.spring.objectmapper;

import com.fasterxml.jackson.databind.ObjectMapper;

interface ObjectMapperContextRetriever {

	ObjectMapper retrieve();
	
}