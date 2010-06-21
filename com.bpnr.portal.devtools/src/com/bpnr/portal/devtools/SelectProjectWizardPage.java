package com.bpnr.portal.devtools;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class SelectProjectWizardPage extends WizardPage {
	private SelectProjectPane pane;
	private Control selectPaneControl;

	public SelectProjectWizardPage(String pageName, String title) {
		super(pageName, title, PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT));
	}

	public SelectProjectWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	public boolean canFlipToNextPage() {
		return this.pane.getSelectedProject() != null;
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible)
			return;
		this.selectPaneControl.setFocus();
	}

	public void createControl(Composite composite) {
		Composite content = new Composite(composite, 0);
		pane = new SelectProjectPane(content);
		content.setLayout(new GridLayout());
		pane.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(pane.getSelectedProject() != null);
			}
		});
		pane.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				IWizardPage followUp = getWizard().getNextPage(SelectProjectWizardPage.this);

				if ((followUp == null) || (pane.getSelectedProject() == null))
					return;
				getContainer().showPage(followUp);
			}
		});
		pane.getContent().setLayoutData(new GridData(1808));
		selectPaneControl = this.pane.getContent();
		setControl(content);
		setPageComplete(this.pane.getSelectedProject() != null);
	}

	public IProject getSelectedProject() {
		return this.pane.getSelectedProject();
	}
}