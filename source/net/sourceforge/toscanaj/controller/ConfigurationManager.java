package net.sourceforge.toscanaj.controller;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.JFrame;

/**
 * Handles all persistent configuration information.
 *
 * @TODO document
 */
public class ConfigurationManager {
    /**
     * Stores the data we manage.
     */
    static private Properties properties = new Properties();

    /**
     * Initialization code.
     */
    static {
        try {
            FileInputStream in = new FileInputStream("toscanaj.prop");
            properties.load(in);
            in.close();
        }
        catch(FileNotFoundException e) {
            // we will just use the defaults
        }
        catch(Exception e) {
            // nothing we can do here, just print the stack trace
            e.printStackTrace();
        }
    }

    /**
     * No public instances allowed.
     */
    private ConfigurationManager() {
    }

    /**
     * Saves the current configuration.
     */
    static public void saveConfiguration() {
        try {
            FileOutputStream out = new FileOutputStream("toscanaj.prop");
            properties.store(out, "--- ToscanaJ settings ---");
            out.close();
        }
        catch(Exception e) {
            // nothing useful we can do here, just print the stack trace
            e.printStackTrace();
        }
    }

    /**
     * Stores the size and position of a window.
     */
    static public void storePlacement(String section, JFrame window) {
        properties.setProperty(section + "-x", String.valueOf(window.getX()));
        properties.setProperty(section + "-y", String.valueOf(window.getY()));
        properties.setProperty(section + "-width", String.valueOf(window.getWidth()));
        properties.setProperty(section + "-height", String.valueOf(window.getHeight()));
    }

    /**
     * Restores the size and position of a window.
     *
     * If the configuration could not be found or is broken this will do nothing.
     */
    static public void restorePlacement(String section, JFrame window, Rectangle defaultPlacement) {
        try {
            int x = Integer.parseInt(properties.getProperty(section+"-x"));
            int y = Integer.parseInt(properties.getProperty(section+"-y"));
            int w = Integer.parseInt(properties.getProperty(section+"-width"));
            int h = Integer.parseInt(properties.getProperty(section+"-height"));
            window.setBounds(x,y,w,h);
        }
        catch(NumberFormatException e) {
            // use default
            window.setBounds(defaultPlacement);
        }
    }

    /**
     * Stores an int value.
     */
    static public void storeInt(String section, String key, int value) {
        properties.setProperty(section + "-" + key, String.valueOf(value));
    }

    /**
     * Retrieves an int value.
     */
    static public int fetchInt(String section, String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(section + "-" + key));
        }
        catch(NumberFormatException e) {
            return defaultValue;
        }
    }
}