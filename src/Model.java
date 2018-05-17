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

    static final double GRAVITY = 0;
    double areaWidth, areaHeight;

    ArrayList<Ball> balls;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new ArrayList<Ball>();
        balls.add(new Ball(width / 3, height * 1 / 3, 2, 1.1, 0.3));
        balls.add(new Ball(width / (2 * 3), height * 1 / 3, -0.1, 1.1, 0.2));
    }

    void step(double deltaT) {
        // TODO this method implements one step of simulation with a step deltaT
        for (Ball b : balls) {
            //
            if (b.x < b.radius) {
                if (b.vx < 0)
                    b.vx *= -1;
            }
            if (b.x > areaWidth - b.radius) {
                if (b.vx > 0)
                    b.vx *= -1;
            }
            if (b.y < b.radius) {
                if (b.vy < 0)
                    b.vy *= -1;
            }
            if (b.y > areaHeight - b.radius) {
                if (b.vy > 0)
                    b.vy *= -1;
            }


            b.vy = b.vy - GRAVITY * deltaT;
            // compute new position according to the speed of the ball
            b.x = b.x + deltaT * b.vx;
            b.y = b.y + deltaT * b.vy;

        }
    }


    private static int ballCount = 0;

    /**
     * Simple inner class describing balls.
     */
    class Ball {
        int idx;

        double x, y, vx, vy, radius;

        public Ball(double x, double y, double vx, double vy, double radius) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = radius;
            this.idx = ++ballCount;
        }

        double potentialEnergy() {
            // calculate the potential energy E = mgh
            return mass() * GRAVITY * y;
        }

        double kineticEnergy() {
            // calculate the kinetic energy E = mv/2
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

        public boolean collidesWith(Ball other) {
            return !other.equals(this) && distance(x, y, other.x, other.y) < radius + other.radius;
        }
    }

    static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) +
                (y1 - y2) * (y1 - y2));
    }
}