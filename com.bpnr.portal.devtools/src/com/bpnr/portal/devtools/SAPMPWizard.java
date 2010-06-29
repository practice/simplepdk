package com.bpnr.portal.devtools;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.bpnr.portal.devtools.actions.DeployProgressRunnable;
import com.bpnr.portal.devtools.preferences.PortalServer;

public class SAPMPWizard extends Wizard implements IExportWizard {

	protected IProject selectedProject;
	private String mm_statusType;
	private String mm_statusMessage;
	SAPMPWizardPage serversAndDeployPage;
	SelectProjectWizardPage selectProjectPage;
	private PortalServer selectedServer;
	private ChooseServerComponent chooseServerComponent;
//	private IWorkbench workbench;

	public void init(IWorkbench workbench, IStructuredSelection selection) {
//		this.workbench = workbench;
		super.setNeedsProgressMonitor(true);
		this.serversAndDeployPage = new SAPMPWizardPage();
		// super.setDefaultPageImageDescriptor(SAPImageDescriptors.SAP_LOGO);
	}

	public boolean performFinish() {
		boolean success = false;
		this.chooseServerComponent = this.serversAndDeployPage.getServerConfigSelector();
		this.selectedServer = this.chooseServerComponent.getSelectedServerConfig();
		selectedProject = getSelectedProject();

		try {
			getContainer().run(false, false, new DeployProgressRunnable(selectedProject, selectedServer));
		} catch (InvocationTargetException e) {
			Throwable cause = e.getTargetException();
			if (cause != null) {
				PdkToolsLog.logError(e);
				this.mm_statusType = cause.getClass().getName();
				this.mm_statusMessage = cause.getMessage();
			}
		} catch (Exception e) {
			PdkToolsLog.logError(e);
			this.mm_statusType = e.getClass().getName();
			this.mm_statusMessage = e.getMessage();
		}

		if (success) {
			MessageDialog.openError(super.getShell(), this.mm_statusType, this.mm_statusMessage);
		}

		return true;
	}

	public IProject getSelectedProject() {
		if (this.selectProjectPage != null) {
			return this.selectProjectPage.getSelectedProject();
		}
		return null;
	}

	public void addPages() {
		super.addPages();
		this.selectProjectPage = new SelectProjectWizardPage("Package a Portal Application into a PAR file", "Package a Portal Application into a PAR file");
		addPage(this.selectProjectPage);
		addPage(this.serversAndDeployPage);
	}

	public boolean canFinish() {
		return (super.getContainer().getCurrentPage() == this.serversAndDeployPage) && (this.serversAndDeployPage.isPageComplete());
	}

	public String getWindowTitle() {
		return "PAR Export";
	}

	public void setSAPMPWizardPage(SAPMPWizardPage wp) {
		this.serversAndDeployPage = wp;
	}
}
