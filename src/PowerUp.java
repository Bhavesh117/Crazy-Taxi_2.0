import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class PowerUp extends Animation {
    private Image image;
    private GameContainer gc;
    private SoundEffect sf;
    private int type;
    private double pickUpTime = System.nanoTime() / 1000000000.0;
    private static final int SIZE = 20;
    private boolean isVisible = false;
    private boolean isHeart = false;
    private boolean isFocus = false;
    private boolean isFocusActive = false;
    private String heartUpAudio = "assets/audio/HeartUp.wav";
    private String focusAudio = "assets/audio/Focus.wav";
    private ArrayList framesCopy;        // copy of frames for effects


    public PowerUp(GameContainer gc, int type, int x, int y, int dx, int dy,
                   int xSize, int ySize,
                   String filename) {
        super(gc, x, y, dx, dy, SIZE, SIZE, filename);
        this. gc = gc;
        this.type = type;

        sf = new SoundEffect();

        framesCopy = new ArrayList();

        if (type == 0){
            BufferedImage heart = loadImage("assets/images/heart.png");
            BufferedImage heartTwo = loadImage("assets/images/heart3.png");
            BufferedImage heartThree = loadImage("assets/images/heart2.png");
            BufferedImage heartFour = loadImage("assets/images/heart1.png");

            addFrame(heart, 1000);
            addFrame(heartTwo, 1000);
            addFrame(heartThree, 1000);
            addFrame(heartFour, 1000);
            addFrame(heartThree, 1000);
            addFrame(heartTwo, 1000);


        }
        else{
            BufferedImage focus = loadImage("assets/images/focus.png");

            addFrame(focus, 500);
        }

        if(gc.getScore() < 150 || type == 0){ // Only heart power up available for scores below 150.
            sf.setFile(heartUpAudio);
            isHeart = true;
        }
        else {
            sf.setFile(focusAudio);
            isFocus = true;
        }

        copyAnimation (framesCopy, frames);
    }

    public void copyAnimation (ArrayList framesCopy, ArrayList frames) {
        framesCopy.clear();
        for (int i=0; i<frames.size(); i++) {
            AnimFrame frame = (AnimFrame)frames.get(i);
            BufferedImage copy = copyImage(frame.image);
            framesCopy.add(copy);
        }
    }

    public synchronized BufferedImage getImage() {
        if (framesCopy.size() == 0) {
            return null;
        }
        else {
            return (BufferedImage)framesCopy.get(currFrameIndex);
        }
    }


    // make a copy of the BufferedImage src

    public BufferedImage copyImage(BufferedImage src) {
        if (src == null)
            return null;

        BufferedImage copy = new BufferedImage (src.getWidth(), src.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics g = copy.createGraphics();

        // copy image

        g.drawImage(src, 0, 0, null);
        g.dispose();

        return copy;
    }

    public void draw(Graphics2D g2) {
        if(isVisible){
            g2.drawImage(getImage(), x, y, xSize, ySize, null);
        }
    }

    public synchronized void update() {
        super.update();

        double presentTime = System.nanoTime() / 1000000000.0;

        if(gc.getPlayer().isPowerUp(this) && isVisible){
            pickUpTime = System.nanoTime() / 1000000000.0;

            if (type == 0){
                gc.getPlayer().healthUp();
            }
            else if (!isFocusActive){
                isFocusActive = true;
            }

            sf.play();
            isVisible = false;
        }

        if (presentTime - pickUpTime > 6 && isFocusActive){
            isFocusActive = false;
        }
    }

    public boolean isFocusActive() {
        return isFocusActive;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
