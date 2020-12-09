package fr.pacifista.bot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileActions {

    public static String getFileContent(File file) throws IOException {
        if (!file.exists())
            throw new IOException("The file is not created");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        if (fis.read(data) == -1)
            throw new IOException("Error while reading file");
        String fileContent = new String(data, StandardCharsets.UTF_8);
        fis.close();
        return fileContent;
    }

    public static void writeInFile(File file, String toWrite, boolean append) throws IOException {
        FileWriter fw = new FileWriter(file.getAbsoluteFile(), append);
        fw.write(toWrite);
        fw.close();
    }

}
