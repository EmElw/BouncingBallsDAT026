import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The physics model.
 * <angle>
 * This class is where you should implement your bouncing balls model.
 * <angle>
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 *
 * @author Simon Robillard
 */
class Model {

	static final double GRAVITY = 10;
	private static final double PRECISION = 0.01;
	double areaWidth, areaHeight;

	List<Ball> balls;

	Map<Ball, List<Point>> paths;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		// Initialize the model with a few balls
		balls = new ArrayList<>();
		balls.add(new Ball(width / 3, height * 1 / 2, 2, 0.4, 0.4));
		balls.add(new Ball(width / (2 * 3), height * 1 / 2, -2, 0, 0.2));

		paths = new HashMap<>();
		for (Ball b : balls) {
			paths.put(b, new ArrayList<>());
		}
	}

	static boolean collisionEnabled = true;

	void applyCollision(Ball b1, Ball b2) {
		if (!collisionEnabled)
			return;
		System.out.println("collision!");
		System.out.println(b1.v + " - " + b2.v);

		// TODO: Explain these?
		paths.get(b1).add(new Point(b1.x, b1.y));
		paths.get(b2).add(new Point(b2.x, b2.y));

		// ball/ball collision
		double dy = b1.y - b2.y;
		double dx = b1.x - b2.x;
		double angle = Math.atan(dy / dx);
		if (dx < 0){
			angle += Math.PI;
		}

		Double TAU = 2*Math.PI;

		// rotate vector along collision angle
		b1.v.rotate(-angle);
		b2.v.rotate(-angle);

		// only calculate collision if we have actually collided
		if (!(	// b1 is left ball and has lower v.x than b2
				(3/4 * TAU > angle && angle > 1/4 * TAU)  && b1.v.x < b2.v.x ||
				// b1 is right ball and has higher v.x than b2
						(3/4 * TAU < angle || angle < 1/4 * TAU) && b1.v.x > b2.v.x)){

			// calculate total momentum
			double i = b1.mass() * b1.v.x +
					b2.mass() * b2.v.x;

			// relative velocity
			double r = -(b2.v.x - b1.v.x);

			b1.v.x = (i - (r * b2.mass())) /
					(b1.mass() + b2.mass());
			b2.v.x = r + b1.v.x;
		} else {
			System.out.print("NO COLLISION\n");
		}


		// return vector to normal form
		b1.v.rotate(angle);
		b2.v.rotate(angle);

		System.out.println(b1.v + " - " + b2.v);
	}

	void step(double deltaT) {
		for (Ball b : balls) {

			if (b.x < b.radius) {
				if (b.v.x < 0) {
					b.v.x *= -1;
					paths.get(b).add(new Point(b.x, b.y));
				}
			}
			if (b.x > areaWidth - b.radius) {
				if (b.v.x > 0) {
					b.v.x *= -1;
					paths.get(b).add(new Point(b.x, b.y));
				}
			}
			if (b.y < b.radius) {
				if (b.v.y < 0) {
					b.v.y *= -1;
					paths.get(b).add(new Point(b.x, b.y));
				}
			}
			if (b.y > areaHeight - b.radius) {
				if (b.v.y > 0) {
					b.v.y *= -1;
					paths.get(b).add(new Point(b.x, b.y));
				}
			}


			b.x = b.x + deltaT * b.v.x;
			b.y = b.y + deltaT * b.v.y;


			if (b.y > b.radius)
				b.v.y = b.v.y - GRAVITY * deltaT;
		}

		Ball b1 = balls.get(0);
		Ball b2 = balls.get(1);

		if (b1.collidesWith(b2)) {
			applyCollision(b1, b2);
		}
	}

	private static int ballCount = 0;

	/**
	 * Simple inner class describing balls.
	 */
	class Ball {

		int idx;

		Vector v;

		double x, y, radius;

		public Ball(double x, double y, double vx, double vy, double radius) {
			v = new Vector(0, 0);
			this.x = x;
			this.y = y;
			this.v.x = vx;
			this.v.y = vy;
			this.radius = radius;
			this.idx = ballCount++;
		}

		//----

		double potentialEnergy() {
			// calculate the potential energy E = mgh
			return mass() * GRAVITY * y;
		}

		double kineticEnergy() {
			// calculate the kinetic energy E = mv/2
			return (1.0 / 2.0) * mass() * Math.pow(velocity(), 2);
		}

		double mass() {
			// calculate the mass (volume of sphere: 4/3*pi*r^3
			return (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
		}

		double velocity() {
			return Math.sqrt((v.x * v.x) + (v.y * v.y));
		}

		public boolean collidesWith(Ball other) {
			if (!(!other.equals(this) && distance(x, y, other.x, other.y) < radius + other.radius))
				return false;

			// they do not collide if they move away from each other

			// Vector r = v.minus(other.v);
			// double dx = x - other.x;
			// double dy = y - other.x;
			// double angle = Math.atan(dy / dx);


			// v.rotate(-angle);
			// other.v.rotate(-angle);


			// if the sign of
			// if (Math.signum(dx * r.x) == 1 && Math.signum(dy * r.y) == 1) {
			//    System.out.println("stopped illegal collision! ");
			//    return false;
			//}

//            double dx = x - other.x;
//            double dy = y - other.y;
//            double colAngle = Math.atan(dy / dx);
//            double myAngle = 0;
//            double otherAngle = 0;
//
//            if ((myAngle - colAngle)

			return true;
		}
	}

	static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) +
				(y1 - y2) * (y1 - y2));
	}

	class Vector {

		@Override
		public String toString() {
			return String.format("{x: %f, y: %f}",
					x, y);
		}

		// rectangular form vector
		double x;
		double y;

		//used for polar vector
		double r;       // radius
		double angle;

		public Vector(double x, double y) {
			this.x = x;
			this.y = y;
			this.r = 0;
			this.angle = 0;
		}

		// rotates the coordinate system of the vector by angle radians
		public void rotate(double angle) {
			rectToPolar();
			this.angle += angle;
			polarToRect();
		}

		double rectLength() {
			return Math.sqrt(x * x + y * y);
		}

		private void rectToPolar() {
			r = Math.sqrt(x * x + y * y);
			angle = Math.atan(y / x);
			if (x < 0) angle += Math.PI;
		}

		private void polarToRect() {
			x = r * Math.cos(angle);
			y = r * Math.sin(angle);
		}

		public Vector minus(Vector ov) {
			return new Vector(x - ov.x, y - ov.y);
		}
	}

	class Point {
		double x, y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}
}