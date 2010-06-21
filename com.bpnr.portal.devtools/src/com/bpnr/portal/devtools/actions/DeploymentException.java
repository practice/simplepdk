package com.bpnr.portal.devtools.actions;

public class DeploymentException extends Exception {
	private String m_response;
	private String m_targetComponent;
	private DeployEngine m_engine;

	public DeploymentException(String message, String targetComponent, String response, DeployEngine engine) {
		super(message);
		this.m_engine = engine;
		this.m_targetComponent = targetComponent;
		this.m_response = response;
	}

	public String getMessage() {
		return super.getMessage();
	}

	public String getResponse() {
		return this.m_response;
	}

	public DeployEngine getEngine() {
		return this.m_engine;
	}

	public void setResponse(String response) {
		this.m_response = response;
	}

	public String getTargetComponent() {
		return this.m_targetComponent;
	}
}