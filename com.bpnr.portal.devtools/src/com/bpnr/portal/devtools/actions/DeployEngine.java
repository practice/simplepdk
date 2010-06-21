package com.bpnr.portal.devtools.actions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import com.bpnr.portal.devtools.RememberServerPassword;
import com.bpnr.portal.devtools.preferences.PortalServer;

public class DeployEngine {

//	private static final int SOCKET_TIMEOUT = 10000;
	private String m_hostName;
	private int m_port;
	private String m_userName;
	private String m_password;
	private boolean m_isVerbose = false;

	public DeployEngine(PortalServer srv) {
		this.m_hostName = srv.getHost();
		this.m_password = RememberServerPassword.get().getPassword(srv.getName());
		this.m_port = srv.getPort();
		this.m_userName = srv.getLoginId();
	}

	public String getHostName() {
		return this.m_hostName;
	}

	public String getPassword() {
		return this.m_password;
	}

	public int getPort() {
		return this.m_port;
	}

	public String getUserName() {
		return this.m_userName;
	}

	public void setVerbose(boolean isVerbose) {
		this.m_isVerbose = isVerbose;
	}

	public boolean isVerbose() {
		return this.m_isVerbose;
	}

	public void deploy(String filePath) throws IOException, DeploymentException {
		if (filePath.lastIndexOf(".par") == -1)
			filePath = filePath + ".par";
		deploy(new File(filePath));
	}

	public void deploy(File parFile) throws IOException, DeploymentException {
		uploadPar(parFile);
	}

//	private String getComponetName(File parFile) {
//		String componentName = parFile.getName();
//		return componentName.substring(0, componentName.lastIndexOf('.'));
//	}

	private URL createURL(String irjPath) throws MalformedURLException {
		String urlstr = "http://" + this.m_hostName + ":" + this.m_port + "/" + irjPath + "?login_submit=on&j_user=" + this.m_userName + "&j_password=" + this.m_password
				+ "&j_authscheme=default&uidPasswordLogon=Log%20on";

		return new URL(urlstr);
	}

	private byte[] createUploadPrefix(File parFile) {
		StringBuffer buffer = new StringBuffer("-----------------------------7d22371e1f0356\r\nContent-Disposition: form-data; name=\"thefile\"; filename=\"");
		buffer.append(parFile.getAbsolutePath());
		buffer.append("\"\r\nContent-Type: application/x-zip-compressed\r\n\r\n");

		return new String(buffer).getBytes();
	}

	private byte[] createUploadSuffix(File parFile) {
		return UploadStrings.UPLOAD_SUFFIX;
	}

	private URLConnection openConnection(String irjPath) throws IOException {
		URL url = createURL(irjPath);
		System.out.println(url);
		URLConnection con = url.openConnection();

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; T312461)");
		con.setRequestProperty("Accept", "text/xml");

		return con;
	}

	private URLConnection openUploadConnection() throws IOException, DeploymentException {
		if (!isHttp()) {
			throw new DeploymentException("HTTPS protocol is not supported, please ensure this server is not using HTTPS.", "", null, this);
		}

		URLConnection con = openConnection("irj/servlet/prt/portal/prteventname/upload/prtroot/com.sap.portal.runtime.system.console.ArchiveUploader");
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------7d22371e1f0356");

		return con;
	}

	private byte[] readFile(File parFile) throws IOException {
		int length = (int) parFile.length();
		byte[] buffer = new byte[length];
		FileInputStream fin = new FileInputStream(parFile);

		fin.read(buffer);

		return buffer;
	}

	private String readResponse(URLConnection con) throws IOException, DeploymentException {
		int buffSize = 1024;
		char[] buf = new char[buffSize];
		StringBuffer response = new StringBuffer(buffSize);
		InputStreamReader reader = new InputStreamReader(con.getInputStream());
		int numRead;
		while ((numRead = reader.read(buf, 0, 1024)) > -1) {
			response.append(buf, 0, numRead);
		}

		return new String(response);
	}

//	private void removePar(String componentName) throws IOException, DeploymentException {
//		URLConnection con = openConnection("irj/servlet/prt/portal/prteventname/delete/prtroot/com.sap.portal.runtime.system.console.ArchiveRemover");
//		OutputStream out = con.getOutputStream();
//
//		out.write(("parname=" + componentName).getBytes());
//		out.flush();
//		out.close();
//
//		String response = readResponse(con);
//
//		if (this.m_isVerbose) {
//			System.out.println(response);
//		}
//
//		if ((this.m_deleteMode != DELETE_FAIL_ERROR) || (response.indexOf("been successfully removed from the PCD") != -1))
//			return;
//		throw new DeploymentException("Remove seems to have failed", componentName, response, this);
//	}

	private void uploadPar(File parFile) throws IOException, DeploymentException {
		byte[] fileBytes = readFile(parFile);
		byte[] prefix = createUploadPrefix(parFile);
		byte[] suffix = createUploadSuffix(parFile);
		URLConnection con = openUploadConnection();
		OutputStream out = con.getOutputStream();

		out.write(prefix);
		out.write(fileBytes);
		out.write(UploadStrings.NL);
		out.write(suffix);
		out.flush();
		out.close();

		String response = readResponse(con);
		System.out.println(response);

		if (this.m_isVerbose) {
			System.out.println(response);
		}

		if ((response.indexOf("<?xml") != -1) && (response.indexOf("<query-result><info><type>1</type>") == -1)) {
			throw new DeploymentException("Upload seems to have failed", parFile.getAbsolutePath(), response, this);
		}

		if (response.indexOf("Application successfully stored in the PCD") != -1) {
			return;
		}
		throw new DeploymentException("Upload seems to have failed", parFile.getAbsolutePath(), response, this);
	}

	private boolean isHttp() throws UnknownHostException, ConnectException {
		String host = this.m_hostName;
		int port = this.m_port;
		InetAddress address = null;

		address = InetAddress.getByName(host);

		if (address != null) {
			Socket socket = null;
			try {
				socket = new Socket(address, port);
				socket.setSoTimeout(10000);
			} catch (IOException ioe) {
//				SapPortalPluginsLogger.logError(this, ioe);
				return false;
			}

			if (socket != null) {
				InputStream istream = null;
				OutputStream ostream = null;
				try {
					istream = socket.getInputStream();
					ostream = socket.getOutputStream();
				} catch (IOException e) {
					e.printStackTrace();
					try {
						istream.close();
						ostream.close();
						socket.close();
					} catch (IOException ioe) {
//						SapPortalPluginsLogger.logError(this, ioe);
					}

					return false;
				}

				try {
					DataOutputStream outbound = new DataOutputStream(socket.getOutputStream());
					BufferedReader inbound = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					outbound.writeBytes("GET / HTTP/1.0\r\n\r\n");

					inbound.readLine();
					try {
						istream.close();
						ostream.close();
						socket.close();
						return true;
					} catch (IOException ioe) {
//						SapPortalPluginsLogger.logError(this, ioe);

						return true;
					}
				} catch (InterruptedIOException e2) {
					return false;
				} catch (IOException ioe) {
//					SapPortalPluginsLogger.logError(this, ioe);
					try {
						istream.close();
						ostream.close();
						socket.close();
					} catch (IOException ioe2) {
//						SapPortalPluginsLogger.logError(this, ioe2);
					}

					return false;
				}
			}
		}
		return false;
	}

}
