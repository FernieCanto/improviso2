/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import improviso.mocks.GroupSignalMock;
import improviso.mocks.RandomMock;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author User
 */
public class SequenceGroupTest {
    private Pattern pattern1;
    private Pattern pattern2;
    private LeafGroup leafGroup1;
    private LeafGroup leafGroup2;
    
    @Before
    public void setUp() {
        this.pattern1 = mock(Pattern.class);
        this.pattern2 = mock(Pattern.class);
        
        LeafGroup.LeafGroupBuilder leafBuilder1 = new LeafGroup.LeafGroupBuilder();
        LeafGroup.LeafGroupBuilder leafBuilder2 = new LeafGroup.LeafGroupBuilder();
        
        leafBuilder1.setId("leafGroup1")
                .setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        this.leafGroup1 = leafBuilder1.setLeafPattern(this.pattern1).build();
        leafBuilder2.setId("leafGroup2")
                .setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        this.leafGroup2 = leafBuilder2.setLeafPattern(this.pattern2).build();
    }
    
    @After
    public void tearDown() {
        this.pattern1 = null;
        this.pattern2 = null;
        this.leafGroup1 = null;
        this.leafGroup2 = null;
    }
    
    /**
     * Test of generateGroupXML method, of class Group.
     */
    @Test
    public void testBuildSequenceGroup() {
        RandomMock random = new RandomMock();
        SequenceGroup.SequenceGroupBuilder seqBuilder = new SequenceGroup.SequenceGroupBuilder();
        SequenceGroup seqGroup;
        seqBuilder.setId("seqGroup")
                .setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        seqBuilder.addChild(this.leafGroup1, null, null).addChild(this.leafGroup2, null, null);
        seqGroup = seqBuilder.build();
        
        assertNotNull(seqGroup);
        assertEquals(2, seqGroup.getChildren().size());
        assertTrue(seqGroup.getChildren().contains(this.leafGroup1));
        assertTrue(seqGroup.getChildren().contains(this.leafGroup2));
        assertEquals(this.pattern1, seqGroup.execute(random));
        assertEquals(this.pattern2, seqGroup.execute(random));
        assertEquals(this.pattern1, seqGroup.execute(random));
    }
    
    /**
     * Test of generateGroupXML method, of class Group.
     */
    @Test
    public void testSequenceGroupRepetitions() {
        RandomMock random = new RandomMock();
        SequenceGroup.SequenceGroupBuilder seqBuilder = new SequenceGroup.SequenceGroupBuilder();
        SequenceGroup seqGroup;
        seqBuilder.setId("seqGroupRepet")
                .setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        seqBuilder.addChild(this.leafGroup1, 2, null).addChild(this.leafGroup2, 3, null);
        seqGroup = seqBuilder.build();
        
        assertEquals(this.pattern1, seqGroup.execute(random));
        assertEquals(this.pattern1, seqGroup.execute(random));
        assertEquals(this.pattern2, seqGroup.execute(random));
        assertEquals(this.pattern2, seqGroup.execute(random));
        assertEquals(this.pattern2, seqGroup.execute(random));
        assertEquals(this.pattern1, seqGroup.execute(random));
        assertEquals(this.pattern1, seqGroup.execute(random));
    }
    
    /**
     * Test of generateGroupXML method, of class Group.
     */
    @Test
    public void testSequenceGroupInertia() {
        RandomMock random = new RandomMock();
        SequenceGroup.SequenceGroupBuilder seqBuilder = new SequenceGroup.SequenceGroupBuilder();
        SequenceGroup seqGroup;
        seqBuilder.setId("seqGroupInert")
                .setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        seqBuilder.addChild(this.leafGroup1, 0, 0.3).addChild(this.leafGroup2, 0, 0.8);
        seqGroup = seqBuilder.build();
        
        assertEquals(this.pattern1, seqGroup.execute(random));
        
        random.addDouble(0.21);
        assertEquals(this.pattern1, seqGroup.execute(random));
        
        random.addDouble(0.32);
        assertEquals(this.pattern2, seqGroup.execute(random));
        
        random.addDouble(0.63);
        assertEquals(this.pattern2, seqGroup.execute(random));
        
        random.addDouble(0.75);
        assertEquals(this.pattern2, seqGroup.execute(random));
        
        random.addDouble(0.85);
        assertEquals(this.pattern1, seqGroup.execute(random));
    }
}
