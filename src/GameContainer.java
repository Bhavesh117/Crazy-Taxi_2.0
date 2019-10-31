import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

public class GameContainer extends JFrame implements Runnable{
    private static final int NUM_BUFFERS = 2;

    private static GameContainer instance;

    private String title = "Crazy Taxi";

    private Thread gameThread;
    private GameInput gameInput;
    private Image bgImage;
    private BufferStrategy bs;
    private Player player;
    private Passenger passengers;
    private PowerUp powerUp;
    private Traffic traffic;
    private Graphics2D g;
    private Splatter splatter;
    private Dimension dimension;
    private SoundEffect backgroundSound;
    private SoundEffect selectEffect;
    private UIManager uiManager;
    private GraphicsDevice device;

    private int pWidth, pHeight;
    private volatile boolean isRunning;
    private volatile boolean isPlaying;
    private volatile boolean isPaused;
    private volatile boolean canSplatter;
    private volatile boolean canSpawnPowerUp;
    private volatile boolean isPlayerDead;
    private final double UPDATE_CAP = 1.0/60.0;
    private int maxPassengers;
    private int score;
    private int highScore;
    private int fps;
    private int tMultiplier = 5;
    private int lastPowerUpScore = 0;
    private double tsMultiplier = 10;
    private int psMultiplier = 5;
    private String backgroundAudio = "assets/audio/Background.wav";
    private String selectAudio = "assets/audio/Select.wav";


    private GameContainer () {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initFullScreen();
        this.start();
    }

    public static synchronized GameContainer getInstance(){
        if(instance == null){

            instance = new GameContainer();
        }
        return instance;
    }

    public void start() {
        if (gameThread == null) {
            setUpGame();
            gameThread.start();
        }
    }

    private void initFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = ge.getDefaultScreenDevice();

        setUndecorated(true);	// no menu bar, borders, etc.
        setIgnoreRepaint(true);	// turn off all paint events since doing active rendering
        setResizable(false);	// screen cannot be resized

        if (!device.isFullScreenSupported()) {
            System.out.println("Full-screen exclusive mode not supported");
            System.exit(0);
        }

        device.setFullScreenWindow(this); // switch on full-screen exclusive mode

        // we can now adjust the display modes, if we wish

        showCurrentMode();

        pWidth = getBounds().width;
        pHeight = getBounds().height;
        dimension = new Dimension(pWidth, pHeight);

        try {
            createBufferStrategy(NUM_BUFFERS);
        }
        catch (Exception e) {
            System.out.println("Error while creating buffer strategy " + e);
            System.exit(0);
        }

        bs = getBufferStrategy();
    }

    //    sets up the game.
    public void setUpGame() {
        isRunning = true;
        gameInput = new GameInput(this);
        gameThread = new Thread(this);
        player = new Player(this, 200, 200, 0, 0, 0, 0, "assets/images/taxi_one.png");
        powerUp = new PowerUp(this, 0, 0, 0, 0, 0, 0, 0, "assets/images/heart.png");
        passengers = new Passenger(this);
        traffic = new Traffic(this);
        backgroundSound = new SoundEffect();
        selectEffect = new SoundEffect();
        uiManager = UIManager.getInstance(this);
        bgImage = ImageReader.loadImage("assets/images/background.png");
        score = 0;
        maxPassengers = 1;
        canSpawnPowerUp = true;
        isPlayerDead = false;
        isPaused = false;
        loadHighScore();
    }

    public void run () {
        isPlaying = false;
        boolean render = false;
        double firstTime;
        double lastTime = System.nanoTime() / 1000000000.0;
        double passedTime;
        double unprocessedTime = 0;
        double frameTime = 0;
        int frames = 0;

        backgroundSound.setFile(backgroundAudio);
        backgroundSound.play();
        backgroundSound.loop();

        while (isRunning) {
            render = false;
            requestFocus();
            firstTime = System.nanoTime() / 1000000000.0;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;


            while(unprocessedTime >= UPDATE_CAP) {
                unprocessedTime -= UPDATE_CAP;
                render = true;

                update();

                if (frameTime >= 1.0){
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                }
            }

            pauseGame();
            playGame();
            newGame();
            endGame();

            if(render){
                try {
                    g = (Graphics2D)bs.getDrawGraphics();
                    g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
                    this.draw();
                    frames++;
                    g.dispose();
                    if (!bs.contentsLost())
                        bs.show();
                    else
                        System.out.println("Contents of buffer lost.");

                    Toolkit.getDefaultToolkit().sync();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    isRunning = false;
                }

            }
            else {
                try {
                    Thread.sleep (10);	// increase value of sleep time to slow down ball
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        this.dispose();
    }

    // This method provides details about the current display mode.

    private void showCurrentMode() {
        DisplayMode dm = device.getDisplayMode();
        System.out.println("Current Display Mode: (" +
                dm.getWidth() + "," + dm.getHeight() + "," +
                dm.getBitDepth() + "," + dm.getRefreshRate() + ")  " );
    }

    public void pauseGame(){
        if(gameInput.pauseGame() && !isPaused){
            selectEffect.setFile(selectAudio);
            selectEffect.play();
            isPaused = true;
            isPlaying = false;
        }
    }

    public void playGame(){
        if(!isPlaying && !isPlayerDead){
            if(gameInput.startGame()){
                selectEffect.setFile(selectAudio);
                selectEffect.play();
                isPlaying = true;
                isPaused = false;
            }
        }
    }

    public void endGame(){
        if(gameInput.endGame()){
            selectEffect.setFile(selectAudio);
            selectEffect.play();
            isRunning = false;
        }
    }

    public void newGame(){
        if(!isPlaying && isPlayerDead){
            if(gameInput.startGame()){
                selectEffect.setFile(selectAudio);
                selectEffect.play();
                setUpGame();
                isPlaying = true;

            }
        }
    }

    public void draw() {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawImage(bgImage, 0, 0, pWidth, pHeight, null);
        if(player.isImmune())
            splatter.draw(getG());

        powerUp.draw(getG());
        passengers.draw();
        traffic.draw();
        player.draw(getG());
        uiManager.draw(getG());
    }

    public void update(){
        if(!isPaused){
            if(isPlaying && !isPlayerDead){
                gameInput.playerUpdate();
                player.update();
                canSpawnPowerUp = updatePowerUpSpawn();
                powerUp.update();
                passengers.update();
                refreshPowerUp();
                addTraffic();
                increaseSpeed();
                increaseMaxPassengers();
            }

            traffic.update();
        }

        if (!player.isImmune()) canSplatter = true;

        if (player.isImmune() && canSplatter){
            splatter = new Splatter(this, player.getX(), player.getY(), 0, 0, 50, 50, "assets/images/blood.png");
            canSplatter = false;
        }

        if (player.getHealth() <= 0){
            isPlayerDead = true;
            isPlaying = false;
            updateHighScore();
        }
    }

    //  Adds more vehicles to the game according to the players score
    public void addTraffic(){
        if (score > traffic.trafficSize()*tMultiplier){
            traffic.addVehicle();
            tMultiplier += 2;
        }
    }

    //  Refreshes the power up based on the score if none are available.
    public void refreshPowerUp() {
        if (score != 0 && !powerUp.isVisible() && canSpawnPowerUp){
            Random rand = new Random();
            int type = rand.nextInt(2); // USed to randomly choose a power up type 0 is heart and 1 is focus.
            int x = rand.nextInt((getDimension().width - 100) + 1) + 50;
            int y = rand.nextInt((getDimension().height - 100) + 1) + 50;
            lastPowerUpScore = score;

            if(getScore() < 150 || type == 0) {
                type = 0;
                powerUp = new PowerUp(this, type, x, y, 0, 0, 0, 0, "assets/images/heart.png");

            }
            else{
                powerUp = new PowerUp(this, type, x, y, 0, 0, 0, 0, "assets/images/focus.png");
            }

            powerUp.setVisible(true);
        }
    }

    //  Used to determine if it is possible for a power up to be spawned.
    public boolean updatePowerUpSpawn(){
        return (score - lastPowerUpScore >= 50);
    }

    //    Increases new traffic vehicles speed and the speed of the player based on the score.
    public void increaseSpeed(){
        if (score > traffic.getSpeed()*tsMultiplier){
            traffic.increaseSpeed();
            tsMultiplier += 1.5;
        }

        if (score > player.getSpeed()*psMultiplier){
            player.increaseSpeed();
            psMultiplier += 2;
        }
    }

    private void loadHighScore(){
        Scanner scoreScanner;

        try{
            scoreScanner = new Scanner(new File("assets/HighScore.txt"));

            String highScoreString;

            while(scoreScanner.hasNext()){
                highScoreString = scoreScanner.next();
                highScore = Integer.valueOf(highScoreString);
            }

            scoreScanner.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updateHighScore(){
        if (highScore < score){
            highScore = score;

            try {
                FileWriter highScoreWriter = new FileWriter("assets/HighScore.txt");
                highScoreWriter.write(Integer.toString(highScore));
                highScoreWriter.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void increaseScore(int amount){
        score += amount;
    }

    public void increaseMaxPassengers(){
        if (score > maxPassengers*20 && maxPassengers < 3)
            maxPassengers += 1;
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }

    public Graphics2D getG() {
        return g;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public int getScore() {
        return score;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public int getFps() {
        return fps;
    }

    public boolean isPlayerDead() {
        return isPlayerDead;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getHighScore() {
        return highScore;
    }

}
