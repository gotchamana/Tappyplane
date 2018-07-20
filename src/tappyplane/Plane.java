/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tappyplane;

import java.io.IOException;
import javax.imageio.ImageIO;
import processing.core.PImage;

/**
 *
 * @author shootingstar
 */
public class Plane {

    // Store the images
    static private PImage[] planes = new PImage[3];
    static private PImage[] puffs = new PImage[2];

    static {
        // Load the images
        try {
            for (int i = 0; i < planes.length; i++) {
                planes[i] = new PImage(ImageIO.read(Plane.class.getResource("img/Planes/planeYellow" + (i + 1) + ".png")));
            }

            puffs[0] = new PImage(ImageIO.read(Plane.class.getResource("img/Puffs/puffSmall.png")));
            puffs[1] = new PImage(ImageIO.read(Plane.class.getResource("img/Puffs/puffLarge.png")));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Plane's location
    private float x, y;

    // For changing the images
    private int frameCount;

    // Image arrays' indexes
    private int planeIndex, puffIndex;

    // Detect the mouse is pressed
    private boolean isMousePressed;

    public Plane(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void updatePlaneImg() {
        if (isMousePressed) {
            if (frameCount % 10 == 0) {
                planeIndex++;
                planeIndex %= 3;
            }

            frameCount++;
        }
    }

    public void updatePuffImg() {
        if (isMousePressed) {
            if (frameCount % 15 == 0) {
                puffIndex++;
                puffIndex %= 2;
            }
        } else {
            frameCount = 0;
            puffIndex = 0;
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getPuffX() {
        return x - puffs[puffIndex].width;
    }

    public float getPuffY() {
        return y + planes[planeIndex].height / 2 - puffs[puffIndex].height / 2;
    }

    public void setMousePressed(boolean isMousePressed) {
        this.isMousePressed = isMousePressed;
    }

    public PImage getPlaneImage() {
        return planes[planeIndex];
    }

    public PImage getPuffImage() {
        return puffs[puffIndex];
    }
}
