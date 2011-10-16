
public abstract class Virus implements Comparable<Virus> {
	
	public abstract String getName();
	
	public abstract java.awt.Color getColor();
	
	public double getContagiousness() {
		return ViralSpread.getContagiousness();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int compareTo(Virus o) {
		return toString().compareToIgnoreCase(o.toString());
	}

	private static final int PRIME = 37;
	@Override
	public int hashCode() {
		int result = 1;
		String name = getName();
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!getClass().equals(o.getClass()))
			return false;
		final Virus v = (Virus) o;
		return getName().equalsIgnoreCase(v.getName());
	}

}
