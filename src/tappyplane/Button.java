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
public class Button {

    private static PImage[] buttons = new PImage[2];

    static {
        try {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i] = new PImage(ImageIO.read(Button.class.getResource("img/UI/button" + i + ".png")));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private int x, y;
    public static final int WIDTH = 190, HEIGHT = 49;
    private boolean isMouseOn, isMousePressed;
    private String text;

    public Button(int x, int y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public int getBtnX() {
        return x;
    }

    public int getBtnY() {
        return y;
    }

    public int getTxtX() {
        return x + WIDTH / 2;
    }

    public int getTxtY() {
        return y + HEIGHT / 2 - 5;
    }

    public String getText() {
        return text;
    }

    public PImage getImage() {
        if (isMouseOn) {
            return buttons[1];
        } else {
            return buttons[0];
        }
    }

    public boolean isMouseOn() {
        return isMouseOn;
    }

    public void setMouseOn(int mouseX, int mouseY) {
        isMouseOn = mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + HEIGHT;
    }

    public boolean isMousePressed() {
        return isMousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        isMousePressed = mousePressed;
    }
}
