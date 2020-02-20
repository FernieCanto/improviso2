/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.Random;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author fernando
 */
public class PatternTest extends ImprovisoTest {
    /**
     * Test of getId method, of class Pattern.
     */
    @Test
    public void testExecuteEmptyPattern() {
        Pattern pattern = new Pattern.PatternBuilder()
                .setId("empty")
                .setDuration(getIntegerRangeMock(100))
                .build();
        
        assertNotNull(pattern);
        assertEquals("empty", pattern.getId());
        
        PatternExecution execution = pattern.getNextExecution(getRandomMock());
        assertEquals(100, execution.getLength());
        
        MIDINoteList notes = execution.execute(getRandomMock(), 1, 1);
        assertTrue(notes.isEmpty());
    }
    
    @Test
    public void testExecutePattern() {
        MIDINote MIDInote1 = new MIDINote(1, 2, 3, 4, 5);
        MIDINote MIDInote2 = new MIDINote(2, 3, 4, 5, 6);
        
        Note note1 = mock(Note.class);
        when(note1.execute(any(Random.class), anyInt(), anyDouble(), anyInt())).thenReturn(new MIDINoteList(MIDInote1));
        Note note2 = mock(Note.class);
        when(note2.execute(any(Random.class), anyInt(), anyDouble(), anyInt())).thenReturn(new MIDINoteList(MIDInote2));
        Note note3 = mock(Note.class);
        when(note3.execute(any(Random.class), anyInt(), anyDouble(), anyInt())).thenReturn(new MIDINoteList());
        
        Pattern pattern = new Pattern.PatternBuilder()
                .setId("pattern1")
                .setDuration(getIntegerRangeMock(100))
                .addNote(note1)
                .addNote(note2)
                .addNote(note3)
                .build();
        
        assertNotNull(pattern);
        PatternExecution execution = pattern.getNextExecution(getRandomMock());
        assertEquals(100, execution.getLength());
        
        MIDINoteList result = execution.execute(getRandomMock(), 0, 99);
        assertEquals(2, result.size());
        assertEquals(MIDInote1, result.get(0));
        assertEquals(MIDInote2, result.get(1));
        verify(note1).execute(getRandomMock(), 100, 0, 99);
        verify(note2).execute(getRandomMock(), 100, 0, 99);
        verify(note3).execute(getRandomMock(), 100, 0, 99);
    }
    
    @Test
    public void testExecutePatternBeginningEnd() {
        MIDINote MIDInote1 = new MIDINote(1, 2, 3, 4, 5);
        MIDINote MIDInote2 = new MIDINote(2, 3, 4, 5, 6);
        
        Note note1 = mock(Note.class);
        when(note1.executeRange(any(Random.class), anyInt(), anyInt(), anyInt(), anyDouble(), anyInt())).thenReturn(new MIDINoteList(MIDInote1));
        Note note2 = mock(Note.class);
        when(note2.executeRange(any(Random.class), anyInt(), anyInt(), anyInt(), anyDouble(), anyInt())).thenReturn(new MIDINoteList(MIDInote2));
        Note note3 = mock(Note.class);
        when(note3.executeRange(any(Random.class), anyInt(), anyInt(), anyInt(), anyDouble(), anyInt())).thenReturn(new MIDINoteList());
        
        Pattern pattern = new Pattern.PatternBuilder()
                .setId("pattern1")
                .setDuration(getIntegerRangeMock(200))
                .addNote(note1)
                .addNote(note2)
                .addNote(note3)
                .build();
        assertNotNull(pattern);
        PatternExecution execution = pattern.getNextExecution(getRandomMock());
        assertEquals(200, execution.getLength());
        
        MIDINoteList result = execution.executeRange(getRandomMock(), 50, 150, 0, 199);
        assertEquals(2, result.size());
        assertEquals(MIDInote1, result.get(0));
        assertEquals(MIDInote2, result.get(1));
        verify(note1).executeRange(getRandomMock(), 50, 150, 200, 0, 199);
        verify(note2).executeRange(getRandomMock(), 50, 150, 200, 0, 199);
        verify(note3).executeRange(getRandomMock(), 50, 150, 200, 0, 199);
    }
}
