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
public class Ground {
    
    // Store the ground's image
    static private PImage[] grounds = new PImage[4];

    static {
        try {
            // Load the images
            for (int i = 0; i < grounds.length; i++) {
                grounds[i] = new PImage(ImageIO.read(Ground.class.getResource("img/Grounds/ground" + i + ".png")));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    // The ground's location
    private int x, y;
    
    // The ground's image
    private PImage image;

    public Ground(int x, int y) {
        this.x = x;
        this.y = y;

        image = grounds[Main.level];
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

    public void updateImage() {
        image = grounds[Main.level];
    }

    public PImage getImage() {
        return image;
    }
}
