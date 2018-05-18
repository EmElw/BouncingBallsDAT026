import java.util.ArrayList;

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

    static final double GRAVITY = 0;
    private static final double PRECISION = 0.01;
    double areaWidth, areaHeight;

    ArrayList<Ball> balls;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new ArrayList<Ball>();
        balls.add(new Ball(width / 3, height * 1 / 2, 2, 5, 0.2));
        balls.add(new Ball(width / (2 * 3), height * 1 / 2, -0.1, 0, 0.2));
    }

    static boolean collisionEnabled = true;

    void applyCollision(Ball b1, Ball b2) {
        if (!collisionEnabled)
            return;
        System.out.println("collision!");
        System.out.println(b1.v + " - " + b2.v);
        // ball/ball collision
        double angle = Math.atan((b1.y - b2.y) / (b1.x - b2.x));

        // rotate vector along collision angle

        b1.v.rotate(-angle);
        b2.v.rotate(-angle);

        // calculate new velocities using conservation of energy
        double i = b1.mass() * b1.v.x +
                b2.mass() * b2.v.x;

        double r = -(b2.v.x - b1.v.x);

        b1.v.x = (i - (r * b2.mass())) /
                (b1.mass() / b2.mass());
        b2.v.x = r + b1.v.x;

        // return vector to normal form
        b1.v.rotate(angle);
        b2.v.rotate(angle);

        System.out.println(b1.v + " - " + b2.v);
    }

    void step(double deltaT) {
        for (Ball b : balls) {

            if (b.x < b.radius) {
                if (b.v.x < 0)
                    b.v.x *= -1;
            }
            if (b.x > areaWidth - b.radius) {
                if (b.v.x > 0)
                    b.v.x *= -1;
            }
            if (b.y < b.radius) {
                if (b.v.y < 0)
                    b.v.y *= -1;
            }
            if (b.y > areaHeight - b.radius) {
                if (b.v.y > 0)
                    b.v.y *= -1;
            }

            b.v.y = b.v.y - GRAVITY * deltaT;

            b.x = b.x + deltaT * b.v.x;
            b.y = b.y + deltaT * b.v.y;
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
            return !other.equals(this) && distance(x, y, other.x, other.y) < radius + other.radius;
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
    }
}