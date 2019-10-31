import java.awt.geom.Rectangle2D;

public class DropOff extends Sprite {

    private static final int XSIZE = 20;
    private static final int YSIZE = 25;

    public DropOff (GameContainer gc, int x, int y, int dx, int dy,
                   int xSize, int ySize,
                   String filename) {

        super(gc, x, y, dx, dy, XSIZE, YSIZE, filename);

    }

    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double (x - XSIZE / 2, y - YSIZE/2, XSIZE * 2, YSIZE * 2);
    }

    public void update(){}
}
