package improviso;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VariableSectionTest extends ImprovisoTest {
    private static final int PATTERN1_LENGTH = 200;
    private static final int PATTERN2_LENGTH = 300;
    private static final int PATTERN3_LENGTH = 700;
    
    private Pattern.PatternExecution execution1;
    private Pattern.PatternExecution execution2;
    private Pattern.PatternExecution execution3;
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
}
