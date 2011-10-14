import java.awt.Graphics;

public class People {

	private Person[] people;
	
	private Viruses viruses;

	public People(int peopleCount) {
		people = new Person[peopleCount];
		viruses = new Viruses();
		for (int i = 0; i < peopleCount; i++) {
			int x = 0, y = 0;
			boolean keepGoing = true;
			while (keepGoing) {
				x = getRandomInRange(0, GamePanel.WIDTH - Person.RADIUS*2);
				y = getRandomInRange(0, GamePanel.HEIGHT - Person.RADIUS*2);
				keepGoing = false;
				for (int j = 0; j < i; j++) {
					if (occupiesSameSpace(people[j].getX(), people[j].getY(), x, y)) {
						keepGoing = true;
						break;
					}
				}
			}
			people[i] = new Person(x, y, viruses.getRandomVirus());
		}
		Statistics.setupPeople(people);
	}

	public void draw(Graphics g) {
		for (Person p : people)
			p.draw(g);
	}

	public void checkCollision() {
		for (int i = 0; i < people.length; i++)
			for (int j = i+1; j < people.length; j++)
				if (people[i].colliding(people[j]))
					people[i].collide(people[j]);
	}

	public void move() {
		for (Person p : people)
			p.move();
	}
	
	public int getCount() {
		return people.length;
	}

	private static int getRandomInRange(int low, int high) {
		return (int) ((Math.random() * (high - low + 1)) + low);
	}
	
	static boolean occupiesSameSpace(double x1, double y1, double x2, double y2) {
		return (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2) < Person.RADIUS*Person.RADIUS*4;
	}

}
