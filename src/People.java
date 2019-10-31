import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class People extends Sprite {

    private Random rand;
    private static final int XSIZE = 20;
    private static final int YSIZE = 25;
    private boolean isPassengerCollected = false;
    private boolean isDroppedOff = false;
    private double pickUpTime;
    private DropOff dropOff;

    public People (GameContainer gc, int x, int y, int dx, int dy,
                                  int xSize, int ySize,
                                  String filename) {

        super(gc, x, y, dx, dy, XSIZE, YSIZE, filename);

        Random rand = new Random();

        int dox = rand.nextInt((dimension.width - 100) + 1) + 50;
        int doy = rand.nextInt((dimension.height - 100) + 1) + 50;

        dropOff = new DropOff(gc, dox, doy, 0, 0, 0, 0, "assets/images/dropoff.png");
    }

    public void draw(Graphics2D g2) {
        if (!isPassengerCollected) super.draw(g2);
        if (!isDroppedOff && isPassengerCollected) dropOff.draw(g2);
    }

    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double (x - XSIZE / 2, y - YSIZE/2, XSIZE * 2, YSIZE * 2);
    }

    public void setPassengerCollected(boolean passengerCollected) {
        isPassengerCollected = passengerCollected;
    }

    public void setDroppedOff(boolean droppedOff) {
        isDroppedOff = droppedOff;
    }

    public boolean isPassengerCollected() {
        return isPassengerCollected;
    }

    public void setPickUpTime(double pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public double getPickUpTime() {
        return pickUpTime;
    }

    public DropOff getDropOff() {
        return dropOff;
    }

    public void update(){ }
}
