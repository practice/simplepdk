package com.bpnr.portal.devtools;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class QuickPARUploadWizard extends SAPMPWizard implements INewWizard {
	private SAPMPWizardPage mm_wizardPage;

	public QuickPARUploadWizard() {
		super.setNeedsProgressMonitor(true);
		super.setWindowTitle("New");
	}

	public QuickPARUploadWizard(IProject selectedProject) {
		this.selectedProject = selectedProject;
	}

	public IProject getSelectedProject() {
		return this.selectedProject;
	}

	public boolean needsPreviousAndNextButtons() {
		return false;
	}

	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		if (this.selectedProject != null) {
			this.mm_wizardPage = new SAPMPWizardPage();
			super.setSAPMPWizardPage(this.mm_wizardPage);
			this.mm_wizardPage.setWizard(this);
		}
	}

	public void addPages() {
		super.addPage(this.mm_wizardPage);
	}

	public boolean canFinish() {
		return this.mm_wizardPage.isPageComplete();
	}

	public IWizardPage getNextPage(IWizardPage arg0) {
		return null;
	}

	public int getPageCount() {
		return 1;
	}

	public IWizardPage getPreviousPage(IWizardPage arg0) {
		return null;
	}

	public boolean isHelpAvailable() {
		return false;
	}
}