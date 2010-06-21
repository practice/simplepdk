package com.bpnr.portal.devtools.preferences;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class ServerListLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		PortalServer server = (PortalServer) element;
		switch (columnIndex) {
		case 0:
			return server.getName();
		case 1: 
			return server.getHost();
		case 2: 
			return String.valueOf(server.getPort());
		case 3:
			return server.getLoginId();
		case 4:
			return server.getDesc();
		}
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}


}
