/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.Random;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runners.model.MultipleFailureException;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.*;
import static org.mockito.Mockito.*;
import org.mockito.stubbing.OngoingStubbing;

/**
 *
 * @author User
 */
public class TrackTest extends ImprovisoTest {
    private Pattern pattern;
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
        
        pattern = mock(Pattern.class);
        when(pattern.getNextExecution(any(Random.class))).thenReturn(execution);
        
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
    
    @Test
    public void testQueryTrackSinglePattern() {
        MIDINoteList noteList1_1 = new MIDINoteList();
        MIDINoteList noteList1_2 = new MIDINoteList(new MIDINote(1, 150, 100, 100, 1));
        MIDINoteList noteList1_3 = new MIDINoteList(new MIDINote(2, 250, 100, 100, 1));
        MIDINoteList noteList1_4 = new MIDINoteList(new MIDINote(3, 300, 100, 100, 1));
        
        MIDINoteList noteList2_1 = new MIDINoteList(new MIDINote(4,   5, 100, 100, 1));
        
        MIDINoteList noteList3_1 = new MIDINoteList(new MIDINote(5,   5, 100, 100, 1));
        
        PatternExecution execution1 = mock(PatternExecution.class);
        when(execution1.getLength()).thenReturn(320);
        when(execution1.executeRange(any(Random.class),   eq(0),  eq(99), anyDouble(), anyInt())).thenReturn(noteList1_1);
        when(execution1.executeRange(any(Random.class), eq(100), eq(199), anyDouble(), anyInt())).thenReturn(noteList1_2);
        when(execution1.executeRange(any(Random.class), eq(200), eq(299), anyDouble(), anyInt())).thenReturn(noteList1_3);
        when(execution1.executeRange(any(Random.class), eq(300), eq(399), anyDouble(), anyInt())).thenReturn(noteList1_4);
        
        PatternExecution execution2 = mock(PatternExecution.class);
        when(execution2.getLength()).thenReturn(60);
        when(execution2.executeRange(any(Random.class),   eq(0),  eq(79), anyDouble(), anyInt())).thenReturn(noteList2_1);
        
        PatternExecution execution3 = mock(PatternExecution.class);
        when(execution3.getLength()).thenReturn(1000);
        when(execution3.executeRange(any(Random.class), eq(0),    eq(19), anyDouble(), anyInt())).thenReturn(noteList3_1);
        
        GroupMessage groupMessageMock1 = mock(GroupMessage.class);
        GroupMessage groupMessageMock2 = mock(GroupMessage.class);
        when(groupMessageMock2.getInterrupt()).thenReturn(true);
        Group groupMock1 = mock(Group.class);
        when(groupMock1.getMessage()).thenReturn(groupMessageMock1, groupMessageMock2, groupMessageMock1);
        when(groupMock1.execute(any(Random.class))).thenReturn(execution1, execution2, execution3);
                
        Track track;
        Track.TrackBuilder trackBuilder = new Track.TrackBuilder()
                .setId("trackTest").setRootGroup(groupMock1);
        track = trackBuilder.build();
        track.initialize(getRandomMock());
        assertEquals(0, track.getCurrentPosition());
        assertEquals(0, track.getCurrentRealTimePosition());
        
        MIDINoteList result1 = track.executeTicks(getRandomMock(), Section.SectionEnd.createEnd(5000), 100, true, true);
        assertTrue(result1.isEmpty());
        assertEquals(0, track.getCurrentPosition());
        assertEquals(100, track.getCurrentRealTimePosition());
        assertNull(track.getPositionInterrupt());
        
        MIDINoteList result2 = track.executeTicks(getRandomMock(), Section.SectionEnd.createEnd(5000), 100, true, true);
        assertFalse(result2.isEmpty());
        assertEquals(1, result2.size());
        assertEquals(1, result2.get(0).getPitch());
        assertEquals(150, result2.get(0).getStart());
        assertEquals(0, track.getCurrentPosition());
        assertEquals(200, track.getCurrentRealTimePosition());
        assertNull(track.getPositionInterrupt());
        
        MIDINoteList result3 = track.executeTicks(getRandomMock(), Section.SectionEnd.createEnd(5000), 100, true, true);
        assertFalse(result3.isEmpty());
        assertEquals(1, result3.size());
        assertEquals(2, result3.get(0).getPitch());
        assertEquals(250, result3.get(0).getStart());
        assertEquals(0, track.getCurrentPosition());
        assertEquals(300, track.getCurrentRealTimePosition());
        assertNull(track.getPositionInterrupt());
        
        MIDINoteList result4 = track.executeTicks(getRandomMock(), Section.SectionEnd.createEnd(5000), 100, true, true);
        assertFalse(result4.isEmpty());
        assertEquals(3, result4.size());
        assertEquals(3, result4.get(0).getPitch());
        assertEquals(300, result4.get(0).getStart());
        assertEquals(4, result4.get(1).getPitch());
        assertEquals(325, result4.get(1).getStart());
        assertEquals(5, result4.get(2).getPitch());
        assertEquals(385, result4.get(2).getStart());
        assertEquals(380, track.getCurrentPosition());
        assertEquals(400, track.getCurrentRealTimePosition());
        assertNotNull(track.getPositionInterrupt());
        assertEquals(380, (int)track.getPositionInterrupt());
    }
}
