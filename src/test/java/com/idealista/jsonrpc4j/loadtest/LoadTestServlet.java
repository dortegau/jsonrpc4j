package com.idealista.jsonrpc4j.loadtest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idealista.jsonrpc4j.JsonRpcServer;

import java.io.IOException;

/**
 * @author Eduard Szente
 */
@SuppressWarnings("serial")
public class LoadTestServlet
	extends HttpServlet {

	private JsonRpcServer jsonRpcServer;

    @Override
	public void init() {
		jsonRpcServer = new JsonRpcServer(new JsonRpcServiceImpl());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		jsonRpcServer.handle(req, resp);
	}
}
