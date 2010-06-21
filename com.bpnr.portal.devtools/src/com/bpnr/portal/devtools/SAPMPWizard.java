package com.bpnr.portal.devtools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.bpnr.portal.devtools.actions.DeployEngine;
import com.bpnr.portal.devtools.actions.DeploymentException;
import com.bpnr.portal.devtools.actions.FileNotExistException;
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

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					selectedProject = getSelectedProject();
					monitor.beginTask("Processing request...", 4);
					EclipseIDE.saveAllDirtyEditorsForProject(selectedProject, ProgressMonitors.getTaskToSubtaskWrapper(monitor));
					monitor.worked(1);

					monitor.subTask("Rebuilding the project...");

					if (!EclipseIDE.rebuildCurrentProject(selectedProject, monitor)) {
						PdkToolsLog.logError("Build Error: Build of the project " + selectedProject.getName() + " failed.");
						return;
					}

					monitor.worked(1);

					try {
						File prospectedParFile = new File(EclipseIDE.getParArchiveName(selectedProject));
						monitor.subTask("Creating the archive...");

						EclipseIDE.makeParArchiveFromProject(selectedProject);
						try {
							selectedProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
						} catch (CoreException e) {
						}
						monitor.worked(1);

						processUpload(monitor, prospectedParFile);
					} catch (FileNotExistException e) {
						PdkToolsLog.logError("Sorry, file " + e.getFile().getAbsolutePath() + " does not exist on harddisk. Cancelling process.", e);
					} catch (Exception e) {
						PdkToolsLog.logError(e);
					}
					monitor.worked(1);
				} catch (Exception e) {
					PdkToolsLog.logError(e);
				}
				monitor.done();
			}

			private void processUpload(IProgressMonitor monitor, File prospectedParFile) {
				int uploadFinished = 0;
				monitor.subTask("Deploying archive...");
				do {
					try {
						DeployEngine deployEngine = new DeployEngine(selectedServer);
						System.out.println(selectedServer);
						deployEngine.deploy(prospectedParFile);
						uploadFinished = 0;
					} catch (UnknownHostException e) {
						PdkToolsLog.logError("Operation failed: Unknown host \nPlease make sure the server '" + selectedServer.getName() + "' (" + selectedServer.getHost() + ":"
								+ selectedServer.getPort() + ") exists and is running.", e);
					} catch (ConnectException e) {
						PdkToolsLog.logError("Operation failed: " + e.getMessage() + "\nPlease make sure the server '" + selectedServer.getName() + "' (" + selectedServer.getHost() + ":"
								+ selectedServer.getPort() + ") is running. Unable to connect to the Portal", e);
					} catch (DeploymentException e) {
						PdkToolsLog.logError("PAR upload failed: " + e.getTargetComponent() + ". Please check the user ID and password.", e);
					} catch (Exception e) {
						PdkToolsLog.logError(e);
					}
				} while (uploadFinished != 0);
			}

		};
		try {
			super.getContainer().run(false, false, runnable);
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

	public void setSelectedProject(IProject project) {
		this.selectedProject = project;
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

	public SAPMPWizardPage getSAPMPWizardPage() {
		return this.serversAndDeployPage;
	}

}
