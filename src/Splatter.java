import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Splatter extends Animation {
    private int x, y;
    private int alpha, alphaChange;
    private ArrayList framesCopy;        // copy of frames for effects

    public Splatter (GameContainer gc, int x, int y, int dx, int dy,
                     int xSize, int ySize,
                     String filename){

        super(gc, x, y, dx, dy, xSize, ySize, filename);
        this.x = x;
        this.y = y;
        alpha = 255;                // set to 255 (fully opaque)
        alphaChange = 5;            // set to 15

        framesCopy = new ArrayList();

        BufferedImage blood = loadImage(filename);
        addFrame(blood, 500);
        copyAnimation(framesCopy, frames);
    }

    public void draw(Graphics2D g2){
        if (alpha > 0){
            BufferedImage copy = getImage();

            int imWidth = copy.getWidth();
            int imHeight = copy.getHeight();

            int[] pixels = new int[imWidth * imHeight];
            copy.getRGB(0, 0, width, height, pixels, 0, imWidth);

            int red, green, blue, newValue;
            int pixelAlpha;


            for (int i = 0; i < pixels.length; i++) {
                pixelAlpha = (pixels[i] >> 24) & 255;
                red = (pixels[i] >> 16) & 255;
                green = (pixels[i] >> 8) & 255;
                blue = pixels[i] & 255;

                if (pixelAlpha != 0) {
                    newValue = blue | (green << 8) | (red << 16) | (alpha << 24);
                    pixels[i] = newValue;
                }
            }

            copy.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
            g2.drawImage(copy, x, y, xSize, ySize, null);
        }

        alpha = alpha - alphaChange;
    }

    public void copyAnimation(ArrayList framesCopy, ArrayList frames) {
        framesCopy.clear();
        for (int i = 0; i < frames.size(); i++) {
            AnimFrame frame = (AnimFrame) frames.get(i);
            BufferedImage copy = copyImage(frame.image);
            framesCopy.add(copy);
        }
    }

    public synchronized BufferedImage getImage() {
        if (framesCopy.size() == 0) {
            return null;
        } else {
            return (BufferedImage) framesCopy.get(currFrameIndex);
        }
    }

    public BufferedImage copyImage(BufferedImage src) {
        if (src == null)
            return null;

        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics g = copy.createGraphics();

        // copy image

        g.drawImage(src, 0, 0, null);
        g.dispose();

        return copy;
    }
}
