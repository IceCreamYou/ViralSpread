import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;


public class GamePanel extends JPanel {

	/**
	 * Required for subclasses of serial components.
	 */
	private static final long serialVersionUID = 4597546797509690692L;

	public static final int WIDTH = 800, HEIGHT = 600, TIMER_INTERVAL = 30;

	private Timer timer;

	private People people;
	
	ViralSpread parent;
	
	public GamePanel(final ViralSpread parent) {
		// Set properties.
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setFocusable(true);
		setBackground(Color.WHITE);
		grabFocus();

		// Add things to the environment.
		people = new People(ViralSpread.getPeopleCount());

		// Instantiate the game timer that will run the main action loop.
		timer = new Timer(TIMER_INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Statistics.isOver()) {
					stopTimer();
					parent.disableStartStopButton();
				}
				else {
					tick();
				}
			}
		});
		
		this.parent = parent;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Draw the soft connections (first, so they're under the nodes).
		ViralSpread.ShowConnections connectionType = ViralSpread.getConnectionType();
		if (connectionType == ViralSpread.ShowConnections.SOFT ||
				connectionType == ViralSpread.ShowConnections.HARD_SOFT)
			Statistics.drawSoftConnections(g);
		// Draw the nodes.
		people.draw(g);
		// Draw the hard connections (last, so they're over the nodes).
		if (connectionType == ViralSpread.ShowConnections.HARD ||
				connectionType == ViralSpread.ShowConnections.HARD_SOFT)
			Statistics.drawHardConnections(g);
		// Draw the graphs.
		parent.repaintGraphs();
	}

	protected void tick() {
		people.checkCollision();
		people.move();
		repaint();
	}
	
	public void startTimer() {
		timer.start();
		Statistics.startTiming();
	}
	
	public void stopTimer() {
		Statistics.stopTiming();
		timer.stop();
	}
	
	public void reset() {
		Statistics.reset();
		stopTimer();
		people = new People(ViralSpread.getPeopleCount());
		repaint();
	}

}
