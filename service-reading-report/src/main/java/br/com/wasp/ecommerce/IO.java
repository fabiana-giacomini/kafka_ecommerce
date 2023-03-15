package br.com.wasp.ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class IO {

    public static void copyTo(Path source, File target) throws IOException {
        target.getParentFile().mkdirs();
        Files.copy(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void append(File target, String content) throws IOException {
        Files.write(target.toPath(), content.getBytes(), StandardOpenOption.APPEND);
    }
}
