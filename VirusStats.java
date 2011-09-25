public class VirusStats {
	private int count;
	private Virus virus;
	private String extinctTime;
	public VirusStats(int c, Virus v, String e) {
		setCount(c);
		setVirus(v);
		setExtinctTime(e);
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCount() {
		return count;
	}
	public void setVirus(Virus virus) {
		this.virus = virus;
	}
	public Virus getVirus() {
		return virus;
	}
	public void setExtinctTime(String extinctTime) {
		this.extinctTime = extinctTime;
	}
	public String getExtinctTime() {
		return extinctTime;
	}
	public String getVirusName() {
		return virus.getName();
	}
}
