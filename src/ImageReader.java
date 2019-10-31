import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageReader {

    public static Image loadImage(String path){
        return new ImageIcon(path).getImage();
    }
}
