package com.idealista.jsonrpc4j.spring.client;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;

import com.idealista.jsonrpc4j.JsonRpcHttpClient;
import com.idealista.jsonrpc4j.ReflectionUtil;

class RemoteInvocationHandler {

	private JsonRpcHttpClient jsonRpcHttpClient;
	
	private Map<String, String> extraHttpHeaders;
	
	private Object arguments;
		
	private Type returnType;
	
	private String methodName;
	
	public RemoteInvocationHandler(JsonRpcHttpClient jsonRpcHttpClient, Map<String, String> extraHttpHeaders, MethodInvocation invocation){
		this.jsonRpcHttpClient = jsonRpcHttpClient;
		this.extraHttpHeaders = extraHttpHeaders;
		
		this.arguments = getArguments(invocation);		
		this.returnType = getReturnType(invocation);
		this.methodName = getMethodName(invocation);		
	}
	
	public Object perform() throws Throwable {		
		return jsonRpcHttpClient.invoke(methodName, arguments, returnType, extraHttpHeaders);
	}
		
	private Object getArguments(MethodInvocation invocation) {
		return ReflectionUtil.parseArguments(invocation.getMethod(), invocation.getArguments());
	}
	
	private Type getReturnType(MethodInvocation invocation) {
		Method invokedMethod = invocation.getMethod();
		
		if(invokedMethod.getGenericReturnType() != null) return invokedMethod.getGenericReturnType();
		
		return invokedMethod.getReturnType();
	}	
	
	private String getMethodName(MethodInvocation invocation) {
		return invocation.getMethod().getName();
	}	
}