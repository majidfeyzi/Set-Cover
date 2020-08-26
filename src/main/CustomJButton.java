package main;

import javax.swing.*;
import java.awt.*;

/**
 * JButton with custom background color.
 * @author Majid Feyzi
 * */
public class CustomJButton extends JButton {

    // Custom background color
    private Color backgroundColor;

    public CustomJButton(String title) {
        super(title);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isPressed())
            g.setColor(backgroundColor.darker());
        else
            g.setColor(backgroundColor);
        g.fillRect(0,0,getWidth(),getHeight());
        super.paintComponent(g);
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
