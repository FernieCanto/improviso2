/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

/**
 *
 * @author User
 */
public class RepetitionGroupTest extends ImprovisoTest {
    private Pattern pattern1;
    private Pattern pattern2;
        
    @Before
    public void setUp() {
        pattern1 = mock(Pattern.class);
        pattern2 = mock(Pattern.class);
    }
    
    @Test
    public void testSequenceGroupSignals() {
        LeafGroup leafGroup1;
        LeafGroup leafGroup2;
        GroupSignal signalMockFinished1 = mock(GroupSignal.class);
        GroupSignal signalMockFinished2 = mock(GroupSignal.class);
        GroupSignal signalMockInterrupt1 = mock(GroupSignal.class);
        GroupSignal signalMockInterrupt2 = mock(GroupSignal.class);
        
        LeafGroup.LeafGroupBuilder leafBuilder1 = new LeafGroup.LeafGroupBuilder();
        leafBuilder1.setId("leafGroup1")
                .setFinishedSignal(signalMockFinished1)
                .setInterruptSignal(signalMockInterrupt1);
        leafGroup1 = leafBuilder1.setLeafPattern(pattern1).build();
        
        LeafGroup.LeafGroupBuilder leafBuilder2 = new LeafGroup.LeafGroupBuilder();
        leafBuilder2.setId("leafGroup2")
                .setFinishedSignal(signalMockFinished2)
                .setInterruptSignal(signalMockInterrupt2);
        leafGroup2 = leafBuilder2.setLeafPattern(pattern2).build();
        
        SequenceGroup.SequenceGroupBuilder seqBuilder = new SequenceGroup.SequenceGroupBuilder();
        SequenceGroup seqGroup;
        seqBuilder.setId("seqGroup")
                .setFinishedSignal(mock(GroupSignal.class))
                .setInterruptSignal(mock(GroupSignal.class));
        seqBuilder.addChild(leafGroup1, null, null).addChild(leafGroup2, null, null);
        seqGroup = seqBuilder.build();
        
        when(signalMockFinished2.signal(anyInt(), any(Random.class))).thenReturn(true);
        
        seqGroup.execute(getRandomMock());
        assertFalse(seqGroup.getMessage().getFinished());
        assertFalse(seqGroup.getMessage().getInterrupt());
        
        when(signalMockInterrupt1.signal(anyInt(), any(Random.class))).thenReturn(true);
        
        seqGroup.execute(getRandomMock());
        assertTrue(seqGroup.getMessage().getFinished());
        assertFalse(seqGroup.getMessage().getInterrupt());
        
        seqGroup.execute(getRandomMock());
        assertFalse(seqGroup.getMessage().getFinished());
        assertTrue(seqGroup.getMessage().getInterrupt());
    }
}
