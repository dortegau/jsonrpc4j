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

package com.idealista.restexporter.client;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idealista.jsonrpc4j.JsonRpcHttpClient;
import com.idealista.jsonrpc4j.JsonRpcClient.RequestListener;
import com.idealista.objectmapper.ObjectMapperRetriever;

/**
 * {@link FactoryBean} for creating a {@link UrlBasedRemoteAccessor}
 * (aka consumer) for accessing an HTTP based JSON-RPC service.
 *
 */
public class JsonProxyFactoryBean extends UrlBasedRemoteAccessor implements MethodInterceptor, InitializingBean, FactoryBean<Object>, ApplicationContextAware {

	private static final boolean IS_SINGLETON = true;

	private Object proxyObject;
	
	private RequestListener requestListener;
	
	private ObjectMapper objectMapper;
	
	private JsonRpcHttpClient jsonRpcHttpClient;
	
	private Map<String, String> extraHttpHeaders;

	private SSLContext sslContext;
	
	private HostnameVerifier hostNameVerifier;

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void setExtraHttpHeaders(Map<String, String> extraHttpHeaders) {
		this.extraHttpHeaders = extraHttpHeaders;
	}

	public void setRequestListener(RequestListener requestListener) {
		this.requestListener = requestListener;
	}

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public void setHostNameVerifier(HostnameVerifier hostNameVerifier)   {
        this.hostNameVerifier = hostNameVerifier;
    }
    
	public Object getObject() {
		return proxyObject;
	}
	
	public Class<?> getObjectType() {
		return getServiceInterface();
	}

	public boolean isSingleton() {
		return IS_SINGLETON;
	}
	
	public JsonProxyFactoryBean() {
		this.extraHttpHeaders = new HashMap<String, String>();
	}

	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		this.proxyObject = retrieveProxyObject();
		this.objectMapper = retrieveObjectMapper();
		
		this.jsonRpcHttpClient = buildJsonRpcHttpClient();
	}

	private JsonRpcHttpClient buildJsonRpcHttpClient() {
		try {
			JsonRpcHttpClient jsonRpcHttpClient = new JsonRpcHttpClient(objectMapper, new URL(getServiceUrl()), extraHttpHeaders);
			jsonRpcHttpClient.setRequestListener(requestListener);
            jsonRpcHttpClient.setSslContext(sslContext);
            jsonRpcHttpClient.setHostNameVerifier(hostNameVerifier);
            
            return jsonRpcHttpClient;
		} catch (MalformedURLException mue) {
			throw new RuntimeException(mue);
		}
	}

	@SuppressWarnings("unchecked")
	private Object retrieveProxyObject() {
		return ProxyFactory.getProxy(getServiceInterface(), this);
	}

	private ObjectMapper retrieveObjectMapper() {
		ObjectMapperRetriever objectMapperRetriever = new ObjectMapperRetriever(applicationContext);
		return objectMapperRetriever.retrieve();
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {		
		if (isToStringInvocation(invocation.getMethod())) return buildInvokedServiceToString();

		RemoteInvocationHandler remoteInvocationHandler = new RemoteInvocationHandler(jsonRpcHttpClient, extraHttpHeaders, invocation);
		return remoteInvocationHandler.perform();
	}

	private String buildInvokedServiceToString() {
		return proxyObject.getClass().getName() + "@" + System.identityHashCode(proxyObject);
	}

	private boolean isToStringInvocation(Method method) {
		return method.getDeclaringClass() == Object.class && method.getName().equals("toString");
	}
}
