/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tappyplane;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

/**
 *
 * @author shootingstar
 */
public class Main extends PApplet {

    // Window's width and height
    private int width = 800, height = 480;

    // For changing the ground's and rock's images
    public static int level = 0;

    private int timer = 180;

    // Record the score
    private int score = 0;

    // Record the game state
    private int gameState = 0;
    private final int GAME_START = 0, GAME_READY = 1, GAME_RUN = 2, GAME_OVER = 3;

    // Backgrounds' X coordinate and speed
    private int bg1X = 0, bg2X = width;
    private int speed = -5;

    // Plane's vertical speed
    private float ySpeed;

    // Gravity acceleration and plane's lift 
    private final float GRAVITY = 0.5f, LIFT = -1f;

    // Determine the level increasing or decreasing
    private boolean isRight = true;

    private PFont font;

    // Game ready and game over images
    private PImage getReady, gameOver;

    // Store the background images
    private PImage[] bgs = new PImage[2];

    // Store the cursor images
    private PImage[] cursors = new PImage[2];

    // Store the ground object
    private Ground[] grounds = new Ground[2];

    // Start game and restart game buttons
    private Button btnStart = new Button(width / 2 - Button.WIDTH / 2, height * 2 / 3, "Start Game");
    private Button btnRestart = new Button(width / 2 - Button.WIDTH / 2, height * 2 / 3, "Restart");

    // Store the rock object
    private List<Rock> rocks = new ArrayList<>();

    // Player
    private Plane plane = new Plane(width / 10, height / 3);

    @Override
    public void settings() {
        // Window sizess
        size(width, height, P2D);
    }

    @Override
    public void setup() {
        // Window's title
        surface.setTitle("FlappyPlane");

        // Initialize the game
        initGame();
    }

    @Override
    public void draw() {
        switch (gameState) {
            case GAME_START:
                changeLevel();

                updateBackground();
                drawBackground();

                updateRocks();
                drawRocks();

                updateGround();
                drawGround();

                drawTitle();

                updateBtnStart();
                drawBtnStart();
                break;

            case GAME_READY:
                drawBackground();
                drawRocks();
                drawGround();
                drawPlane();

                if (timer > 0) {
                    drawGameReady();
                    drawTimer();

                    timer--;
                } else {
                    timer = 180;
                    gameState = GAME_RUN;
                }
                break;

            case GAME_RUN:
                changeLevel();

                updateBackground();
                drawBackground();

                updateRocks();
                drawRocks();

                updateGround();
                drawGround();

                updatePlane();
                drawPlane();

                drawCurrentScore();

                detectCollisionWithPlane();
                break;

            case GAME_OVER:
                drawBackground();
                drawRocks();
                drawGround();
                drawPlane();

                drawGameOver();
                drawFinalScore();

                updateBtnRestart();
                drawBtnRestart();
                break;
        }

        showCursor();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////setup()/////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initGame() {
        loadImage();
        loadFont();

        // Construct the grounds
        grounds[0] = new Ground(0, height - 71);
        grounds[1] = new Ground(width, height - 71);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////load resource///////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadImage() {
        try {
            // Background
            bgs[0] = new PImage(ImageIO.read(getClass().getResource("img/background.png")));
            bgs[1] = (PImage) bgs[0].clone();

            // Cursor
            cursors[0] = new PImage(ImageIO.read(getClass().getResource("img/UI/tap.png")));
            cursors[1] = new PImage(ImageIO.read(getClass().getResource("img/UI/tapTick.png")));

            // Game ready and game over
            getReady = new PImage(ImageIO.read(getClass().getResource("img/UI/textGetReady.png")));
            gameOver = new PImage(ImageIO.read(getClass().getResource("img/UI/textGameOver.png")));

        } catch (IOException | CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
    }

    private void loadFont() {
        try {
            URL url = getClass().getResource("font/future.vlw");
            URLConnection con = url.openConnection();

            if (con instanceof JarURLConnection) {
                font = loadFont(url.toExternalForm());
            } else {
                font = loadFont(url.getPath());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////draw()///////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////button///////////////////////////////////////////////////////////////////////////////////////////////////
    private void updateBtnStart() {
        // Detect the mouse's location
        btnStart.setMouseOn(mouseX, mouseY);
    }

    private void drawBtnStart() {
        pushStyle();

        // Button's image
        image(btnStart.getImage(), btnStart.getBtnX(), btnStart.getBtnY());

        // Button's text
        fill(164, 124, 0);
        textAlign(CENTER, CENTER);
        textFont(font, 20);
        text(btnStart.getText(), btnStart.getTxtX(), btnStart.getTxtY());

        popStyle();
    }

    private void updateBtnRestart() {
        // Detect the mouse's location
        btnRestart.setMouseOn(mouseX, mouseY);
    }

    private void drawBtnRestart() {
        pushStyle();

        // Button's image
        image(btnRestart.getImage(), btnRestart.getBtnX(), btnRestart.getBtnY());

        // Button's text
        fill(164, 124, 0);
        textAlign(CENTER, CENTER);
        textFont(font, 20);
        text(btnRestart.getText(), btnRestart.getTxtX(), btnRestart.getTxtY());

        popStyle();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////background///////////////////////////////////////////////////////////////////////////////////////////////////////
    private void updateBackground() {
        // Move the backgrounds
        bg1X += speed;
        bg2X += speed;

        bg1X = bg1X <= -width ? width : bg1X;
        bg2X = bg2X <= -width ? width : bg2X;
    }

    private void drawBackground() {
        image(bgs[0], bg1X, 0);
        image(bgs[1], bg2X, 0);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////rock/////////////////////////////////////////////////////////////////////////////////////////////////
    private void updateRocks() {
        // Increase the number of rocks and the speed every 5 seconds
        if (frameCount % 300 == 0) {
            if (rocks.size() < 5) {
                Rock rock = new Rock(width, height);

                rocks.add(rock);
                while (checkRockDistLessThanMinDist(rocks, rock)) {
                    rock.randomAttr(width, height);
                }
            }

            if (speed > -10) {
                speed--;
            }
        }

        // Record the number of the rocks now
        int rockSize = rocks.size();

        // Move the rocks
        for (int i = 0; i < rocks.size(); i++) {
            Rock rock = rocks.get(i);

            rock.setX(rock.getX() + speed);
            rock.updatePeak();
        }

        // If any rock is out of the window, then remove it
        for (int i = 0; i < rocks.size(); i++) {
            Rock rock = rocks.get(i);

            if (rock.getX() + Rock.WIDTH <= 0) {
                rocks.remove(rock);
            }
        }

        // Re-add these removed rocks
        for (int i = 0; i < rockSize - rocks.size(); i++) {
            Rock rock = new Rock(width, height);

            rocks.add(rock);
            while (checkRockDistLessThanMinDist(rocks, rock)) {
                rock.randomAttr(width, height);
            }
        }
    }

    private void drawRocks() {
        for (Rock rock : rocks) {
            image(rock.getImage(), rock.getX(), rock.getY());
        }
    }

    private boolean checkRockDistLessThanMinDist(List<Rock> rocks, Rock rock) {
        final int MIN_DIST = 100;

        Collections.sort(rocks, (Rock o1, Rock o2) -> o1.getX() - o2.getX());

        int frontIndex = rocks.indexOf(rock) - 1;
        if (frontIndex >= 0) {
            if (rock.getPeak().distance(rocks.get(frontIndex).getPeak()) < MIN_DIST) {
                return true;
            }
        }

        int backIndex = rocks.indexOf(rock) + 1;
        if (backIndex < rocks.size()) {
            if (rock.getPeak().distance(rocks.get(backIndex).getPeak()) < MIN_DIST) {
                return true;
            }
        }

        return false;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////ground//////////////////////////////////////////////////////////////////////////////////////////////////
    private void updateGround() {
        for (Ground ground : grounds) {
            // Move the grounds
            ground.setX(ground.getX() + speed);

            // Change the image by level
            if (!(ground.getX() > -width && ground.getX() < width)) {
                ground.updateImage();
            }
        }

        // Background loop
        if (grounds[0].getX() <= -width) {
            grounds[0].setX(grounds[1].getX() + width);
        }

        if (grounds[1].getX() <= -width) {
            grounds[1].setX(grounds[0].getX() + width);
        }
    }

    private void drawGround() {
        for (Ground ground : grounds) {
            image(ground.getImage(), ground.getX(), ground.getY());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////plane/////////////////////////////////////////////////////////////////////////////////////////////////
    private void updatePlane() {
        // Update images
        plane.updatePlaneImg();
        plane.updatePuffImg();

        // Update location
        ySpeed += GRAVITY;
        plane.setY(plane.getY() + ySpeed);

        if (mousePressed) {
            ySpeed += LIFT;
        }
    }

    private void drawPlane() {
        // Draw plane
        image(plane.getPlaneImage(), plane.getX(), plane.getY());

        // Draw puff
        if (mousePressed) {
            image(plane.getPuffImage(), plane.getPuffX(), plane.getPuffY());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////score/////////////////////////////////////////////////////////////////////////////////////////////////
    private void drawCurrentScore() {
        pushStyle();

        // Current score
        fill(255);
        textAlign(RIGHT, BOTTOM);
        textFont(font, 25);
        text(nf(frameCount, 5), width, height);

        // Previous score
        textAlign(LEFT, BOTTOM);
        text(nf(score, 5), 0, height);

        popStyle();
    }

    private void drawFinalScore() {
        pushStyle();

        fill(0);
        textAlign(CENTER, CENTER);
        textFont(font, 30);
        text("Score:" + score, width / 2, height / 2);

        popStyle();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////collision detection/////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean detectCollision(PImage imgA, PImage imgB, int aX, int aY, int bX, int bY) {
        if (aX > bX + imgB.width || aX + imgA.width < bX || aY > bY + imgB.height || aY + imgA.height < bY) {
            return false;
        }

        imgA.loadPixels();
        imgB.loadPixels();

        int leftO = aX > bX ? aX : bX;
        int rightO = aX + imgA.width > bX + imgB.width ? bX + imgB.width : aX + imgA.width;
        int topO = aY > bY ? aY : bY;
        int bottomO = aY + imgA.height > bY + imgB.height ? bY + imgB.height : aY + imgA.height;

        boolean rlt = false;

        outter:
        for (int x = leftO; x < rightO; x++) {
            for (int y = topO; y < bottomO; y++) {
                int pixelAX = x - aX;
                int pixelAY = y - aY;
                int alphaA = (int) alpha(imgA.pixels[pixelAX + pixelAY * imgA.width]);

                int pixelBX = x - bX;
                int pixelBY = y - bY;
                int alphaB = (int) alpha(imgB.pixels[pixelBX + pixelBY * imgB.width]);

                if (alphaA > 10 && alphaB > 10) {
                    rlt = true;
                    break outter;
                }
            }
        }

        return rlt;
    }

    private void detectCollisionWithPlane() {
        // Check collision with rocks
        for (Rock rock : rocks) {
            if (detectCollision(plane.getPlaneImage(), rock.getImage(), (int) plane.getX(), (int) plane.getY(), rock.getX(), rock.getY())) {
                score = frameCount;
                gameState = GAME_OVER;
            }
        }

        // Check collision with ground
        for (Ground ground : grounds) {
            if (detectCollision(plane.getPlaneImage(), ground.getImage(), (int) plane.getX(), (int) plane.getY(), ground.getX(), ground.getY())) {
                score = frameCount;
                gameState = GAME_OVER;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////others/////////////////////////////////////////////////////////////////////////////////////////////////
    private void changeLevel() {
        // Change the rock's and ground's images every 10 seconds
        if (frameCount % 600 == 0) {

            // The level will change from 0 to 3 and change from 3 to 0
            if (isRight) {
                level++;
            } else {
                level--;
            }

            if (level < 0) {
                level = 0;
                isRight = true;
            } else if (level > 3) {
                level = 3;
                isRight = false;
            }
        }
    }

    private void resetGame() {
        frameCount = 0;

        level = 0;
        speed = -5;

        // Backgrounds' location
        bg1X = 0;
        bg2X = width;

        // Grounds's location
        grounds[0].setX(0);
        grounds[1].setX(width);

        // Grounds' images
        for (Ground ground : grounds) {
            ground.updateImage();
        }

        // Rocks' number
        rocks.clear();

        // Plane's speed and location
        ySpeed = 0;
        plane.setY(height / 3);
    }

    private void drawTitle() {
        pushStyle();

        fill(0);
        textAlign(CENTER, CENTER);
        textFont(font, 60);
        text("FlappyPlane", width / 2, height / 5);

        popStyle();
    }

    private void drawGameReady() {
        pushStyle();

        imageMode(CENTER);
        image(getReady, width / 2, height / 4);

        popStyle();
    }

    private void drawTimer() {
        pushStyle();

        fill(0);
        textAlign(CENTER, CENTER);
        textFont(font, 70);
        text(timer / 60 + 1, width / 2, height / 2);

        popStyle();
    }

    private void drawGameOver() {
        pushStyle();

        imageMode(CENTER);
        image(gameOver, width / 2, height / 4);

        popStyle();
    }

    private void showCursor() {
        // Replace the original cursor with the custom images
        if (!mousePressed) {
            cursor(cursors[0], 25, 25);
        } else {
            cursor(cursors[1], 25, 25);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////mouse event//////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void mousePressed() {
        if (gameState == GAME_RUN) {
            plane.setMousePressed(mousePressed);
        }
    }

    @Override
    public void mouseReleased() {
        if (gameState == GAME_RUN) {
            plane.setMousePressed(mousePressed);
        }
    }

    @Override
    public void mouseClicked() {
        switch (gameState) {
            case GAME_START:
                // Handle start button's mouseClicked event
                if (btnStart.isMouseOn()) {
                    gameState = GAME_READY;
                    resetGame();
                }
                break;

            case GAME_OVER:
                // Handle restart button's mouseClicked event
                if (btnRestart.isMouseOn()) {
                    gameState = GAME_READY;
                    resetGame();
                }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////main////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        PApplet.main("tappyplane.Main");
    }
}
