package com.bpnr.portal.devtools;

import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class NewSapportalProjectWizard extends Wizard implements INewWizard {

	private NewJavaProjectWizardPageOne pageOne;
	private NewJavaProjectWizardPageTwo pageTwo;
	private IWorkbench workbench;
//	private IStructuredSelection selection;

	public NewSapportalProjectWizard() {
	}

	@Override
	public boolean performFinish() {
		final IJavaElement newElement = pageTwo.getJavaProject();

		IWorkingSet[] workingSets = pageOne.getWorkingSets();
		if (workingSets.length > 0) {
			PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(newElement, workingSets);
		}

		// BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
		selectAndReveal(pageTwo.getJavaProject().getProject());

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPart activePart = getActivePart();
				if (activePart instanceof IPackagesViewPart) {
					PackageExplorerPart view = PackageExplorerPart.openInActivePerspective();
					view.tryToReveal(newElement);
				}
			}
		});

		System.out.println("performFinish");
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(pageOne.getProjectName());
		IPath projectPath = project.getFullPath();
		try {
//			if (project.isAccessible()) {
//				pageTwo.createProject(project, pageOne.getProjectLocationURI(), null);
//				project.open(null);
//			}
			IPath dist = createFolderResource(workspaceRoot, projectPath, "dist");

			createFolderResource(workspaceRoot, dist, "css");
			createFolderResource(workspaceRoot, dist, "images");
			createFolderResource(workspaceRoot, dist, "scripts");
			IPath portalInf = createFolderResource(workspaceRoot, dist, "PORTAL-INF");

			createFolderResource(workspaceRoot, portalInf, "classes");
			createFolderResource(workspaceRoot, portalInf, "jsp");
			createFolderResource(workspaceRoot, portalInf, "lib");
			createFolderResource(workspaceRoot, portalInf, "localization");
			createFolderResource(workspaceRoot, portalInf, "logger");
			createFolderResource(workspaceRoot, portalInf, "pagelet");
			IPath privateRes = createFolderResource(workspaceRoot, portalInf, "private");
			createFolderResource(workspaceRoot, portalInf, "srclib.api");
			createFolderResource(workspaceRoot, portalInf, "srclib.core");
			createFolderResource(workspaceRoot, portalInf, "taglib");

			createFolderResource(workspaceRoot, privateRes, "classes");
			createFolderResource(workspaceRoot, privateRes, "lib");
			createPortalAppXml(workspaceRoot, portalInf);
		} catch (CoreException e) {
			Status status = new Status(IStatus.ERROR, PdkToolsActivator.PLUGIN_ID, IStatus.ERROR, e.getMessage() != null ? e.getMessage() : e.toString(), e);
			ErrorDialog.openError(getShell(), "Cannot create project resource folders", e.getMessage(), status);
			return false;
		}
		return true;
	}

	private IWorkbenchPart getActivePart() {
		IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
		if (activeWindow != null) {
			IWorkbenchPage activePage = activeWindow.getActivePage();
			if (activePage != null) {
				return activePage.getActivePart();
			}
		}
		return null;
	}

	protected void selectAndReveal(IResource newResource) {
		BasicNewResourceWizard.selectAndReveal(newResource, workbench.getActiveWorkbenchWindow());
	}

	@Override
	public boolean performCancel() {
		pageTwo.performCancel();
		return super.performCancel();
	}

	private void createPortalAppXml(IWorkspaceRoot workspaceRoot, IPath portalInf) throws CoreException {
		IPath portalappPath = portalInf.append("portalapp.xml");
		IFile file = workspaceRoot.getFile(portalappPath);
		InputStream is = getClass().getResourceAsStream("/com/bpnr/portal/devtools/portalapp.xml");
		file.create(is, true, null);
	}

	private IPath createFolderResource(IWorkspaceRoot workspaceRoot, IPath parent, String name) throws CoreException {
		IPath distPath = parent.append(name);
		IFolder distFolder = workspaceRoot.getFolder(distPath);
		createFolder(distFolder);
		return distFolder.getFullPath();
	}

	private void createFolder(IFolder folder) throws CoreException {
		if (!folder.exists()) {
			IContainer parent = folder.getParent();
			if (parent instanceof IFolder && (!((IFolder) parent).exists()))
				createFolder((IFolder) parent);
			folder.create(true, true, null);
		}
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
//		this.selection = selection;
	}

	@Override
	public void addPages() {
		super.addPages();
		pageOne = new NewSapPortalProjectWizardPageOne();
		pageTwo = new NewJavaProjectWizardPageTwo(pageOne);
		addPage(pageOne);
		addPage(pageTwo);
	}

}
