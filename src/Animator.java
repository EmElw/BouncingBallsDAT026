
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


/**
 * Animated JPanel drawing the bouncing balls. No modifications are needed in this class.
 *
 * @author Simon Robillard
 */
@SuppressWarnings("serial")
public final class Animator extends JPanel implements ActionListener {

    private static final int SAMPLE_PERIOD = 10;
    private static final int FPS = 100;

    private PrintWriter pw;

    public Animator(int pixelWidth, int pixelHeight, int fps) {
        super(true);
        this.timer = new Timer(1000 / fps, this);
        this.deltaT = 1.0 / fps;
        this.model = new Model(pixelWidth / pixelsPerMeter, pixelHeight / pixelsPerMeter);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(pixelWidth, pixelHeight));

        try {
            this.pw = new PrintWriter("output.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Drawing scale
     */
    private static final double pixelsPerMeter = 200;

    /**
     * Physical model
     */
    private Model model;

    /**
     * Timer that triggers redrawing
     */
    private Timer timer;

    /**
     * Time interval between redrawing, also used as time step for the model
     */
    private double deltaT;

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // clear the canvas
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        // draw balls

        double sumEnergy = 0;

        for (Model.Ball b : model.balls) {
            g2.setColor(Color.GREEN);
            double x = b.x - b.radius;
            double y = b.y + b.radius;

            int xx = (int) (x * pixelsPerMeter);
            int yy = (int) (getHeight() - (y * pixelsPerMeter));
            int cx = (int) ((x + b.radius) * pixelsPerMeter);
            int cy = (int) (getHeight() - ((y - b.radius) * pixelsPerMeter));

            for (Model.Ball other : model.balls) {
                if (b.collidesWith(other)) g2.setColor(Color.BLUE);
            }

            // paint balls (y-coordinates are inverted)
            Ellipse2D.Double e = new Ellipse2D.Double(xx, yy,
                    b.radius * 2 * pixelsPerMeter, b.radius * 2 * pixelsPerMeter);
            g2.fill(e);
            sumEnergy += b.potentialEnergy() + b.kineticEnergy();

            g2.setColor(Color.WHITE);

            g2.drawLine(cx, cy, (int) (cx + 20 * b.v.x), (int) (cy - 20 * b.v.y));
            g2.drawLine(cx, cy, (int) (cx + 20 * b.v.x), cy);
            g2.drawLine(cx, cy, cx, (int) (cy - 20 * b.v.y));

            String str = String.format("x %f y %f m %f Ek %f \n Ep %f \n Et %f",
                    b.x,
                    b.y,
                    b.mass(),
                    b.kineticEnergy(),
                    b.potentialEnergy(),
                    b.potentialEnergy() + b.kineticEnergy());
            g2.drawString(str, 10, 10 + 20 * b.idx);
        }
        g2.drawString("total energy: " + sumEnergy, 10, 10);
    }

    private int count = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        if (count == SAMPLE_PERIOD) {
            count = 0;
        }
        model.step(deltaT);
        this.repaint();
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Animator anim = new Animator(800, 600, FPS);
                JFrame frame = new JFrame("Bouncing balls");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(anim);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                anim.start();
            }
        });
    }
}