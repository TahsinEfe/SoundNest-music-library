package com.soundnest.soundnest.API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class API {
    private static final String API_KEY = "AIzaSyDyWdvU3qhy5JAKRjN4YECrbg0yEyeiXYM";

    public API() {
    }

    public static boolean isVideoEmbeddable(String videoId) {
        String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id=" + videoId + "&key=AIzaSyDyWdvU3qhy5JAKRjN4YECrbg0yEyeiXYM";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("GET request failed. Response Code: " + responseCode);
                return false;
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();

                String inputLine;
                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                String jsonResponse = response.toString();
                return jsonResponse.contains("\"embeddable\":true");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        String videoId = "dQw4w9WgXcQ";
        boolean embeddable = isVideoEmbeddable(videoId);
        if (embeddable) {
            System.out.println("The video is embeddable.");
        } else {
            System.out.println("The video is NOT embeddable.");
        }

    }
}