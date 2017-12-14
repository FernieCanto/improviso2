package improviso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Fernie Canto
 */
public class StringInterpreter {

    /**
     * Regular expression for durations in beats and ticks (e.g. "2:060").
     */
    protected static Pattern beatsPlusTicksPattern = Pattern.compile("^(?<seminimas>\\d+):(?<ticks>\\d\\d\\d)$");
    /**
     * Regular expression for percentages, (e.g. "55%", "20.5%", ".3").
     */
    protected static Pattern floatPercentPattern = Pattern.compile("^(?<val>(\\d+)|(\\d+\\.\\d+)|(\\.\\d+))(?<percent>%)?$");
    /**
     * Regular expression for durations in bars (e.g. "
     */
    protected static Pattern timeSignaturesPattern = Pattern.compile("((?<quant>\\d+)\\s)?(?<num>\\d+)/(?<denom>\\d+)\\s?");
    /**
     * Regular expression for integer ranges (e.g. "50", "100-200").
     */
    protected static Pattern numericIntervalPattern = Pattern.compile("^(?<val>\\d+)(-(?<valMax>\\d+))?$");

    /**
     * Parses a string as a percentage, returning the float value.
     *
     * @param valueString
     * @return Parsed value
     * @throws ImprovisoException
     */
    public static double parseFloatPercentage(String valueString) throws ImprovisoException {
        double value;
        Matcher m = StringInterpreter.floatPercentPattern.matcher(valueString);
        if (m.matches()) {
            value = Double.parseDouble(m.group("val"));
            if (m.group("percent") != null) {
                value /= 100.0;
            }
            return value;
        } else {
            throw new ImprovisoException("Invalid float value: " + valueString);
        }
    }

    /**
     * Interprets a double interval String of the kind "A - B | C - D", where A,
     * B, C and D are double values or percentages, and generates a
     * DoubleInterval.
     *
     * @param intervalString The String to be read
     * @return The corresponding DoubleInterval
     * @throws improviso.ImprovisoException
     */
    public static DoubleRange createDoubleInterval(String intervalString) throws ImprovisoException {
        double minVal;
        double maxVal;
        double minEndVal;
        double maxEndVal;
        try {
            String[] beginEnd = intervalString.split(" \\| ");
            String[] minMax = beginEnd[0].split(" - ");
            minVal = StringInterpreter.parseFloatPercentage(minMax[0]);
            if (minMax.length > 1) {
                maxVal = StringInterpreter.parseFloatPercentage(minMax[1]);
            } else {
                maxVal = minVal;
            }
            if (beginEnd.length > 1) {
                String[] minMaxEnd = beginEnd[1].split(" - ");
                minEndVal = StringInterpreter.parseFloatPercentage(minMaxEnd[0]);
                if (minMaxEnd.length > 1) {
                    maxEndVal = StringInterpreter.parseFloatPercentage(minMaxEnd[1]);
                } else {
                    maxEndVal = minEndVal;
                }
            } else {
                minEndVal = minVal;
                maxEndVal = maxVal;
            }
            return new DoubleRange(minVal, maxVal, minEndVal, maxEndVal);
        } catch (NumberFormatException e) {
            ImprovisoException exception = new ImprovisoException("Invalid length: " + intervalString);
            exception.addSuppressed(e);
            throw exception;
        }
    }

    /**
     * Reads a length or position string, of any form, and returns the
     * corresponding length in ticks.
     *
     * @param lengthString The String to be read
     * @return The corresponding length or position in ticks
     * @throws ImprovisoException
     */
    public static int parseLength(String lengthString) throws ImprovisoException {
        int ticks = 0;
        try {
            Matcher m = StringInterpreter.beatsPlusTicksPattern.matcher(lengthString);
            if (m.matches()) {
                ticks += Integer.parseInt(m.group("seminimas")) * Composition.TICKS_WHOLENOTE / 4;
                ticks += Integer.parseInt(m.group("ticks"));
                return ticks;
            }
            Matcher m2 = StringInterpreter.timeSignaturesPattern.matcher(lengthString);
            if (m2.find()) {
                do {
                    int quant = 1;
                    int numerator;
                    int denominator;
                    if (m2.group("quant") != null) {
                        quant = Integer.parseInt(m2.group("quant"));
                    }
                    numerator = Integer.parseInt(m2.group("num"));
                    denominator = Integer.parseInt(m2.group("denom"));
                    ticks += quant * numerator * (Composition.TICKS_WHOLENOTE / denominator);
                } while (m2.find());
                return ticks;
            }
            ticks = Integer.parseInt(lengthString);
            return ticks;
        } catch (NumberFormatException e) {
            ImprovisoException exception = new ImprovisoException("Invalid length: " + lengthString);
            exception.addSuppressed(e);
            throw exception;
        }
    }

    /**
     * Interprets a length or position interval String of the kind "A - B | C -
     * D", where A, B, C and D are lengths or positions, and generates a
     * NumericInterval in ticks.
     *
     * @param intervalString The String to be read
     * @return The corresponding NumericInterval
     * @throws improviso.ImprovisoException
     */
    public static IntegerRange createLengthInterval(String intervalString) throws ImprovisoException {
        int minVal;
        int maxVal;
        int minEndVal;
        int maxEndVal;
        try {
            String[] beginEnd = intervalString.split(" \\| ");
            String[] minMax = beginEnd[0].split(" - ");
            minVal = parseLength(minMax[0]);
            if (minMax.length > 1) {
                maxVal = parseLength(minMax[1]);
            } else {
                maxVal = minVal;
            }
            if (beginEnd.length > 1) {
                String[] minMaxEnd = beginEnd[1].split(" - ");
                minEndVal = parseLength(minMaxEnd[0]);
                if (minMaxEnd.length > 1) {
                    maxEndVal = parseLength(minMaxEnd[1]);
                } else {
                    maxEndVal = minEndVal;
                }
            } else {
                minEndVal = minVal;
                maxEndVal = maxVal;
            }
            return new IntegerRange(minVal, maxVal, minEndVal, maxEndVal);
        } catch (NumberFormatException e) {
            ImprovisoException exception = new ImprovisoException("Invalid length: " + intervalString);
            exception.addSuppressed(e);
            throw exception;
        }
    }

    /**
     * Interprets a numeric interval String of the kind "A - B | C - D", where
     * A, B, C and D are integer numbers, and generates a NumericInterval from
     * it.
     *
     * @param intervalString The String to be read
     * @return The corresponding NumericInterval
     * @throws improviso.ImprovisoException
     */
    public static IntegerRange createNumericInterval(String intervalString) throws ImprovisoException {
        int minVal;
        int maxVal;
        int minEndVal;
        int maxEndVal;
        try {
            String[] beginEnd = intervalString.split(" \\| ");
            String[] minMax = beginEnd[0].split(" - ");
            minVal = Integer.parseInt(minMax[0]);
            if (minMax.length > 1) {
                maxVal = Integer.parseInt(minMax[1]);
            } else {
                maxVal = minVal;
            }
            if (beginEnd.length > 1) {
                String[] minMaxEnd = beginEnd[1].split(" - ");
                minEndVal = Integer.parseInt(minMaxEnd[0]);
                if (minMaxEnd.length > 1) {
                    maxEndVal = Integer.parseInt(minMaxEnd[1]);
                } else {
                    maxEndVal = minEndVal;
                }
            } else {
                minEndVal = minVal;
                maxEndVal = maxVal;
            }
            return new IntegerRange(minVal, maxVal, minEndVal, maxEndVal);
        } catch (NumberFormatException e) {
            ImprovisoException exception = new ImprovisoException("Invalid values: " + intervalString);
            exception.addSuppressed(e);
            throw exception;
        }
    }
}
