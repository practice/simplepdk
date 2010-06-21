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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
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

		selectAndReveal(pageTwo.getJavaProject().getProject());

		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(pageOne.getProjectName());
		IPath projectPath = project.getFullPath();
		try {
			IPath dist = createFolderResource(workspaceRoot, projectPath, "dist");

			createFolderResource(workspaceRoot, dist, "css");
			createFolderResource(workspaceRoot, dist, "images");
			createFolderResource(workspaceRoot, dist, "scripts");
			IPath portalInf = createFolderResource(workspaceRoot, dist, "PORTAL-INF");

			createFolderResource(workspaceRoot, portalInf, "classes");
			IPath jspPath = createFolderResource(workspaceRoot, portalInf, "jsp");
			copyFileResource(workspaceRoot, jspPath, "sample.jsp");
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
			copyFileResource(workspaceRoot, portalInf, "portalapp.xml");
			
			// create .settings 
			IPath settings = createFolderResource(workspaceRoot, projectPath, ".settings");
			copyFileResource(workspaceRoot, settings, "org.eclipse.jst.jsp.core.prefs");
			copyFileResource(workspaceRoot, settings, "org.eclipse.wst.html.core.prefs");
		} catch (CoreException e) {
			PdkToolsLog.logError("Cannot create project", e);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean canFinish() {
		return pageTwo.getJavaProject() != null;
	}

	private void copyFileResource(IWorkspaceRoot workspaceRoot, IPath parent, String filename) throws CoreException {
		IPath filePath = parent.append(filename);
		IFile file = workspaceRoot.getFile(filePath);
		InputStream is = getClass().getResourceAsStream("/com/bpnr/portal/devtools/resources/" + filename);
		file.create(is, true, null);
		
	}

	protected void selectAndReveal(IResource newResource) {
		BasicNewResourceWizard.selectAndReveal(newResource, workbench.getActiveWorkbenchWindow());
	}

	@Override
	public boolean performCancel() {
		pageTwo.performCancel();
		return super.performCancel();
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
