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
 * @author User
 */
public class TrackTest extends ImprovisoTest {
    private Pattern pattern1;
    private Pattern.PatternExecution execution;
    private MIDINoteList noteList;
    
    private GroupMessage groupMessageMock;
    private Group groupMock;
    
    @Before
    public void setUp() {
        noteList = mock(MIDINoteList.class);
        
        execution = mock(Pattern.PatternExecution.class);
        when(execution.getLength()).thenReturn(150);
        when(execution.execute(any(Random.class), anyDouble(), anyInt())).thenReturn(noteList);
        
        pattern1 = mock(Pattern.class);
        when(pattern1.initialize(any(Random.class))).thenReturn(execution);
        
        groupMessageMock = mock(GroupMessage.class);
        groupMock = mock(Group.class);
        when(groupMock.getMessage()).thenReturn(groupMessageMock);
        when(groupMock.execute(any(Random.class))).thenReturn(pattern1);
    }
    
    @Test
    public void testCreateTrack() {
        Track track;
        Track.TrackBuilder trackBuilder = new Track.TrackBuilder()
                .setId("trackTest").setRootGroup(groupMock);
        track = trackBuilder.build();
        
        assertNotNull(track);
        assertEquals("trackTest", track.getId());
        
        track.initialize();
        assertEquals(0, track.getCurrentPosition());
        verify(groupMock).resetGroup();
    }
    
    @Test
    public void testExecuteTrackNoSectionEnd() {
        Track track;
        Track.TrackBuilder trackBuilder = new Track.TrackBuilder()
                .setId("trackTest").setRootGroup(groupMock);
        track = trackBuilder.build();
        track.initialize();
        
        track.selectNextPattern(getRandomMock());
        assertNotNull(track.getCurrentExecution());
        assertEquals(groupMessageMock, track.getMessage());
        assertEquals(150, track.getEnd());
        
        track.execute(getRandomMock(), new Section.UnknownSectionEnd(), false);
        verify(execution).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE);
        verify(noteList).offsetNotes(0);
        assertEquals(150, track.getCurrentPosition());
    }
    
    @Test
    public void testExecuteTrackSectionEndInterrupt() {
        // TODO: TEST MULTIPLE EXECUTIONS OF TRACK!
        Track track;
        Track.TrackBuilder trackBuilder = new Track.TrackBuilder()
                .setId("trackTest").setRootGroup(groupMock);
        track = trackBuilder.build();
        
        track.initialize();
        
        track.selectNextPattern(getRandomMock());
        assertNotNull(track.getCurrentExecution());
        assertEquals(groupMessageMock, track.getMessage());
        assertEquals(150, track.getEnd());
        
        track.execute(getRandomMock(), Section.SectionEnd.createEnd(60), true);
        verify(execution).execute(getRandomMock(), 1.0, 60);
        verify(noteList).offsetNotes(0);
        assertEquals(60, track.getCurrentPosition());
    }
}
