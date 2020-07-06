package morse.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ReadFileUtil {
    public static String read(String file) {
        try (BufferedReader reader = getBufferedReader(file)) {
            return readAll(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedReader getBufferedReader(String file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    private static String readAll(BufferedReader reader) {
        return reader.lines().collect(Collectors.joining(" "));
    }
}
