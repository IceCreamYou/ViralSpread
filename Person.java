import java.awt.Color;
import java.awt.Graphics;


public class Person {
	
	private static final double RESTITUTION = 1.0; // The percent velocity people retain after bouncing off each other.
	
	public static final int RADIUS = 20;
	
	private Vector2d position, velocity;
	private Virus v;
	
	public Person(int x, int y, Virus virus) {
		double xVel = 0, yVel = 0;
		while (xVel*xVel+yVel*yVel < ViralSpread.getMinVelocity()*ViralSpread.getMinVelocity()) {
			xVel = getRandomInRange(-ViralSpread.getMax1DVelocity(), ViralSpread.getMax1DVelocity());
			yVel = getRandomInRange(-ViralSpread.getMax1DVelocity(), ViralSpread.getMax1DVelocity());
		}
	    position = new Vector2d(x, y);
	    velocity = new Vector2d(xVel, yVel);
		v = virus;
	}

	private static double getRandomInRange(double low, double high) {
		return (Math.random() * (high - low + 1)) + low;
	}

	public void draw(Graphics g) {
		g.setColor(v.getColor());
		g.fillOval((int) getX(), (int) getY(), RADIUS*2, RADIUS*2);
		g.setColor(Color.BLACK);
		g.drawOval((int) getX(), (int) getY(), RADIUS*2, RADIUS*2);
		if (ViralSpread.debug) { // This is debugging code. It doesn't have to be efficient.
			g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
			g.setColor(new Color(255-v.getColor().getRed(), 255-v.getColor().getGreen(), 255-v.getColor().getBlue()));
			g.drawString(((int) getX()) +","+ ((int) getY()), (int) getX()+2, (int) getY() + RADIUS + 6);
		}
	}

	public void collide(Person ball) {
		Statistics.collision();
		resolveCollision(ball);
		transferVirus(ball);
	}
	
	public void transferVirus(Person ball) {
		Virus thisVirus = getVirus(), ballVirus = ball.getVirus();
		if (thisVirus.equalsVirus(ballVirus)) {
			Statistics.updateConnections(this, ball);
		}
		else {
			if (Math.random() < (thisVirus.getContagiousness() / 2)) {
				Statistics.transferVirus(ballVirus, thisVirus);
				Statistics.updateConnections(ball, this);
				ball.setVirus(thisVirus);
			}
			else if (Math.random() < ((ballVirus.getContagiousness() / 2) / (1 - thisVirus.getContagiousness() / 2))) {
				Statistics.transferVirus(thisVirus, ballVirus);
				Statistics.updateConnections(this, ball);
				setVirus(ballVirus);
			}
		}
	}

	// based on http://stackoverflow.com/questions/345838/ball-to-ball-collision-detection-and-handling
	private void resolveCollision(Person ball) {
	    // get the mtd
	    Vector2d delta = (position.subtract(ball.position));
	    double d = delta.getLength();
	    // minimum translation distance to push balls apart after intersecting
	    Vector2d mtd = delta.multiply(((getRadius() + ball.getRadius())-d)/d); 


	    // resolve intersection --
	    // inverse mass quantities
	    double im1 = 1 / getMass(); 
	    double im2 = 1 / ball.getMass();

	    // push-pull them apart based off their mass
	    position = position.add(mtd.multiply(im1 / (im1 + im2)));
	    ball.position = ball.position.subtract(mtd.multiply(im2 / (im1 + im2)));

	    // impact speed
	    Vector2d v = (velocity.subtract(ball.velocity));
	    double vn = v.dot(mtd.normalize());

	    // sphere intersecting but moving away from each other already
	    if (vn > 0.0) return;

	    // collision impulse
	    double i = (-(1.0 + RESTITUTION) * vn) / (im1 + im2);
	    Vector2d impulse = mtd.multiply(i);

	    // change in momentum
	    velocity = velocity.add(impulse.multiply(im1));
	    ball.velocity = ball.velocity.subtract(impulse.multiply(im2));
	}

	public void move() {
		position = position.add(velocity);
		bounceOffWalls();
	}
	
	private void bounceOffWalls() {
		double nextXpos = position.getX()+velocity.getX();
		double nextYpos = position.getY()+velocity.getY();
		if (nextXpos < 0)
			velocity.setX(Math.abs(velocity.getX()));
		else if (nextXpos + RADIUS*2 > GamePanel.WIDTH)
			velocity.setX(-Math.abs(velocity.getX()));
		if (nextYpos < 0)
			velocity.setY(Math.abs(velocity.getY()));
		else if (nextYpos + RADIUS*2 > GamePanel.HEIGHT)
			velocity.setY(-Math.abs(velocity.getY()));
	}

	public double getX() {
		return position.getX();
	}

	public double getY() {
		return position.getY();
	}
	
	public double getRadius() {
		return RADIUS;
	}
	
	private double getMass() {
		return 1.0;
	}
	
	private void setVirus(Virus v) {
		this.v = v;
	}
	
	public Virus getVirus() {
		return v;
	}
	
	public boolean colliding (Person ball) {
		return (getX()-ball.getX())*(getX()-ball.getX())+(getY()-ball.getY())*(getY()-ball.getY())
		< getRadius()*getRadius()*4;
	}
	
	public boolean samePosition(Person other) {
		return getX() == other.getX() && getY() == other.getY();
	}
	
	@Override
	public String toString() {
		return getVirus().getName() +":("+ Math.round(getX()) +", "+ Math.round(getY()) +")";
	}

}
