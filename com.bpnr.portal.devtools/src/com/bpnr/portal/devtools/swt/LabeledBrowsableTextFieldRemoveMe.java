package com.bpnr.portal.devtools.swt;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LabeledBrowsableTextFieldRemoveMe extends Composite implements Labeled {
	public static final int FILES_ONLY = 0;
	public static final int DIRECTORIES_ONLY = 1;
	private String mm_labelText;
	private Label mm_label;
	private Text mm_textField;
	private Button mm_browseButton;
	private int mm_fileSelectionMode;
	private int mm_dialogFlags = 8196;
	private String[] mm_filterNames;
	private String[] mm_filterExtensions;
	private static String mm_filterPath;
	private String mm_initalDirectory;

	public LabeledBrowsableTextFieldRemoveMe(Composite composite, int style, String labelText) {
		super(composite, style);
		this.mm_labelText = labelText;

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = 5;
		layout.marginWidth = 0;
		super.setLayout(layout);

		GridData gd = new GridData(32);
		this.mm_label = new Label(this, 0);
		this.mm_label.setText(labelText);
		this.mm_label.setLayoutData(gd);

		gd = new GridData(768);
		this.mm_textField = new Text(this, 2052);
		this.mm_textField.setLayoutData(gd);

		gd = new GridData(128);
		this.mm_browseButton = new Button(this, 8);
		this.mm_browseButton.setText("...");
		this.mm_browseButton.setLayoutData(gd);
		this.mm_browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		super.setLayoutData(new GridData(768));
	}

	protected void handleBrowse() {
		String fileName = null;

		if (this.mm_fileSelectionMode == 0) {
			FileDialog fd = new FileDialog(super.getShell(), this.mm_dialogFlags);
			fd.setFilterExtensions(this.mm_filterExtensions);
			fd.setFilterNames(this.mm_filterNames);
			fd.setFilterPath(mm_filterPath);
			fd.setFileName(this.mm_initalDirectory);
			fileName = fd.open();
			mm_filterPath = fd.getFilterPath();
		}

		if (this.mm_fileSelectionMode == 1) {
			DirectoryDialog dd = new DirectoryDialog(super.getShell(), this.mm_dialogFlags);
			dd.setFilterPath(mm_filterPath);
			fileName = dd.open();
			mm_filterPath = dd.getFilterPath();
		}

		if (fileName == null)
			return;
		this.mm_textField.setText(fileName);
	}

	public void setFilterNames(String[] filterNames) {
		this.mm_filterNames = filterNames;
	}

	public void setFilterExtensions(String[] filterExtensions) {
		this.mm_filterExtensions = filterExtensions;
	}

	public void setFilterPath(String filterPath) {
		mm_filterPath = filterPath;
	}

	public void setDialogFlags(int dialogFlags) {
		this.mm_dialogFlags = dialogFlags;
	}

	public void setInitalDirectory(String initalDirectory) {
		this.mm_initalDirectory = initalDirectory;
	}

	public void setFileSelectionMode(int fileSelectionMode) {
		this.mm_fileSelectionMode = fileSelectionMode;
	}

	public void setLabelWidth(int width) {
		((GridData) this.mm_label.getLayoutData()).widthHint = width;
	}

	public int getMinimumLabelWidth() {
		return this.mm_label.computeSize(-1, -1, true).x;
	}

	public String getValue() {
		return this.mm_textField.getText();
	}

	public void setValue(String value) {
		if (value == null) {
			value = "";
		}

		this.mm_textField.setText(value);
	}

	public Text getTextField() {
		return this.mm_textField;
	}

	public void setLabelText(String text) {
		this.mm_label.setText(text);
	}

	public String getLabelText() {
		return this.mm_label.getText();
	}

	public void addModifyListener(ModifyListener listener) {
		this.mm_textField.addModifyListener(listener);
	}

	public void setEditable(boolean b) {
		this.mm_textField.setEditable(b);
		this.mm_textField.setEnabled(b);
		this.mm_label.setEnabled(b);
		this.mm_browseButton.setEnabled(b);
	}
}