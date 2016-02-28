package com.idealista.objectmapper;

import com.fasterxml.jackson.databind.ObjectMapper;

interface ObjectMapperContextRetriever {

	ObjectMapper retrieve();
	
}