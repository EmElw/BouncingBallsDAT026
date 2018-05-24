import java.util.*;

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

	private static final double GRAVITY = 10;
	private static final double TAU = 2 * Math.PI;
	private double areaWidth, areaHeight;

	List<Ball> balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		// Initialize the model with a few balls
		balls = new ArrayList<>();
		balls.add(new Ball(width / 3, height * 1 / 2, 2, 0.4, 0.4));
		balls.add(new Ball(width / (2 * 3), height * 1 / 2, -2, 0, 0.2));
		balls.add(new Ball(width / 8, height * 2 / 3, 2, 0.4, 0.3));
	}


	private void applyCollision(Ball b1, Ball b2) {
		// ball/ball collision
		double dy = b1.y - b2.y;
		double dx = b1.x - b2.x;
		double angle = Math.atan(dy / dx);
		// find the correct solution to atan
		if (dx < 0) {
			angle += Math.PI;
		}

		// rotate vector along collision angle
		b1.v.rotate(-angle);
		b2.v.rotate(-angle);

		// ignore collisions between balls that are moving away from each other
		// i. e. only collide if not invalid collision
		if (!(  // if b1 is left ball and has lower v.x than b2 the collision is invalid
				(3 / 4 * TAU > angle && angle > 1 / 4 * TAU) && b1.v.x < b2.v.x ||
				// if b1 is right ball and has higher v.x than b2 the collision is invalid
				(3 / 4 * TAU < angle || angle < 1 / 4 * TAU) && b1.v.x > b2.v.x)) {

			// calculate total momentum
			double i = b1.mass() * b1.v.x +
					b2.mass() * b2.v.x;

			// relative velocity
			double r = -(b2.v.x - b1.v.x);

			b1.v.x = (i - (r * b2.mass())) /
					(b1.mass() + b2.mass());
			b2.v.x = r + b1.v.x;
		}

		// return vector to normal form
		b1.v.rotate(angle);
		b2.v.rotate(angle);
	}

	void step(double deltaT) {
		for (Ball b : balls) {

			// bordering (limiting the balls to a specific area)
			if (b.x < b.radius && b.v.x < 0)                // left side
				b.v.x *= -1;

			if (b.x > areaWidth - b.radius && b.v.x > 0)    // right side
				b.v.x *= -1;

			if (b.y < b.radius && b.v.y < 0)                // bottom
				b.v.y *= -1;

			if (b.y > areaHeight - b.radius && b.v.y > 0)   // top
				b.v.y *= -1;

			// apply movement based on simulated time
			b.x = b.x + deltaT * b.v.x;
			b.y = b.y + deltaT * b.v.y;

			// apply gravity
			// the check simulates normal force, as balls in contact with the floor should not accelerate downwards
			if (b.y > b.radius)
				b.v.y = b.v.y - GRAVITY * deltaT;
		}

		// Apply collisions
		// applies collision twice for each pair of balls, but this is problem is handled in applyCollision
		for (Ball b : balls) {
			for (Ball o : balls) {
				if (b.collidesWith(o)) {
					applyCollision(b, o);
				}
			}
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

		Ball(double x, double y, double vx, double vy, double radius) {
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
			// calculate the mass (volume of sphere: 4/3*pi*radius^3
			return (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
		}

		double velocity() {
			return Math.sqrt((v.x * v.x) + (v.y * v.y));
		}

		boolean collidesWith(Ball o) {
			return !o.equals(this) && Math.sqrt((x - o.x) * (x - o.x) +
					(y - o.y) * (y - o.y)) < radius + o.radius;
		}
	}

	class Vector {

		// rectangular form vector
		double x;
		double y;

		//used for polar vector
		double radius;
		double angle;

		Vector(double x, double y) {
			this.x = x;
			this.y = y;
			this.radius = 0;
			this.angle = 0;
		}

		// rotates the coordinate system of the vector by angle radians
		void rotate(double angle) {
			rectToPolar();
			this.angle += angle;
			polarToRect();
		}

		// convert rectangular vector to polar form
		private void rectToPolar() {
			radius = Math.sqrt(x * x + y * y);
			angle = Math.atan(y / x);
			if (x < 0) angle += Math.PI;
		}

		// convert polar vector to rectangular form
		private void polarToRect() {
			x = radius * Math.cos(angle);
			y = radius * Math.sin(angle);
		}
	}
}