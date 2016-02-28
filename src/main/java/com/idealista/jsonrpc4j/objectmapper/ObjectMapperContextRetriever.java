package com.idealista.jsonrpc4j.objectmapper;

import com.fasterxml.jackson.databind.ObjectMapper;

interface ObjectMapperContextRetriever {

	ObjectMapper retrieve();
	
}