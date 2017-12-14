/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import improviso.mocks.*;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class TrackTest {
    PatternMock pattern1;
    RandomMock random;
    
    @Before
    public void setUp() {
        this.random = new RandomMock();
        PatternMock.PatternMockBuilder patternBuilder = new PatternMock.PatternMockBuilder();
        patternBuilder.setDuration(new IntegerRangeMock(150));
        this.pattern1 = patternBuilder.build();
        
        ArrayList<NoteMock> notes1 = new ArrayList<>();
        notes1.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(10, 50, 50, 100, 1)
        ).build());
        notes1.add((NoteMock) new NoteMock.NoteMockBuilder().setNote(
                new MIDINote(10, 100, 50, 100, 1)
        ).build());
        this.pattern1.setNotes(notes1);
    }
    
    @Test
    public void testCreateTrack() {
        GroupMock group;
        GroupMock.GroupMockBuilder groupBuilder = new GroupMock.GroupMockBuilder();
        groupBuilder.setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        group = groupBuilder.build();
        
        Track track;
        Track.TrackBuilder trackBuilder = new Track.TrackBuilder()
                .setId("trackTest").setRootGroup(group);
        track = trackBuilder.build();
        
        assertNotNull(track);
        assertEquals("trackTest", track.getId());
        
        track.initialize();
        assertEquals(0, track.getCurrentPosition());
    }
    
    @Test
    public void testExecuteTrackNoSectionEnd() {
        GroupMock group;
        GroupMock.GroupMockBuilder groupBuilder = new GroupMock.GroupMockBuilder();
        groupBuilder.setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        group = groupBuilder.build();
        
        Track track;
        Track.TrackBuilder trackBuilder = new Track.TrackBuilder()
                .setId("trackTest").setRootGroup(group);
        track = trackBuilder.build();
        
        assertNotNull(track);
        assertEquals("trackTest", track.getId());
        
        track.initialize();
        group.setNextPattern(this.pattern1);
        
        GroupMessage message = new GroupMessage("test");
        group.setNextMessage(message);
        
        this.pattern1.setNextDuration(150);
        track.selectNextPattern(this.random);
        assertEquals(message, track.getMessage());
        assertEquals(150, track.getEnd());
        assertEquals(2, this.pattern1.getNotes().size());
        
        MIDINoteList notes = track.execute(this.random, new Section.UnknownSectionEnd(), false);
        assertEquals(2, notes.size());
        assertEquals(150, track.getCurrentPosition());
    }
    
    @Test
    public void testExecuteTrackSectionEndInterrupt() {
        GroupMessage message = new GroupMessage("test");
        GroupMock group;
        GroupMock.GroupMockBuilder groupBuilder = new GroupMock.GroupMockBuilder();
        groupBuilder.setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        group = groupBuilder.build();
        
        Track track;
        Track.TrackBuilder trackBuilder = new Track.TrackBuilder()
                .setId("trackTest").setRootGroup(group);
        track = trackBuilder.build();
        
        track.initialize();
        
        group.setNextPattern(this.pattern1);
        group.setNextMessage(message);
        
        this.pattern1.setNextDuration(150);
        track.selectNextPattern(this.random);
        assertEquals(message, track.getMessage());
        assertEquals(150, track.getEnd());
        
        MIDINoteList notes = track.execute(this.random, Section.SectionEnd.createEnd(60), true);
        assertEquals(1, notes.size());
        assertEquals(10, notes.get(0).getLength());
        assertEquals(60, track.getCurrentPosition());
    }
}
