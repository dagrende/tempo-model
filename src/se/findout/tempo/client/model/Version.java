package se.findout.tempo.client.model;

/*
 * Describes a version of data that is obtained by applying a change to a base version.
 */
public class Version {
	private int id = 0;
	
	/**
	 * The origin of this version.
	 */
	private final Version base;

	/**
	 * The change of the base version to obtain this version.
	 */
	private final Command change;

	public Version(int id, Version base, Command change) {
		super();
		this.setId(id);
		this.base = base;
		this.change = change;
	}
	
	public Version getBase() {
		return base;
	}

	public Command getChange() {
		return change;
	}
	
	@Override
	public String toString() {
		return "" + getId();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + id);
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
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
