import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Player extends Animation {

    private static final int XSIZE = 30;
    private int YSIZE = 60;
    private static final int XPOS = 200;
    private static final int YPOS = 200;
    private int speed = 4;
    private int health = 5;
    private int numPassengers = 0;
    private int angle = 0;
    private double accidentTime = System.nanoTime() / 1000000000.0;
    private List<People> passengers;
    private GameContainer gc;
    private Dimension dimension;
    private SoundEffect sf;
    private String pickUpAudio = "assets/audio/Pickup.wav";
    private String dropOffAudio = "assets/audio/DropOff.wav";
    private String crashAudio = "assets/audio/Crash.wav";
    private boolean isImmune  = false;


    public Player (GameContainer gc, int x, int y, int dx, int dy,
                   int xSize, int ySize,
                   String filename){
        super(gc, x, y, dx, dy, XSIZE, 60, filename);

        this.gc = gc;

        dimension = gc.getSize();

        x = XPOS;
        y = YPOS;
        xSize = XSIZE;
        ySize = YSIZE;
        passengers = new ArrayList<>();
        sf = new SoundEffect();


        BufferedImage one = loadImage("assets/images/taxi_one.png");
        BufferedImage two = loadImage("assets/images/taxi_two.png");
        BufferedImage three = loadImage("assets/images/taxi_three.png");
        addFrame(one, 500);
        addFrame(two, 500);
        addFrame(three, 500);
    }

//    Player immune check.
    public void update(){
        double presentTime = System.nanoTime() / 1000000000.0;
        if (presentTime - accidentTime > 3){
            isImmune = false;
           // color = Color.YELLOW;
        }
    }

    public void draw (Graphics2D g2) {
        rotateDraw(g2, angle);
    }

    public int toGray (int pixel) {

        int alpha, red, green, blue, gray;
        int newPixel;

        alpha = (pixel >> 24) & 255;
        red = (pixel >> 16) & 255;
        green = (pixel >> 8) & 255;
        blue = pixel & 255;

        // Calculate the value for gray

        gray = (red + green + blue) / 3;

        // Set red, green, and blue channels to gray

        red = green = blue = gray;

        newPixel = blue | (green << 8) | (red << 16) | (alpha << 24);
        return newPixel;
    }

    public BufferedImage copyImage(BufferedImage src) {
        if (src == null)
            return null;

        BufferedImage copy = new BufferedImage (src.getWidth(), src.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = copy.createGraphics();

        // copy image
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();

        return copy;
    }

    private void rotateDraw(Graphics2D g2, int angle){

        BufferedImage dest;

        if (numPassengers <= 1)
            dest = getFrame(0).image;
        else if (numPassengers == 2)
            dest = getFrame(1).image;
        else
            dest = getFrame(2).image;

        dest = rotateImage(dest, angle);

        if (!isImmune) {
                g2.drawImage(dest, x, y, xSize, ySize, null);
        }
        else {
                drawGreyImage(g2, dest, xSize, ySize);
        }
    }

    private void drawGreyImage(Graphics2D g2, BufferedImage copy, int xSize, int ySize){

        int imWidth = copy.getWidth();
        int imHeight = copy.getHeight();

        int [] pixels = new int[imWidth * imHeight];
        copy.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

        int alpha, red, green, blue, gray;

        for (int i=0; i<pixels.length; i++) {
            pixels[i] = toGray(pixels[i]);
        }

        copy.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

        g2.drawImage(copy, x, y, xSize, ySize, null);
    }

    public static BufferedImage rotateImage(BufferedImage image, int quadrants) {

        int w0 = image.getWidth();
        int h0 = image.getHeight();
        int w1 = w0;
        int h1 = h0;
        int centerX = w0 / 2;
        int centerY = h0 / 2;

        if (quadrants % 2 == 1) {
            w1 = h0;
            h1 = w0;
        }

        if (quadrants % 4 == 1) {
            centerX = h0 / 2;
            centerY = h0 / 2;
        } else if (quadrants % 4 == 3) {
            centerX = w0 / 2;
            centerY = w0 / 2;
        }

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToQuadrantRotation(quadrants, centerX, centerY);
        AffineTransformOp opRotated = new AffineTransformOp(affineTransform,
                AffineTransformOp.TYPE_BILINEAR);
        BufferedImage transformedImage = new BufferedImage(w1, h1,
                image.getType());
        transformedImage = opRotated.filter(image, transformedImage);

        return transformedImage;
    }

//    Returns bounding area for player.


    public void moveLeft () {

        if (!gc.isVisible ()) return;

        if (xSize < ySize){
            int temp = xSize;
            xSize = ySize;
            ySize = temp;
        }

        x = x - speed;
        angle = 3;

        if (x < 0) {					// hits left wall
            x = 0;
        }
    }

    public void moveRight () {

        if (xSize < ySize){
            int temp = xSize;
            xSize = ySize;
            ySize = temp;
        }

        if (!gc.isVisible ()) return;
        x = x + speed;
        angle = 1;

        if (x + xSize >= dimension.width) {		// hits right wall
            x = dimension.width - xSize;
        }
    }

    public void moveUp () {
        if (!gc.isVisible ()) return;

        if (xSize > ySize){
            int temp = xSize;
            xSize = ySize;
            ySize = temp;
        }

        y = y - speed;
        angle = 0;

        if (y < 0) {					// hits left wall
            y = 0;
        }
    }

    public void moveDown () {
        if (!gc.isVisible ()) return;

        y = y + speed;
        angle = 2;

        if (xSize > ySize){
            int temp = xSize;
            xSize = ySize;
            ySize = temp;
        }

        if (y + ySize >= dimension.height) {					// hits left wall
            y = dimension.height - ySize;
        }
    }


    public void increaseSpeed() {
        speed += 1;
    }

//    Determines if the player has collied with a vehicle.
    public boolean isAccident(Vehicle v) {
        Rectangle2D playerBox = getBoundingRectangle();
        Rectangle2D vehicleRec = v.getBoundingRectangle();

        return (playerBox.intersects(vehicleRec));
    }

//    decreases player health and sets player to being immune to hits.
    public void damage(){
        if(!isImmune){
            health -= 1;
            isImmune = true;
            accidentTime = System.nanoTime() / 1000000000.0;
            //color = Color.WHITE;
            sf.setFile(crashAudio);
            sf.play();
        }
    }

//    Determines if the player has collied with a power up.
    public boolean isPowerUp(Sprite powerUp){
        Rectangle2D playerBox = getBoundingRectangle();
        Rectangle2D powerUpRec = powerUp.getBoundingRectangle();

        return (playerBox.intersects(powerUpRec));
    }

//    Determines if the player has collied with a passenger.
    public boolean isPassenger(People passenger){
        Rectangle2D playerBox = getBoundingRectangle();
        Rectangle2D passengerRec = passenger.getBoundingRectangle();

        return (playerBox.intersects(passengerRec));
    }

//    Determines if the player has collied with a passenger drop off.
    public boolean isDropOff(People passenger){
        Rectangle2D playerBox = getBoundingRectangle();
        Rectangle2D dropOffRec = passenger.getDropOff().getBoundingRectangle();

        return (playerBox.intersects(dropOffRec) && passengers.contains(passenger));
    }

//    Adds a passenger to the player car and increases player size.
    public void pickUpPassenger(People passenger){
        passenger.setPickUpTime(System.nanoTime() / 1000000000.0);
        passengers.add(passenger);
        sf.setFile(pickUpAudio);
        sf.play();
        numPassengers++;

        if (ySize > xSize)
            ySize += 5;
        else
            xSize += 5;
    }

//    Removes a passenger from the player car and decreases player size.
    public void dropOffPassenger(People passenger){
        double presentTime = System.nanoTime() / 1000000000.0;
        passengers.remove(passenger);
        sf.setFile(dropOffAudio);
        sf.play();

        if(presentTime - passenger.getPickUpTime() >= 5)
            gc.increaseScore(2);
        else
            gc.increaseScore(5);

        if (ySize > xSize)
            ySize -= 5;
        else
            xSize -= 5;

        numPassengers--;
    }

    public void healthUp(){
        health += 1;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isImmune() {
        return isImmune;
    }

    public int getHealth() {
        return health;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
