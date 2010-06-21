package com.bpnr.portal.devtools;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class SAPMPWizardPage extends WizardPage {

	private ChooseServerComponent serverChooser;
//	private IProject project;

	public SAPMPWizardPage() {
		super("PAR Deploy");
		super.setMessage("Choose target server for the PAR file for your Portal Application");
		super.setTitle("Deploy a PAR file from the Portal Application Project");
		super.setDescription("Choose target server for the PAR file for your Portal Application");
	}

	public SAPMPWizardPage(String parPath) {
		super("PAR Deploy");
		super.setMessage("Choose target server for the PAR file for your Portal Application");
		super.setTitle("Upload a PAR file");
		super.setDescription("Choose target server for the PAR file for your Portal Application");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		layout.verticalSpacing = 9;

		GridData gd = new GridData(768);

		this.serverChooser = new ChooseServerComponent(container);
		container.setFocus();

		super.setControl(container);

		this.serverChooser.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				getContainer().updateButtons();
			}
		});
		container.setFocus();
	}

//	public void setVisible(boolean visible) {
//		this.project = ((SAPMPWizard) super.getWizard()).getSelectedProject();
//		super.setVisible(visible);
//	}

//	public void setSelectedProject(IProject project) {
//		this.project = project;
//	}

	public ChooseServerComponent getServerConfigSelector() {
		return this.serverChooser;
	}

	public boolean isPageComplete() {
		return ((this.serverChooser != null) && (this.serverChooser.getSelectedServerConfig() != null));
	}
}
