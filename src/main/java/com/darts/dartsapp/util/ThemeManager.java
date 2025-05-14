package com.darts.dartsapp.util;

import javafx.scene.Scene;

public class ThemeManager {

    private static boolean darkMode = false;

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean enabled) {
        darkMode = enabled;
    }

    public static void applyTheme(Scene scene) {
        if (scene == null) return;

        scene.getStylesheets().clear();
        String theme = darkMode ? "dark.css" : "light.css";
        var url = ThemeManager.class.getResource("/themes/" + theme);

        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
        } else {
            System.err.println("Theme file not found: " + theme);
        }
    }
}
