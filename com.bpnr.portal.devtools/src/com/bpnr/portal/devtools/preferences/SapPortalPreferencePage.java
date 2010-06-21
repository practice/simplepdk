package com.bpnr.portal.devtools.preferences;

import java.util.Iterator;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.bpnr.portal.devtools.PdkToolsActivator;

/**
 * 
 */

public class SapPortalPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button btnAdd;
	private Button btnEdit;
	private Button btnCopy;
	private Button btnRemove;

	private CheckboxTableViewer tableViewer;
	private ITableLabelProvider labelProvider = new ServerListLabelProvider();
	private IStructuredContentProvider contentProvider = new ServerListContentProvider(PortalServerPref.getServers());

	public SapPortalPreferencePage() {
		setPreferenceStore(PdkToolsActivator.getDefault().getPreferenceStore());
		setDescription("Enter information to define your servers.");
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		// parent.setLayout(new FillLayout());
		root.setLayout(new FillLayout());

		Group serverGroup = new Group(root, SWT.NULL);
		serverGroup.setText("Servers definition");
		serverGroup.setLayout(new GridLayout());

		tableViewer = createTableViewer(serverGroup);
		final Table table = tableViewer.getTable();

		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == 32) {
				}
				if (table.getSelectionCount() == 0) {
					btnRemove.setEnabled(false);
					btnEdit.setEnabled(false);
					btnCopy.setEnabled(false);
				} else if (table.getSelectionCount() == 1) {
					btnRemove.setEnabled(true);
					btnEdit.setEnabled(true);
					btnCopy.setEnabled(true);
				} else {
					btnRemove.setEnabled(true);
					btnEdit.setEnabled(false);
					btnCopy.setEnabled(false);
				}
			}
		});
		TableColumn columnAlias = new TableColumn(table, 0);
		columnAlias.setText("Alias");
		columnAlias.setWidth(120);
		TableColumn columnHost = new TableColumn(table, 0);
		columnHost.setText("Host");
		columnHost.setWidth(80);
		TableColumn columnPort = new TableColumn(table, 0);
		columnPort.setText("Port");
		columnPort.setWidth(50);
		TableColumn columnLogin = new TableColumn(table, 0);
		columnLogin.setText("Login");
		columnLogin.setWidth(80);
		TableColumn columnDescription = new TableColumn(table, 131072);
		columnDescription.setText("Description");
		columnDescription.setWidth(100);

		initializeTable();

		Composite compositeListButtons = new Composite(serverGroup, SWT.NULL);
		RowLayout lbRowLayout = new RowLayout(256);
		lbRowLayout.pack = false;
		lbRowLayout.type = 256;
		lbRowLayout.marginLeft = 5;
		lbRowLayout.marginTop = 5;
		lbRowLayout.marginRight = 5;
		lbRowLayout.marginBottom = 5;
		lbRowLayout.spacing = 5;
		compositeListButtons.setLayout(lbRowLayout);

		this.btnAdd = new Button(compositeListButtons, SWT.NULL);
		this.btnAdd.setText("Add...");
		this.btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doAddAction();
			}
		});
		this.btnEdit = new Button(compositeListButtons, SWT.NULL);
		this.btnEdit.setText("Edit...");
		this.btnEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doEditAction();
			}
		});
		this.btnCopy = new Button(compositeListButtons, SWT.NULL);
		this.btnCopy.setText("Copy...");
		this.btnCopy.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doCopyAction();
			}
		});
		this.btnRemove = new Button(compositeListButtons, SWT.NULL);
		this.btnRemove.setText("Remove");
		this.btnRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doRemoveAction();
			}
		});
		this.btnRemove.setEnabled(false);
		this.btnEdit.setEnabled(false);
		this.btnCopy.setEnabled(false);

		return root;
	}

	protected void doAddAction() {
		ServerInputDialog diag = new ServerInputDialog(super.getShell(), this, "Add New Server Settings", ServerInputDialog.ADD_OPERATION);
		diag.open();
		if (diag.getReturnCode() != 0)
			return;
		PortalServer toAdd = new PortalServer(diag.getAliasValue(), diag.getHostValue(), diag.getPortValue(), diag.getLoginValue(), diag.getDescriptionValue());
		PortalServerPref.getServers().add(toAdd);
		tableViewer.setInput(toAdd);
	}

	@SuppressWarnings("unchecked")
	protected void doRemoveAction() {
		ISelection s = tableViewer.getSelection();
		Iterator it = ((IStructuredSelection) s).iterator();
		while (it.hasNext()) {
			Object o = it.next();
			PortalServerPref.getServers().remove(o);
			tableViewer.setInput(o);
		}
		this.btnRemove.setEnabled(false);
		this.btnEdit.setEnabled(false);
		this.btnCopy.setEnabled(false);
	}

	protected void doCopyAction() {
		IStructuredSelection ss = (IStructuredSelection) tableViewer.getSelection();
		if (ss.isEmpty())
			return;
		PortalServer os = (PortalServer) ss.getFirstElement();

		ServerInputDialog diag = new ServerInputDialog(super.getShell(), this, "Duplicate Server Settings", "Copy_Of_" + os.getName(), os.getHost(), os.getPort(), os.getLoginId(), os.getDesc(),
				ServerInputDialog.COPY_OPERATION);

		diag.open();
		if (diag.getReturnCode() != 0)
			return;
		PortalServer toAdd = new PortalServer(diag.getAliasValue(), diag.getHostValue(), diag.getPortValue(), diag.getLoginValue(), diag.getDescriptionValue());
		PortalServerPref.getServers().add(toAdd);
		this.tableViewer.setInput(toAdd);
	}

	protected void doEditAction() {
		IStructuredSelection ss = (IStructuredSelection) tableViewer.getSelection();
		if (ss.isEmpty())
			return;
		PortalServer os = (PortalServer) ss.getFirstElement();

		ServerInputDialog diag = new ServerInputDialog(super.getShell(), this, "Change Server Settings", os.getName(), os.getHost(), os.getPort(), os.getLoginId(), os.getDesc(),
				ServerInputDialog.EDIT_OPERATION);

		diag.open();
		if (diag.getReturnCode() != 0)
			return;
		// PortalServer ns = new PortalServer(diag.getAliasValue(),
		// diag.getHostValue(), diag.getPortValue(), diag.getLoginValue(),
		// diag.getDescriptionValue());
		os.setDesc(diag.getDescriptionValue());
		os.setHost(diag.getHostValue());
		os.setLoginId(diag.getLoginValue());
		os.setName(diag.getAliasValue());
		os.setPort(diag.getPortValue());

		this.tableViewer.setInput(os);
	}

	private void initializeTable() {
		PortalServerPref.loadServerList();
		tableViewer.setInput("");
		// updateDefServerChecked();
	}

	// private void createServerListContents(Composite parent) {
	// GridLayout layout = new GridLayout();
	// layout.marginHeight = 0;
	// layout.marginWidth = 0;
	// layout.numColumns = 2;
	//
	// Composite serverListComposite = new Composite(parent, SWT.NULL);
	// createTableViewer(serverListComposite);
	// GridData data = new GridData(GridData.FILL_BOTH);
	// listControl.setLayoutData(data);

	// Control buttonsControl=
	// fTodoTasksList.getButtonBox(serverListComposite);
	// buttonsControl.setLayoutData(new
	// GridData(GridData.HORIZONTAL_ALIGN_FILL |
	// GridData.VERTICAL_ALIGN_BEGINNING));
	// }

	protected CheckboxTableViewer createTableViewer(Composite parent) {
		Table table = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		// table.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL |
		// GridData.VERTICAL_ALIGN_BEGINNING));
		tableViewer = new CheckboxTableViewer(table);
		tableViewer.setCheckStateProvider(new ICheckStateProvider() {
			@Override
			public boolean isGrayed(Object element) {
				return false;
			}

			@Override
			public boolean isChecked(Object element) {
				PortalServer ps = (PortalServer) element;
				return ps.isDefaultServer();
			}
		});
		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent e) {
				doCheckStateChanged(e);
			}
		});
		tableViewer.setLabelProvider(labelProvider);
		tableViewer.setContentProvider(contentProvider);
		return tableViewer;
	}

	protected void doCheckStateChanged(CheckStateChangedEvent e) {
		PortalServer ps = (PortalServer) e.getElement();
		tableViewer.setCheckedElements(new PortalServer[] { ps });

		if (e.getChecked()) {
			PortalServerPref.setDefServer(ps.getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		PortalServerPref.saveServerList();
		return true;
	}

	@Override
	protected void performApply() {
		performOk();
	}

	public String getPageId() {
		return this.getClass().getName();
	}
}