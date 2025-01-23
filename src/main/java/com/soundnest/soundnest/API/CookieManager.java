package com.soundnest.soundnest.API;

import javafx.scene.web.WebEngine;

import java.io.*;

public class CookieManager {
    private static final String COOKIE_FILE = "cookies.ser";

    public CookieManager() {
    }

    public static void saveCookies(WebEngine webEngine) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("cookies.ser"))) {
            String cookies = (String)webEngine.executeScript("document.cookie");
            oos.writeObject(cookies);
            System.out.println("Cookies saved successfully: " + cookies);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadCookies(WebEngine webEngine) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("cookies.ser"))) {
            String cookies = (String)ois.readObject();
            webEngine.executeScript("document.cookie = '" + cookies + "';");
            System.out.println("Cookies loaded successfully: " + cookies);
        } catch (ClassNotFoundException | IOException e) {
            ((Exception)e).printStackTrace();
        }

    }
}
