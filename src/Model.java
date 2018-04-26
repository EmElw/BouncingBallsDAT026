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

    private static final double GRAVITY = 0.1;
    double areaWidth, areaHeight;

    Ball[] balls;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new Ball[2];
        balls[0] = new Ball(width / 3, height * 0.9, 0, 0, 0.2);
        balls[1] = new Ball(2 * width / 3, height * 0.7, 0, 0, 0.3);
    }

    void step(double deltaT) {
        // TODO this method implements one step of simulation with a step deltaT
        for (Ball b : balls) {
            // detect collision with the border
            if (b.x < b.radius || b.x > areaWidth - b.radius) {
                b.vx *= -1; // change direction of ball
            }
            if (b.y < b.radius || b.y > areaHeight - b.radius) {
                b.vy *= -1;
            }

            b.vy-=GRAVITY;

            // compute new position according to the speed of the ball
            b.x += deltaT * b.vx;
            b.y += deltaT * b.vy;
        }
    }

    /**
     * Simple inner class describing balls.
     */
    class Ball {

        Ball(double x, double y, double vx, double vy, double r) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = r;
        }

        /**
         * Position, speed, and radius of the ball. You may wish to add other attributes.
         */
        double x, y, vx, vy, radius;

        public double potentialEnergy() {
            // calculate the potential energy E = mgh
            return mass() * GRAVITY * y;
        }

        public double kineticEnergy() {
            // calculate the kinetic energy E = mv/2
            return 1.0 / 2.0 * mass() * Math.pow(velocity(),2);
        }

        private double mass() {
            // calculate the mass (volume of sphere: 4/3*pi*r^3
            return 4.0 / 3.0 * Math.PI * Math.pow(radius, 3);
        }

        public double velocity() {
            return Math.sqrt((vx * vx) + (vy * vy));
        }

    }
}