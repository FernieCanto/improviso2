/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import improviso.mocks.GroupSignalMock;
import improviso.mocks.IntegerRangeMock;
import improviso.mocks.PatternMock;
import improviso.mocks.RandomMock;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author FernieCanto
 */
public class RandomGroupTest {
    private Pattern pattern1;
    private Pattern pattern2;
    private Pattern pattern3;
    private LeafGroup leafGroup1;
    private LeafGroup leafGroup2;
    private LeafGroup leafGroup3;
    
    @Before
    public void setUp() {
        PatternMock.PatternMockBuilder patternBuilder1 = new PatternMock.PatternMockBuilder();
        PatternMock.PatternMockBuilder patternBuilder2 = new PatternMock.PatternMockBuilder();
        PatternMock.PatternMockBuilder patternBuilder3 = new PatternMock.PatternMockBuilder();
        
        patternBuilder1.setId("pattern1").setDuration(new IntegerRangeMock(100));
        this.pattern1 = patternBuilder1.build();
        patternBuilder2.setId("pattern2").setDuration(new IntegerRangeMock(100));
        this.pattern2 = patternBuilder2.build();
        patternBuilder3.setId("pattern3").setDuration(new IntegerRangeMock(100));
        this.pattern3 = patternBuilder3.build();
        
        LeafGroup.LeafGroupBuilder leafBuilder1 = new LeafGroup.LeafGroupBuilder();
        LeafGroup.LeafGroupBuilder leafBuilder2 = new LeafGroup.LeafGroupBuilder();
        LeafGroup.LeafGroupBuilder leafBuilder3 = new LeafGroup.LeafGroupBuilder();
        
        leafBuilder1.setId("leafGroup1")
                .setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        this.leafGroup1 = leafBuilder1.setLeafPattern(this.pattern1).build();
        leafBuilder2.setId("leafGroup2")
                .setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        this.leafGroup2 = leafBuilder2.setLeafPattern(this.pattern2).build();
        leafBuilder3.setId("leafGroup3")
                .setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        this.leafGroup3 = leafBuilder3.setLeafPattern(this.pattern3).build();
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
    public void testBuildSequenceGroup() throws Exception {
        RandomMock random = new RandomMock();
        RandomGroup.RandomGroupBuilder rndBuilder = new RandomGroup.RandomGroupBuilder();
        RandomGroup rndGroup;
        rndBuilder.setId("rndGroup")
                .setFinishedSignal(new GroupSignalMock())
                .setInterruptSignal(new GroupSignalMock());
        rndBuilder.addChild(this.leafGroup1, 3, 0, 0.0).addChild(this.leafGroup2, 5, 0, 0.0).addChild(this.leafGroup3, 2, 0, 0.0);
        rndGroup = rndBuilder.build();
        
        assertNotNull(rndGroup);
        assertEquals(3, rndGroup.getChildren().size());
        assertTrue(rndGroup.getChildren().contains(this.leafGroup1));
        assertTrue(rndGroup.getChildren().contains(this.leafGroup2));
        assertTrue(rndGroup.getChildren().contains(this.leafGroup3));
        
        random.addInteger(2);
        assertEquals(this.pattern1, rndGroup.execute(random));
        
        random.addInteger(3);
        assertEquals(this.pattern2, rndGroup.execute(random));
        
        random.addInteger(7);
        assertEquals(this.pattern2, rndGroup.execute(random));
        
        random.addInteger(8);
        assertEquals(this.pattern3, rndGroup.execute(random));
    }
}
