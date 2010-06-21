package com.bpnr.portal.devtools.actions;

import java.io.IOException;

public interface StringBasedFileFilter {
	public static final StringBasedFileFilter ACCEPT_EVERYTHING_FILE_FILTER = new StringBasedFileFilter() {
		public boolean accept(String[] segments, boolean isDir) {
			return true;
		}
	};

	public abstract boolean accept(String[] segments, boolean isDir) throws IOException;
}
