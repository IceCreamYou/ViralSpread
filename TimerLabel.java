import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;


public class TimerLabel extends JLabel {

	/**
	 * Automatically generated. Required by subclasses of JLabel.
	 */
	private static final long serialVersionUID = -6784578139352959405L;
	
	private Timer timer;

	public TimerLabel(int interval, final TimerListener listener) {
		final TimerLabel t = this;
		timer = new Timer(interval, new ActionListener() {
			public void actionPerformed(ActionEvent e) { listener.execute(t); }
		});
		timer.start();
	}

}
