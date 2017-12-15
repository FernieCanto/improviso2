/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import improviso.mocks.*;
import java.util.ArrayList;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class FixedSectionTest extends ImprovisoTest {
    private static final int PATTERN1_LENGTH = 200;
    private static final int PATTERN2_LENGTH = 300;
    private PatternMock pattern1;
    private PatternMock pattern2;
    private GroupMock group1;
    private GroupMock group2;
            
    @Before
    public void setUp() {
        PatternMock.PatternMockBuilder patternBuilder1 = new PatternMock.PatternMockBuilder();
        ArrayList<NoteMock> notes1 = new ArrayList<>();
        notes1.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(10, 150, 50, 10, 1)
        ).build());
        this.pattern1 = patternBuilder1.build();
        this.pattern1.setNextDuration(FixedSectionTest.PATTERN1_LENGTH);
        this.pattern1.setNotes(notes1);
        
        GroupMock.GroupMockBuilder groupBuilder1 = new GroupMock.GroupMockBuilder();
        this.group1 = groupBuilder1.build();
        this.group1.setNextMessage(new GroupMessage("test1"));
        this.group1.setNextPattern(this.pattern1);
        
        PatternMock.PatternMockBuilder patternBuilder2 = new PatternMock.PatternMockBuilder();
        ArrayList<NoteMock> notes2 = new ArrayList<>();
        notes2.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(10, 180, 40, 10, 1)
        ).build());
        notes2.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(20, 250, 50, 20, 1)
        ).build());
        this.pattern2 = patternBuilder2.build();
        this.pattern2.setNextDuration(FixedSectionTest.PATTERN2_LENGTH);
        this.pattern2.setNotes(notes2);
        
        GroupMock.GroupMockBuilder groupBuilder2 = new GroupMock.GroupMockBuilder();
        this.group2 = groupBuilder2.build();
        this.group2.setNextMessage(new GroupMessage("test2"));
        this.group2.setNextPattern(this.pattern2);
    }
    
    @Test
    public void testBuildFixedSection() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(new IntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new TrackMock.TrackMockBuilder().setRootGroup(this.group1).build());
        section = sectionBuilder.build();
        
        assertNotNull(section);
        assertEquals(1, section.getTracks().size());
        
        //assertEquals(500, section.getEnd().intValue());
        assertEquals(0, section.getActualEnd());
    }
    
    @Test(expected = ImprovisoException.class)
    public void testExecuteSectionNoTracks() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(new IntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        section = sectionBuilder.build();
        section.execute(getRandomMock());
    }
    
    @Test
    public void testExecuteSectionOneTrack() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(new IntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.group1).build());
        section = sectionBuilder.build();
        assertEquals(1, section.getTracks().size());
        
        this.pattern1.resetExecutions();
        MIDINoteList notes = section.execute(getRandomMock());
        
        assertEquals(3, this.pattern1.getExecutions()); // 200 - 400 - 600
        assertEquals(600, section.getActualEnd());
        assertEquals(3, notes.size());
    }
    
    @Test
    public void testExecuteFixedSectionTwoTracks() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(new IntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.group1).setId("track1").build());
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.group2).setId("track2").build());
        section = sectionBuilder.build();
        assertEquals(2, section.getTracks().size());
        
        this.pattern1.resetExecutions();
        this.pattern2.resetExecutions();
        MIDINoteList notes = section.execute(getRandomMock());
        
        assertEquals(3, this.pattern1.getExecutions()); // 200 - 400 - 600
        assertEquals(2, this.pattern2.getExecutions()); // 300 - 600
        assertEquals(600, section.getActualEnd());
        assertEquals(7, notes.size());
    }
    
    @Test
    public void testExecuteFixedSectionOneTrackInterrupt() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(new IntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.group1).build());
        sectionBuilder.setInterruptTracks(true);
        section = sectionBuilder.build();
        
        this.pattern1.resetExecutions();
        MIDINoteList notes = section.execute(getRandomMock());
        
        assertEquals(3, this.pattern1.getExecutions()); // 200 - 400 - 500
        assertEquals(500, section.getActualEnd());
        assertEquals(2, notes.size());
    }
    
    @Test
    public void testExecuteFixedSectionTwoTracksInterrupt() throws ImprovisoException {
        FixedSection section;
        FixedSection.FixedSectionBuilder sectionBuilder = new FixedSection.FixedSectionBuilder();
        sectionBuilder.setLength(new IntegerRangeMock(500)).setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.group1).setId("track1").build());
        sectionBuilder.addTrack(new Track.TrackBuilder().setRootGroup(this.group2).setId("track2").build());
        sectionBuilder.setInterruptTracks(true);
        section = sectionBuilder.build();
        
        this.pattern1.resetExecutions();
        this.pattern2.resetExecutions();
        MIDINoteList notes = section.execute(getRandomMock());
        
        assertEquals(3, this.pattern1.getExecutions()); // 200 - 400 - 500
        assertEquals(2, this.pattern2.getExecutions()); // 300 - 500
        assertEquals(500, section.getActualEnd());
        assertEquals(5, notes.size());
    }
}
