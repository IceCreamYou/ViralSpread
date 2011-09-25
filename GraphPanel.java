import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;



class GraphPanel extends JPanel {
	
	public static final int WIDTH = 800, HEIGHT = 600;

	/**
	 * Required for subclasses of serial components.
	 */
	private static final long serialVersionUID = 182702943190064632L;
	
	public GraphPanel() {
		// Set properties.
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.WHITE);
		setFocusable(true);
		grabFocus();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Statistics.drawGraph(g);
	}
	
}
