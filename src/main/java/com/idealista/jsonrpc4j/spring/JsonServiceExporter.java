/*
The MIT License (MIT)

Copyright (c) 2014 jsonrpc4j

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.idealista.jsonrpc4j.spring;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idealista.jsonrpc4j.ErrorResolver;
import com.idealista.jsonrpc4j.InvocationListener;
import com.idealista.jsonrpc4j.JsonRpcServer;

/**
 * {@link RemoteExporter} that exports services using Json
 * according to the JSON-RPC proposal specified at:
 * <a href="http://groups.google.com/group/json-rpc">
 * http://groups.google.com/group/json-rpc</a>.
 *
 */
public class JsonServiceExporter extends RemoteExporter implements InitializingBean, ApplicationContextAware, HttpRequestHandler {

	private Level exceptionLogLevel = Level.WARNING;
	
	private ObjectMapper objectMapper;
	
	private JsonRpcServer jsonRpcServer;
	
	private ApplicationContext applicationContext;
	
	private ErrorResolver errorResolver;
	
	private InvocationListener invocationListener;
	
	private boolean backwardsCompatible = true;
	
	private boolean rethrowExceptions = false;
	
	private boolean allowExtraParams = false;
	
	private boolean allowLessParams = false;		

	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() throws Exception {

		// find the ObjectMapper
		if (objectMapper == null
			&& applicationContext != null
			&& applicationContext.containsBean("objectMapper")) {
			objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
		}
		if (objectMapper == null && applicationContext != null) {
			try {
				objectMapper = (ObjectMapper)BeanFactoryUtils
					.beanOfTypeIncludingAncestors(applicationContext, ObjectMapper.class);
			} catch (Exception e) { /* no-op */ }
		}
		if (objectMapper==null) {
			objectMapper = new ObjectMapper();
		}

		jsonRpcServer = new JsonRpcServer(objectMapper, getProxyForService(), getServiceInterface());
		jsonRpcServer.setErrorResolver(errorResolver);
		jsonRpcServer.setBackwardsCompatible(backwardsCompatible);
		jsonRpcServer.setRethrowExceptions(rethrowExceptions);
		jsonRpcServer.setAllowExtraParams(allowExtraParams);
		jsonRpcServer.setAllowLessParams(allowLessParams);
		jsonRpcServer.setExceptionLogLevel(exceptionLogLevel);
        jsonRpcServer.setInvocationListener(invocationListener);
	}
	
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		jsonRpcServer.handle(request, response);
		response.getOutputStream().flush();
	}

	/**
	 * @return the objectMapper
	 */
	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * @return the jsonRpcServer
	 */
	protected JsonRpcServer getJsonRpcServer() {
		return jsonRpcServer;
	}

	/**
	 * @return the applicationContext
	 */
	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * @param errorResolver the errorResolver to set
	 */
	public void setErrorResolver(ErrorResolver errorResolver) {
		this.errorResolver = errorResolver;
	}

	/**
	 * @param backwardsCompatible the backwardsComaptible to set
	 */
	public void setBackwardsComaptible(boolean backwardsCompatible) {
		this.backwardsCompatible = backwardsCompatible;
	}

	/**
	 * @param rethrowExceptions the rethrowExceptions to set
	 */
	public void setRethrowExceptions(boolean rethrowExceptions) {
		this.rethrowExceptions = rethrowExceptions;
	}

	/**
	 * @param allowExtraParams the allowExtraParams to set
	 */
	public void setAllowExtraParams(boolean allowExtraParams) {
		this.allowExtraParams = allowExtraParams;
	}

	/**
	 * @param allowLessParams the allowLessParams to set
	 */
	public void setAllowLessParams(boolean allowLessParams) {
		this.allowLessParams = allowLessParams;
	}

	/**
	 * @param exceptionLogLevel the exceptionLogLevel to set
	 */
	public void setExceptionLogLevel(Level exceptionLogLevel) {
		this.exceptionLogLevel = exceptionLogLevel;
	}

    /**
     * @param invocationListener the invocationListener to set
     */
    public void setInvocationListener(InvocationListener invocationListener) {
        this.invocationListener = invocationListener;
    }

}
