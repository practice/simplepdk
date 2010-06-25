package com.bpnr.portal.devtools;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;

public class NewSapPortalProjectWizardPageOne extends NewJavaProjectWizardPageOne {
	@Override
	public IClasspathEntry[] getSourceClasspathEntries() {
		IPath sourceFolderPath = new Path(getProjectName()).makeAbsolute();
		IPath apiSrc = sourceFolderPath.append(new Path("src.api"));
		IPath coreSrc = sourceFolderPath.append(new Path("src.core"));
		IPath apiClasses = sourceFolderPath.append(new Path("classes.api"));
		IPath coreClasses = sourceFolderPath.append(new Path("classes.core"));
		IClasspathEntry apiSrcEntry = JavaCore.newSourceEntry(apiSrc, new IPath[] {}, apiClasses);
		IClasspathEntry coreSrcEntry = JavaCore.newSourceEntry(coreSrc, new IPath[] {}, coreClasses);
		return new IClasspathEntry[] { apiSrcEntry, coreSrcEntry };
	}

	@Override
	public IClasspathEntry[] getDefaultClasspathEntries() {
//		UserLibraryManager libraryManager = JavaModelManager.getUserLibraryManager();
//		UserLibrary portalLibs = libraryManager.getUserLibrary("SAPPORTAL_LIBS");
//		if (portalLibs == null) {
//			JavaCore.new
//			libraryManager.setUserLibrary("SAPPORTAL_LIBS", entries, false);
//		}
//		IClasspathEntry containerEntry = JavaCore.newContainerEntry(new Path(JavaCore.USER_LIBRARY_CONTAINER_ID + "/SAPPORTAL_LIBS"));
//		UserLibraryClasspathContainer container = new UserLibraryClasspathContainer("");
//		entries.add(JavaCore.newContainerEntry(new Path(JavaCore.USER_LIBRARY_CONTAINER_ID + "/SAPPORTAL_LIBS"), true));
		ArrayList<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		entries.add(newVariableEntry("SAPPORTAL_LIBS/prtapi.jar"));
		entries.add(newVariableEntry("SAPPORTAL_LIBS/servlet.jar"));
		entries.add(newVariableEntry("SAPPORTAL_LIBS/activation.jar"));
		entries.add(newVariableEntry("SAPPORTAL_LIBS/com.sap.security.api.jar"));
		entries.add(newVariableEntry("SAPPORTAL_LIBS/mail.jar"));
		entries.add(newVariableEntry("SAPPORTAL_LIBS/GenericConnector.jar"));
		entries.add(newVariableEntry("SAPPORTAL_LIBS/connector1.0.jar"));
		entries.add(newVariableEntry("SAPPORTAL_LIBS/portal_services_api_lib.jar"));
		entries.add(newVariableEntry("SAPPORTAL_LIBS/com.sap.portal.ivs.connectorservice_api.jar"));
		entries.add(newVariableEntry("SAPPORTAL_LIBS/exception.jar"));
		
		IClasspathEntry[] defaultEntries = super.getDefaultClasspathEntries();
		ArrayList<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		list.addAll(Arrays.asList(defaultEntries));
		list.addAll(entries);

		return list.toArray(new IClasspathEntry[list.size()]);
	}

	private IClasspathEntry newVariableEntry(String path) {
		return JavaCore.newVariableEntry(new Path(path), // library location
				null, // source archive location
				null, // source archive root path
				true); // exported
	}

}
