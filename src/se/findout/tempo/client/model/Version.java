package se.findout.tempo.client.model;

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
	private final Command change;

	public Version(String name, Version base, Command change) {
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

	public Command getChange() {
		return change;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Version other = (Version) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
