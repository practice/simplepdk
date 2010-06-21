package com.bpnr.portal.devtools.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.bpnr.portal.devtools.EclipseIDE;
import com.bpnr.portal.devtools.QuickPARUploadWizard;
import com.bpnr.portal.devtools.RememberServerPassword;
import com.bpnr.portal.devtools.preferences.PortalServer;
import com.bpnr.portal.devtools.preferences.PortalServerPref;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class UploadParAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	private IProject selectedProject;
	private String parFileName;
	private PortalServer uploadServer = null;
//	private boolean uploadComplete = false;
	private boolean errorStatus = false;
	private String statusMessage;

	/**
	 * The constructor.
	 */
	public UploadParAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
//		String actionId = action.getId();

		if (this.selectedProject == null) {
			IEditorInput editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
			if ((editor instanceof FileEditorInput) && (editor != null)) {
				FileEditorInput editorInput = (FileEditorInput) editor;
				this.selectedProject = editorInput.getFile().getProject();
			} else {
				return;
			}
		}

		PortalServer defServer = PortalServerPref.getDefServer();
		PortalServer lastServer = PortalServerPref.getLastServer();

		if (lastServer != null) {
			this.uploadServer = lastServer;
		} else if (defServer != null) {
			this.uploadServer = defServer;
		}

		if ((this.uploadServer == null) || (RememberServerPassword.get().getPassword(uploadServer.getName()) == null)) {
			QuickPARUploadWizard wizard = new QuickPARUploadWizard(this.selectedProject);
			wizard.init(PlatformUI.getWorkbench(), null);
			WizardDialog wd = new WizardDialog(window.getShell(), wizard);
			wd.open();
		} else {
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Processing request...", 3);
					monitor.subTask("Building project...");
					try {
						if (!EclipseIDE.rebuildCurrentProject(selectedProject, monitor)) {
							MessageDialog.openError(window.getShell(), "PAR Export", "Build of the project " + selectedProject.getName() + " failed.");
							monitor.done();
							return;
						}
					} catch (Exception e) {
						MessageDialog.openError(window.getShell(), "PAR Export", "Build of the project " + selectedProject.getName() + " failed 2.");
						monitor.done();
						return;
					}

					monitor.worked(1);
					monitor.subTask("Creating PAR file...");
					try {
						EclipseIDE.makeParArchiveFromProject(selectedProject);
					} catch (Exception e) {
						MessageDialog.openError(window.getShell(), "PAR Export", "Creation of PAR file " + parFileName + " failed.");
						monitor.done();
						return;
					}
					try {
						selectedProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					} catch (CoreException e) {
					}

					monitor.worked(1);
					monitor.subTask("Deploying PAR file...");
					try {
						DeployEngine deployEngine = new DeployEngine(uploadServer);
						deployEngine.deploy(parFileName);
					} catch (Exception e) {
						MessageDialog.openError(window.getShell(), "PAR Export", "Connection refused to " + uploadServer.getName() + " server.");
						return;
					}
					monitor.worked(1);
					monitor.done();
				}
			};
			try {
				new ProgressMonitorDialog(window.getShell()).run(true, true, runnable);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!this.errorStatus)
				return;
			MessageDialog.openError(window.getShell(), "Error", this.statusMessage);
		}
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {

		if ((selection == null) || !(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection ss = (IStructuredSelection) selection;

		Object obj = ss.getFirstElement();

		IProject currentProject = null;
		if (obj instanceof IJavaElement) {
			currentProject = ((IJavaElement) obj).getJavaProject().getProject();
		} else if (obj instanceof IResource) {
			currentProject = ((IResource) obj).getProject();
		}
		if ((currentProject == null) || (!currentProject.isOpen())) {
			action.setEnabled(false);
			return;
		}

		action.setEnabled(true);
		this.selectedProject = currentProject;
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}


}