/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import improviso.mocks.*;
import java.io.IOException;
import java.util.Random;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class CompositionTest {
    SectionMock section1;
    SectionMock section2;
    
    @Before
    public void SetUp() {
        section1 = new SectionMock("section1");
        section1.setTempo(111);
        section1.setTimeSignature(5, 8);
        MIDINote[] notes1 = {
            new MIDINote(20, 0, 100, 100, 1),
            new MIDINote(25, 100, 100, 100, 1),
            new MIDINote(30, 200, 100, 100, 1),
            new MIDINote(40, 300, 100, 100, 1),
        };
        section1.setNotes(notes1);
        section1.setActualEnd(400);
        
        section2 = new SectionMock("section2");
        section2.setTempo(222);
        section2.setTimeSignature(11, 16);
        MIDINote[] notes2 = {
            new MIDINote(20, 0, 100, 100, 1),
            new MIDINote(25, 100, 100, 100, 1),
            new MIDINote(30, 200, 100, 100, 1),
            new MIDINote(40, 300, 100, 100, 1),
            new MIDINote(45, 400, 100, 100, 1),
            new MIDINote(50, 500, 100, 100, 1),
        };
        section2.setNotes(notes2);
        section2.setActualEnd(800);
    }
    
    @Test
    public void testBeatsAndTicks() {
        assertEquals("2:40", Composition.showBeatsAndTicks(280));
    }
    
    @Test(expected = ImprovisoException.class)
    public void testCreateComposition() throws ImprovisoException, InvalidMidiDataException, IOException, MidiUnavailableException {
        Composition composition = new Composition(100);
        composition.execute(new MIDIGeneratorMock());
    }
    
    @Test
    public void testSeed() throws ImprovisoException, InvalidMidiDataException, IOException, MidiUnavailableException {
        Composition composition = new Composition(100, 12345L);
        Random random = new Random();
        random.setSeed(12345L);
        assertEquals(random.nextDouble(), composition.getRandom().nextDouble(), 0.01d);
    }
    
    @Test(expected = ImprovisoException.class)
    public void testAddWrongArrow() throws ImprovisoException, InvalidMidiDataException, IOException, MidiUnavailableException {
        Composition composition = new Composition(100, 12345L);
        composition.addArrow("nowhere", new Arrow.ArrowBuilder().setDestinationSection("alsoNowhere").build());
    }
    
    @Test
    public void testCompositionOneSection() throws InvalidMidiDataException, ImprovisoException, IOException, MidiUnavailableException {
        Composition composition = new Composition(100);
        composition.addMIDITrack(new MIDITrack(1, 0, 100, 64));
        composition.addSection("section1", this.section1);
        
        MIDIGeneratorMock generator = new MIDIGeneratorMock();
        composition.execute(generator);
        
        assertEquals(1, generator.getResultingMIDITracks().size());
        assertEquals(1, generator.getResultingMIDITracks().get(0).getChannel());
        assertEquals(0, generator.getResultingMIDITracks().get(0).getInstrument());
        assertEquals(100, generator.getResultingMIDITracks().get(0).getVolume());
        assertEquals(64, generator.getResultingMIDITracks().get(0).getPan());
        assertEquals(111, (int)generator.getTempos().get(0));
        assertEquals(5, (int)generator.getTimeSignatureNumerators().get(0));
        assertEquals(8, (int)generator.getTimeSignatureDenominators().get(0));
        assertEquals(100, (long)generator.getTicks().get(0));
        
        assertEquals(1, generator.getNoteLists().size());
        assertEquals(4, generator.getNoteLists().get(0).size());
        assertEquals(100, generator.getNoteLists().get(0).get(0).getStart());
        assertEquals(200, generator.getNoteLists().get(0).get(1).getStart());
        assertEquals(300, generator.getNoteLists().get(0).get(2).getStart());
        assertEquals(400, generator.getNoteLists().get(0).get(3).getStart());
    }
    
    @Test
    public void testCompositionTwoSections() throws ImprovisoException, InvalidMidiDataException, IOException, MidiUnavailableException {
        Composition composition = new Composition(100);
        composition.addMIDITrack(new MIDITrack(1, 0, 100, 64));
        composition.addSection("section2", this.section2);
        composition.addSection("section1", this.section1);
        
        composition.addArrow(null, new Arrow.ArrowBuilder()
                .setDestinationSection("section1")
                .setMaxExecutions(1)
                .build()
        );
        composition.addArrow("section1", new Arrow.ArrowBuilder()
                .setDestinationSection("section2")
                .setMaxExecutions(1)
                .build()
        );
        composition.addArrow("section2", new Arrow.ArrowBuilder()
                .setDestinationSection("section1")
                .setMaxExecutions(1)
                .build()
        );
        
        MIDIGeneratorMock generator = new MIDIGeneratorMock();
        composition.execute(generator);
        
        assertEquals(1, generator.getResultingMIDITracks().size());
        assertEquals(111, (int)generator.getTempos().get(0));
        assertEquals(222, (int)generator.getTempos().get(1));
        assertEquals(111, (int)generator.getTempos().get(2));
        
        assertEquals(5, (int)generator.getTimeSignatureNumerators().get(0));
        assertEquals(8, (int)generator.getTimeSignatureDenominators().get(0));
        assertEquals(11, (int)generator.getTimeSignatureNumerators().get(1));
        assertEquals(16, (int)generator.getTimeSignatureDenominators().get(1));
        assertEquals(5, (int)generator.getTimeSignatureNumerators().get(2));
        assertEquals(8, (int)generator.getTimeSignatureDenominators().get(2));
                
        assertEquals(100, (long)generator.getTicks().get(0));
        assertEquals(500, (long)generator.getTicks().get(1));
        assertEquals(1300, (long)generator.getTicks().get(2));
        
        assertEquals(3, generator.getNoteLists().size());
        assertEquals(4, generator.getNoteLists().get(0).size());
        assertEquals(6, generator.getNoteLists().get(1).size());
        assertEquals(4, generator.getNoteLists().get(2).size());
        
        assertEquals(100, generator.getNoteLists().get(0).get(0).getStart());
        assertEquals(200, generator.getNoteLists().get(0).get(1).getStart());
        assertEquals(300, generator.getNoteLists().get(0).get(2).getStart());
        assertEquals(400, generator.getNoteLists().get(0).get(3).getStart());
        
        assertEquals(500, generator.getNoteLists().get(1).get(0).getStart());
        assertEquals(600, generator.getNoteLists().get(1).get(1).getStart());
        assertEquals(700, generator.getNoteLists().get(1).get(2).getStart());
        assertEquals(800, generator.getNoteLists().get(1).get(3).getStart());
        assertEquals(900, generator.getNoteLists().get(1).get(4).getStart());
        assertEquals(1000, generator.getNoteLists().get(1).get(5).getStart());
        
        assertEquals(1300, generator.getNoteLists().get(2).get(0).getStart());
        assertEquals(1400, generator.getNoteLists().get(2).get(1).getStart());
        assertEquals(1500, generator.getNoteLists().get(2).get(2).getStart());
        assertEquals(1600, generator.getNoteLists().get(2).get(3).getStart());
    }
}
