package com.bpnr.portal.devtools.preferences;

import java.io.Serializable;

public class PortalServer implements Serializable {

	private static final long serialVersionUID = 6536292305269989694L;
	
	private boolean defaultServer = false;
	private String name;
	private String host;
	private int port;
	private String loginId;
	private String desc;
	
	public PortalServer(String name, String host, int port, String loginId, String description) {
		this.name = name;
		this.host = host;
		this.port = port;
		this.loginId = loginId;
		this.desc = description;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getHost() {
		return host;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getPort() {
		return port;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDesc() {
		return desc;
	}

	public void setDefaultServer(boolean defaultServer) {
		this.defaultServer = defaultServer;
	}

	public boolean isDefaultServer() {
		return defaultServer;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof PortalServer) {
			return name.equals(((PortalServer) obj).getName());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return name + ": http://" + host + ":" + port + " with [" + loginId + "]";  
	}
}
