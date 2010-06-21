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
	private IWorkbench workbench;

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
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
						MessageDialog.openError(workbench.getActiveWorkbenchWindow().getShell(), "Build Error", "Build of the project " + selectedProject.getName() + " failed.");
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
						e.printStackTrace();
						// SapPortalPluginsLogger.logError(this, e);
						// uploadSucceededOrOmitted = true;
						//
						// String fileNameRelativeToProjectRoot =
						// AbstractIDE.getSlashSeparatedNameFromStringArray(e.getSegmentsInProject(),
						// File.separatorChar);
						// SAPMPWizard.access$102(SAPMPWizard.this, false);
						// SAPMPWizard.access$202(SAPMPWizard.this,
						// "Sorry, file " + e.getFile().getAbsolutePath() +
						// " does not exist on harddisk,\nalthough it is referenced as "
						// + fileNameRelativeToProjectRoot +
						// " in the project.\nCancelling process.");
						// SAPMPWizard.access$302(SAPMPWizard.this,
						// e.getClass().getName());
					} catch (Exception e) {
						e.printStackTrace();
						// SapPortalPluginsLogger.logError(this, e);
						// uploadSucceededOrOmitted = true;
						// SAPMPWizard.access$102(SAPMPWizard.this, false);
						// SAPMPWizard.access$202(SAPMPWizard.this,
						// e.getMessage());
						// SAPMPWizard.access$302(SAPMPWizard.this,
						// e.getClass().getName());
					}

					monitor.worked(1);
				} catch (Exception e) {
					e.printStackTrace();
					// SapPortalPluginsLogger.logError(this, e);
					// SAPMPWizard.access$102(SAPMPWizard.this, false);
					// SAPMPWizard.access$302(SAPMPWizard.this,
					// e.getClass().getName());
					// SAPMPWizard.access$202(SAPMPWizard.this, e.getMessage());
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
						e.printStackTrace();
						// uploadFinished =
						// guiSystem.showOptionDialog(SAPMPWizard.this.getShell(),
						// "Operation failed: Unknown host \nPlease make sure the server '"
						// + SAPMPWizard.this.mm_server.getAlias() + "' (" +
						// SAPMPWizard.this.mm_server.getHost() + ":" +
						// SAPMPWizard.this.mm_server.getPort() +
						// ") exists and is running.",
						// "Unable to connect to the Portal", 2,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS[1]);
						//
						// if (uploadFinished == 0) {
						// SAPMPWizard.access$702(SAPMPWizard.this, true);
						// }
						//
						// if (uploadFinished == 1) {
						// uploadSucceededOrOmitted = false;
						// }
					} catch (ConnectException e) {
						e.printStackTrace();
						// SapPortalPluginsLogger.logError(this, e);
						// uploadFinished =
						// guiSystem.showOptionDialog(SAPMPWizard.this.getShell(),
						// "Operation failed: " + e.getMessage() +
						// "\nPlease make sure the server '"
						// + SAPMPWizard.this.mm_server.getAlias() + "' (" +
						// SAPMPWizard.this.mm_server.getHost() + ":" +
						// SAPMPWizard.this.mm_server.getPort() +
						// ") is running.",
						// "Unable to connect to the Portal", 2,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS[1]);
						//
						// if (uploadFinished == 0) {
						// SAPMPWizard.access$702(SAPMPWizard.this, true);
						// }
						//
						// if (uploadFinished == 1) {
						// uploadSucceededOrOmitted = false;
						// }
					} catch (DeploymentException e) {
						e.printStackTrace();
						String response = e.getResponse();

						// SapPortalPluginsLogger.logError(this,
						// "Upload Error message: " + e.getMessage());
						// SapPortalPluginsLogger.logError(this,
						// "Upload Response: " + response);
						// SapPortalPluginsLogger.logError(this,
						// "Upload Target Component: " +
						// e.getTargetComponent());
						//
						// if (response != null) {
						// if (response.indexOf("<html>") != -1) {
						// uploadFinished =
						// guiSystem.showOptionDialog(SAPMPWizard.this.getShell(),
						// "PAR upload failed: " + e.getTargetComponent() +
						// ".\n"
						// + "Please check the user ID and password.",
						// "Unable to connect to the Portal", 2,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS[1]);
						// } else if (response.indexOf("<?xml") != -1) {
						// uploadFinished =
						// guiSystem.showOptionDialog(SAPMPWizard.this.getShell(),
						// "PAR upload failed: " + e.getTargetComponent() +
						// ".\n"
						// +
						// "Please check the log (sap-plugin.log) for more detail.",
						// "Unable to connect to the Portal", 2,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS[1]);
						// } else {
						// uploadFinished =
						// guiSystem.showOptionDialog(SAPMPWizard.this.getShell(),
						// "PAR upload failed: " + e.getTargetComponent() +
						// ".\n" + "No detail have been provided.",
						// "Unable to connect to the Portal", 2,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS[1]);
						// }
						//
						// } else {
						// uploadFinished =
						// guiSystem.showOptionDialog(SAPMPWizard.this.getShell(),
						// "PAR upload failed: " + e.getTargetComponent() + "."
						// + "\nPlease make sure the server '"
						// + SAPMPWizard.this.mm_server.getAlias() + "' (" +
						// SAPMPWizard.this.mm_server.getHost() + ":" +
						// SAPMPWizard.this.mm_server.getPort() +
						// ") is running." + "\n"
						// + message, "Unable to connect to the Portal", 2,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS[1]);
						// }
						//
						// if (uploadFinished == 0) {
						// SAPMPWizard.access$702(SAPMPWizard.this, true);
						// }
						//
						// if (uploadFinished == 1) {
						// uploadSucceededOrOmitted = false;
						// }
					} catch (Exception e) {
						e.printStackTrace();
						// SapPortalPluginsLogger.logError(this, e);
						//
						// uploadFinished =
						// guiSystem.showOptionDialog(SAPMPWizard.this.getShell(),
						// "Operation failed: Please make sure the server '" +
						// SAPMPWizard.this.mm_server.getAlias() + "' ("
						// + SAPMPWizard.this.mm_server.getHost() + ":" +
						// SAPMPWizard.this.mm_server.getPort() +
						// ") is running or check the log (" +
						// SapPortalPluginsLogger.LOG_NAME
						// + ") for more detail.",
						// "Unable to connect to the Portal", 2,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS,
						// SAPMPWizardStringLiterals.UPLOAD_NO_SUCCESS_OPTIONS[1]);
						//
						// if (uploadFinished == 0) {
						// SAPMPWizard.access$702(SAPMPWizard.this, true);
						// }

					}
				} while (uploadFinished != 0);
			}

		};
		try {
			super.getContainer().run(false, false, runnable);
		} catch (InvocationTargetException ite) {
			Throwable cause = ite.getTargetException();
			if (cause != null) {
				cause.printStackTrace();
				// SapPortalPluginsLogger.logError(this, cause);
				this.mm_statusType = cause.getClass().getName();
				this.mm_statusMessage = cause.getMessage();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// SapPortalPluginsLogger.logError(this, e);
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
