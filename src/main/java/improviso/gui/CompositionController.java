/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.gui;

import improviso.*;

import improviso.ImprovisoException;
import improviso.XMLCompositionParser;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author User
 */
public class CompositionController {
    Composition composition;
    public void openComposition(String filename)
            throws ImprovisoException, ParserConfigurationException, SAXException, IOException {
        XMLCompositionParser parser = new XMLCompositionParser(filename);

        this.composition = parser.processXML();
    }
    
    public void playComposition() throws InvalidMidiDataException, ImprovisoException, IOException, MidiUnavailableException {
        MIDIGenerator generator = new MIDIGenerator();
        composition.execute(generator);
        generator.play();
    }
    
    public boolean isCompositionLoaded() {
        return this.composition != null;
    }

    public String[] getSectionList() {
        return this.composition.getSectionIds();
    }

    SectionConfiguration getSectionConfiguration(String selectedValue) {
        SectionConfiguration configuration = new SectionConfiguration();
        this.composition.getSection(selectedValue).accept(configuration);
        return configuration;
    }

    void applyChangesToSection(String selectedValue, SectionConfiguration config) {
        this.composition.addSection(selectedValue, config.buildSection());
    }

    public void playSection(String sectionId) throws InvalidMidiDataException, ImprovisoException, IOException, MidiUnavailableException {
        MIDIGenerator generator = new MIDIGenerator();
        composition.executeSection(generator, sectionId);
        generator.play();
    }
}
