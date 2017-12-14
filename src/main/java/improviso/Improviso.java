package improviso;

/**
 * Main class of the program, which is responsible for generating the
 * composition from the input XML file and either playing the resulting MIDI
 * or outputting it as a file, depending on the parameters.
 * @author Fernie Canto
 */
public class Improviso {

    /**
     * @param args the command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String newline = System.getProperty("line.separator");

        if (args.length >= 1) {
            try {
                XMLCompositionParser parser = new XMLCompositionParser(args[0]);
                Composition composition = parser.processXML();
                MIDIGenerator generator = new MIDIGenerator();
                composition.execute(generator);
                if (args.length == 1) {
                    generator.play();
                } else {
                    generator.generateFile(args[1]);
                }
            } catch (ImprovisoException e) {
                System.out.println("Error in composition file:" + newline + e.getMessage() + newline
                        + "Check your composition file and try again.");
            } catch (javax.xml.parsers.ParserConfigurationException e) {
                System.out.println("XML error:" + newline + e.getMessage());
            } catch (java.io.IOException e) {
                System.out.println("I/O error:" + newline + e.getMessage() + newline
                        + "Check the execution parameters and try again.");
            } catch (org.xml.sax.SAXException e) {
                System.out.println("XML parse error:" + newline + e.getMessage() + newline
                        + "Check your composition file and try again.");
            } catch (javax.sound.midi.InvalidMidiDataException e) {
                System.out.println("Invalid MIDI data error:" + newline + e.getMessage() + newline
                        + "Check your composition file and try again.");
            }
        } else {
            System.out.println(
                    "Improviso" + newline
                    + "Usage: improviso [composition_file].xml [MIDI_file].mid");
        }

    }
}
