package main;

import java.awt.*;

/**
 * Configuration class of main package.
 * Configuration of main part of program that contains frames of program is keeping in this class
 * and you can config everything you want or use default values.
 * @author Majid Feyzi
 * */
public class Config {

    // Width and height of frame with ratio 16:9
    public final static int FRAME_WIDTH = 1024;
    public final static int FRAME_HEIGHT = FRAME_WIDTH / 16 * 9;

    // Font name and font size of buttons
    public final static String FONT_NAME = "Arial";
    public final static int FONT_SIZE = 14;

    // Widths of left and right panels in main frame
    public final static int LEFT_PANEL_WIDTH = 180;
    public final static int RIGHT_PANEL_WIDTH = 150;

    // Widths and heights of buttons in program
    public final static int BUTTON_WIDTH = 150;
    public final static int BUTTON_HEIGHT = 45;

    // Color of theme
    public final static Color THEME_COLOR = Color.decode("#6AC1D9");
}
