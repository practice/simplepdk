package com.bpnr.portal.devtools.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;

import com.bpnr.portal.devtools.EclipseIDE;
import com.bpnr.portal.devtools.PdkToolsActivator;
import com.bpnr.portal.devtools.PdkToolsLog;
import com.bpnr.portal.devtools.ProgressMonitors;
import com.bpnr.portal.devtools.preferences.PortalServer;

public class DeployProgressRunnable implements IRunnableWithProgress {

	private final IProject project;
	private final PortalServer server;

	public DeployProgressRunnable(IProject project, PortalServer server) {
		this.project = project;
		this.server = server;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			monitor.beginTask("Processing request...", 4);
			EclipseIDE.saveAllDirtyEditorsForProject(project, ProgressMonitors.getTaskToSubtaskWrapper(monitor));
			monitor.worked(1);

			monitor.subTask("Rebuilding the project...");

			if (!EclipseIDE.rebuildCurrentProject(project, monitor)) {
				PdkToolsLog.logError("Build Error: Build of the project " + project.getName() + " failed.");
				return;
			}

			monitor.worked(1);

			try {
				File prospectedParFile = new File(EclipseIDE.getParArchiveName(project));
				monitor.subTask("Creating the archive...");

				EclipseIDE.makeParArchiveFromProject(project);
				try {
					project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
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
		monitor.subTask("Deploying archive...");
		boolean deploySuccess = false;
		try {
			DeployEngine deployEngine = new DeployEngine(server);
			System.out.println(server);
			deployEngine.deploy(prospectedParFile);
			deploySuccess = true;
		} catch (UnknownHostException e) {
			PdkToolsLog.logError("Operation failed: Unknown host. Please make sure the server '" + server.getName() + "' (" + server.getHost() + ":" + server.getPort() + ") exists and is running.",
					e);
		} catch (ConnectException e) {
			PdkToolsLog.logError("Operation failed: " + e.getMessage() + ". Please make sure the server '" + server.getName() + "' (" + server.getHost() + ":" + server.getPort()
					+ ") is running. Unable to connect to the Portal", e);
		} catch (DeploymentException e) {
			PdkToolsLog.logError("PAR upload failed: " + e.getTargetComponent() + ". Please check the user ID and password.", e);
		} catch (Exception e) {
			PdkToolsLog.logError(e);
		} finally {
			if (!deploySuccess) {
				try {
					PdkToolsActivator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.pde.runtime.LogView");
				} catch (PartInitException e) {
					PdkToolsLog.logError(e);
				}
			}
		}
	}

}
