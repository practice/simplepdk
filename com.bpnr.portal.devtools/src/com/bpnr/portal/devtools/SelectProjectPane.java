package com.bpnr.portal.devtools;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

public class SelectProjectPane {
	private IProject selectedProject;
	private TableViewer tv;
	private Table table;
	private Composite contentPane;

	public SelectProjectPane(Composite parent) {
		this.contentPane = new Composite(parent, SWT.NULL);
		this.contentPane.setLayout(new GridLayout(1, false));

		Label label = new Label(this.contentPane, 64);
		label.setText("Please select a project from the list:");

		this.table = new Table(this.contentPane, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 200;
		this.table.setLayoutData(layoutData);

		this.tv = new TableViewer(this.table);

		this.tv.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setSelectedProject((IProject) ((IStructuredSelection) event.getSelection()).getFirstElement());
			}
		});
		ProjectsProvider providerAll = new ProjectsProvider();

		this.tv.setContentProvider(providerAll);
		this.tv.setLabelProvider(providerAll);
		IContainer workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		this.tv.setInput(workspaceRoot);

		
		IProject currentProject = EclipseIDE.getCurrentProject();

		if (currentProject != null) {
			StructuredSelection structuredSelection = new StructuredSelection(currentProject);
			this.tv.setSelection(structuredSelection);
		}
	}

	protected void setSelectedProject(IProject project) {
		this.selectedProject = project;
	}

	public void addOpenListener(IOpenListener iOpenListener) {
		this.tv.addOpenListener(iOpenListener);
	}

	public void addSelectionChangedListener(ISelectionChangedListener iSelectionChangedListener) {
		this.tv.addSelectionChangedListener(iSelectionChangedListener);
	}

	public IProject getSelectedProject() {
		return this.selectedProject;
	}

	public Control getContent() {
		return this.contentPane;
	}
}