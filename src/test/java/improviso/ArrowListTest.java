/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import improviso.mocks.RandomMock;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class ArrowListTest {
    RandomMock random;
    
    @Before
    public void setUp() {
        random = new RandomMock();
    }
    
    @Test
    public void testCreateArrowList() {
        ArrowList list = new ArrowList();
        
        assertEquals(0, list.getNumArrows());
        assertNull(list.getNextDestination(random));
    }
    
    @Test
    public void testOneArrow() {
        ArrowList list = new ArrowList();
        Arrow.ArrowBuilder arrowBuilder = new Arrow.ArrowBuilder();
        list.addArrow(arrowBuilder.setDestinationSection("destinationTest").build());
        
        assertEquals(1, list.getNumArrows());
        assertEquals("destinationTest", list.getNextDestination(random));
    }
    
    @Test
    public void testThreeArrows() {
        ArrowList list = new ArrowList();
        list.verbose();
        Arrow.ArrowBuilder arrowBuilder1 = new Arrow.ArrowBuilder()
                .setDestinationSection("destinationTest1")
                .setProbability(3);
        Arrow.ArrowBuilder arrowBuilder2 = new Arrow.ArrowBuilder()
                .setDestinationSection("destinationTest2")
                .setProbability(5);
        Arrow.ArrowBuilder arrowBuilder3 = new Arrow.ArrowBuilder()
                .setDestinationSection("destinationTest3")
                .setProbability(2);
        list.addArrow(arrowBuilder1.build());
        list.addArrow(arrowBuilder2.build());
        list.addArrow(arrowBuilder3.build());
        
        assertEquals(3, list.getNumArrows());
        
        random.addInteger(2);
        assertEquals("destinationTest1", list.getNextDestination(random));
        
        random.addInteger(4);
        assertEquals("destinationTest2", list.getNextDestination(random));
        
        random.addInteger(7);
        assertEquals("destinationTest2", list.getNextDestination(random));
        
        random.addInteger(8);
        assertEquals("destinationTest3", list.getNextDestination(random));
    }
    
    @Test
    public void testThreeArrowsProbabilities() {
        ArrowList list = new ArrowList();
        Arrow.ArrowBuilder arrowBuilder1 = new Arrow.ArrowBuilder()
                .setDestinationSection("destinationTest1")
                .setProbability(3)
                .setMaxExecutions(1);
        Arrow.ArrowBuilder arrowBuilder2 = new Arrow.ArrowBuilder()
                .setDestinationSection("destinationTest2")
                .setProbability(5)
                .setMaxExecutions(2);
        Arrow.ArrowBuilder arrowBuilder3 = new Arrow.ArrowBuilder()
                .setDestinationSection("destinationTest3")
                .setProbability(2)
                .setMaxExecutions(1);
        list.addArrow(arrowBuilder1.build());
        list.addArrow(arrowBuilder2.build());
        list.addArrow(arrowBuilder3.build());
        
        assertEquals(3, list.getNumArrows());
        
        random.addInteger(2);
        assertEquals("destinationTest1", list.getNextDestination(random));
        
        random.addInteger(2);
        assertEquals("destinationTest2", list.getNextDestination(random));
        
        random.addInteger(4);
        assertEquals("destinationTest2", list.getNextDestination(random));
        
        random.addInteger(4);
        assertEquals("destinationTest3", list.getNextDestination(random));
        
        random.addInteger(7);
        assertEquals(null, list.getNextDestination(random));
    }
    
    @Test
    public void testThreeArrowsProbabilitiesEndComposition() {
        ArrowList list = new ArrowList();
        Arrow.ArrowBuilder arrowBuilder1 = new Arrow.ArrowBuilder()
                .setDestinationSection("destinationTest1")
                .setProbability(3)
                .setMaxExecutions(1)
                .setEndCompositionAfterMax(true);
        Arrow.ArrowBuilder arrowBuilder2 = new Arrow.ArrowBuilder()
                .setDestinationSection("destinationTest2")
                .setProbability(5)
                .setMaxExecutions(2);
        Arrow.ArrowBuilder arrowBuilder3 = new Arrow.ArrowBuilder()
                .setDestinationSection("destinationTest3")
                .setProbability(10)
                .setMaxExecutions(1);
        list.addArrow(arrowBuilder1.build());
        list.addArrow(arrowBuilder2.build());
        list.addArrow(arrowBuilder3.build());
        
        assertEquals(3, list.getNumArrows());
        
        random.addInteger(2);
        assertEquals("destinationTest1", list.getNextDestination(random));
        
        random.addInteger(2);
        assertEquals(null, list.getNextDestination(random));
    }
}
