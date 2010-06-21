package com.bpnr.portal.devtools;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class ExportParWizard extends Wizard implements IExportWizard {

//	private IWorkbench workbench;
//	private IStructuredSelection selection;

	public ExportParWizard() {
		setWindowTitle("Export PAR file");
	}

	private IExportWizard delegatee;

	public int hashCode() {
		return this.delegatee.hashCode();
	}

	public void addPages() {
		this.delegatee.addPages();
	}

	public boolean canFinish() {
		return this.delegatee.canFinish();
	}

	public boolean equals(Object obj) {
		return this.delegatee.equals(obj);
	}

	public void createPageControls(Composite composite) {
		this.delegatee.createPageControls(composite);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
//		this.workbench = workbench;
//		this.selection = selection;
//		System.out.println("export init");
		try {
//			this.loader = new SimpleOneDirClassLoader(this.myDir);
//			Class c = this.loader.loadClass("com.sap.portal.developmentTools.ideSpecific.eclipse.wizards.sapmakepar.SAPMPWizard");
//			this.delegatee = ((IExportWizard) c.newInstance());
			this.delegatee = new SAPMPWizard();
			this.delegatee.init(workbench, selection);
		} catch (Exception e) {
//			SapPortalPluginsLogger.logError(this, e);
		}
	}

	public void dispose() {
		this.delegatee.dispose();
	}

	public String toString() {
		return this.delegatee.toString();
	}

	public boolean performFinish() {
		try {
			return this.delegatee.performFinish();
		} catch (Exception e) {
//			SapPortalPluginsLogger.logError(this, e);
		}

		return true;
	}

	public IWizardContainer getContainer() {
		return this.delegatee.getContainer();
	}

	public Image getDefaultPageImage() {
		return this.delegatee.getDefaultPageImage();
	}

	public IDialogSettings getDialogSettings() {
		return this.delegatee.getDialogSettings();
	}

	public IWizardPage getNextPage(IWizardPage page) {
		return this.delegatee.getNextPage(page);
	}

	public IWizardPage getPage(String s) {
		return this.delegatee.getPage(s);
	}

	public int getPageCount() {
		return this.delegatee.getPageCount();
	}

	public IWizardPage[] getPages() {
		return this.delegatee.getPages();
	}

	public IWizardPage getPreviousPage(IWizardPage page) {
		return this.delegatee.getPreviousPage(page);
	}

	public IWizardPage getStartingPage() {
		return this.delegatee.getStartingPage();
	}

	public RGB getTitleBarColor() {
		return this.delegatee.getTitleBarColor();
	}

	public String getWindowTitle() {
		return this.delegatee.getWindowTitle();
	}

	public boolean isHelpAvailable() {
		return this.delegatee.isHelpAvailable();
	}

	public boolean needsPreviousAndNextButtons() {
		return this.delegatee.needsPreviousAndNextButtons();
	}

	public boolean needsProgressMonitor() {
		return this.delegatee.needsProgressMonitor();
	}

	public boolean performCancel() {
		return this.delegatee.performCancel();
	}

	public void setContainer(IWizardContainer container) {
		this.delegatee.setContainer(container);
	}

}
