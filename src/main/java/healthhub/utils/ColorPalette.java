package healthhub.utils;

import java.awt.Color;

public class ColorPalette {
    // Brand colors
    public static final Color PRIMARY = new Color(17, 82, 154);      // #11529A - Deep Blue
    public static final Color ACCENT = new Color(41, 111, 187);      // #296FBB - Lighter Blue

    // Backgrounds
    public static final Color BACKGROUND = new Color(250, 250, 250); // #FAFAFA - Off-white
    public static final Color WHITE = Color.WHITE;                   // #FFFFFF

    // Text colors
    public static final Color TEXT_DARK = new Color(0, 0, 0);        // #000000
    public static final Color TEXT_LIGHT = new Color(194, 195, 195); // #C2C3C3
    public static final Color TEXT_MEDIUM = new Color(100, 100, 100); // For descriptions

    // Status colors (for later use in dashboard/reports)
    public static final Color SUCCESS = new Color(56, 161, 105);     // Green
    public static final Color WARNING = new Color(237, 137, 54);     // Orange
    public static final Color DANGER = new Color(229, 62, 62);       // Red
}