package com.bpnr.portal.devtools;

import java.util.HashMap;

public class RememberServerPassword {
	private static RememberServerPassword singleton = new RememberServerPassword();
	
	private HashMap<String, String> passwords = new HashMap<String, String>();
	
	public static RememberServerPassword get() {
		return singleton;
	}
	
	public void setPassword(String serverName, String password) {
		passwords.put(serverName, password);
	}
	
	public String getPassword(String serverName) {
		return passwords.get(serverName);
	}
}
