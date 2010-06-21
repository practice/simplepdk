package com.bpnr.portal.devtools.swt;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LabeledComboBox extends Composite implements Labeled {
	private Label label;
	private Combo combo;
	private boolean editable;

	public LabeledComboBox(Composite composite, int compositeConstants, String labelText, Object[] items, Object preSelection, boolean editable) {
		super(composite, compositeConstants);

		this.editable = editable;

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 5;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		super.setLayout(layout);

		GridData gd = new GridData(32);
		this.label = new Label(this, 0);
		this.label.setText(labelText);
		this.label.setLayoutData(gd);

		gd = new GridData(768);
		this.combo = new Combo(this, (editable) ? 4 : 12);
		this.combo.setLayoutData(gd);
		setComboBoxItems(items, preSelection);
		super.setLayoutData(new GridData(768));
	}

	public void setEnabled(boolean enabled) {
		this.label.setEnabled(enabled);
		this.combo.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	public void setComboBoxItems(Object[] items, Object preSelection) {
		if (this.combo == null)
			return;

		String[] strItems = new String[items.length];
		if (items instanceof String[])
			strItems = (String[]) items;
		else {
			for (int i = 0; i < items.length; ++i) {
				strItems[i] = items[i].toString();
			}
		}
		this.combo.setItems(strItems);

		String strPreSelection = null;
		if (preSelection != null) {
			if (preSelection instanceof String)
				strPreSelection = (String) preSelection;
			else {
				strPreSelection = preSelection.toString();
			}

			if (this.editable) {
				this.combo.setText(strPreSelection);
			} else {
				int index = this.combo.indexOf(strPreSelection);
				if (index != -1)
					this.combo.select(index);
			}
		} else {
			this.combo.select(0);
		}
	}

	public void setLabelWidth(int width) {
		((GridData) this.label.getLayoutData()).widthHint = width;
	}

	public int getMinimumLabelWidth() {
		return this.label.computeSize(-1, -1, true).x;
	}

	public void setToolTipText(String s) {
		this.label.setToolTipText(s);
	}

	public String getToolTipText() {
		return this.label.getToolTipText();
	}

	public Combo getComboBox() {
		return this.combo;
	}

	public String getValue() {
		int selectionIndex = this.combo.getSelectionIndex();
		if (selectionIndex != -1)
			return this.combo.getItem(selectionIndex);
		if (this.editable) {
			return this.combo.getText();
		}
		return null;
	}

	public void addModifyListener(ModifyListener listener) {
		this.combo.addModifyListener(listener);
	}
}