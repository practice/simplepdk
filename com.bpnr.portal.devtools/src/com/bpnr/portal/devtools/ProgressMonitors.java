package com.bpnr.portal.devtools;

import org.eclipse.core.runtime.IProgressMonitor;

public class ProgressMonitors {
	public static final IProgressMonitor getTaskToSubtaskWrapper(final IProgressMonitor toWrap) {
		if (toWrap == null)
			return null;
		return new IProgressMonitor() {

			public void beginTask(String s, int i) {
				toWrap.subTask(s);
			}

			public void done() {
			}

			public void internalWorked(double v) {
			}

			public boolean isCanceled() {
				return toWrap.isCanceled();
			}

			public void setCanceled(boolean b) {
			}

			public void setTaskName(String s) {
				subTask(s);
			}

			public void subTask(String s) {
				toWrap.subTask(s);
			}

			public void worked(int i) {
			}
		};
	}
}