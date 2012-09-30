package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;

public class VersionsModel {
	private final List<Version> heads = new ArrayList<Version>();
	private final List<Version> allVersions = new ArrayList<Version>();
	private List<VersionChangeListener> versionChangeListeners = new ArrayList<VersionsModel.VersionChangeListener>();
	
	public VersionsModel() {
		Version initialVersion = new Version("1", null, null);
		getHeads().add(initialVersion);
		allVersions.add(initialVersion);
	}

	public List<Version> getHeads() {
		return heads;
	}
	
	public Version addVersion(Version base, Change change) {
		int i = heads.indexOf(base);
		String name = i == -1 ? base.getName() + ".1" : incVersion(base.getName());
		Version newVersion = new Version(name, base, change);
		if (i == -1) {
			heads.add(newVersion);	// let new version become a new head (new branch)
		} else {
			heads.set(i, newVersion);	// let the new version replace a head
		}
		allVersions.add(newVersion);
		
		fireVersionChangeEvent(new VersionChangeEvent(this, newVersion, i == -1));
		return newVersion;
	}
	
	public String incVersion(String name) {
		int i = name.lastIndexOf('.');
		String lastPart = name.substring(i + 1);
		String newLastPart = Integer.toString(Integer.parseInt(lastPart) + 1);
		if (i == -1) {
			return newLastPart;
		} else {
			return name.substring(0, i + 1) + newLastPart;
		}
	}

	public void addVersionChangeListener(VersionChangeListener listener) {
		versionChangeListeners.add(listener);
	}

	private void fireVersionChangeEvent(VersionChangeEvent versionChangeEvent) {
		for (VersionChangeListener listener : versionChangeListeners) {
			listener.versionChanged(versionChangeEvent);
		}
	}
	
	public List<Version> getAllVersions() {
		return allVersions;
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
	
	public Version getInitialVersion() {
		for (Version version : allVersions) {
			if (version.getBase() == null) {
				return version;
			}
		}
		throw new RuntimeException("missing initial version");
	}

	/**
	 * Returns all versions that have version as base.
	 * @param version the base of all successors
	 * @return the succesors
	 */
	public List<Version> getVersionSuccessors(Version baseVersion) {
		List<Version> result = new ArrayList<Version>();		
		for (Version version : allVersions) {
			if (baseVersion == version.getBase()) {
				result.add(version);
			}
		}
		return result;
	}
}
