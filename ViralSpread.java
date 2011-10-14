import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;


// -----------2.5------------- DISPLAYING DATA
// TODO: Show graph axis labels
// TODO: Add a button to output graph data in a file when the simulation is complete
// -----------3.0------------- VIRUS BEHAVIOR
// TODO: Allow configuring the contagiousness of each virus via the UI
// TODO: Add an option to do a random walk instead of bounce
// TODO: Add an option to add node "gravity" so that connected nodes or same-colored nodes would be attracted to each other
// TODO: Add an option for nodes with more connections to be more contagious
// TODO: Add an option for nodes to grow in size as their degree increases
// -----------4.0------------- DISPLAY TWEAKS
// TODO: If paused and you mouse over a node, show its hard connections in a different color
// TODO: Show the largest hard-connected component in a different color
// ---------------------------
public class ViralSpread {
	
	public static boolean debug = false;
	
	private static final double
		SLOW_MAX_VELOCITY = 3.0,
		MEDIUM_MAX_VELOCITY = 5.5,
		FAST_MAX_VELOCITY = 8.0,
		VERY_FAST_MAX_VELOCITY = 13.0;
	private static final int DEFAULT_PEOPLECOUNT = 48;
	private static final double DEFAULT_CONTAGIOUSNESS = 0.50;
	private static final ShowConnections DEFAULT_SHOWCONNECTIONS = ShowConnections.HARD;
	private static final int DEFAULT_VIRUSCOUNT = 8;
	private static final double DEFAULT_RESTITUTION = 1.0;
	
	private static double maxVelocity = FAST_MAX_VELOCITY;
	private static double contagiousness = DEFAULT_CONTAGIOUSNESS;
	private static int peopleCount = DEFAULT_PEOPLECOUNT;
	private static ShowConnections connectionType = DEFAULT_SHOWCONNECTIONS;
	private static int virusCount = DEFAULT_VIRUSCOUNT;
	private static double restitution = DEFAULT_RESTITUTION;

	final JFrame frame;
	final JButton startStopButton;
	final JButton showGraphButton;
	final JComboBox peopleSpeed;
	final JComboBox defaultNodeCount;
	final JComboBox defaultVirusCount;
	final JComboBox defaultContagion;
	final ScoreLabel totalCollisionsLabel;
	final ScoreLabel infectiousCollisionsLabel;
	final JComboBox connectionTypeSelect;
	final JDialog dialog;
	final GraphPanel contents;
	final JComboBox graphTypeSelect;
	
	public enum ShowConnections {
		HARD,
		SOFT,
		HARD_SOFT,
		NONE
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ViralSpread();
			}
		});
	}

	public ViralSpread() {
    	// Set up the window.
    	frame = new JFrame("Viral Spread");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set up the panel in which the game is actually played.
		final GamePanel gamePanel = new GamePanel(this);
		frame.add(gamePanel, BorderLayout.CENTER);
		
		// Set up the information panel.
		final JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Dimension d = new Dimension(224, 600); // enough room for the numbers to grow
		infoPanel.setMinimumSize(d);
		infoPanel.setPreferredSize(d);
		frame.add(infoPanel, BorderLayout.LINE_END);
		final JLabel largestComponent = new JLabel("Largest component: 0");
		TimerLabel numComponents = new TimerLabel(925, new TimerListener() {
			public void execute(TimerLabel parent) {
				parent.setText("Number of components: "+ Statistics.getNumComponents());
				largestComponent.setText("Largest component: "+ Statistics.getLargestConnectedComponent());
			}
		});
		numComponents.setText("Number of components: 0");
		TimerLabel timeElapsed = new TimerLabel(200, new TimerListener() {
			public void execute(TimerLabel parent) {
				parent.setText(Statistics.getElapsedTime());
			}
		});
		infoPanel.add(timeElapsed);
		infoPanel.add(new JLabel("                    "));
		totalCollisionsLabel = new ScoreLabel("Total collisions: 0", "Total collisions", 0);
		infoPanel.add(totalCollisionsLabel);
		infoPanel.add(new JLabel("                    "));
		infectiousCollisionsLabel = new ScoreLabel("Infectious collisions: 0", "Infectious collisions", 0);
		infoPanel.add(infectiousCollisionsLabel);
		Statistics.setup(totalCollisionsLabel, infectiousCollisionsLabel);
		infoPanel.add(new JLabel("                    "));
		final JLabel percentInfectious = new JLabel("% infectious collisions: 0.0");
		infoPanel.add(percentInfectious);
		final JLabel numHardConnections = new JLabel("Number of hard connections: 0");
		final JLabel numSoftConnections = new JLabel("Number of soft connections: 0");
		final JLabel hardSoftRatio = new JLabel("Hard:soft ratio: 0.0");
		final JLabel hardPerNode = new JLabel("Avg. hard connections per node: 0.0");
		final JLabel maxPerNode = new JLabel("Max connections: 0");
		final JLabel edgeProbability = new JLabel("Clustering coefficient: 0.0");
		final JLabel maxDistDisplay = new JLabel("Largest connection distance: 0");
		final JLabel avgDistDisplay = new JLabel("Average connection distance: 0");
		TimerLabel virusCountTable = new TimerLabel(1000, new TimerListener() {
			public void execute(TimerLabel parent) {
				parent.setText(Statistics.getVirusCountTable());
				int numHard = Statistics.getNumHardConnections();
				int numSoft = Statistics.getNumSoftConnections();
				double hsRatio = Math.round((numSoft == 0 ? 0 : numHard / (double) numSoft) * 1000.0) / 1000.0;
				double perNode = Math.round((numHard / (double) peopleCount) * 1000.0) / 1000.0;
				double edgeProb = Math.round(numHard / (peopleCount*(peopleCount-1)/2.0) * 1000.0) / 1000.0;
				percentInfectious.setText("% infectious collisions: "+ Statistics.getPercentInfectiousCollisions());
				numHardConnections.setText("Number of hard connections: "+ numHard);
				numSoftConnections.setText("Number of soft connections: "+ numSoft);
				hardSoftRatio.setText("Hard:soft ratio: "+ hsRatio);
				hardPerNode.setText("Avg. hard connections per node: "+ perNode);
				maxPerNode.setText("Max connections: "+ Statistics.getMaxConnections());
				edgeProbability.setText("Clustering coefficient: "+ edgeProb);
				maxDistDisplay.setText("Largest connection distance: "+ Statistics.getLargestPhysicalDistance());
				avgDistDisplay.setText("Average connection distance: "+ Statistics.getAveragePhysicalDistance());
			}
		});
		virusCountTable.setText(Statistics.getVirusCountTable());
		infoPanel.add(virusCountTable);
		infoPanel.add(numHardConnections);
		infoPanel.add(numSoftConnections);
		infoPanel.add(hardSoftRatio);
		infoPanel.add(hardPerNode);
		infoPanel.add(maxPerNode);
		infoPanel.add(edgeProbability);
		infoPanel.add(maxDistDisplay);
		infoPanel.add(avgDistDisplay);
		infoPanel.add(numComponents);
		infoPanel.add(largestComponent);
		final JLabel diameterLabel = new TimerLabel(1150, new TimerListener() {
			public void execute(TimerLabel parent) {
				int diameter = Statistics.getDiameter();
				if (diameter < 0)
					parent.setText("Diameter of LCC: too slow (est: "+ (-diameter) +")");
				else
					parent.setText("Diameter of LCC: "+ diameter);
			}
		});
		diameterLabel.setText("Diameter of LCC: 0");
		infoPanel.add(diameterLabel);
		
		
		// Set up the control panel.
		JPanel controlPanel = new JPanel();
		frame.add(controlPanel, BorderLayout.PAGE_END);
		startStopButton = new JButton("Start");
		controlPanel.add(startStopButton);
		startStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = startStopButton.getText();
				if (text.equals("Start")) {
					startStopButton.setText("Stop");
					gamePanel.startTimer();
				}
				else if (text.equals("Stop")) {
					startStopButton.setText("Start");
					gamePanel.stopTimer();
				}
			}
		});
		final JButton resetButton = new JButton("Reset");
		controlPanel.add(resetButton);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (startStopButton.getText().equals("Stop"))
					startStopButton.setText("Start");
				reset(gamePanel);
			}
		});
		showGraphButton = new JButton("Show graphs");
		controlPanel.add(showGraphButton);
		showGraphButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Center the dialog.
				int xpos = frame.getX() + (frame.getWidth() / 2) - (dialog.getWidth() / 2);
				int ypos = frame.getY() + (frame.getHeight() / 2) - (dialog.getHeight() / 2);
				dialog.setLocation(xpos, ypos);
				// Show the dialog.
				dialog.setVisible(true);
				showGraphButton.setEnabled(false);
			}
		});
		controlPanel.add(new JLabel("Speed: "));
		String[] peopleSpeeds = {"Slow", "Medium", "Fast", "Very fast"};
		peopleSpeed = new JComboBox(peopleSpeeds);
		controlPanel.add(peopleSpeed);
		peopleSpeed.setSelectedItem("Fast");
		peopleSpeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = (String) peopleSpeed.getSelectedItem();
				if (result.equals("Slow"))
					maxVelocity = SLOW_MAX_VELOCITY;
				else if (result.equals("Medium"))
					maxVelocity = MEDIUM_MAX_VELOCITY;
				else if (result.equals("Fast"))
					maxVelocity = FAST_MAX_VELOCITY;
				else if (result.equals("Very fast"))
					maxVelocity = VERY_FAST_MAX_VELOCITY;
				if (startStopButton.getText().equals("Start"))
					reset(gamePanel);
			}
		});
		controlPanel.add(new JLabel("Show connections: "));
		String[] connectionTypes = {"Hard", "Soft", "Both", "None"};
		connectionTypeSelect = new JComboBox(connectionTypes);
		controlPanel.add(connectionTypeSelect);
		connectionTypeSelect.setSelectedItem("Hard");
		connectionTypeSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = (String) connectionTypeSelect.getSelectedItem();
				if (result.equals("Hard"))
					connectionType = ShowConnections.HARD;
				else if (result.equals("Soft"))
					connectionType = ShowConnections.SOFT;
				else if (result.equals("Both"))
					connectionType = ShowConnections.HARD_SOFT;
				else if (result.equals("None"))
					connectionType = ShowConnections.NONE;
				gamePanel.repaint();
			}
		});
		controlPanel.add(new JLabel("Nodes: "));
		Integer[] nodeOptions = new Integer[99];
		for (int i = 2; i <= 100; i++)
			nodeOptions[i-2] = i;
		defaultNodeCount = new JComboBox(nodeOptions);
		controlPanel.add(defaultNodeCount);
		defaultNodeCount.setSelectedItem(DEFAULT_PEOPLECOUNT);
		defaultNodeCount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				peopleCount = (Integer) defaultNodeCount.getSelectedItem();
				if (startStopButton.getText().equals("Start"))
					reset(gamePanel);
			}
		});
		controlPanel.add(new JLabel("Viruses: "));
		Integer[] virusOptions = new Integer[8];
		for (int i = 2; i <= 9; i++)
			virusOptions[i-2] = i;
		defaultVirusCount = new JComboBox(virusOptions);
		controlPanel.add(defaultVirusCount);
		defaultVirusCount.setSelectedItem(DEFAULT_VIRUSCOUNT);
		defaultVirusCount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				virusCount = (Integer) defaultVirusCount.getSelectedItem();
				if (startStopButton.getText().equals("Start"))
					reset(gamePanel);
			}
		});
		controlPanel.add(new JLabel("Contagiousness: "));
		Double[] contagionOptions = new Double[100];
		for (int i = 1; i <= 100; i++) {
			contagionOptions[i-1] = i/100.0;
		}
		defaultContagion = new JComboBox(contagionOptions);
		controlPanel.add(defaultContagion);
		defaultContagion.setSelectedIndex((int)(DEFAULT_CONTAGIOUSNESS * 100) - 1);
		defaultContagion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				contagiousness = (Double) defaultContagion.getSelectedItem();
				if (startStopButton.getText().equals("Start"))
					reset(gamePanel);
			}
		});

		// Put the frame on the screen
		frame.pack();
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		frame.setLocation(screenSize.width / 2 - frame.getWidth() / 2, screenSize.height / 2 - frame.getHeight() / 2);
        frame.setVisible(true);
        
        // Set up the dialog.
		dialog = new JDialog(frame);
		dialog.setMinimumSize(new Dimension(300, 200));
		dialog.setTitle("Number of viruses graph");
		contents = new GraphPanel();
		dialog.add(contents, BorderLayout.CENTER);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				dialog.setVisible(false);
				showGraphButton.setEnabled(true);
			}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});
		JPanel graphTypeOptionPanel = new JPanel();
		dialog.add(graphTypeOptionPanel, BorderLayout.PAGE_END);
		String[] graphTypeOptions = {
				"Number of viruses",
				"Degree distribution",
				"Number of components",
				"Largest component as % of whole",
				"Number of edges per node",
				"Hard:soft edge ratio",
				"Clustering coefficient",
				"Largest edge length",
				"Average edge length",
				"% infectious collisions",
		};
		graphTypeSelect = new JComboBox(graphTypeOptions);
		graphTypeOptionPanel.add(graphTypeSelect);
		graphTypeSelect.setSelectedItem("Number of viruses");
		final JFileChooser saveDialog = new JFileChooser();
		graphTypeSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = (String) graphTypeSelect.getSelectedItem();
				dialog.setTitle(result +" graph");
				saveDialog.setSelectedFile(new File(result +" graph.png"));
				if (result.equals("Number of viruses"))
					Statistics.setGraphType(Statistics.GraphType.COUNT);
				if (result.equals("Degree distribution"))
					Statistics.setGraphType(Statistics.GraphType.DEGREE);
				if (result.equals("Number of components"))
					Statistics.setGraphType(Statistics.GraphType.NUM_COMPONENTS);
				if (result.equals("Largest component as % of whole"))
					Statistics.setGraphType(Statistics.GraphType.LARGEST_COMPONENT);
				if (result.equals("Number of edges per node"))
					Statistics.setGraphType(Statistics.GraphType.CONNECTIONS_PER_NODE);
				if (result.equals("Hard:soft edge ratio"))
					Statistics.setGraphType(Statistics.GraphType.HARD_SOFT_RATIO);
				if (result.equals("Clustering coefficient"))
					Statistics.setGraphType(Statistics.GraphType.CLUSTERING_COEFF);
				if (result.equals("Largest edge length"))
					Statistics.setGraphType(Statistics.GraphType.LARGEST_EDGE_DIST);
				if (result.equals("Average edge length"))
					Statistics.setGraphType(Statistics.GraphType.AVG_EDGE_DIST);
				if (result.equals("% infectious collisions"))
					Statistics.setGraphType(Statistics.GraphType.INFECTIOUS_COLLISIONS);
				gamePanel.repaint();
			}
		});
		saveDialog.setSelectedFile(new File("Number of viruses graph.png"));
		saveDialog.setAcceptAllFileFilterUsed(false);
		saveDialog.setDialogTitle("Save graph");
		saveDialog.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory())
					return f.getName().charAt(0) != '.';
				String ext = getExtension(f);
				return ext != null && ext.equals("png");
			}
			public String getDescription() {
				return "PNG";
			}
		});
		JButton saveButton = new JButton("Save graph");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean isRunning = gamePanel.isRunning();
				if (isRunning)
					gamePanel.stopTimer();
				boolean show = true;
				while (show)
					show = !saveDialog(saveDialog);
				if (isRunning)
					gamePanel.startTimer();
			}
		});
		graphTypeOptionPanel.add(saveButton, BorderLayout.PAGE_END);
		dialog.pack();
	}
	
	// The number of people in the simulation. Avoid numbers above 100.
	public static int getPeopleCount() {
		return peopleCount;
	}
	
	// Set the number of people. Assumed to be between 0 and 100.
	public static void setPeopleCount(int c) {
		peopleCount = c;
	}

	public static double getContagiousness() {
		return contagiousness;
	}

	public static void setContagiousness(double c) {
		contagiousness = c;
	}

	public static ShowConnections getConnectionType() {
		return connectionType;
	}

	public static void setConnectionType(ShowConnections c) {
		connectionType = c;
	}

	public static int getVirusCount() {
		return virusCount;
	}
	
	public static void setVirusCount(int c) {
		virusCount = c;
	}
	
	public static double getRestitution() {
		return restitution;
	}
	
	public static void setRestitution(double r) {
		restitution = r;
	}

	// Maximum starting speed of any person in pixels per cycle.
	public static double getMaxVelocity() {
		return maxVelocity;
	}
	
	// Maximum speed along a single dimension.
	public static double getMax1DVelocity() {
		return getMaxVelocity() / Math.sqrt(2);
	}
	
	// Minimum starting speed of any person in pixels per cycle.
	public static double getMinVelocity() {
		return Math.max(0.0, getMaxVelocity()-1.5);
	}
	
	// Set the maximum velocity. Assumed to be between 1.5 and 8.
	public static void setMaxVelocity(double max) {
		maxVelocity = max;
	}
	
	public void repaintGraphs() {
		if (dialog != null && dialog.isVisible())
			dialog.repaint();
	}
	
	public void saveGraph(File file) {
		BufferedImage image = new BufferedImage(
				contents.getWidth(),
				contents.getHeight(),
				BufferedImage.TYPE_INT_RGB
		);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, contents.getWidth(), contents.getHeight());
		Statistics.drawGraph(g, contents);
		g.dispose();
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(
					dialog,
					"Error: "+ e.getMessage(),
					"Error saving graph",
					JOptionPane.ERROR_MESSAGE
			);
		}
	}
	
	void disableStartStopButton() {
		startStopButton.setEnabled(false);
	}

	// From http://download.oracle.com/javase/tutorial/uiswing/components/filechooser.html
    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    private boolean saveDialog(JFileChooser saveDialog) {
    	if (saveDialog == null)
    		throw new NullPointerException();
		if (saveDialog.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
			File file = saveDialog.getSelectedFile();
			String ext = getExtension(file);
			if (ext == null || !ext.equals("png"))
				file = new File(file.getPath() +".png");
			if (file.exists()) {
				int confirm = JOptionPane.showConfirmDialog(
						saveDialog,
						file.getName() + " already exists! Would you like to overwrite it?",
						"File already exists",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE
				);
				if (confirm != JOptionPane.YES_OPTION)
					return false;
			}
			saveGraph(file);
		}
		return true;
    }
	
	// Restart the simulation.
	private void reset(GamePanel gamePanel) {
		gamePanel.reset();
		startStopButton.setEnabled(true);
		//graphTypeSelect.setSelectedItem("Number of viruses");
	}

}
