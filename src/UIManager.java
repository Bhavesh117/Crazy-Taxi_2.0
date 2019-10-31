import java.awt.*;

public class UIManager {

    private GameContainer gc;
    private int rgb = 252;
    private int scale = 4;
    private Font space, info;

    private static volatile UIManager instance;

    private UIManager(GameContainer gc){
        this.gc = gc;

        space = new Font("Arial Black", Font.PLAIN, 40);
        info = new Font("Arial Black", Font.PLAIN, 20);
    }

    public static synchronized UIManager getInstance(GameContainer gc){
        if(instance == null){
            instance = new UIManager(gc);
        }

        return instance;
    }

    public void draw(Graphics2D g2){
        if(!gc.isPlaying() && !gc.isPlayerDead() && !gc.isPaused()){
            gameStart(g2);
        }
        else if (gc.isPlayerDead()){
            gameOver(g2);
        }
        else if (gc.isPaused()){
            gamePaused(g2);
        }
        else {
            gamePlaying(g2);
        }
    }

    public void gameStart(Graphics2D g2){
        g2.setColor(new Color(rgb, rgb, rgb));
        g2.setFont(space);
        g2.drawString("High Score: " + gc.getHighScore(), 150 * scale, 100 * scale);
        g2.drawString("Press Space To Start", 135 * scale, 150 * scale);

        rgb -= 2;

        if(rgb <= 0){
            rgb = 252;
        }
    }

    public void gamePaused(Graphics2D g2){
        g2.setColor(new Color(rgb, rgb, rgb));
        g2.setFont(space);
        g2.drawString("Press Space To Resume", 125 * scale, 130 * scale);

        rgb -= 2;

        if(rgb <= 0){
            rgb = 252;
        }
    }

    public void gamePlaying(Graphics2D g2){
        g2.setColor(Color.white);
        g2.setFont(info);
        g2.drawString("Score: " + gc.getScore(), 180 * scale, 10 * scale);
        g2.drawString("Health: " + gc.getPlayer().getHealth(), 10 * scale, 10 * scale);
        g2.drawString("FPS: " + gc.getFps(), 360 * scale, 10 * scale);
    }

    public void gameOver(Graphics2D g2){
        g2.setColor(Color.red);
        g2.setFont(space);
        g2.drawString("You Died!", 170 * scale, 100 * scale);

        g2.setColor(Color.white);
        g2.drawString("Your Score: " + gc.getScore(), 150 * scale, 150 * scale);

        g2.setColor(new Color(rgb, rgb, rgb));
        g2.drawString("High Score: " + gc.getHighScore(), 150 * scale, 50 * scale);
        g2.drawString("Press Space To Start Over", 125 * scale, 180 * scale);

        rgb -= 2;

        if(rgb <= 0){
            rgb = 252;
        }
    }
}
