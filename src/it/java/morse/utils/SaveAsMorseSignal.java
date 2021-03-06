package morse.utils;

import morse.utils.statistics.Range;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SaveAsMorseSignal {
    private static final Map<Character, String> MORSE = new HashMap<>();
    private static final String UP = "1";
    private static final String DOWN = "0";
    private static final String NEW_LINE = ",\n";
    private static final char SPACE = ' ';
    private static final String EOF = ".-.-.-";

    static {
        MORSE.put('a', ".-");
        MORSE.put('b', "-...");
        MORSE.put('c', "-.-.");
        MORSE.put('d', "-..");
        MORSE.put('e', ".");
        MORSE.put('f', "..-.");
        MORSE.put('g', "--.");
        MORSE.put('h', "....");
        MORSE.put('i', "..");
        MORSE.put('j', ".---");
        MORSE.put('k', "-.-");
        MORSE.put('l', ".-..");
        MORSE.put('m', "--");
        MORSE.put('n', "-.");
        MORSE.put('o', "---");
        MORSE.put('p', ".--.");
        MORSE.put('q', "--.-");
        MORSE.put('r', ".-.");
        MORSE.put('s', "...");
        MORSE.put('t', "-");
        MORSE.put('u', "..-");
        MORSE.put('v', "...-");
        MORSE.put('w', ".--");
        MORSE.put('x', "-..-");
        MORSE.put('y', "-.--");
        MORSE.put('z', "--..");
        MORSE.put('1', ".----");
        MORSE.put('2', "..---");
        MORSE.put('3', "...--");
        MORSE.put('4', "....-");
        MORSE.put('5', ".....");
        MORSE.put('6', "-....");
        MORSE.put('7', "--...");
        MORSE.put('8', "---..");
        MORSE.put('9', "----.");
        MORSE.put('0', "-----");
    }

    private Range<Integer> shortRange = new Range<>(4, 6);
    private Range<Integer> longRange = new Range<>(11, 15);
    private Range<Integer> veryLongRange = new Range<>(28, 36);

    private Random random = new Random();

    public void saveLine(String message, BufferedWriter writer) throws IOException {
        boolean isAfterChar = false;
        for (char c : message.toCharArray()) {
            if (c == SPACE) {
                isAfterChar = false;
                writer.write(wordSeparation().append(NEW_LINE).toString());
            } else {
                if (isAfterChar) {
                    writer.write(letterSeparation().append(NEW_LINE).toString());
                }
                isAfterChar = true;
                writer.write(toMorse(c).append(NEW_LINE).toString());
            }
        }
        writer.write(letterSeparation().append(NEW_LINE).toString());
        writer.write(eof().toString());
    }

    private StringBuilder letterSeparation() {
        return getSignalLine(DOWN, longRange);
    }

    private StringBuilder wordSeparation() {
        return getSignalLine(DOWN, veryLongRange);
    }

    private StringBuilder eof() {
        return morseToSignal(EOF);
    }

    private StringBuilder toMorse(char character) {
        String morse = MORSE.get(character);
        if (morse != null) {
            return morseToSignal(morse);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private StringBuilder morseToSignal(String morse) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (char m : morse.toCharArray()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(NEW_LINE);
                sb.append(getSignalLine(DOWN, shortRange));
                sb.append(NEW_LINE);
            }
            if (m == '.') {
                sb.append(getSignalLine(UP, shortRange));
            } else {
                sb.append(getSignalLine(UP, longRange));
            }
        }
        return sb;
    }

    private StringBuilder getSignalLine(String value, Range<Integer> range) {
        StringBuilder signalLine = new StringBuilder(value);
        int signalLength = signalLength(range);
        for (int i = 1; i < signalLength; i++) {
            signalLine.append(", ");
            signalLine.append(value);
        }
        return signalLine;
    }

    private int signalLength(Range<Integer> range) {
        return range.getFrom() + random.nextInt(range.getTo() - range.getFrom() + 1);
    }
}
