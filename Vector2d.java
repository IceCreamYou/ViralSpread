
// Inspired by http://stackoverflow.com/questions/3682542/how-to-handle-ball-to-ball-collision-with-trigonometry-instead-of-vectors
class Vector2d {

    private double x;
    private double y;

    public Vector2d() {
        set(0, 0);
    }

    public Vector2d(double x, double y) {
        set(x, y);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double dot(Vector2d v2) {
        return getX() * v2.getX() + getY() * v2.getY();
    }

    public double getLength() {
        return (double) Math.sqrt(getX() * getX() + getY() * getY());
    }

    public Vector2d add(Vector2d v2) {
        return new Vector2d(getX()+v2.getX(), getY()+v2.getY());
    }

    public Vector2d subtract(Vector2d v2) {
        return new Vector2d(getX()-v2.getX(), getY()-v2.getY());
    }

    public Vector2d multiply(double i) {
        return new Vector2d(getX()*i, getY()*i);
    }

    // note: this is the only method that modifies this vector instead of returning a new one
    public Vector2d normalize() {
    	double length = getLength();
        if (length != 0.0) {
            setX(getX() / length);
            setY(getY() / length);
        } else {
            setX(0.0);
            setY(0.0);
        }
        return this;
    }
    
    @Override
    public String toString() {
    	return "("+ getX() +", "+ getY() +")";
    }

}