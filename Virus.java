
public abstract class Virus {
	
	public abstract String getName();
	
	public abstract java.awt.Color getColor();
	
	public double getContagiousness() {
		return ViralSpread.getContagiousness();
	}
	
	public boolean equalsVirus(Virus other) {
		return getName().equals(other.getName());
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
