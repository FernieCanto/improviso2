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
public class VariableSectionTest {
    private static final int PATTERN1_LENGTH = 200;
    private static final int PATTERN2_LENGTH = 300;
    private static final int PATTERN3_LENGTH = 700;
    private RandomMock random;
    private PatternMock pattern1;
    private PatternMock pattern2;
    private PatternMock pattern3;
    private GroupMock group1;
    private GroupMock group2;
    private GroupMock group3;
    
    @Before
    public void setUp() {
        this.random = new RandomMock();
        PatternMock.PatternMockBuilder patternBuilder1 = new PatternMock.PatternMockBuilder();
        ArrayList<NoteMock> notes1 = new ArrayList<>();
        notes1.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(10, 110, 10, 10, 1)
        ).build());
        this.pattern1 = patternBuilder1.build();
        this.pattern1.setNextDuration(VariableSectionTest.PATTERN1_LENGTH);
        this.pattern1.setNotes(notes1);
        
        GroupMock.GroupMockBuilder groupBuilder1 = new GroupMock.GroupMockBuilder();
        this.group1 = groupBuilder1.build();
        this.group1.setNextMessage(new GroupMessage("test1"));
        this.group1.setNextPattern(this.pattern1);
        
        PatternMock.PatternMockBuilder patternBuilder2 = new PatternMock.PatternMockBuilder();
        ArrayList<NoteMock> notes2 = new ArrayList<>();
        notes2.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(10, 90, 20, 10, 1)
        ).build());
        notes2.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(20, 250, 20, 20, 1)
        ).build());
        this.pattern2 = patternBuilder2.build();
        this.pattern2.setNextDuration(VariableSectionTest.PATTERN2_LENGTH);
        this.pattern2.setNotes(notes2);
        
        GroupMock.GroupMockBuilder groupBuilder2 = new GroupMock.GroupMockBuilder();
        this.group2 = groupBuilder2.build();
        this.group2.setNextMessage(new GroupMessage("test2"));
        this.group2.setNextPattern(this.pattern2);
        
        PatternMock.PatternMockBuilder patternBuilder3 = new PatternMock.PatternMockBuilder();
        ArrayList<NoteMock> notes3 = new ArrayList<>();
        notes3.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(10, 10, 10, 10, 1)
        ).build());
        notes3.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(20, 20, 20, 20, 1)
        ).build());
        notes3.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(30, 30, 30, 30, 1)
        ).build());
        this.pattern3 = patternBuilder3.build();
        this.pattern3.setNextDuration(VariableSectionTest.PATTERN3_LENGTH);
        this.pattern3.setNotes(notes3);
        
        GroupMock.GroupMockBuilder groupBuilder3 = new GroupMock.GroupMockBuilder();
        this.group3 = groupBuilder3.build();
        this.group3.setNextMessage(new GroupMessage("test3"));
        this.group3.setNextPattern(this.pattern3);
    }
    
    @Test
    public void testCreateVariableSection() throws ImprovisoException {
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(new TrackMock.TrackMockBuilder().setRootGroup(this.group1).build());
        section = sectionBuilder.build();
        
        assertNotNull(section);
        
        assertEquals(0, section.getActualEnd());
    }
    
    @Test
    public void testExecuteVariableSectionOneTrackFinished() throws ImprovisoException {
        TrackMock track1 = (TrackMock) new TrackMock.TrackMockBuilder().setRootGroup(this.group1).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        section = sectionBuilder.build();
        
        track1.addPositionFinished(null);
        track1.addPositionFinished(null);
        track1.addPositionFinished(600);
        
        this.pattern1.resetExecutions();
        MIDINoteList notes = section.execute(this.random);
        
        assertEquals(3, this.pattern1.getExecutions()); // 200 - 400 - 600
        assertEquals(600, section.getActualEnd());
        assertEquals(3, notes.size());
    }
    
    @Test
    public void testExecuteVariableSectionTwoTracksFinished() throws ImprovisoException {
        TrackMock track1 = (TrackMock) new TrackMock.TrackMockBuilder().setId("track1").setRootGroup(this.group1).build();
        TrackMock track2 = (TrackMock) new TrackMock.TrackMockBuilder().setId("track2").setRootGroup(this.group2).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        section = sectionBuilder.build();
        
        track1.addPositionFinished(null);
        track1.addPositionFinished(null);
        track1.addPositionFinished(600);
        
        track2.addPositionFinished(null);
        track2.addPositionFinished(600);
        
        this.pattern1.resetExecutions();
        this.pattern2.resetExecutions();
        MIDINoteList notes = section.execute(this.random);
        
        assertEquals(3, this.pattern1.getExecutions()); // 200 - 400 - 600!
        assertEquals(2, this.pattern2.getExecutions()); // 300 - 600!
        assertEquals(600, section.getActualEnd());
        assertEquals(7, notes.size());
    }
    
    @Test
    public void testExecuteVariableSectionThreeTracksFinished() throws ImprovisoException {
        TrackMock track1 = (TrackMock) new TrackMock.TrackMockBuilder().setId("track1").setRootGroup(this.group1).build();
        TrackMock track2 = (TrackMock) new TrackMock.TrackMockBuilder().setId("track2").setRootGroup(this.group2).build();
        TrackMock track3 = (TrackMock) new TrackMock.TrackMockBuilder().setId("track3").setRootGroup(this.group3).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        sectionBuilder.addTrack(track3);
        section = sectionBuilder.build();
        
        track1.addPositionFinished(null);
        track1.addPositionFinished(400);
        
        track2.addPositionFinished(null);
        track2.addPositionFinished(600);
        
        track3.addPositionFinished(700);
        
        this.pattern1.resetExecutions();
        this.pattern2.resetExecutions();
        MIDINoteList notes = section.execute(this.random);
        
        assertEquals(4, this.pattern1.getExecutions()); // 200 - 400! - 600 - 800
        assertEquals(3, this.pattern2.getExecutions()); // 300 - 600! - 900
        assertEquals(1, this.pattern3.getExecutions()); // 700!
        assertEquals(900, section.getActualEnd());
        assertEquals(13, notes.size());
    }
    
    @Test
    public void testExecuteVariableSectionOneTrackInterrupt() throws ImprovisoException {
        TrackMock track1 = (TrackMock) new TrackMock.TrackMockBuilder().setRootGroup(this.group1).setId("interruptTrack").build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        section = sectionBuilder.build();
        
        track1.addPositionInterrupt(null);
        track1.addPositionInterrupt(null);
        track1.addPositionInterrupt(600);
        
        this.pattern1.resetExecutions();
        MIDINoteList notes = section.execute(this.random);
        
        assertEquals(3, this.pattern1.getExecutions()); // 200 - 400 - 600!!
        assertEquals(600, section.getActualEnd());
        assertEquals(3, notes.size());
    }
    
    @Test
    public void testExecuteVariableSectionTwoTracksInterrupt() throws ImprovisoException {
        TrackMock track1 = (TrackMock) new TrackMock.TrackMockBuilder().setRootGroup(this.group1).build();
        TrackMock track2 = (TrackMock) new TrackMock.TrackMockBuilder().setRootGroup(this.group2).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        section = sectionBuilder.build();
        track1.addPositionInterrupt(null);
        track1.addPositionInterrupt(null);
        track1.addPositionInterrupt(null);
        track1.addPositionInterrupt(800);
        
        this.pattern1.resetExecutions();
        this.pattern2.resetExecutions();
        MIDINoteList notes = section.execute(this.random);
        
        assertEquals(4, this.pattern1.getExecutions()); // 200 - 400 - 600 - 800!!
        assertEquals(3, this.pattern2.getExecutions()); // 300 - 600 - 900
        assertEquals(900, section.getActualEnd());
        assertEquals(10, notes.size());
    }
    
    @Test
    public void testExecuteVariableSectionThreeTracksFinishedCutSection() throws ImprovisoException {
        TrackMock track1 = (TrackMock) new TrackMock.TrackMockBuilder().setId("track1").setRootGroup(this.group1).build();
        TrackMock track2 = (TrackMock) new TrackMock.TrackMockBuilder().setId("track2").setRootGroup(this.group2).build();
        TrackMock track3 = (TrackMock) new TrackMock.TrackMockBuilder().setId("track3").setRootGroup(this.group3).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        sectionBuilder.addTrack(track3);
        sectionBuilder.setInterruptTracks(true);
        section = sectionBuilder.build();
        
        track1.addPositionFinished(null);
        track1.addPositionFinished(400);
        
        track2.addPositionFinished(null);
        track2.addPositionFinished(600);
        
        track3.addPositionFinished(700);
        
        this.pattern1.resetExecutions();
        this.pattern2.resetExecutions();
        MIDINoteList notes = section.execute(this.random);
        
        assertEquals(4, this.pattern1.getExecutions()); // 200 - 400! - 600 - 700
        assertEquals(3, this.pattern2.getExecutions()); // 300 - 600! - 700
        assertEquals(1, this.pattern3.getExecutions()); // 700!
        assertEquals(700, section.getActualEnd());
        assertEquals(11, notes.size());
    }
    
    @Test
    public void testExecuteVariableSectionTwoTracksInterruptCutSection() throws ImprovisoException {
        TrackMock track1 = (TrackMock) new TrackMock.TrackMockBuilder().setRootGroup(this.group1).build();
        TrackMock track2 = (TrackMock) new TrackMock.TrackMockBuilder().setRootGroup(this.group2).build();
        VariableSection section;
        VariableSection.VariableSectionBuilder sectionBuilder = new VariableSection.VariableSectionBuilder();
        sectionBuilder.setId("sectionTest").setTempo(100);
        sectionBuilder.addTrack(track1);
        sectionBuilder.addTrack(track2);
        sectionBuilder.setInterruptTracks(true);
        section = sectionBuilder.build();
        
        track1.addPositionInterrupt(null);
        track1.addPositionInterrupt(null);
        track1.addPositionInterrupt(null);
        track1.addPositionInterrupt(800);
        
        this.pattern1.resetExecutions();
        this.pattern2.resetExecutions();
        MIDINoteList notes = section.execute(this.random);
        
        assertEquals(4, this.pattern1.getExecutions()); // 200 - 400 - 600 - 800!!
        assertEquals(3, this.pattern2.getExecutions()); // 300 - 600 - 800
        assertEquals(800, section.getActualEnd());
        assertEquals(9, notes.size());
    }
}
