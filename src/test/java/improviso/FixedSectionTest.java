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
public class FixedSectionTest extends ImprovisoTest {
    private static final int PATTERN1_LENGTH = 200;
    private static final int PATTERN2_LENGTH = 300;
    
    private Pattern.PatternExecution execution1;
    private Pattern.PatternExecution execution2;
    private Group groupMock1;
    private Group groupMock2;
            
    @Before
    public void setUp() {
        execution1 = getPatternExecutionMock(FixedSectionTest.PATTERN1_LENGTH);
        execution2 = getPatternExecutionMock(FixedSectionTest.PATTERN2_LENGTH);
        
        groupMock1 = getGroupMock(execution1);
        groupMock2 = getGroupMock(execution2);
    }
    
    @Test
    public void testBuildFixedSection() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(getIntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(groupMock1).build());
        section = sectionBuilder.build();
        
        assertNotNull(section);
        assertEquals(1, section.getTracks().size());
        assertEquals(0, section.getActualEnd());
    }
    
    @Test(expected = ImprovisoException.class)
    public void testExecuteSectionNoTracks() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(getIntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        section = sectionBuilder.build();
        section.execute(getRandomMock());
    }
    
    @Test
    public void testExecuteSectionOneTrack() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(getIntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.groupMock1).build());
        section = sectionBuilder.build();
        assertEquals(1, section.getTracks().size());
        
        section.execute(getRandomMock());
        
        verify(execution1).execute(getRandomMock(), 0.4d, Integer.MAX_VALUE);
        verify(execution1).execute(getRandomMock(), 0.8d, Integer.MAX_VALUE);
        verify(execution1).execute(getRandomMock(), 1.0d, Integer.MAX_VALUE);
        
        assertEquals(600, section.getActualEnd());
    }
    
    @Test
    public void testExecuteFixedSectionTwoTracks() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(getIntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.groupMock1).setId("track1").build());
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.groupMock2).setId("track2").build());
        section = sectionBuilder.build();
        assertEquals(2, section.getTracks().size());
        
        section.execute(getRandomMock());
        
        verify(execution1).execute(getRandomMock(), 0.4d, Integer.MAX_VALUE);
        verify(execution1).execute(getRandomMock(), 0.8d, Integer.MAX_VALUE);
        verify(execution1).execute(getRandomMock(), 1.0d, Integer.MAX_VALUE);
        
        verify(execution2).execute(getRandomMock(), 0.6d, Integer.MAX_VALUE);
        verify(execution2).execute(getRandomMock(), 1.0d, Integer.MAX_VALUE);
        assertEquals(600, section.getActualEnd());
    }
    
    @Test
    public void testExecuteFixedSectionOneTrackInterrupt() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(getIntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.groupMock1).build());
        sectionBuilder.setInterruptTracks(true);
        section = sectionBuilder.build();
        
        section.execute(getRandomMock());
        
        verify(execution1).execute(getRandomMock(), 0.4d, 500);
        verify(execution1).execute(getRandomMock(), 0.8d, 300);
        verify(execution1).execute(getRandomMock(), 1.0d, 100);
        assertEquals(500, section.getActualEnd());
    }
    
    @Test
    public void testExecuteFixedSectionTwoTracksInterrupt() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(getIntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.groupMock1).setId("track1").build());
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.groupMock2).setId("track2").build());
        sectionBuilder.setInterruptTracks(true);
        section = sectionBuilder.build();
        
        section.execute(getRandomMock());
        
        verify(execution1).execute(getRandomMock(), 0.4d, 500);
        verify(execution1).execute(getRandomMock(), 0.8d, 300);
        verify(execution1).execute(getRandomMock(), 1.0d, 100);
        
        verify(execution2).execute(getRandomMock(), 0.6d, 500);
        verify(execution2).execute(getRandomMock(), 1.0d, 200);
        
        assertEquals(500, section.getActualEnd());
    }
}
