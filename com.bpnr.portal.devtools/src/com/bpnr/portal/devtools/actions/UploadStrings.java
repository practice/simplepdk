package com.bpnr.portal.devtools.actions;

public class UploadStrings {

	public static final String DESCRIPTION = "Enter the following information for upload and libraries process.";
	public static final String ID_SERVER = "com.sap.developmentTools.server";
	public static final String ID_PORT = "com.sap.developmentTools.port";
	public static final String ID_NAME = "com.sap.developmentTools.name";
	public static final String ID_BOOLEAN_LIBS = "com.sap.developmentTools.libs";
	public static final String ID_BOOLEAN_NON_JAVA_SOURCES = "com.sap.developmentTools.sources";
	public static final String ID_BOOLEAN_SHORT_JAR_NAMES = "com.sap.developmentTools.shortnames";
	public static final String DESCRIPTION_LIBS = "Default libraires for plugin created projects";
	public static final String LABEL_LIBS = "Yes, I want the default libraries set for each new plugin generated project.";
	public static final String LABEL_NON_JAVA_SOURCES = "Yes, I want the non java sources included into the portal components jars.";
	public static final String LABEL_SHORT_NAMES = "Yes, I want short jar names (api.jar and core.jar) into the par archive.";
	public static final String DEFAULT_BOOLEAN_LIBS = "true";
	public static final String DEFAULT_BOOLEAN_NON_JAVA_SOURCES = "false";
	public static final String DEFAULT_SHORT_JAR_NAMES = "false";
	public static final String HOST = "Host: ";
	public static final String PORT = "Port: ";
	public static final String LOGIN = "Login: ";
	public static final String PASSWORD = "Password: ";
	public static final String HTTP = "http://";
	public static final String SLASH = "/";
	public static final String J_PASSWORD = "&j_password=";
	public static final String AUTOSCHEME = "&j_authscheme=default&uidPasswordLogon=Log%20on";
	public static final String LOGIN_SUBMIT_USER = "?login_submit=on&j_user=";
	public static final String REQUEST_PROPERTY_USER_AGENT = "User-Agent";
	public static final String REQUEST_PROPERTY_MOZILLA = "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; T312461)";
	public static final String PAR_NAME = "parname=";
	public static final String UPLOAD_PREFIX_1 = "-----------------------------7d22371e1f0356\r\nContent-Disposition: form-data; name=\"thefile\"; filename=\"";
	public static final String UPLOAD_PREFIX_2 = "\"\r\nContent-Type: application/x-zip-compressed\r\n\r\n";
	public static final String MULTIPART_COTENT_TYPE = "multipart/form-data; boundary=---------------------------7d22371e1f0356";
	public static final String COTENT_TYPE = "Content-Type";
	public static final String COTENT_LENGTH = "Content-Length";
	public static final String UPLOAD_SUCCESS_STR = "Application successfully stored in the PCD";
	public static final String REMOVE_SUCCESS_STR = "been successfully removed from the PCD";
	public static final String UPLOAD_ERR_MSG = "Upload seems to have failed";
	public static final String REMOVE_ERR_MSG = "Remove seems to have failed";
	public static final String ARCHIVE_REMOVER_PATH = "irj/servlet/prt/portal/prteventname/delete/prtroot/com.sap.portal.runtime.system.console.ArchiveRemover";
	public static final String ARCHIVE_UPLOADER_PATH = "irj/servlet/prt/portal/prteventname/upload/prtroot/com.sap.portal.runtime.system.console.ArchiveUploader";
	public static final byte[] NL = "\r\n".getBytes();
	public static final byte[] UPLOAD_SUFFIX = "-----------------------------7d22371e1f0356\r\nContent-Disposition: form-data; name=\"updateall\"\r\n\r\non\r\n-----------------------------7d22371e1f0356--"
			.getBytes();
	public static final String NON_HTTP_SERVER = "HTTPS protocol is not supported, please ensure this server is not using HTTPS.";

}
