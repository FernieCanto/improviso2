/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import improviso.mocks.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author User
 */
public class RepetitionGroupTest {
    private Pattern pattern1;
    private Pattern pattern2;
        
    @Before
    public void setUp() {
        PatternMock.PatternMockBuilder patternBuilder1 = new PatternMock.PatternMockBuilder();
        PatternMock.PatternMockBuilder patternBuilder2 = new PatternMock.PatternMockBuilder();
        
        patternBuilder1.setId("pattern1").setDuration(new IntegerRangeMock(100));
        pattern1 = patternBuilder1.build();
        patternBuilder2.setId("pattern2").setDuration(new IntegerRangeMock(100));
        pattern2 = patternBuilder2.build();
    }
    
    @Test
    public void testSequenceGroupSignals() {
        RandomMock random = new RandomMock();
        LeafGroup leafGroup1;
        LeafGroup leafGroup2;
        GroupSignalMock signalMockFinished1 = new GroupSignalMock();
        GroupSignalMock signalMockFinished2 = new GroupSignalMock();
        GroupSignalMock signalMockInterrupt1 = new GroupSignalMock();
        GroupSignalMock signalMockInterrupt2 = new GroupSignalMock();
        
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
                .setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        seqBuilder.addChild(leafGroup1, null, null).addChild(leafGroup2, null, null);
        seqGroup = seqBuilder.build();
        
        signalMockFinished2.setNextResult(true);
        
        GroupMessage message;
        seqGroup.execute(random);
        message = seqGroup.getMessage();
        assertFalse(message.getFinished());
        assertFalse(message.getInterrupt());
        
        signalMockInterrupt1.setNextResult(true);
        
        seqGroup.execute(random);
        message = seqGroup.getMessage();
        assertTrue(message.getFinished());
        assertFalse(message.getInterrupt());
        
        seqGroup.execute(random);
        message = seqGroup.getMessage();
        assertFalse(message.getFinished());
        assertTrue(message.getInterrupt());
    }
}
