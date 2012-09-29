package se.findout.tempo.client;

/*
 * Describes a version of data that is obtained by applying a change to a base version.
 */
public class Version {
	/**
	 * The origin of this version.
	 */
	private final Version base;

	/**
	 * The change of the base version to obtain this version.
	 */
	private final Change change;

	public Version(Version base, Change change) {
		super();
		this.base = base;
		this.change = change;
	}
	
	public Version getBase() {
		return base;
	}

	public Change getChange() {
		return change;
	}
}
