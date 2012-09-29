package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;

public class VersionsModel {
	private final List<Version> heads = new ArrayList<Version>();
	private List<VersionChangeListener> versionChangeListeners = new ArrayList<VersionsModel.VersionChangeListener>();
	
	public VersionsModel() {
		getHeads().add(new Version(null, null));
	}

	public List<Version> getHeads() {
		return heads;
	}
	
	public Version addVersion(Version base, Change change) {
		Version newVersion = new Version(base, change);
		int i = heads.indexOf(base);
		if (i == -1) {
			heads.add(base);	// let new version become a new head (new branch)
		} else {
			heads.set(i, newVersion);	// let the new version replace a head
		}
		fireVersionChangeEvent(new VersionChangeEvent(this, newVersion, i == -1));
		return newVersion;
	}
	
	public void addVersionChangeListener(VersionChangeListener listener) {
		versionChangeListeners.add(listener);
	}

	private void fireVersionChangeEvent(VersionChangeEvent versionChangeEvent) {
		for (VersionChangeListener listener : versionChangeListeners) {
			listener.versionChanged(versionChangeEvent);
		}
	}
	
	public static class VersionChangeEvent {
		private final VersionsModel model;
		private final Version newVersion;
		private final boolean isNewBranch;

		public VersionChangeEvent(VersionsModel model, Version newVersion,
				boolean isNewBranch) {
			this.model = model;
			this.newVersion = newVersion;
			this.isNewBranch = isNewBranch;
		}

		public VersionsModel getModel() {
			return model;
		}

		public Version getNewVersion() {
			return newVersion;
		}

		public boolean isNewBranch() {
			return isNewBranch;
		}
		
	}
	
	public interface VersionChangeListener {
		void versionChanged(VersionChangeEvent event);
	}
}
