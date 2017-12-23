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
public class SequenceGroupTest extends ImprovisoTest {
    private PatternExecution execution1;
    private PatternExecution execution2;
    private Pattern pattern1;
    private Pattern pattern2;
    private LeafGroup leafGroup1;
    private LeafGroup leafGroup2;
    
    @Before
    public void setUp() {
        this.execution1 = mock(PatternExecution.class);
        this.execution2 = mock(PatternExecution.class);
        
        this.pattern1 = mock(Pattern.class);
        when(this.pattern1.getNextExecution(getRandomMock())).thenReturn(this.execution1);
        this.pattern2 = mock(Pattern.class);
        when(this.pattern2.getNextExecution(getRandomMock())).thenReturn(this.execution2);
        
        LeafGroup.LeafGroupBuilder leafBuilder1 = new LeafGroup.LeafGroupBuilder();
        LeafGroup.LeafGroupBuilder leafBuilder2 = new LeafGroup.LeafGroupBuilder();
        
        leafBuilder1.setId("leafGroup1")
                .setFinishedSignal(mock(GroupSignal.class))
                .setInterruptSignal(mock(GroupSignal.class));
        this.leafGroup1 = leafBuilder1.setLeafPattern(this.pattern1).build();
        leafBuilder2.setId("leafGroup2")
                .setFinishedSignal(mock(GroupSignal.class))
                .setInterruptSignal(mock(GroupSignal.class));
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
        SequenceGroup.SequenceGroupBuilder seqBuilder = new SequenceGroup.SequenceGroupBuilder();
        SequenceGroup seqGroup;
        seqBuilder.setId("seqGroup")
                .setFinishedSignal(mock(GroupSignal.class))
                .setInterruptSignal(mock(GroupSignal.class));
        seqBuilder.addChild(this.leafGroup1, null, null).addChild(this.leafGroup2, null, null);
        seqGroup = seqBuilder.build();
        
        assertNotNull(seqGroup);
        assertEquals(2, seqGroup.getChildren().size());
        assertTrue(seqGroup.getChildren().contains(this.leafGroup1));
        assertTrue(seqGroup.getChildren().contains(this.leafGroup2));
        assertEquals(this.execution1, seqGroup.execute(getRandomMock()));
        assertEquals(this.execution2, seqGroup.execute(getRandomMock()));
        assertEquals(this.execution1, seqGroup.execute(getRandomMock()));
    }
    
    /**
     * Test of generateGroupXML method, of class Group.
     */
    @Test
    public void testSequenceGroupRepetitions() {
        SequenceGroup.SequenceGroupBuilder seqBuilder = new SequenceGroup.SequenceGroupBuilder();
        SequenceGroup seqGroup;
        seqBuilder.setId("seqGroupRepet")
                .setFinishedSignal(mock(GroupSignal.class))
                .setInterruptSignal(mock(GroupSignal.class));
        seqBuilder.addChild(this.leafGroup1, 2, null).addChild(this.leafGroup2, 3, null);
        seqGroup = seqBuilder.build();
        
        assertEquals(this.execution1, seqGroup.execute(getRandomMock()));
        assertEquals(this.execution1, seqGroup.execute(getRandomMock()));
        assertEquals(this.execution2, seqGroup.execute(getRandomMock()));
        assertEquals(this.execution2, seqGroup.execute(getRandomMock()));
        assertEquals(this.execution2, seqGroup.execute(getRandomMock()));
        assertEquals(this.execution1, seqGroup.execute(getRandomMock()));
        assertEquals(this.execution1, seqGroup.execute(getRandomMock()));
    }
    
    /**
     * Test of generateGroupXML method, of class Group.
     */
    @Test
    public void testSequenceGroupInertia() {
        SequenceGroup.SequenceGroupBuilder seqBuilder = new SequenceGroup.SequenceGroupBuilder();
        SequenceGroup seqGroup;
        seqBuilder.setId("seqGroupInert")
                .setFinishedSignal(mock(GroupSignal.class))
                .setInterruptSignal(mock(GroupSignal.class));
        seqBuilder.addChild(this.leafGroup1, 0, 0.3).addChild(this.leafGroup2, 0, 0.8);
        seqGroup = seqBuilder.build();
        
        assertEquals(this.execution1, seqGroup.execute(getRandomMock()));
        
        when(getRandomMock().nextDouble()).thenReturn(0.21);
        assertEquals(this.execution1, seqGroup.execute(getRandomMock()));
        
        when(getRandomMock().nextDouble()).thenReturn(0.31);
        assertEquals(this.execution2, seqGroup.execute(getRandomMock()));
        
        when(getRandomMock().nextDouble()).thenReturn(0.63);
        assertEquals(this.execution2, seqGroup.execute(getRandomMock()));
        
        when(getRandomMock().nextDouble()).thenReturn(0.75);
        assertEquals(this.execution2, seqGroup.execute(getRandomMock()));
        
        when(getRandomMock().nextDouble()).thenReturn(0.85);
        assertEquals(this.execution1, seqGroup.execute(getRandomMock()));
    }
}
