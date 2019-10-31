import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameInput implements KeyListener {

    private GameContainer gc;
    private final int NUM_KEYS = 256;
    private boolean[] keys = new boolean[NUM_KEYS];

    public GameInput(GameContainer gc) {
        this.gc = gc;

        gc.addKeyListener(this);
    }


    public void playerUpdate() {
        if (isKey(KeyEvent.VK_A)){
            gc.getPlayer().moveLeft();
        }
        else if (isKey(KeyEvent.VK_D)){
            gc.getPlayer().moveRight();
        }
        else if (isKey(KeyEvent.VK_W )){
            gc.getPlayer().moveUp();
        }
        else if (isKey(KeyEvent.VK_S)){
            gc.getPlayer().moveDown();
        }
    }

    public boolean startGame() {
        return (isKey(KeyEvent.VK_SPACE));
    }

    public boolean endGame() {
        return (isKey(KeyEvent.VK_ESCAPE));
    }

    public boolean pauseGame() {
        return (isKey(KeyEvent.VK_SHIFT));
    }

    public boolean isKey(int keyCode) {
        return keys[keyCode];
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
}
