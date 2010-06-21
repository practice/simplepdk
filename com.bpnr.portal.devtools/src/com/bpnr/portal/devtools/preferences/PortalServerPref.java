package com.bpnr.portal.devtools.preferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PortalServerPref {
	private final static String serverListFile = System.getProperty("user.home") + "/.sap.ep.servers";
	private static ArrayList<PortalServer> servers = new ArrayList<PortalServer>();
	private static PortalServer lastServer = null;
	static {
		loadServerList();
	}

	@SuppressWarnings("unchecked")
	public static void loadServerList() {
		try {
			FileInputStream inputStream = new FileInputStream(serverListFile);
			ObjectInputStream ois = new ObjectInputStream(inputStream);
			getServers().clear();
			getServers().addAll((ArrayList<PortalServer>) ois.readObject());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void saveServerList() {
		try {
			FileOutputStream outputStream = new FileOutputStream(serverListFile);
			ObjectOutputStream oos = new ObjectOutputStream(outputStream);
			oos.writeObject(getServers());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<PortalServer> getServers() {
		return servers;
	}

	public static void setDefServer(String defServer) {
		for (PortalServer ps : servers) {
			if (ps.getName().equals(defServer))
				ps.setDefaultServer(true);
			else
				ps.setDefaultServer(false);
		}
//		PortalServerPref.defServer = defServer;
	}

	public static PortalServer getDefServer() {
		for (PortalServer ps : servers) {
			if (ps.isDefaultServer())
				return ps;
		}
		return null;
	}
	
	public static PortalServer getLastServer() {
		return PortalServerPref.lastServer;
	}

	public static void setLastServer(PortalServer lastServer) {
		PortalServerPref.lastServer = lastServer;
	}

}
