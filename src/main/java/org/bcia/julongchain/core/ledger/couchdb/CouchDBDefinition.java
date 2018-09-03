/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.ledger.couchdb;

import org.bcia.julongchain.core.node.NodeConfigFactory;

import java.util.Map;

/**
 * CouchDB描述类
 *
 * @author sunzongyu
 * @date 2018/07/17
 * @company Dingxuan
 */
public class CouchDBDefinition {
	private final Map<String, String> couchDBConfig = NodeConfigFactory.getNodeConfig().getLedger().getState().getCouchDBConfig();
	private String host;
	private int port;
	private String userName;
	private String password;
	private int maxRetries;
	private int maxRetriesOnStartUp;
	private int requestTimeOut;

	public CouchDBDefinition() {
		this.host = couchDBConfig.get("couchDBAddress");
		this.port = Integer.valueOf(couchDBConfig.get("couchDBPort"));
		// TODO: 7/17/18 if set couchdb username & password, server will response 401
		this.userName = null;
		this.password = null;
		this.maxRetries = Integer.valueOf(couchDBConfig.get("maxRetries"));
		this.maxRetriesOnStartUp = Integer.valueOf(couchDBConfig.get("maxRetriesOnStartup"));
		this.requestTimeOut = Integer.valueOf(couchDBConfig.get("requestTimeout"));
	}

	public Map<String, String> getCouchDBConfig() {
		return couchDBConfig;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public int getMaxRetriesOnStartUp() {
		return maxRetriesOnStartUp;
	}

	public void setMaxRetriesOnStartUp(int maxRetriesOnStartUp) {
		this.maxRetriesOnStartUp = maxRetriesOnStartUp;
	}

	public int getRequestTimeOut() {
		return requestTimeOut;
	}

	public void setRequestTimeOut(int requestTimeOut) {
		this.requestTimeOut = requestTimeOut;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
