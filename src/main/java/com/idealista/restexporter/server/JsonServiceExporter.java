package com.idealista.restexporter.server;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idealista.restexporter.ErrorResolver;
import com.idealista.jsonrpc4j.InvocationListener;
import com.idealista.restexporter.RestServer;
import com.idealista.objectmapper.ObjectMapperRetriever;

public class JsonServiceExporter extends RemoteExporter implements InitializingBean, ApplicationContextAware, HttpRequestHandler {

	private Level exceptionLogLevel = Level.WARNING;
	
	private ObjectMapper objectMapper;
	
	private RestServer restServer;
	
	private ApplicationContext applicationContext;
	
	private ErrorResolver errorResolver;
	
	private InvocationListener invocationListener;
	
	private boolean rethrowExceptions = false;
	
	private boolean allowExtraParams = false;
	
	private boolean allowLessParams = false;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void setErrorResolver(ErrorResolver errorResolver) {
		this.errorResolver = errorResolver;
	}
	
	public void setRethrowExceptions(boolean rethrowExceptions) {
		this.rethrowExceptions = rethrowExceptions;
	}
	
	public void setAllowExtraParams(boolean allowExtraParams) {
		this.allowExtraParams = allowExtraParams;
	}
	
	public void setAllowLessParams(boolean allowLessParams) {
		this.allowLessParams = allowLessParams;
	}
	
	public void setExceptionLogLevel(Level exceptionLogLevel) {
		this.exceptionLogLevel = exceptionLogLevel;
	}
	
    public void setInvocationListener(InvocationListener invocationListener) {
        this.invocationListener = invocationListener;
    }

	public void afterPropertiesSet() throws Exception {
		this.objectMapper = retrieveObjectMapper();
		this.restServer = buildRestServer();
	}

	private RestServer buildRestServer() {
		RestServer restServer = new RestServer(objectMapper, getProxyForService(), getServiceInterface());
		
		restServer.setErrorResolver(errorResolver);
		restServer.setRethrowExceptions(rethrowExceptions);
		restServer.setAllowExtraParams(allowExtraParams);
		restServer.setAllowLessParams(allowLessParams);
		restServer.setExceptionLogLevel(exceptionLogLevel);
        restServer.setInvocationListener(invocationListener);
        
        return restServer;
	}
	
	private ObjectMapper retrieveObjectMapper() {
		ObjectMapperRetriever objectMapperRetriever = new ObjectMapperRetriever(applicationContext);
		return objectMapperRetriever.retrieve();
	}
	
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		restServer.handle(request, response);
		response.getOutputStream().flush();
	}
}
