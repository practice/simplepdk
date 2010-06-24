package com.bpnr.portal.devtools.actions;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.part.FileEditorInput;

import com.bpnr.portal.devtools.PdkToolsActivator;
import com.bpnr.portal.devtools.PdkToolsLog;
import com.bpnr.portal.devtools.QuickPARUploadWizard;
import com.bpnr.portal.devtools.preferences.PortalServer;
import com.bpnr.portal.devtools.preferences.PortalServerPref;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class RunAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	private IProject selectedProject;

	/**
	 * The constructor.
	 */
	public RunAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	@SuppressWarnings("rawtypes")
	public void run(IAction action) {
		if (this.selectedProject == null) {
			IEditorInput editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
			if ((editor instanceof FileEditorInput) && (editor != null)) {
				FileEditorInput editorInput = (FileEditorInput) editor;
				this.selectedProject = editorInput.getFile().getProject();
			} else {
				return;
			}
		}
		IFile portalapp = selectedProject.getFile("dist/PORTAL-INF/portalapp.xml");
        SAXReader reader = new SAXReader();
        try {
			Document doc = reader.read(portalapp.getContents());
			List components = doc.getRootElement().element("components").elements("component");
			ArrayList<String> compNames = new ArrayList<String>();
			for (Object compObj : components) {
				Element comp = (Element) compObj;
				compNames.add(comp.attributeValue("name"));
			}
			ElementListSelectionDialog selectionDialog = new ElementListSelectionDialog(window.getShell(), new LabelProvider());
			selectionDialog.setElements(compNames.toArray(new String[compNames.size()]));
			selectionDialog.setMessage("Choose a component to run.");
			selectionDialog.open();
			selectionDialog.setMultipleSelection(false);
			Object[] result = selectionDialog.getResult();
			if (result == null)
				return;
			String selectedComp = (String) result[0];
			PortalServer server = guessLauchServer();
			if (server == null) {
				MessageDialog diag = new MessageDialog(window.getShell(), "Server definition error", null, "No server defined: Check your setting", 1, new String[] { "OK" }, 0);
		        diag.open();
		        return;
			}
			String url = "http://" + server.getHost() + ":" + server.getPort() + "/irj/servlet/prt/portal/prtroot/" + selectedProject.getName() + "." + selectedComp;
			Program.launch(url);
		} catch (DocumentException e) {
			PdkToolsLog.logError(e);
		} catch (CoreException e) {
			PdkToolsLog.logError(e);
		}
	}

	private PortalServer guessLauchServer() {
		PortalServer server = PortalServerPref.getLastServer();
		if (server != null)
			return server;
		return PortalServerPref.getDefServer();
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {

		if ((selection == null) || !(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection ss = (IStructuredSelection) selection;

		Object obj = ss.getFirstElement();

		IProject currentProject = null;
		if (obj instanceof IJavaElement) {
			currentProject = ((IJavaElement) obj).getJavaProject().getProject();
		} else if (obj instanceof IResource) {
			currentProject = ((IResource) obj).getProject();
		}
		if ((currentProject == null) || (!currentProject.isOpen())) {
			action.setEnabled(false);
			return;
		}

		action.setEnabled(true);
		this.selectedProject = currentProject;
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}