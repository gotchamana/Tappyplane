/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tappyplane;

import java.awt.Point;
import java.io.IOException;
import javax.imageio.ImageIO;
import processing.core.PImage;

/**
 *
 * @author shootingstar
 */
public class Rock {

    // The rock image's width and height
    static final int WIDTH = 110, HEIGHT = 240;

    // Store the rock images
    static private PImage[] rocks = new PImage[8];

    static {
        // Load the images
        try {
            for (int i = 0; i < rocks.length; i++) {
                rocks[i] = new PImage(ImageIO.read(Rock.class.getResource("img/Rocks/rock" + i + ".png")));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // The rock's location
    private int x, y;

    // The rock image's direction
    private boolean isDown;

    // The rock's peak
    private Point peak = new Point();

    // The rock's image
    private PImage image;

    public Rock(int windowW, int windowH) {
        randomAttr(windowW, windowH);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isDown() {
        return isDown;
    }

    public void setDown(boolean isDown) {
        this.isDown = isDown;
    }

    public void updatePeak() {
        int tmpX = x + WIDTH / 2;
        int tmpY = isDown ? y + HEIGHT : y;

        peak.setLocation(tmpX, tmpY);
    }

    public Point getPeak() {
        return peak;
    }

    public void randomAttr(int windowW, int windowH) {
        isDown = Math.random() < 0.5;
        x = (int) (Math.random() * (windowW - WIDTH) + windowW);
        y = isDown ? (int) (Math.random() * HEIGHT / 2 - HEIGHT / 2) : (int) (Math.random() * HEIGHT / 2 + windowH - HEIGHT);
        image = isDown ? rocks[Main.level * 2 + 1] : rocks[Main.level * 2];

        updatePeak();
    }

    public PImage getImage() {
        return image;
    }
}
