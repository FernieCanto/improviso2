/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.gui;

import improviso.*;

import improviso.ImprovisoException;
import improviso.XMLCompositionParser;
import java.io.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author User
 */
public class CompositionController {
    MidiDevice midiDevice;
    Composition composition;
    
    public void importXML(String filename)
            throws ImprovisoException, ParserConfigurationException, SAXException, IOException {
        XMLCompositionParser parser = new XMLCompositionParser(filename);

        composition = parser.processXML();
    }
    
    public void playComposition() throws InvalidMidiDataException, ImprovisoException, IOException, MidiUnavailableException {
        MIDIGenerator generator = new MIDIGenerator(midiDevice);
        composition.execute(generator);
        generator.play();
    }
    
    public boolean isCompositionLoaded() {
        return composition != null;
    }

    public String[] getSectionList() {
        return composition.getSectionIds();
    }
    
    public String[] getTrackList(String sectionId) {
        Section section = composition.getSection(sectionId);
        return section.getTrackIds();
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
        MIDIGenerator generator = new MIDIGenerator(midiDevice);
        composition.executeSection(generator, sectionId);
        generator.play();
    }

    void playSectionRealTime(String sectionId) throws InvalidMidiDataException, ImprovisoException, IOException, MidiUnavailableException {
        MIDIGenerator generator = new MIDIGenerator(midiDevice);
        composition.executeSection(generator, sectionId);
        generator.playSequenceRealTime();
    }

    void saveComposition(String absolutePath) throws FileNotFoundException, IOException {
        FileOutputStream outputStream = new FileOutputStream(absolutePath);
        BufferedOutputStream bufferedStream = new BufferedOutputStream(outputStream);
        try (ObjectOutputStream objectStream = new ObjectOutputStream(bufferedStream)) {
            objectStream.writeObject(this.composition);
        }
    }

    void openComposition(String absolutePath) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream inputStream = new FileInputStream(absolutePath);
        BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
        try (ObjectInputStream objectStream = new ObjectInputStream(bufferedStream)) {
            this.composition = (Composition)objectStream.readObject();
        }
    }

    void setDeviceInfo(MidiDevice.Info midiInfo) throws MidiUnavailableException {
        this.midiDevice = MidiSystem.getMidiDevice(midiInfo);
    }
}
