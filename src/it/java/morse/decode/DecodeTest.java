package morse.decode;

import com.intuit.karate.KarateOptions;
import com.intuit.karate.junit4.Karate;
import morse.utils.SaveAsMorseSignal;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RunWith(Karate.class)
@KarateOptions(tags = {"~@ignore"})
public class DecodeTest {
    // @BeforeClass
    public static void doBefore() throws IOException {
        Path resourceDirectory = Paths.get("src", "it", "java", "morse", "decode", "mocks");
        SaveAsMorseSignal asMorseSignal = new SaveAsMorseSignal();
        try (
                BufferedReader reader = getBufferedReader(resourceDirectory.resolve("longMessage.txt"));
                BufferedWriter writer = getBufferedWriter(resourceDirectory.resolve("longSignal.json"))) {
            writer.write("[\n");
            asMorseSignal.save(reader, writer);
            writer.write(asMorseSignal.eof() + "\n");
            writer.write("]");
        }
    }

    private static BufferedWriter getBufferedWriter(Path path) throws IOException {
        return new BufferedWriter(new FileWriter(path.toFile()));
    }

    private static BufferedReader getBufferedReader(Path path) throws FileNotFoundException {
        return new BufferedReader(new FileReader(path.toFile()));
    }
}
