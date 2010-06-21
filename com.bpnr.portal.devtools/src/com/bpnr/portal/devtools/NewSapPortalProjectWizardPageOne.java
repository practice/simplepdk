package com.bpnr.portal.devtools;

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
		IClasspathEntry varEntry = JavaCore.newVariableEntry(new Path("SAPPORTAL/prtapi.jar"), // library location
				null, // source archive location
				null, // source archive root path
				true); // exported

		IClasspathEntry[] defaultEntries = super.getDefaultClasspathEntries();
		IClasspathEntry[] classpathEntries = Arrays.copyOf(defaultEntries, defaultEntries.length + 1);
		classpathEntries[defaultEntries.length] = varEntry;
		return classpathEntries;
	}
	
}
