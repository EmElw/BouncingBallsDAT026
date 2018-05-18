import java.util.ArrayList;

/**
 * The physics model.
 * <p>
 * This class is where you should implement your bouncing balls model.
 * <p>
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 *
 * @author Simon Robillard
 */
class Model {

    static final double GRAVITY = 10;
    double areaWidth, areaHeight;

    ArrayList<Ball> balls;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new ArrayList<Ball>();
        balls.add(new Ball(width / 3, height * 0.9, 0, 0, 0.2));
    }

    void step(double deltaT) {
        // TODO this method implements one step of simulation with a step deltaT
        // If the ball is at one of the edges and is starting to move through, change the sign of the velocity.
        for (Ball b : balls) {
            if (b.x < b.radius) {
                b.x = b.radius;
                if (b.vx < 0)
                    b.vx *= -1;
            }
            if (b.x > areaWidth - b.radius) {
                b.x = areaWidth - b.radius;
                if (b.vx > 0)
                    b.vx *= -1;
            }
            if (b.y < b.radius) {
                b.y = b.radius;
                if (b.vy < 0)
                    b.vy *= -1;
            }
            if (b.y > areaHeight - b.radius) {
                b.y = areaHeight - b.radius;
                if (b.vy > 0)
                    b.vy *= -1;
            }


            // compute new position according to the speed of the ball
            b.x = b.x + deltaT * b.vx;
            b.y = b.y + deltaT * b.vy;
            b.vy = b.vy - GRAVITY * deltaT;

        }
    }

    /**
     * Simple inner class describing balls.
     */
    class Ball {
        double x, y, vx, vy, radius;

        public Ball(double x, double y, double vx, double vy, double radius) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = radius;
        }

        double potentialEnergy() {
            // calculate the potential energy E = mgh
            return mass() * GRAVITY * y;
        }

        double kineticEnergy() {
            // calculate the kinetic energy E = mv^2/2
            return (1.0 / 2.0) * mass() * Math.pow(velocity(), 2);
        }

        double mass() {
            return 1;
            // calculate the mass (volume of sphere: 4/3*pi*r^3
            // return (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
        }

        double velocity() {
            return Math.sqrt((vx * vx) + (vy * vy));
        }

    }
}