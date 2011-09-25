
public class StopWatch {
	
	private long elapsed = 0, startTime = 0;
	private boolean running = false;
	
	public void start() {
		running = true;
		startTime = System.currentTimeMillis();
	}
	
	public void stop() {
		if (running)
			elapsed += System.currentTimeMillis() - startTime;
		running = false;
	}
	
	public long getElapsedTime() {
		if (running)
			return elapsed + System.currentTimeMillis() - startTime;
		return elapsed;
	}
	
	public void reset() {
		elapsed = 0;
	}

}
