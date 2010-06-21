package com.bpnr.portal.devtools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.bpnr.portal.devtools.preferences.PortalServer;
import com.bpnr.portal.devtools.preferences.PortalServerPref;
import com.bpnr.portal.devtools.preferences.SapPortalPreferencePage;

public class ChooseServerComponent {

	private Composite mm_container;
	private Group mm_group;
	private Table mm_table;
	private static int PASSWD_COL = 2;
	private Button mm_buttonAdvanced;
	private Label mm_serverDetails;
	private TableViewer mm_tviewer;
	private TableContentProvider mm_cp;

	public ChooseServerComponent(Composite container) {
		this.mm_container = container;

		createTable();
		addListeners();
		createTablesColumns();

		initializeTable();
		initializeTableSelection();
		createDetailsField();
		createButton();
	}

	private void createTable() {
		this.mm_group = new Group(this.mm_container, 32);
		this.mm_group.setText("Servers");
		this.mm_group.setLayout(new GridLayout());
		this.mm_group.setLayoutData(new GridData(1808));

//		int style = 67588;
		this.mm_table = new Table(this.mm_group, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 120;
		this.mm_table.setLayoutData(gd);

		this.mm_tviewer = new TableViewer(this.mm_table);
		this.mm_cp = new TableContentProvider();
		this.mm_tviewer.setContentProvider(this.mm_cp);
		this.mm_tviewer.setLabelProvider(new TableLabelProvider());
	}

	private void addListeners() {
		final TableEditor editor = new TableEditor(this.mm_table);

		this.mm_table.addListener(3, new Listener() {

			public void handleEvent(Event event) {
				Rectangle clientArea = mm_table.getClientArea();
				Point pt = new Point(event.x, event.y);
				TableItem[] selectedItems = mm_table.getSelection();
				if ((selectedItems == null) || (selectedItems.length != 1)) {
					return;
				}
				int selectedIndex = mm_table.indexOf(mm_table.getSelection()[0]);
				int index = mm_table.getTopIndex();
				while (index < mm_table.getItemCount()) {
					boolean visible = false;
					final TableItem item = mm_table.getItem(index);
					final Text text = new Text(mm_table, 0);
					text.setEchoChar('*');
					text.addFocusListener(new FocusListener() {						
						@Override
						public void focusLost(FocusEvent e) {
							PortalServer server = (PortalServer) item.getData();
							RememberServerPassword.get().setPassword(server.getName(), text.getText());
						}
						@Override
						public void focusGained(FocusEvent e) {
						}
					});
					Rectangle rect = item.getBounds(PASSWD_COL);
					if (rect.contains(pt)) {
						editor.horizontalAlignment = 16384;
						editor.grabHorizontal = true;
						editor.minimumWidth = 50;

						editor.setEditor(text, item, PASSWD_COL);

						if ((item.getData() != null) && (item.getData() instanceof PortalServer)) {
							PortalServer s = (PortalServer) item.getData();
							if (RememberServerPassword.get().getPassword(s.getName()) != null) {
								text.setText(RememberServerPassword.get().getPassword(s.getName()));
							} else {
								text.setText("");
							}
						}

						text.setFocus();
					} else {
						PortalServer s = (PortalServer) PortalServerPref.getServers().get(selectedIndex);
						mm_serverDetails.setText("Host: " + s.getHost() + "  Port: " + s.getPort() + "  Login: " + s.getLoginId());
					}

					if ((!visible) && (rect.intersects(clientArea))) {
						visible = true;
					}

					if (!visible)
						return;
					++index;
				}
			}
		});
		this.mm_table.addListener(32, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = mm_table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = mm_table.getTopIndex();
				while (index < mm_table.getItemCount()) {
					boolean visible = false;
					TableItem item = mm_table.getItem(index);
					for (int i = 0; i < 3; ++i) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							if (i == 1) {
								PortalServer s = (PortalServer) PortalServerPref.getServers().get(index);
								mm_table.setToolTipText("Alias:" + s.getName() + "\n" + "Host: " + s.getHost() + "\n" + "Port: " + s.getPort());
							} else {
								mm_table.setToolTipText(null);
							}
						}
						if ((visible) || (!rect.intersects(clientArea)))
							continue;
						visible = true;
					}

					if (!visible)
						return;
					++index;
				}
			}
		});
	}

	public void createTablesColumns() {
		this.mm_table.setLinesVisible(true);
		this.mm_table.setHeaderVisible(true);

		TableColumn columnAlias = new TableColumn(this.mm_table, 0);
		columnAlias.setText("Alias");
		columnAlias.setWidth(120);
		TableColumn columnLogin = new TableColumn(this.mm_table, 0);
		columnLogin.setText("Login");
		columnLogin.setWidth(80);
		TableColumn columnPasswd = new TableColumn(this.mm_table, 0);
		columnPasswd.setText("Password");
		columnPasswd.setWidth(80);
		TableColumn columnDesc = new TableColumn(this.mm_table, 0);
		columnDesc.setText("Description");
		columnDesc.setWidth(300);

		this.mm_table.setSize(this.mm_table.computeSize(400, 150));
	}

	private String toStar(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); ++i) {
			sb.append('*');
		}
		return sb.toString();
	}

	private void initializeTable() {
		ArrayList<PortalServer> serversList = PortalServerPref.getServers();
		if (serversList == null)
			return;
		mm_tviewer.setInput(serversList);
	}

	public void createButton() {
		Composite comp = new Composite(this.mm_group, 0);
		GridLayout gl = new GridLayout(3, false);
		comp.setLayout(gl);

		this.mm_buttonAdvanced = new Button(comp, 131080);
		this.mm_buttonAdvanced.setText("Configure servers settings...");
		this.mm_buttonAdvanced.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SapPortalPreferencePage page = new SapPortalPreferencePage();
				page.setTitle("SAP Enterprise Portal");
				showPreferencePage(page.getPageId(), page);
				List<PortalServer> v = PortalServerPref.getServers();
				for (int i = 0; i < v.size(); ++i) {
					PortalServer s = v.get(i);
					System.out.println("Server: " + RememberServerPassword.get().getPassword(s.getName()));
				}
			}
		});
	}

	protected void showPreferencePage(String id, IPreferencePage page) {
		final IPreferenceNode targetNode = new PreferenceNode(id, page);

		PreferenceManager manager = new PreferenceManager();
		manager.addToRoot(targetNode);
		final PreferenceDialog dialog = new PreferenceDialog(this.mm_container.getShell(), manager);
		BusyIndicator.showWhile(this.mm_container.getDisplay(), new Runnable() {
			public void run() {
				dialog.create();
				dialog.setMessage(targetNode.getLabelText());
				dialog.open();
			}
		});
		mm_tviewer.setInput(PortalServerPref.getServers());
		initializeTableSelection();
	}

	public void setVisible(boolean b) {
		this.mm_table.setVisible(b);
		this.mm_buttonAdvanced.setVisible(b);
		this.mm_group.setVisible(b);
	}

	public void createDetailsField() {
		this.mm_serverDetails = new Label(this.mm_group, 16777216);
		this.mm_serverDetails.setLayoutData(new GridData(256));
	}

	private void initializeTableSelection() {
		PortalServer targetServer = PortalServerPref.getLastServer();

		if (targetServer == null) {
			targetServer = PortalServerPref.getDefServer();
		}
		if (targetServer == null)
			return;
		int ind = indexForElement(targetServer);
		if (ind == -1)
			return;
		this.mm_table.setSelection(ind);
		this.mm_table.showSelection();
	}

	private int indexForElement(PortalServer s) {
		TableItem[] items = this.mm_table.getItems();
		for (int i = 0; i < items.length; ++i) {
			if (items[i].getData().equals(s)) {
				return i;
			}
		}

		return -1;
	}

	public void saveOptions() {
		PortalServer s = getSelectedServer();
		if (s == null)
			return;
		PortalServerPref.setLastServer(s);
	}

	public PortalServer getSelectedServer() {
		int ind = this.mm_table.getSelectionIndex();
		if (ind != -1) {
			return (PortalServer) this.mm_table.getItem(ind).getData();
		}

		return null;
	}

	public void addSelectionListener(SelectionListener listener) {
		this.mm_table.addSelectionListener(listener);
	}

	public void addPaintListener(PaintListener listener) {
		this.mm_table.addPaintListener(listener);
	}

	public PortalServer getSelectedServerConfig() {
		PortalServer server = getSelectedServer();
		if (server != null) {
			saveOptions();
			return server;
		}
		return null;
	}

	public boolean isServerDefined() {
		return this.mm_table.getItemCount() != 0;
	}

	class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		TableLabelProvider() {
		}

		public Image getColumnImage(Object arg0, int arg1) {
			return null;
		}

		public String getColumnText(Object arg0, int arg1) {
			if (arg0 instanceof PortalServer) {
				PortalServer s = (PortalServer) arg0;
				switch (arg1) {
				case 0:
					return s.getName();
				case 1:
					return s.getLoginId();
				case 2:
					if (RememberServerPassword.get().getPassword(s.getName()) == null) {
						return "";
					}
					return toStar(RememberServerPassword.get().getPassword(s.getName()));
				case 3:
					return s.getDesc();
				}

				return "//TO ADD";
			}

			return null;
		}
	}

	class TableContentProvider implements IStructuredContentProvider {
		TableContentProvider() {
		}

		public Object[] getElements(Object arg0) {
			return PortalServerPref.getServers().toArray();
		}

		public void dispose() {
			mm_tviewer.getLabelProvider().dispose();
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			mm_tviewer.refresh();
		}
	}
}
