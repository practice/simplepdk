package com.bpnr.portal.devtools.preferences;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ServerListContentProvider implements IStructuredContentProvider {

	private final ArrayList<PortalServer> portalServers;

	public ServerListContentProvider(ArrayList<PortalServer> portalServers) {
		this.portalServers = portalServers;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return portalServers.toArray();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		viewer.refresh();
	}

}
