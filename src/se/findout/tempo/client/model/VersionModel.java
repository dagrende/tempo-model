package se.findout.tempo.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VersionModel {
    private final static Logger logger = Logger.getLogger(VersionModel.class.getName());
	private final List<Version> heads = new ArrayList<Version>();
	private final List<Version> allVersions = new ArrayList<Version>();
	private List<VersionChangeListener> versionChangeListeners = new ArrayList<VersionModel.VersionChangeListener>();
	
	public VersionModel() {
		Version initialVersion = new Version("1", null, null);
		getHeads().add(initialVersion);
		allVersions.add(initialVersion);
	}

	public List<Version> getHeads() {
		return heads;
	}
	
	public Version addVersion(Version base, Command change) {
		logger.log(Level.FINE, "VersionModel.addVersion('" + base.getName() + "', " + change + ")");
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
		private final VersionModel model;
		private final Version newVersion;
		private final boolean isNewBranch;

		public VersionChangeEvent(VersionModel model, Version newVersion,
				boolean isNewBranch) {
			this.model = model;
			this.newVersion = newVersion;
			this.isNewBranch = isNewBranch;
		}

		public VersionModel getModel() {
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
	
	/**
	 * undo and execute from from to to.
	 * @param from the current version
	 * @param to the version we want
	 */
	public void switchVersion(Version from, Version to, ChangeIterator changeIterator) {
		logger.log(Level.FINE, "VersionModel.switchVersion(" + from + ", " + to + ")");
		Set<Version> undoVersions = new HashSet<Version>();
		Version v = from;
		while (v != null) {
			undoVersions.add(v);
			v = v.getBase();
		}
		
		List<Version> executeVersions = new ArrayList<Version>();
		v = to;
		while (v != null) {
			executeVersions.add(v);
			if (undoVersions.contains(v)) {
				break;
			}
			v = v.getBase();
		}
		
		Collections.reverse(executeVersions);
		
		v = from;
		while (v != null && v != executeVersions.get(0)) {
			changeIterator.undo(v.getChange());
			v = v.getBase();
		}
		
		for (int i = 1; i < executeVersions.size(); i++) {
			changeIterator.execute(executeVersions.get(i).getChange());
		}
	}
	
	public interface ChangeIterator {
		void execute(Command change);
		void undo(Command change);
	}

	public Version getVersionById(String id) {
		for (Version version : allVersions) {
			if (version.getName().equals(id)) {
				return version;
			}
		}
		return null;
	}
}
