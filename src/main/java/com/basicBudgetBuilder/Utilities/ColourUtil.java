package com.basicBudgetBuilder.Utilities;

/**
 * Created by Hanzi Jing on 9/04/2017.
 */

import java.awt.*;

public class ColourUtil {
    public static String getHTMLColorString(Color color) {
        if (color == null){
            return null;
        }
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
