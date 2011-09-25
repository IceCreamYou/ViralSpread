import java.awt.Color;

public class Viruses {
	
	private Virus[] viruses;
	
	private int count = -1;
	
	public Viruses() {
		// Initialize the list of all possible virus choices.
		viruses = new Virus[] {
				//new BlackVirus(),
				new BlueVirus(),
				new CyanVirus(),
				new DarkGrayVirus(),
				new GreenVirus(),
				//new LightGrayVirus(),
				new MagentaVirus(),
				new OrangeVirus(),
				new PinkVirus(),
				new RedVirus(),
				new YellowVirus()
		};
		// Shuffle randomly.
		int num = viruses.length;
		for (int i = 0; i < num; i++) {
			int randomPos = (int) (Math.random() * num);
			Virus temp = viruses[i];
			viruses[i] = viruses[randomPos];
			viruses[randomPos] = temp;
		}
		// Reduce to a set of viruses of the specified size.
		int virusCount = ViralSpread.getVirusCount();
		if (virusCount < viruses.length) {
			Virus[] tempViruses = new Virus[virusCount];
			for (int i = 0; i < virusCount; i++)
				tempViruses[i] = viruses[i];
			viruses = tempViruses;
		}
	}
	
	public Virus getRandomVirus() {
		count++;
		if (count >= viruses.length) {
			count = 0;
		}
		return viruses[count];
	}
	
	

	public class BlackVirus extends Virus {
		public String getName() {
			return "black";
		}
		public Color getColor() {
			return Color.BLACK;
		}
	}
	public class BlueVirus extends Virus {
		public String getName() {
			return "blue";
		}
		public Color getColor() {
			return Color.BLUE;
		}
	}
	public class CyanVirus extends Virus {
		public String getName() {
			return "cyan";
		}
		public Color getColor() {
			return Color.CYAN;
		}
	}
	public class DarkGrayVirus extends Virus {
		public String getName() {
			return "dark gray";
		}
		public Color getColor() {
			return Color.DARK_GRAY;
		}
	}
	public class GreenVirus extends Virus {
		public String getName() {
			return "green";
		}
		public Color getColor() {
			return Color.GREEN;
		}
	}
	public class LightGrayVirus extends Virus {
		public String getName() {
			return "light gray";
		}
		public Color getColor() {
			return Color.LIGHT_GRAY;
		}
	}
	public class MagentaVirus extends Virus {
		public String getName() {
			return "purple";
		}
		public Color getColor() {
			return Color.MAGENTA;
		}
	}
	public class OrangeVirus extends Virus {
		public String getName() {
			return "orange";
		}
		public Color getColor() {
			return Color.ORANGE;
		}
	}
	public class PinkVirus extends Virus {
		public String getName() {
			return "pink";
		}
		public Color getColor() {
			return Color.PINK;
		}
	}
	public class RedVirus extends Virus {
		public String getName() {
			return "red";
		}
		public Color getColor() {
			return Color.RED;
		}
	}
	public class YellowVirus extends Virus {
		public String getName() {
			return "yellow";
		}
		public Color getColor() {
			return Color.YELLOW;
		}
	}

}
