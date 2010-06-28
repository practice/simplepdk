package com.bpnr.portal.devtools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.views.navigator.ResourceNavigator;

import com.bpnr.portal.devtools.actions.FileNotExistException;
import com.bpnr.portal.devtools.actions.StreamUtil;
import com.bpnr.portal.devtools.actions.StringBasedFileFilter;

public class EclipseIDE {

	public static IProject getCurrentProject() {

		IWorkbenchPart activePart = PdkToolsActivator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();

		if (activePart instanceof ResourceNavigator) {
			try {
				ResourceNavigator resourceNavigator = (ResourceNavigator) activePart;
				IStructuredSelection selection = (IStructuredSelection) resourceNavigator.getSite().getSelectionProvider().getSelection();
				IResource selectedResource = (IResource) selection.getFirstElement();

				if (selectedResource != null) {
					return selectedResource.getProject();
				}

			} catch (Exception e) {
			}

		}

		if (activePart instanceof PackageExplorerPart) {
			try {
				PackageExplorerPart packageExplorer = (PackageExplorerPart) activePart;
				IStructuredSelection selection = (IStructuredSelection) packageExplorer.getSite().getSelectionProvider().getSelection();
				if (selection.getFirstElement() instanceof IProject) {
					return (IProject) selection.getFirstElement();
				}
				if (selection.getFirstElement() instanceof IJavaElement) {
					IJavaElement javaElement = (IJavaElement) selection.getFirstElement();
					return (IProject) javaElement.getJavaProject().getAdapter(IProject.class);
				}

			} catch (Exception e) {
			}

		}

		if (activePart instanceof IEditorPart)
			try {
				IEditorPart editor = (IEditorPart) activePart;
				IEditorInput input = editor.getEditorInput();
				if (!(input instanceof IFileEditorInput))
					return null;
				return ((IFileEditorInput) input).getFile().getProject();
			} catch (Exception e) {
				ISelection simple = PdkToolsActivator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
				try {
					if (simple instanceof IStructuredSelection) {
						Object o = ((IStructuredSelection) simple).getFirstElement();

						if ((o != null) && (o instanceof IAdaptable)) {
							IAdaptable adaptable = (IAdaptable) o;
							IResource res = (IResource) adaptable.getAdapter(IResource.class);

							if (res != null) {
								return res.getProject();
							}
						}
					}
				} catch (Exception exc) {
				}

				// try {
				// if (simple instanceof IMarkSelection) {
				// IMarkSelection selection = (IMarkSelection) simple;
				// IDocument doc = selection.getDocument();
				//
				// if (doc instanceof IAdaptable) {
				// IAdaptable adaptable = (IAdaptable) doc;
				// IResource res = (IResource)
				// adaptable.getAdapter(IResource.class);
				//
				// if (res != null) {
				// return res.getProject();
				// }
				// }
				// }
				// } catch (Exception e) {
				// }
			}
		return null;
	}

	public static IJavaProject getCurrentJavaProject(Object project) {
		return JavaCore.create((IProject) project);
	}

	public static String getCurrentProjectFolder(Object project) {
		IProject currentProject = (IProject) project;

		if (currentProject == null) {
			return null;
		}

		return currentProject.getLocation().toFile().getAbsolutePath();
	}

	public static boolean rebuildCurrentProject(IProject project, IProgressMonitor monitor) throws Exception {
		// TODO test build fail case.
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (Exception e) {
			return false;
		}

		// IMarker[] markers =
		// project.findMarkers("org.eclipse.jdt.core.problem", true,
		// IResource.DEPTH_INFINITE);
		// for (int i = 0; i < markers.length; ++i) {
		// if (((Integer) markers[i].getAttribute("severity")).intValue() == 2)
		// {
		// return false;
		// }
		// }
		return true;
	}

	public static void saveAllDirtyEditorsForProject(IProject project, IProgressMonitor monitor) {
		IWorkbenchWindow[] workbenchWindows = PdkToolsActivator.getDefault().getWorkbench().getWorkbenchWindows();

		for (int i = 0; i < workbenchWindows.length; ++i) {
			IWorkbenchWindow workbenchWindow = workbenchWindows[i];
			IWorkbenchPage[] pages = workbenchWindow.getPages();

			for (int j = 0; j < pages.length; ++j) {
				IWorkbenchPage page = pages[j];
				IEditorPart[] dirtyEditors = page.getDirtyEditors();

				for (int k = 0; k < dirtyEditors.length; ++k) {
					IEditorPart dirtyEditor = dirtyEditors[k];
					IFile iFile = (IFile) dirtyEditor.getEditorInput().getAdapter(IFile.class);

					if (!iFile.getProject().equals(project))
						continue;
					dirtyEditor.doSave(monitor);
				}
			}
		}
	}

	public static void makeParArchiveFromProject(final IProject project) throws IOException, Exception {
		String parName = project.getName();
		HashMap<String, File> entries = new HashMap<String, File>();
		HashMap<String, File> apiEntries = new HashMap<String, File>();
		HashMap<String, File> coreEntries = new HashMap<String, File>();
		File classesApiDir = getOutPath(project, 0);

		File parClassApiJarFile = File.createTempFile(parName + "api", "jar");
		File parClassCoreJarFile = File.createTempFile(parName + "core", "jar");

		ZipOutputStream classesApiZos = null;
		FileOutputStream parClassApiJarFos = null;

		try {
			parClassApiJarFos = new FileOutputStream(parClassApiJarFile);
			classesApiZos = new ZipOutputStream(parClassApiJarFos);
			addDirectoryToZipFileRecursively("", classesApiDir, classesApiZos, new FileFilter() {
				public boolean accept(File pathname) {
					return true;
				}
			});
			addVisibleFiles(project, new String[] { "For_Api_Jar" }, "", apiEntries, StringBasedFileFilter.ACCEPT_EVERYTHING_FILE_FILTER);
			addEntriesToZipOutputStream(classesApiZos, apiEntries);

			entries.put("PORTAL-INF/lib/" + parName + "api.jar", parClassApiJarFile);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				classesApiZos.close();
				parClassApiJarFos.close();
			} catch (IOException e) {
			}

		}

		ZipOutputStream classesCoreZos = null;
		FileOutputStream parCLassCoreJarFos = null;
		File classesCoreDir = getOutPath(project, 1);

		try {
			parCLassCoreJarFos = new FileOutputStream(parClassCoreJarFile);
			classesCoreZos = new ZipOutputStream(parCLassCoreJarFos);
			addDirectoryToZipFileRecursively("", classesCoreDir, classesCoreZos, new FileFilter() {
				public boolean accept(File pathname) {
					return true;
				}
			});
			addVisibleFiles(project, new String[] { "For_Core_Jar" }, "", coreEntries, StringBasedFileFilter.ACCEPT_EVERYTHING_FILE_FILTER);
			addEntriesToZipOutputStream(classesCoreZos, coreEntries);

			entries.put("PORTAL-INF/private/lib/" + parName + "core.jar", parClassCoreJarFile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				classesCoreZos.close();
				parCLassCoreJarFos.close();
			} catch (IOException e) {
			}

		}

		ZipOutputStream srcApiZos = null;
		FileOutputStream srcApiJarFos = null;
		File srcApiDirectory = getSrcPath(project, 0);

		// create source jar.
		File srcApiJarFile = null;
		File srcCoreJarFile = null;

		srcApiJarFile = File.createTempFile(parName, "api.src.jar");
		srcCoreJarFile = File.createTempFile(parName, "core.src.jar");

		if (srcApiDirectory.listFiles().length > 0) {
			try {
				srcApiJarFos = new FileOutputStream(srcApiJarFile);
				srcApiZos = new ZipOutputStream(srcApiJarFos);

				addDirectoryToZipFileRecursively("", srcApiDirectory, srcApiZos, new FileFilter() {
					public boolean accept(File pathname) {
						return (pathname.isFile()) || (pathname.isDirectory());
					}
				});
				entries.put("PORTAL-INF/srclib.api/" + parName + "Api.src.jar", srcApiJarFile);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					srcApiZos.close();
					srcApiJarFos.close();
				} catch (IOException e) {
				}
			}

		}

		ZipOutputStream srcCoreZos = null;
		FileOutputStream srcCoreJarFos = null;
		File srcDirectory = getSrcPath(project, 1);

		if (srcDirectory.listFiles().length > 0) {
			try {
				srcCoreJarFos = new FileOutputStream(srcCoreJarFile);
				srcCoreZos = new ZipOutputStream(srcCoreJarFos);

				addDirectoryToZipFileRecursively("", srcDirectory, srcCoreZos, new FileFilter() {
					public boolean accept(File pathname) {
						return (pathname.isFile()) || (pathname.isDirectory());
					}
				});
				entries.put("PORTAL-INF/srclib.core/" + parName + "Core.src.jar", srcCoreJarFile);
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					srcCoreZos.close();
					srcCoreJarFos.close();
				} catch (IOException e) {
				}
			}

		}

		addVisibleFiles(project, new String[] { "dist" }, "", entries, StringBasedFileFilter.ACCEPT_EVERYTHING_FILE_FILTER);

		addEntriesToZipFile(new File(getParArchiveName(project)), entries);
	}

	public static void addDirectoryToZipFileRecursively(String zipEntryNamePrefix, File directory, ZipOutputStream zipFile, FileFilter filter) throws IOException {
		if ((zipEntryNamePrefix == null) || (zipEntryNamePrefix.trim().equals(""))) {
			zipEntryNamePrefix = "";
		} else {
			zipEntryNamePrefix = zipEntryNamePrefix + "/";
		}

		File[] files = directory.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return (!pathname.getAbsolutePath().endsWith(".dependency")) || (pathname.isDirectory());
			}
		});
		if (files == null) {
			return;
		}

		for (int i = 0; i < files.length; ++i) {
			File file = files[i];

			String name = zipEntryNamePrefix + file.getName();

			if (file.isDirectory()) {
				addDirectoryToZipFileRecursively(name, file, zipFile, filter);
			} else {
				InputStream stream = null;
				try {
					if (filter.accept(file)) {
						ZipEntry newEntry = new ZipEntry(name);

						newEntry.setSize(file.length());
						zipFile.putNextEntry(newEntry);
						stream = new FileInputStream(file);
						StreamUtil.copyStream(stream, zipFile);
					}
				} catch (FileNotFoundException e) {
					throw e;
				} catch (IOException e) {
					throw e;
				} finally {
					if (stream != null)
						stream.close();
					zipFile.closeEntry();
				}
			}
		}
	}

	protected static void addEntriesToZipFile(File zipFile, HashMap<String, File> entries) throws IOException {
		zipFile.getParentFile().mkdirs();

		if (!zipFile.exists()) {
			zipFile.createNewFile();
		}

		FileOutputStream zipFileFos = null;
		ZipOutputStream zos = null;
		try {
			zipFileFos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(zipFileFos);
			addEntriesToZipOutputStream(zos, entries);
		} catch (IOException e) {
			throw e;
		} finally {
			if (zos != null)
				zos.close();
			if (zipFileFos != null)
				zipFileFos.close();
		}
	}

	protected static void addEntriesToZipOutputStream(ZipOutputStream zos, HashMap<String, File> entries) throws IOException {
		Set<String> entryNames = entries.keySet();
		InputStream stream = null;

		for (Iterator<String> iterator = entryNames.iterator(); iterator.hasNext();) {
			try {
				String entryName = (String) iterator.next();
				ZipEntry zipEntry = new ZipEntry(entryName);
				zos.putNextEntry(zipEntry);

				Object entry = entries.get(entryName);
				if (entry instanceof File) {
					stream = new FileInputStream((File) entry);
				} else if (entry instanceof InputStream) {
					stream = (InputStream) entry;
				} else if (entry instanceof String) {
					stream = new FileInputStream((String) entry);
				}

				if (stream != null)
					StreamUtil.copyStream(stream, zos);
			} catch (IOException e) {
				throw e;
			} finally {
				if (stream != null)
					stream.close();
				zos.closeEntry();
			}
		}
	}

	protected static void addVisibleFiles(final IProject project, final String[] parentDir, final String zipPrefix, final HashMap<String, File> entries, final StringBasedFileFilter acceptFilter)
			throws IOException {
		if (parentDir == null) {
			return;
		}

		StringBasedFileFilter recursionFilter = new StringBasedFileFilter() {
			public boolean accept(String[] segments, boolean isDir) {
				for (int i = 0; (i < segments.length) && (i < parentDir.length); ++i) {
					if (!segments[i].equals(parentDir[i])) {
						return false;
					}
				}

				return true;
			}
		};
		StringBasedFileFilter fullAcceptAcceptFilter = new StringBasedFileFilter() {
			public boolean accept(String[] segments, boolean isDir) throws IOException {
				if (isDir) {
					return false;
				}

				if (acceptFilter.accept(segments, isDir)) {
					if (segments.length < parentDir.length) {
						return false;
					}

					for (int i = 0; i < parentDir.length; ++i) {
						if (!parentDir[i].equals(segments[i])) {
							return false;
						}
					}

					StringBuffer fileName = new StringBuffer(zipPrefix);

					for (int i = parentDir.length; i < segments.length; ++i) {
						fileName.append("/");
						fileName.append(segments[i]);
					}

					if (fileName.charAt(0) == '/') {
						fileName.deleteCharAt(0);
					}

					File f = getFileObjectFor(project, segments);

					if (!f.exists()) {
						throw new FileNotExistException(f, segments, project);
					}

					entries.put(fileName.toString(), f);
				}

				return false;
			}

		};
		getAllFilesInProject(project, fullAcceptAcceptFilter, recursionFilter);
	}

	public static File getFileObjectFor(Object project, String[] nameSegments) throws IOException {
		File now = new File(EclipseIDE.getCurrentProjectFolder(project));

		for (int i = 0; i < nameSegments.length; ++i) {
			now = new File(now, nameSegments[i]);
		}

		return now;
	}

	public static List<String[]> getAllFilesInProject(Object project, StringBasedFileFilter fileFilter, StringBasedFileFilter recursionFilter) throws IOException {
		List<String[]> retVal = new ArrayList<String[]>();
		String currentProjectFolder = EclipseIDE.getCurrentProjectFolder(project);

		if (currentProjectFolder != null) {
			getAllFilesInProjectHelper(retVal, new String[0], new File(currentProjectFolder), fileFilter, recursionFilter);
		}

		return retVal;
	}

	private static void getAllFilesInProjectHelper(List<String[]> toAddTo, String[] folderName, File folderNow, StringBasedFileFilter fileFilter, StringBasedFileFilter recursionFilter)
			throws IOException {
		File[] children = folderNow.listFiles();

		for (int i = 0; i < children.length; ++i) {
			File child = children[i];
			String[] nameSegsNow = new String[folderName.length + 1];
			System.arraycopy(folderName, 0, nameSegsNow, 0, folderName.length);
			nameSegsNow[(nameSegsNow.length - 1)] = child.getName();

			boolean isDir = child.isDirectory();

			if (fileFilter.accept(nameSegsNow, isDir)) {
				toAddTo.add(nameSegsNow);
			}

			if ((!isDir) || (!recursionFilter.accept(nameSegsNow, true)))
				continue;
			getAllFilesInProjectHelper(toAddTo, nameSegsNow, child, fileFilter, recursionFilter);
		}
	}

	public static File getOutPath(Object project, int outPathIndex) throws Exception {
		// if (AbstractIDE.mm_tests) {
		IProject iTestProject = (IProject) project;
		File projectFile = iTestProject.getLocation().toFile();
		if (outPathIndex == 0) {
			return new File(projectFile, "classes.api");
		}

		return new File(projectFile, "classes.core");
		// }
		//
		// IProject iProject = (IProject) project;
		//
		// IJavaProject javaProject = JavaCore.create(iProject);
		// IPath projectPath = iProject.getLocation();
		// IPath outPath = javaProject.getOutputLocation();
		//
		// String outLocation = projectPath.toOSString();
		// String outPathRelative = outPath.toOSString();
		// outPathRelative =
		// outPathRelative.substring(outPathRelative.indexOf(File.separator) +
		// 1);
		// outPathRelative =
		// outPathRelative.substring(outPathRelative.indexOf(File.separator));
		//
		// return new File(outLocation + outPathRelative);
	}

	public static File getSrcPath(IProject project, int srcPathIndex) throws Exception {
		IJavaProject jProject = EclipseIDE.getCurrentJavaProject(project);
		if (jProject == null) {
			throw new Exception("Current project (" + project + ") is not a Java Project");
		}
		IProject iTestProject = (IProject) project;
		File projectFile = iTestProject.getLocation().toFile();

		if (srcPathIndex == 0) {
			return new File(projectFile, "src.api");
		}

		return new File(projectFile, "src.core");

		// IClasspathEntry[] entries = jProject.getRawClasspath();
		//
		// IClasspathEntry entry = null;
		//
		// for (int i = 0; (i < entries.length) && (srcPathIndex >= 0); ++i) {
		// entry = entries[i];
		//
		// if (entry.getEntryKind() != 3)
		// continue;
		// --srcPathIndex;
		// }
		//
		// if (entry != null) {
		// String[] segs = entry.getPath().segments();
		// String[] projRelSegs = new String[segs.length - 1];
		// System.arraycopy(segs, 1, projRelSegs, 0, projRelSegs.length);
		//
		// return getFileObjectFor(project, projRelSegs);
		// }
		//
		// return null;
	}

	public static String getParArchiveName(IProject project) {
		return getCurrentProjectFolder(project) + File.separator + project.getName() + ".par";
	}

}
