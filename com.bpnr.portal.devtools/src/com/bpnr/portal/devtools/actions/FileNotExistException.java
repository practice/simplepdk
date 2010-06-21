package com.bpnr.portal.devtools.actions;

import java.io.File;
import java.io.IOException;

public class FileNotExistException extends IOException {
	private File file;
	private String[] segmentsInProject;
	private Object project;

	public FileNotExistException(File file, String[] segmentsInProject, Object project) {
		this.file = file;
		this.segmentsInProject = segmentsInProject;
		this.project = project;
	}

	public File getFile() {
		return this.file;
	}

	public String[] getSegmentsInProject() {
		return this.segmentsInProject;
	}

	public Object getProject() {
		return this.project;
	}
}