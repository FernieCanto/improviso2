/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.io.IOException;
import java.util.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import org.junit.*;
import static org.junit.Assert.*;
import org.mockito.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author User
 */
public class CompositionTest {
    final private Section sectionMock1;
    final private Section sectionMock2;
    
    public CompositionTest() throws ImprovisoException {
        MIDINote[] notes1 = {
            new MIDINote(20, 0, 100, 100, 1),
            new MIDINote(25, 100, 100, 100, 1),
            new MIDINote(30, 200, 100, 100, 1),
            new MIDINote(40, 300, 100, 100, 1),
        };
        MIDINote[] notes2 = {
            new MIDINote(20, 0, 100, 100, 1),
            new MIDINote(25, 100, 100, 100, 1),
            new MIDINote(30, 200, 100, 100, 1),
            new MIDINote(40, 300, 100, 100, 1),
            new MIDINote(45, 400, 100, 100, 1),
            new MIDINote(50, 500, 100, 100, 1),
        };
        
        sectionMock1 = mock(Section.class);
        when(sectionMock1.getTempo()).thenReturn(111);
        when(sectionMock1.getTimeSignatureNumerator()).thenReturn(5);
        when(sectionMock1.getTimeSignatureDenominator()).thenReturn(8);
        when(sectionMock1.execute(any(Random.class))).thenReturn(new MIDINoteList(notes1));
        when(sectionMock1.getActualEnd()).thenReturn(400);
        
        sectionMock2 = mock(Section.class);
        when(sectionMock2.getTempo()).thenReturn(222);
        when(sectionMock2.getTimeSignatureNumerator()).thenReturn(11);
        when(sectionMock2.getTimeSignatureDenominator()).thenReturn(16);
        when(sectionMock2.execute(any(Random.class))).thenReturn(new MIDINoteList(notes2));
        when(sectionMock2.getActualEnd()).thenReturn(800);
    }
    
    @Test
    public void testInterpretNoteNames() throws ImprovisoException {
        NoteNameInterpreter interpreter = new NoteNameInterpreter();
        interpreter.addNoteAlias("alias", 15);
        
        long note1 = interpreter.interpretNoteName("D#2");
        assertEquals(51, note1);
        
        long note2 = interpreter.interpretNoteName("Gb1");
        assertEquals(42, note2);
        
        long note3 = interpreter.interpretNoteName("C-2");
        assertEquals(0, note3);
        
        long note4 = interpreter.interpretNoteName("50");
        assertEquals(50, note4);
        
        long note5 = interpreter.interpretNoteName("alias");
        assertEquals(15, note5);
    }
    
    @Test(expected = ImprovisoException.class)
    public void testInterpretNoteError() throws ImprovisoException {
        NoteNameInterpreter interpreter = new NoteNameInterpreter();
        interpreter.interpretNoteName("error");
    }
    
    @Test
    public void testBeatsAndTicks() {
        assertEquals("2:40", Composition.showBeatsAndTicks(280));
    }
    
    @Test(expected = ImprovisoException.class)
    public void testCreateComposition() throws ImprovisoException, InvalidMidiDataException, IOException, MidiUnavailableException {
        Composition composition = new Composition(100);
        composition.execute(mock(MIDIGenerator.class));
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
        composition.addSection("section1", this.sectionMock1);
        
        MIDIGenerator generator = mock(MIDIGenerator.class);
        composition.execute(generator);
        
        ArgumentCaptor<MIDITrackList> argumentMIDITrackList = ArgumentCaptor.forClass(MIDITrackList.class);
        verify(generator).setMIDITracks(argumentMIDITrackList.capture());
        
        assertEquals(1, argumentMIDITrackList.getValue().size());
        assertEquals(1, argumentMIDITrackList.getValue().get(0).getChannel());
        assertEquals(0, argumentMIDITrackList.getValue().get(0).getInstrument());
        assertEquals(100, argumentMIDITrackList.getValue().get(0).getVolume());
        assertEquals(64, argumentMIDITrackList.getValue().get(0).getPan());
        
        verify(generator).setTempo(111, 100);
        verify(generator).setTimeSignature(5, 8, 100);
        ArgumentCaptor<MIDINoteList> argumentNotes = ArgumentCaptor.forClass(MIDINoteList.class);
        verify(generator).addNotes(argumentNotes.capture());
        
        assertEquals(4, argumentNotes.getValue().size());
        assertEquals(100, argumentNotes.getValue().get(0).getStart());
        assertEquals(200, argumentNotes.getValue().get(1).getStart());
        assertEquals(300, argumentNotes.getValue().get(2).getStart());
        assertEquals(400, argumentNotes.getValue().get(3).getStart());
    }
    
    @Test
    public void testCompositionTwoSections() throws ImprovisoException, InvalidMidiDataException, IOException, MidiUnavailableException {
        Composition composition = new Composition(100);
        composition.addMIDITrack(new MIDITrack(1, 0, 100, 64));
        composition.addSection("section2", this.sectionMock2);
        composition.addSection("section1", this.sectionMock1);
        
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
        
        MIDIGenerator generator = mock(MIDIGenerator.class);
        composition.execute(generator);
        
        InOrder generatorInOrder = inOrder(generator);
        
        ArgumentCaptor<MIDINoteList> argumentNotes1 = ArgumentCaptor.forClass(MIDINoteList.class);
        ArgumentCaptor<MIDINoteList> argumentNotes2 = ArgumentCaptor.forClass(MIDINoteList.class);
        ArgumentCaptor<MIDINoteList> argumentNotes3 = ArgumentCaptor.forClass(MIDINoteList.class);
        
        generatorInOrder.verify(generator).setTempo(111, 100);
        generatorInOrder.verify(generator).setTimeSignature(5, 8,100);
        generatorInOrder.verify(generator).addNotes(argumentNotes1.capture());
        
        assertEquals(100, argumentNotes1.getValue().get(0).getStart());
        assertEquals(200, argumentNotes1.getValue().get(1).getStart());
        assertEquals(300, argumentNotes1.getValue().get(2).getStart());
        assertEquals(400, argumentNotes1.getValue().get(3).getStart());
        
        generatorInOrder.verify(generator).setTempo(222,500);
        generatorInOrder.verify(generator).setTimeSignature(11, 16, 500);
        generatorInOrder.verify(generator).addNotes(argumentNotes2.capture());
        
        assertEquals(500, argumentNotes2.getValue().get(0).getStart());
        assertEquals(600, argumentNotes2.getValue().get(1).getStart());
        assertEquals(700, argumentNotes2.getValue().get(2).getStart());
        assertEquals(800, argumentNotes2.getValue().get(3).getStart());
        assertEquals(900, argumentNotes2.getValue().get(4).getStart());
        assertEquals(1000, argumentNotes2.getValue().get(5).getStart());
        
        generatorInOrder.verify(generator).setTempo(111, 1300);
        generatorInOrder.verify(generator).setTimeSignature(5, 8, 1300);
        generatorInOrder.verify(generator).addNotes(argumentNotes3.capture());
        
        assertEquals(1300, argumentNotes3.getValue().get(0).getStart());
        assertEquals(1400, argumentNotes3.getValue().get(1).getStart());
        assertEquals(1500, argumentNotes3.getValue().get(2).getStart());
        assertEquals(1600, argumentNotes3.getValue().get(3).getStart());
    }
    
    @Test
    public void testCompositionTwoSectionsRealTime() throws ImprovisoException, InvalidMidiDataException, IOException, MidiUnavailableException {
        MIDINote[] notes1 = {
            new MIDINote(20,   0, 100, 100, 1),
            new MIDINote(25, 100,  50, 100, 1),
        };
        MIDINote[] notes2 = {
            new MIDINote(30, 250, 100, 100, 1),
        };
        MIDINote[] notes3 = {
            new MIDINote(35,   0,  30,  90, 1),
        };
        MIDINote[] notes4 = {
            new MIDINote(40, 190,  20,  95, 1),
        };
        
        FixedSection section1 = mock(FixedSection.class);
        when(section1.getTempo()).thenReturn(120);
        when(section1.executeTicks(any(Random.class), eq(250))).thenReturn(
                new MIDINoteList(notes1),
                new MIDINoteList(notes2)
        );
        when(section1.isFinished()).thenReturn(false, true);
        when(section1.getCurrentRealTimePosition()).thenReturn(0, 250, 250, 320);
        
        FixedSection section2 = mock(FixedSection.class);
        when(section2.getTempo()).thenReturn(120);
        when(section2.executeTicks(any(Random.class), eq(180))).thenReturn(
                new MIDINoteList(notes3)
        );
        when(section2.executeTicks(any(Random.class), eq(250))).thenReturn(
                new MIDINoteList(notes4)
        );
        when(section2.isFinished()).thenReturn(false, true);
        when(section2.getCurrentRealTimePosition()).thenReturn(0, 180, 180, 290);
        
        Composition composition = new Composition(100);
        composition.addMIDITrack(new MIDITrack(1, 0, 100, 64));
        composition.addSection("section1", section1);
        composition.addSection("section2", section2);
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
        
        MIDIGenerator generator = mock(MIDIGenerator.class);
        InOrder generatorInOrder = inOrder(generator);
        
        composition.initialize(generator);
        verify(section1).initialize(any(Random.class));
        
        composition.executeTicks(generator, 250);
        ArgumentCaptor<MIDINoteList> argumentNotes1 = ArgumentCaptor.forClass(MIDINoteList.class);
        generatorInOrder.verify(generator).addNotes(argumentNotes1.capture());
        
        assertEquals(2, argumentNotes1.getValue().size());
        assertNote((MIDINote)argumentNotes1.getValue().get(0), 20,   0, 100, 100, 1);
        assertNote((MIDINote)argumentNotes1.getValue().get(1), 25, 100,  50, 100, 1);
        assertFalse(composition.getIsFinished());
        
        ArgumentCaptor<MIDINoteList> argumentNotes2 = ArgumentCaptor.forClass(MIDINoteList.class);
        composition.executeTicks(generator, 250);
        generatorInOrder.verify(generator).addNotes(argumentNotes2.capture());
        
        assertEquals(2, argumentNotes2.getValue().size());
        assertNote((MIDINote)argumentNotes2.getValue().get(0), 30, 250, 100, 100, 1);
        assertNote((MIDINote)argumentNotes2.getValue().get(1), 35, 320,  30,  90, 1);
        assertFalse(composition.getIsFinished());
        
        ArgumentCaptor<MIDINoteList> argumentNotes3 = ArgumentCaptor.forClass(MIDINoteList.class);
        composition.executeTicks(generator, 250);
        generatorInOrder.verify(generator).addNotes(argumentNotes3.capture());
        
        assertEquals(1, argumentNotes3.getValue().size());
        assertNote((MIDINote)argumentNotes3.getValue().get(0), 40, 510,  20,  95, 1);
        assertTrue(composition.getIsFinished());
    }
    
    private void assertNote(MIDINote note, int pitch, long start, int length, int velocity, int MIDItrack) {
        assertEquals(pitch, note.getPitch());
        assertEquals(start, note.getStart());
        assertEquals(length, note.getLength());
        assertEquals(velocity, note.getVelocity());
        assertEquals(MIDItrack, note.getMIDITrack());
    }
}
