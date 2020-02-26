package improviso;

import java.util.Random;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VariableSectionTest extends ImprovisoTest {
    private static final int PATTERN1_LENGTH = 200;
    private static final int PATTERN2_LENGTH = 300;
    private static final int PATTERN3_LENGTH = 700;
    
    private PatternExecution execution1;
    private PatternExecution execution2;
    private PatternExecution execution3;
    private Group groupMock1;
    private Group groupMock2;
    private Group groupMock3;
    private GroupMessage message1;
    private GroupMessage message2;
    private GroupMessage message3;
    
    @Before
    public void setUp() {
        execution1 = getPatternExecutionMock(VariableSectionTest.PATTERN1_LENGTH);
        execution2 = getPatternExecutionMock(VariableSectionTest.PATTERN2_LENGTH);
        execution3 = getPatternExecutionMock(VariableSectionTest.PATTERN3_LENGTH);
        
        message1 = mock(GroupMessage.class);
        message2 = mock(GroupMessage.class);
        message3 = mock(GroupMessage.class);
        groupMock1 = getGroupMock(execution1, message1);
        groupMock2 = getGroupMock(execution2, message2);
        groupMock3 = getGroupMock(execution3, message3);
    }
    
    @Test
    public void testCreateVariableSection() throws ImprovisoException {
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(groupMock1).build());
        section = sectionBuilder.build();
        
        assertNotNull(section);
        
        assertEquals(0, section.getActualEnd());
    }
    
    @Test
    public void testExecuteVariableSectionOneTrackFinished() throws ImprovisoException {
        Track track1 = new Track.TrackBuilder().setRootGroup(groupMock1).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        section = sectionBuilder.build();
        
        when(message1.getFinished()).thenReturn(false, false, true);
        
        section.execute(getRandomMock());
        
        verify(execution1, times(3)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 200 - 400 - 600!
        assertEquals(600, section.getActualEnd());
    }
    
    @Test
    public void testExecuteVariableSectionTwoTracksFinished() throws ImprovisoException {
        Track track1 = new Track.TrackBuilder().setId("track1").setRootGroup(groupMock1).build();
        Track track2 = new Track.TrackBuilder().setId("track2").setRootGroup(groupMock2).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        section = sectionBuilder.build();
        
        when(message1.getFinished()).thenReturn(false, false, true);
        when(message2.getFinished()).thenReturn(false, true);
        
        section.execute(getRandomMock());
        
        verify(execution1, times(3)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 200 - 400 - 600!
        verify(execution2, times(2)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 300 - 600!
        assertEquals(600, section.getActualEnd());
    }
    
    @Test
    public void testExecuteVariableSectionThreeTracksFinished() throws ImprovisoException {
        Track track1 = new Track.TrackBuilder().setId("track1").setRootGroup(groupMock1).build();
        Track track2 = new Track.TrackBuilder().setId("track2").setRootGroup(groupMock2).build();
        Track track3 = new Track.TrackBuilder().setId("track3").setRootGroup(groupMock3).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        sectionBuilder.addTrack(track3);
        section = sectionBuilder.build();
        
        when(message1.getFinished()).thenReturn(false, true);
        when(message2.getFinished()).thenReturn(false, true);
        when(message3.getFinished()).thenReturn(true);
        
        section.execute(getRandomMock());
        
        // TODO: SHOULDN'T HAPPEN!!
        verify(execution1, times(4)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 200 - 400! - 600 - 800
        verify(execution2, times(3)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 300 - 600! - 900
        verify(execution3, times(1)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 700!
        assertEquals(900, section.getActualEnd());
    }
    
    @Test
    public void testExecuteVariableSectionOneTrackInterrupt() throws ImprovisoException {
        Track track1 = new Track.TrackBuilder().setRootGroup(groupMock1).setId("interruptTrack").build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        section = sectionBuilder.build();
        
        when(message1.getInterrupt()).thenReturn(false, false, true);
        
        section.execute(getRandomMock());
        
        verify(execution1, times(3)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 200 - 400 - 600!
        assertEquals(600, section.getActualEnd());
    }
    
    @Test
    public void testExecuteVariableSectionTwoTracksInterrupt() throws ImprovisoException {
        Track track1 = new Track.TrackBuilder().setRootGroup(groupMock1).build();
        Track track2 = new Track.TrackBuilder().setRootGroup(groupMock2).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        section = sectionBuilder.build();
        
        when(message1.getInterrupt()).thenReturn(false, false, false, true);
        
        section.execute(getRandomMock());
        
        // TODO: SHOULDN'T HAPPEN
        verify(execution1, times(4)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 200 - 400 - 600 - 800!!
        verify(execution2, times(3)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 300 - 600 - 900
        assertEquals(900, section.getActualEnd());
    }
    
    @Test
    public void testExecuteVariableSectionThreeTracksFinishedCutSection() throws ImprovisoException {
        Track track1 = new Track.TrackBuilder().setId("track1").setRootGroup(groupMock1).build();
        Track track2 = new Track.TrackBuilder().setId("track2").setRootGroup(groupMock2).build();
        Track track3 = new Track.TrackBuilder().setId("track3").setRootGroup(groupMock3).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        sectionBuilder.addTrack(track3);
        sectionBuilder.setInterruptTracks(true);
        section = sectionBuilder.build();
        
        when(message1.getFinished()).thenReturn(false, true);
        when(message2.getFinished()).thenReturn(false, true);
        when(message3.getFinished()).thenReturn(true);
        
        section.execute(getRandomMock());
        
        verify(execution1, times(3)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 200 - 400! - 600
        verify(execution1, times(1)).execute(getRandomMock(), 0.0d, 100); // - 700
        verify(execution2, times(2)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 300 - 600!
        verify(execution2, times(1)).execute(getRandomMock(), 0.0d, 100); //  - 900
        verify(execution3, times(1)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 700!
        assertEquals(700, section.getActualEnd());
    }
    
    @Test
    public void testExecuteVariableSectionTwoTracksInterruptCutSection() throws ImprovisoException {
        Track track1 = new Track.TrackBuilder().setRootGroup(groupMock1).build();
        Track track2 = new Track.TrackBuilder().setRootGroup(groupMock2).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        sectionBuilder.setInterruptTracks(true);
        section = sectionBuilder.build();
        
        when(message1.getInterrupt()).thenReturn(false, false, false, true);
        
        section.execute(getRandomMock());
        
        verify(execution1, times(4)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 200 - 400 - 600 - 800!!
        verify(execution2, times(2)).execute(getRandomMock(), 0.0d, Integer.MAX_VALUE); // 300 - 600
        verify(execution2, times(1)).execute(getRandomMock(), 0.0d, 200); // - 800
        assertEquals(800, section.getActualEnd());
    }
    
    @Test
    public void testExecuteVariableSectionTwoTrackInterruptRealTime() throws ImprovisoException {
        Track track1 = getTrackMock();
        Track track2 = getTrackMock();
        
        Random rand = getRandomMock();
        
        MIDINote list1_1[] = {new MIDINote(1, 0, 10, 100, 1), new MIDINote(1, 200, 10, 100, 1)};
        MIDINote list1_2[] = {new MIDINote(1, 250, 10, 100, 1), new MIDINote(1, 401, 10, 100, 1)};
        when(track1.executeTicks(eq(rand), any(Section.SectionEnd.class), eq(250), eq(true), eq(false))).thenReturn(
                new MIDINoteList(list1_1),
                new MIDINoteList(list1_2)
        );
        when(track1.getPositionFinished()).thenReturn(null);
        when(track1.getPositionInterrupt()).thenReturn(null);
        
        MIDINote list2_1[] = {new MIDINote(1, 0, 10, 100, 1), new MIDINote(1, 100, 10, 100, 1)};
        MIDINote list2_2[] = {new MIDINote(1, 250, 10, 100, 1), new MIDINote(1, 399, 10, 100, 1)};
        when(track2.executeTicks(eq(rand), any(Section.SectionEnd.class), eq(250), eq(true), eq(false))).thenReturn(
                new MIDINoteList(list2_1),
                new MIDINoteList(list2_2)
        );
        when(track2.getPositionFinished()).thenReturn(null);
        when(track2.getPositionInterrupt()).thenReturn(null, 400);
        
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        sectionBuilder.setInterruptTracks(true);
        VariableSection section = sectionBuilder.build();
        section.initialize(rand);
        assertEquals(0, section.getCurrentRealTimePosition());
        
        MIDINoteList list1 = section.executeTicks(rand, 250);
        assertEquals(4, list1.size());
        assertEquals(  0, list1.get(0).getStart());
        assertEquals(200, list1.get(1).getStart());
        assertEquals(  0, list1.get(2).getStart());
        assertEquals(100, list1.get(3).getStart());
        assertFalse(section.isFinished());
        assertEquals(250, section.getCurrentRealTimePosition());
        
        MIDINoteList list2 = section.executeTicks(rand, 250);
        assertEquals(3, list2.size());
        assertEquals(250, list2.get(0).getStart());
        assertEquals(250, list2.get(1).getStart());
        assertEquals(399, list2.get(2).getStart());
        assertTrue(section.isFinished());
        assertEquals(400, section.getCurrentRealTimePosition());
    }
    
    @Test
    public void testExecuteVariableSectionThreeTrackFinishRealTime() throws ImprovisoException {
        Track track1 = getTrackMock();
        Track track2 = getTrackMock();
        Track track3 = getTrackMock();
        
        Random rand = getRandomMock();
        
        MIDINote list1_1[] = {new MIDINote(1,   0, 10, 100, 1), new MIDINote(1, 150, 10, 100, 1)};
        MIDINote list1_2[] = {new MIDINote(1, 300, 10, 100, 1)};
        MIDINote list1_3[] = {new MIDINote(1, 550, 10, 100, 1), new MIDINote(1, 740, 10, 100, 1)};
        when(track1.executeTicks(eq(rand), any(Section.SectionEnd.class), anyInt(), eq(true), eq(false))).thenReturn(
                new MIDINoteList(list1_1),
                new MIDINoteList(list1_2),
                new MIDINoteList(list1_3)
        );
        when(track1.getPositionFinished()).thenReturn(0, 200, 200);
        when(track1.getPositionInterrupt()).thenReturn(null);
        
        MIDINote list2_1[] = {new MIDINote(2,   0, 10, 100, 2)};
        MIDINote list2_2[] = {new MIDINote(2, 250, 10, 100, 2)};
        MIDINote list2_3[] = {new MIDINote(2, 650, 10, 100, 2)};
        when(track2.executeTicks(eq(rand), any(Section.SectionEnd.class), anyInt(), eq(true), eq(false))).thenReturn(
                new MIDINoteList(list2_1),
                new MIDINoteList(list2_2),
                new MIDINoteList(list2_3)
        );
        when(track2.getPositionFinished()).thenReturn(600, 600, 600);
        when(track2.getPositionInterrupt()).thenReturn(null);
        
        MIDINote list3_1[] = {new MIDINote(3,  10, 10, 100, 3)};
        MIDINote list3_2[] = {new MIDINote(3, 260, 10, 100, 3)};
        MIDINote list3_3[] = {new MIDINote(3, 500, 10, 100, 3)};
        when(track3.executeTicks(eq(rand), any(Section.SectionEnd.class), anyInt(), eq(true), eq(false))).thenReturn(
                new MIDINoteList(list3_1),
                new MIDINoteList(list3_2),
                new MIDINoteList(list3_3)
        );
        when(track3.getPositionFinished()).thenReturn(0, 0, 550);
        when(track3.getPositionInterrupt()).thenReturn(null);
        
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        sectionBuilder.addTrack(track3);
        sectionBuilder.setInterruptTracks(true);
        VariableSection section = sectionBuilder.build();
        section.initialize(rand);
        
        MIDINoteList list1 = section.executeTicks(rand, 250);
        assertEquals(4, list1.size());
        assertEquals(  0, list1.get(0).getStart());
        assertEquals(150, list1.get(1).getStart());
        assertEquals(  0, list1.get(2).getStart());
        assertEquals( 10, list1.get(3).getStart());
        assertFalse(section.isFinished());
        
        MIDINoteList list2 = section.executeTicks(rand, 250);
        assertEquals(3, list2.size());
        assertEquals(300, list2.get(0).getStart());
        assertEquals(250, list2.get(1).getStart());
        assertEquals(260, list2.get(2).getStart());
        assertFalse(section.isFinished());
        
        MIDINoteList list3 = section.executeTicks(rand, 250);
        assertEquals(2, list3.size());
        assertEquals(550, list3.get(0).getStart());
        assertEquals(500, list3.get(1).getStart());
        assertTrue(section.isFinished());
        assertEquals(600, section.getCurrentRealTimePosition());
    }
}
