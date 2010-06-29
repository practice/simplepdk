package com.bpnr.portal.devtools;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class SAPMPWizardPage extends WizardPage {

	private ChooseServerComponent serverChooser;
	private IProject project;

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
		container.setLayout(new FillLayout());

		this.serverChooser = new ChooseServerComponent(container);
		container.setFocus();

		super.setControl(container);

		this.serverChooser.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				getContainer().updateButtons();
			}
		});
		this.serverChooser.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				getContainer().updateButtons();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				getContainer().updateButtons();
			}
		});
		container.setFocus();
		
		
	}

	public ChooseServerComponent getServerConfigSelector() {
		return this.serverChooser;
	}

	public boolean isPageComplete() {
		if ((this.serverChooser != null) && (this.serverChooser.getSelectedServerConfig() != null)) {
			String password = RememberServerPassword.get().getPassword(serverChooser.getSelectedServerConfig().getName());
			return (password != null && password.length() > 0);
		}
		return false;
	}
}
