package com.pronsky.tickets.utils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FileUtil {
    public void saveFile(String fileUrl, String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(fileUrl).openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boolean hasBom = checkForBom(content.toString());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            if (hasBom) {
                writer.write(content.toString(), 1, content.length() - 1);
            } else {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(String filename) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.matches("^.*name.*$")) {
                    builder.append(line).append("\n");
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private boolean checkForBom(String content) {
        return content.length() > 0 && content.charAt(0) == 0xFEFF;
    }

}
