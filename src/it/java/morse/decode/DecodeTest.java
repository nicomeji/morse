package morse.decode;

import com.intuit.karate.KarateOptions;
import com.intuit.karate.junit4.Karate;
import morse.utils.SaveAsMorseSignal;
import org.junit.runner.RunWith;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static morse.utils.ReadFileUtil.read;

@RunWith(Karate.class)
@KarateOptions(tags = { "~@ignore" })
public class DecodeTest {
    // @BeforeClass
    public static void doBefore() throws IOException {
        Path resourceDirectory = Paths.get("src", "it", "java", "morse", "decode", "mocks");
        SaveAsMorseSignal asMorseSignal = new SaveAsMorseSignal();
        try (BufferedWriter writer = getBufferedWriter(resourceDirectory.resolve("longSignal.json"))) {
            writer.write("[\n");
            asMorseSignal.saveLine(read(resourceDirectory.resolve("longMessage.txt").toString()), writer);
            writer.write(asMorseSignal.eof() + "\n");
            writer.write("]");
        }
    }

    private static BufferedWriter getBufferedWriter(Path path) throws IOException {
        return new BufferedWriter(new FileWriter(path.toFile()));
    }
}
