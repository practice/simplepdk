package com.bpnr.portal.devtools.preferences;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ServerInputDialog extends Dialog {
	private static int PORT_MIN_VALUE = 0;
	private static int PORT_MAX_VALUE = 65535;

	public static int ADD_OPERATION = 1;
	public static int REMOVE_OPERATION = 2;
	public static int EDIT_OPERATION = 3;
	public static int COPY_OPERATION = 4;
	private String mm_title;
	private Composite mm_parent;
	private String mm_aliasValue = null;
	private String mm_hostValue = null;
	private String mm_portValue = null;
	private String mm_loginValue = null;
	private String mm_descValue = null;
	private Label mm_aliasLabel;
	private Label mm_hostLabel;
	private Label mm_portLabel;
	private Label mm_loginLabel;
	private Label mm_descLabel;
	private Text mm_aliasText;
	private Text mm_hostText;
	private Text mm_portText;
	private Text mm_loginText;
	private Text mm_descText;
//	private SapPortalPreferencePage preferencePage;
	private int mm_kind;

	public ServerInputDialog(Shell parent, SapPortalPreferencePage pp, String title, int kind) {
		super(parent);
		this.mm_title = title;
//		this.preferencePage = pp;
		this.mm_kind = kind;
	}

	public ServerInputDialog(Shell parent, SapPortalPreferencePage pp, String title, String alias, String host, int port, String login, String desc, int kind) {
		this(parent, pp, title, kind);

		this.mm_aliasValue = alias;
		this.mm_hostValue = host;
		this.mm_portValue = String.valueOf(port);
		this.mm_loginValue = login;
		this.mm_descValue = desc;
		createDialogArea(parent);
	}

	protected Control createDialogArea(Composite parent) {
		this.mm_parent = parent;
		setTitle(this.mm_title);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 5;
		this.mm_parent.setLayout(layout);

		GridData gd = new GridData(768);

		this.mm_parent.setLayoutData(gd);

		this.mm_aliasLabel = new Label(this.mm_parent, 0);
		this.mm_aliasLabel.setText("Alias:");

		gd = new GridData(64);
		this.mm_aliasLabel.setLayoutData(gd);
		this.mm_aliasText = new Text(this.mm_parent, 2052);
		if (this.mm_aliasValue != null) {
			this.mm_aliasText.setText(this.mm_aliasValue);
		}

		gd = new GridData(768);
		this.mm_aliasText.setLayoutData(gd);
		this.mm_aliasText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String input = ServerInputDialog.this.mm_aliasText.getText();
				if (input.length() <= 50)
					return;
				ServerInputDialog.this.mm_aliasText.setText(input.substring(0, 50));
				ServerInputDialog.this.mm_aliasText.setSelection(50);
			}
		});
		this.mm_hostLabel = new Label(this.mm_parent, 0);
		gd = new GridData(64);
		this.mm_hostLabel.setLayoutData(gd);
		this.mm_hostLabel.setText("Host:");
		this.mm_hostText = new Text(this.mm_parent, 2052);
		gd = new GridData(768);
		this.mm_hostText.setLayoutData(gd);
		if (this.mm_hostValue != null) {
			this.mm_hostText.setText(this.mm_hostValue);
		}

		this.mm_portLabel = new Label(this.mm_parent, 0);
		gd = new GridData(64);
		this.mm_portLabel.setLayoutData(gd);
		this.mm_portLabel.setText("Port:");
		this.mm_portText = new Text(this.mm_parent, 2052);
		gd = new GridData(768);
		this.mm_portText.setLayoutData(gd);
		if (this.mm_portValue != null) {
			this.mm_portText.setText(this.mm_portValue);
		}

		this.mm_loginLabel = new Label(this.mm_parent, 0);
		this.mm_loginLabel.setText("Login:");
		gd = new GridData(64);
		this.mm_loginLabel.setLayoutData(gd);
		this.mm_loginText = new Text(this.mm_parent, 2052);
		gd = new GridData(768);
		this.mm_loginText.setLayoutData(gd);
		if (this.mm_loginValue != null) {
			this.mm_loginText.setText(this.mm_loginValue);
		}

		this.mm_descLabel = new Label(this.mm_parent, 0);
		this.mm_descLabel.setText("Description:");
		gd = new GridData(64);
		this.mm_descLabel.setLayoutData(gd);
		this.mm_descText = new Text(this.mm_parent, 2052);
		gd = new GridData(768);
		this.mm_descText.setLayoutData(gd);
		if (this.mm_descValue != null) {
			this.mm_descText.setText(this.mm_descValue);
		}

		return super.createDialogArea(this.mm_parent);
	}

	public void setTitle(String title) {
		this.mm_title = title;
		if (this.mm_title == null) {
			this.mm_title = "";
		}
		Shell shell = super.getShell();
		if ((shell == null) || (shell.isDisposed()))
			return;
		shell.setText(this.mm_title);
	}

	public String getAliasValue() {
		return this.mm_aliasValue;
	}

	public String getHostValue() {
		return this.mm_hostValue;
	}

	public int getPortValue() {
		return Integer.parseInt(this.mm_portValue);
	}

	public String getLoginValue() {
		return this.mm_loginValue;
	}

	public String getDescriptionValue() {
		return this.mm_descValue;
	}

	public void setAliasText(String alias) {
		this.mm_aliasText.setText(alias);
	}

	public void setHostText(String host) {
		this.mm_hostText.setText(host);
	}

	public void setPortText(int port) {
		this.mm_portText.setText(String.valueOf(port));
	}

	public void setLoginText(String login) {
		this.mm_loginText.setText(login);
	}

	public void setDescriptionText(String description) {
		this.mm_descText.setText(description);
	}

	public void okPressed() {
		if (!isValid())
			return;
		this.mm_aliasValue = this.mm_aliasText.getText();
		this.mm_hostValue = this.mm_hostText.getText();
		this.mm_portValue = this.mm_portText.getText();
		this.mm_loginValue = this.mm_loginText.getText();
		this.mm_descValue = this.mm_descText.getText();
		super.close();
	}

	private boolean isValid() {
		return (doValidateAlias()) && (doValidatePort());
	}

	private boolean isAliasUnique(String alias) {
		ArrayList<PortalServer> list = PortalServerPref.getServers();
		for (int i = 0; i < list.size(); ++i) {
			PortalServer s = (PortalServer) list.get(i);
			if (s.getName().equals(alias)) {
				return false;
			}
		}
		return true;
	}

	private boolean doValidateAlias() {
		boolean isOk = true;
		String value = this.mm_aliasText.getText().trim();
		String errorMsg = null;
		if (value.equals("")) {
			isOk = false;
			errorMsg = "Alias must be defined";
		}
		if ((this.mm_kind != EDIT_OPERATION) && (!isAliasUnique(value))) {
			isOk = false;
			errorMsg = "Illegal Alias value: Alias must be unique";
		}
		if (!isOk) {
			MessageDialog diag = new MessageDialog(this.mm_parent.getShell(), "Server definition error", null, errorMsg, 1, new String[] { "OK" }, 0);
			diag.open();
		}

		return isOk;
	}

	private boolean doValidatePort() {
		String value = this.mm_portText.getText().trim();
		String errorMsg = null;
		boolean isOk = true;
		int port = 0;
		if (value.equals("")) {
			isOk = false;
			errorMsg = "Port value must be defined";
		}
		try {
			port = Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			isOk = false;
			errorMsg = "Illegal Port value: Check your settings";
		}

		if ((port < PORT_MIN_VALUE) || (port > PORT_MAX_VALUE)) {
			isOk = false;
			errorMsg = "Port value must be greater than " + PORT_MIN_VALUE + " and smaller than " + PORT_MAX_VALUE;
		}
		if (!isOk) {
			MessageDialog diag = new MessageDialog(this.mm_parent.getShell(), "Server definition error", null, errorMsg, 1, new String[] { "OK" }, 0);
			diag.open();
		}

		return isOk;
	}
}