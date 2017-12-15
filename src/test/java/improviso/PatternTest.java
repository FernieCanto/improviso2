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
        
        Pattern.PatternExecution execution = pattern.initialize(getRandomMock());
        assertEquals(100, execution.getLength());
        
        MIDINoteList notes = execution.execute(getRandomMock(), 1, 1);
        assertTrue(notes.isEmpty());
    }
    
    @Test
    public void testExecutePattern() {
        Note note1 = mock(Note.class);
        when(note1.execute(any(Random.class), anyInt(), anyDouble(), anyInt())).thenReturn(new MIDINoteList());
        Note note2 = mock(Note.class);
        when(note2.execute(any(Random.class), anyInt(), anyDouble(), anyInt())).thenReturn(new MIDINoteList());
        
        Pattern pattern = new Pattern.PatternBuilder()
                .setId("pattern1")
                .setDuration(getIntegerRangeMock(100))
                .addNote(note1)
                .addNote(note2)
                .build();
        
        assertNotNull(pattern);
        Pattern.PatternExecution execution = pattern.initialize(getRandomMock());
        assertEquals(100, execution.getLength());
        
        execution.execute(getRandomMock(), 0, 99);
        verify(note1).execute(getRandomMock(), 100, 0, 99);
        verify(note2).execute(getRandomMock(), 100, 0, 99);
    }
}
