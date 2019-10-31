import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Vehicle extends Sprite{

    private Dimension dimension;

    private boolean inFocus = false;

    public Vehicle (GameContainer gc, int x, int y, double dx, double dy,
                    int xSize, int ySize,
                    String filename){
        super(gc, x, y, dx, dy, xSize, ySize, filename);
        this.dx = dx;
        this.dy = dy;
        dimension = gc.getSize();
    }

//    vehicles off screen teleport to the other side at a new random position
    public void update () {
        x += dx;
        y += dy;

        if (x > dimension.width + xSize + 10){
            y = new Random().nextInt((dimension.height - 10) + 1) + 10;
            x = xSize * -1;
        }
        else if (x < (xSize * -1) - 10){
            y = new Random().nextInt((dimension.height - 10) + 1) + 10;
            x = xSize + dimension.width;
        }
        else if (y > dimension.height + ySize + 10){
            x = new Random().nextInt((dimension.width - 10) + 1) + 10;
            y = ySize * -1;
        }
        else if (y < (ySize * -1) - 10){
            x = new Random().nextInt((dimension.width - 10) + 1) + 10;
            y = ySize + dimension.height;
        }
    }

    public void setFocus(double factor) {
        this.dx = dx * factor;
        this.dy = dy * factor;

        if(!inFocus)
            inFocus = true;
        else
            inFocus = false;
    }

    public void draw(Graphics2D g2){
        if (dx > 0){
            rotateDraw(g2, 1);
        }
        else if (dx < 0){
            rotateDraw(g2, 3);
        }
        else if (dy > 0){
            rotateDraw(g2, 2);
        }
        else if (dy < 0){
            super.draw(g2);
        }
    }

    private void rotateDraw(Graphics2D g2, int angle){
        BufferedImage dest = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dest.createGraphics();

        g2d.drawImage(spriteImage, 0, 0, null);	// copy in the image
        g2d.dispose();

        dest = rotateImage(dest, angle);

        g2.drawImage(dest, x, y, xSize, ySize, null);
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


    public boolean isInFocus() {
        return inFocus;
    }
}
