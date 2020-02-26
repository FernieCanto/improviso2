/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.Random;
import org.junit.*;
import static org.junit.Assert.*;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

/**
 *
 * @author User
 */
public class FixedSectionTest extends ImprovisoTest {
    private static final int PATTERN1_LENGTH = 200;
    private static final int PATTERN2_LENGTH = 300;
    
    private PatternExecution execution1;
    private PatternExecution execution2;
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
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(getIntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.groupMock1).setId("track1").build());
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.groupMock2).setId("track2").build());
        sectionBuilder.setInterruptTracks(true);
        FixedSection section = sectionBuilder.build();
        
        section.execute(getRandomMock());
        
        verify(execution1).execute(getRandomMock(), 0.4d, 500);
        verify(execution1).execute(getRandomMock(), 0.8d, 300);
        verify(execution1).execute(getRandomMock(), 1.0d, 100);
        
        verify(execution2).execute(getRandomMock(), 0.6d, 500);
        verify(execution2).execute(getRandomMock(), 1.0d, 200);
        
        assertEquals(500, section.getActualEnd());
    }
    
    @Test
    public void testExecuteFixedSectionTicks() throws ImprovisoException {
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(getIntegerRangeMock(630)).setId("sectionTicks").setTempo(100);
        
        Track track1 = getTrackMock();
        Track track2 = getTrackMock();
        
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        sectionBuilder.setInterruptTracks(false);
        
        FixedSection section = sectionBuilder.build();
        
        Random rand = getRandomMock();
        
        section.initialize(rand);
        verify(track1).initialize(eq(rand));
        verify(track2).initialize(eq(rand));
        
        when(track1.executeTicks(any(Random.class), any(Section.SectionEnd.class), anyInt(), anyBoolean(), anyBoolean())).thenReturn(
                new MIDINoteList(new MIDINote(1, 0, 10, 100, 1)),
                new MIDINoteList(new MIDINote(1, 250, 10, 100, 1)),
                new MIDINoteList(new MIDINote(1, 500, 10, 100, 1))
        );
        when(track2.executeTicks(any(Random.class), any(Section.SectionEnd.class), anyInt(), anyBoolean(), anyBoolean())).thenReturn(
                new MIDINoteList(new MIDINote(2, 0, 10, 100, 2)),
                new MIDINoteList(new MIDINote(2, 250, 10, 100, 2)),
                new MIDINoteList(new MIDINote(2, 500, 10, 100, 2))
        );
        MIDINoteList list1 = section.executeTicks(rand, 250);
        assertEquals(2, list1.size());
        ArgumentCaptor<Section.SectionEnd> sectionEndTrack1_1 = ArgumentCaptor.forClass(Section.SectionEnd.class);
        ArgumentCaptor<Section.SectionEnd> sectionEndTrack1_2 = ArgumentCaptor.forClass(Section.SectionEnd.class);
        verify(track1).executeTicks(eq(rand), sectionEndTrack1_1.capture(), eq(250), eq(false), eq(true));
        verify(track2).executeTicks(eq(rand), sectionEndTrack1_2.capture(), eq(250), eq(false), eq(true));
        assertEquals(630, sectionEndTrack1_1.getValue().intValue());
        assertEquals(630, sectionEndTrack1_2.getValue().intValue());
        
        MIDINoteList list2 = section.executeTicks(rand, 250);
        assertEquals(2, list2.size());
        ArgumentCaptor<Section.SectionEnd> sectionEndTrack2_1 = ArgumentCaptor.forClass(Section.SectionEnd.class);
        ArgumentCaptor<Section.SectionEnd> sectionEndTrack2_2 = ArgumentCaptor.forClass(Section.SectionEnd.class);
        verify(track1, times(2)).executeTicks(eq(rand), sectionEndTrack2_1.capture(), eq(250), eq(false), eq(true));
        verify(track2, times(2)).executeTicks(eq(rand), sectionEndTrack2_2.capture(), eq(250), eq(false), eq(true));
        assertEquals(630, sectionEndTrack2_1.getValue().intValue());
        assertEquals(630, sectionEndTrack2_2.getValue().intValue());
        
        MIDINoteList list3 = section.executeTicks(rand, 250);
        assertEquals(2, list3.size());
        ArgumentCaptor<Section.SectionEnd> sectionEndTrack3_1 = ArgumentCaptor.forClass(Section.SectionEnd.class);
        ArgumentCaptor<Section.SectionEnd> sectionEndTrack3_2 = ArgumentCaptor.forClass(Section.SectionEnd.class);
        verify(track1, times(3)).executeTicks(eq(rand), sectionEndTrack3_1.capture(), eq(250), eq(false), eq(true));
        verify(track2, times(3)).executeTicks(eq(rand), sectionEndTrack3_2.capture(), eq(250), eq(false), eq(true));
        assertEquals(630, sectionEndTrack3_1.getValue().intValue());
        assertEquals(630, sectionEndTrack3_2.getValue().intValue());
    }
}
