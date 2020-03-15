/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author User
 */
public class NoteTest extends ImprovisoTest {
    /**
     * Test a note that's not generated due to the probability.
     */
    @Test
    public void testProbabilityBelowNote() {
        Note noteDef = new Note.NoteBuilder()
                .setMIDITrack(2)
                .setPitch(30)
                .setProbability(0.5)
                .build();
        when(getRandomMock().nextDouble()).thenReturn(0.51);
        MIDINoteList resultNoNote = noteDef.execute(getRandomMock(), 500, 0.5, Integer.MAX_VALUE);
        assertTrue(resultNoNote.isEmpty());
    }
    
    @Test
    public void testNoteAfterMaximumLength() {
        Note noteDef = new Note.NoteBuilder()
                .setMIDITrack(1)
                .setPitch(10)
                .setStart(getIntegerRangeMock(50))
                .setLength(getIntegerRangeMock(50))
                .build();
        MIDINoteList resultNoNote = noteDef.execute(getRandomMock(), 100, 1.0, 49);
        assertTrue(resultNoNote.isEmpty());
    }

    /**
     * Test a note with fixed start and length.
     */
    @Test
    public void testGenerateFixedNote() {
        Note noteDef = new Note.NoteBuilder()
                .setMIDITrack(2)
                .setPitch(30)
                .setStart(getIntegerRangeMock(20))
                .setLength(getIntegerRangeMock(50))
                .setVelocity(getIntegerRangeMock(10))
                .build();
        
        MIDINoteList result = noteDef.execute(getRandomMock(), 55, 0.5, 55);
        assertFalse(result.isEmpty());
        MIDINote note0 = (MIDINote)result.get(0);
        assertEquals(30, note0.getPitch());
        assertEquals(10, note0.getVelocity());
        assertEquals(20, note0.getStart());
        assertEquals(35, note0.getLength());
        assertEquals( 2, note0.getMIDITrack());
    }
    
    /**
     * Test a note with relative start and length
     */
    @Test
    public void testGenerateRelativeNote() {
        Note noteDef = new Note.NoteBuilder()
                .setPitch(30)
                .setStart(getDoubleRangeMock(.4))
                .setLength(getDoubleRangeMock(.25))
                .setVelocity(getIntegerRangeMock(10))
                .build();
        
        MIDINoteList result = noteDef.execute(getRandomMock(), 200, 0.5, Integer.MAX_VALUE);
        assertFalse(result.isEmpty());
        MIDINote note0 = (MIDINote)result.get(0);
        assertEquals(30, note0.getPitch());
        assertEquals(10, note0.getVelocity());
        assertEquals(80, note0.getStart());
        assertEquals(50, note0.getLength());
    }
    
    @Test
    public void testGenerateTransposedNote() {
        Note noteDef = new Note.NoteBuilder()
                .setPitch(30)
                .setTransposition(getIntegerRangeMock(2))
                .build();
        MIDINoteList result = noteDef.execute(getRandomMock(), 200, 0.5, Integer.MAX_VALUE);
        assertFalse(result.isEmpty());
        MIDINote note0 = (MIDINote)result.get(0);
        assertEquals(32, note0.getPitch());
    }
    
    @Test
    public void testGenerateFixedNoteAfterRange() {
        Note noteDef = new Note.NoteBuilder()
                .setPitch(30)
                .setStart(getIntegerRangeMock(50))
                .setLength(getIntegerRangeMock(50))
                .build();
        MIDINoteList resultNoNote = noteDef.executeRange(getRandomMock(), 0, 49, 100, 1.0, 100);
        assertTrue(resultNoNote.isEmpty());
    }
    
    @Test
    public void testGenerateFixedNoteInsideRange() {
        Note noteDef = new Note.NoteBuilder()
                .setPitch(30)
                .setStart(getIntegerRangeMock(50))
                .setLength(getIntegerRangeMock(50))
                .build();
        MIDINoteList result = noteDef.executeRange(getRandomMock(), 0, 50, 100, 1.0, 100);
        assertFalse(result.isEmpty());
        MIDINote note0 = (MIDINote)result.get(0);
        assertEquals(30, note0.getPitch());
        assertEquals(50, note0.getStart());
        assertEquals(50, note0.getLength());
    }
    
    @Test
    public void testGenerateFixedNoteBeforeRange() {
        Note noteDef = new Note.NoteBuilder()
                .setPitch(30)
                .setStart(getIntegerRangeMock(50))
                .setLength(getIntegerRangeMock(50))
                .build();
        MIDINoteList resultNoNote = noteDef.executeRange(getRandomMock(), 51, 100, 100, 1.0, 100);
        assertTrue(resultNoNote.isEmpty());
    }
    
    @Test
    public void testGenerateRelativeNoteAfterRange() {
        Note noteDef = new Note.NoteBuilder()
                .setPitch(30)
                .setStart(getDoubleRangeMock(0.5))
                .setLength(getDoubleRangeMock(0.5))
                .build();
        MIDINoteList resultNoNote = noteDef.executeRange(getRandomMock(), 0, 49, 100, 1.0, 100);
        assertTrue(resultNoNote.isEmpty());
    }
    
    @Test
    public void testGenerateRelativeNoteInsideRange() {
        Note noteDef = new Note.NoteBuilder()
                .setPitch(30)
                .setStart(getDoubleRangeMock(0.5))
                .setLength(getDoubleRangeMock(0.5))
                .build();
        MIDINoteList result = noteDef.executeRange(getRandomMock(), 0, 50, 100, 1.0, 100);
        assertFalse(result.isEmpty());
        MIDINote note0 = (MIDINote)result.get(0);
        assertEquals(30, note0.getPitch());
        assertEquals(50, note0.getStart());
        assertEquals(50, note0.getLength());
    }
    
    @Test
    public void testGenerateRelativeNoteBeforeRange() {
        Note noteDef = new Note.NoteBuilder()
                .setPitch(30)
                .setStart(getDoubleRangeMock(0.5))
                .setLength(getDoubleRangeMock(0.5))
                .build();
        MIDINoteList resultNoNote = noteDef.executeRange(getRandomMock(), 51, 100, 100, 1.0, 100);
        assertTrue(resultNoNote.isEmpty());
    }
}
