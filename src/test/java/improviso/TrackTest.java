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
import org.mockito.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author User
 */
public class TrackTest extends ImprovisoTest {
    private Pattern pattern1;
    private PatternExecution execution;
    private MIDINoteList noteList;
    
    private GroupMessage groupMessageMock;
    private Group groupMock;
    
    @Before
    public void setUp() {
        noteList = mock(MIDINoteList.class);
        
        execution = mock(PatternExecution.class);
        when(execution.getLength()).thenReturn(100);
        when(execution.execute(any(Random.class), anyDouble(), anyInt())).thenReturn(noteList);
        
        pattern1 = mock(Pattern.class);
        when(pattern1.getNextExecution(any(Random.class))).thenReturn(execution);
        
        groupMessageMock = mock(GroupMessage.class);
        groupMock = mock(Group.class);
        when(groupMock.getMessage()).thenReturn(groupMessageMock);
        when(groupMock.execute(any(Random.class))).thenReturn(execution);
    }
    
    @Test
    public void testCreateTrack() {
        Track track;
        Track.TrackBuilder trackBuilder = new Track.TrackBuilder()
                .setId("trackTest").setRootGroup(groupMock);
        track = trackBuilder.build();
        
        assertNotNull(track);
        assertEquals("trackTest", track.getId());
        
        track.initialize(getRandomMock());
        assertEquals(0, track.getCurrentPosition());
        verify(groupMock).resetGroup();
        verify(groupMock).execute(getRandomMock());
    }
    
    @Test
    public void testExecuteTrackNoSectionEnd() {
        Track track;
        Track.TrackBuilder trackBuilder = new Track.TrackBuilder()
                .setId("trackTest").setRootGroup(groupMock);
        track = trackBuilder.build();
        track.initialize(getRandomMock());
        
        InOrder executionInOrder = inOrder(execution);
        InOrder noteListInOrder = inOrder(noteList);
        
        assertEquals(groupMessageMock, track.getMessage());
        assertEquals(100, track.getEnd());
        
        track.execute(getRandomMock(), new Section.UnknownSectionEnd(), false, false);
        executionInOrder.verify(execution).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE);
        noteListInOrder.verify(noteList).offsetNotes(0);
        assertEquals(100, track.getCurrentPosition());
        
        assertEquals(groupMessageMock, track.getMessage());
        assertEquals(200, track.getEnd());
        
        track.execute(getRandomMock(), new Section.UnknownSectionEnd(), false, false);
        executionInOrder.verify(execution).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE);
        noteListInOrder.verify(noteList).offsetNotes(100);
        assertEquals(200, track.getCurrentPosition());
    }
    
    @Test
    public void testExecuteTrackSectionEndInterrupt() {
        Track track;
        Track.TrackBuilder trackBuilder = new Track.TrackBuilder()
                .setId("trackTest").setRootGroup(groupMock);
        track = trackBuilder.build();
        track.initialize(getRandomMock());
        
        InOrder executionInOrder = inOrder(execution);
        InOrder noteListInOrder = inOrder(noteList);
        
        assertEquals(groupMessageMock, track.getMessage());
        assertEquals(100, track.getEnd());
        
        track.execute(getRandomMock(), Section.SectionEnd.createEnd(160), true, true);
        executionInOrder.verify(execution).execute(getRandomMock(), 0.625d, 160);
        noteListInOrder.verify(noteList).offsetNotes(0);
        assertEquals(100, track.getCurrentPosition());
        
        assertEquals(groupMessageMock, track.getMessage());
        assertEquals(200, track.getEnd());
        
        track.execute(getRandomMock(), Section.SectionEnd.createEnd(160), true, true);
        executionInOrder.verify(execution).execute(getRandomMock(), 1.0d, 60);
        noteListInOrder.verify(noteList).offsetNotes(100);
        assertEquals(160, track.getCurrentPosition());
    }
}
