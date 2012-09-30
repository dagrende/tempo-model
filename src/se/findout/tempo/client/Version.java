package se.findout.tempo.client;

/*
 * Describes a version of data that is obtained by applying a change to a base version.
 */
public class Version {
	private final String name;
	
	/**
	 * The origin of this version.
	 */
	private final Version base;

	/**
	 * The change of the base version to obtain this version.
	 */
	private final Change change;

	public Version(String name, Version base, Change change) {
		super();
		this.name = name;
		this.base = base;
		this.change = change;
	}
	
	public String getName() {
		return name;
	}
	
	public Version getBase() {
		return base;
	}

	public Change getChange() {
		return change;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
