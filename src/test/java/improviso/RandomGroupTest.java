package improviso;

import java.util.Random;
import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RandomGroupTest extends ImprovisoTest {
    final private PatternExecution pattern1;
    final private PatternExecution pattern2;
    final private PatternExecution pattern3;
    final private LeafGroup leafGroup1;
    final private LeafGroup leafGroup2;
    final private LeafGroup leafGroup3;
    
    public RandomGroupTest() {
        this.pattern1 = mock(PatternExecution.class);
        this.pattern2 = mock(PatternExecution.class);
        this.pattern3 = mock(PatternExecution.class);
        
        this.leafGroup1 = mock(LeafGroup.class);
        when(this.leafGroup1.execute(any(Random.class))).thenReturn(this.pattern1);
        this.leafGroup2 = mock(LeafGroup.class);
        when(this.leafGroup2.execute(any(Random.class))).thenReturn(this.pattern2);
        this.leafGroup3 = mock(LeafGroup.class);
        when(this.leafGroup3.execute(any(Random.class))).thenReturn(this.pattern3);
    }
    
    @Test
    public void testBuildRandomGroup() throws Exception {
        RandomGroup.RandomGroupBuilder rndBuilder = new RandomGroup.RandomGroupBuilder();
        RandomGroup rndGroup;
        rndBuilder.setId("rndGroup")
                .setFinishedSignal(mock(GroupSignal.class))
                .setInterruptSignal(mock(GroupSignal.class));
        rndBuilder.addChild(this.leafGroup1, 3, 0, 0.0).addChild(this.leafGroup2, 5, 0, 0.0).addChild(this.leafGroup3, 2, 0, 0.0);
        rndGroup = rndBuilder.build();
        
        assertNotNull(rndGroup);
        assertEquals(3, rndGroup.getChildren().size());
        assertTrue(rndGroup.getChildren().contains(this.leafGroup1));
        assertTrue(rndGroup.getChildren().contains(this.leafGroup2));
        assertTrue(rndGroup.getChildren().contains(this.leafGroup3));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(2);
        assertEquals(this.pattern1, rndGroup.execute(getRandomMock()));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(3);
        assertEquals(this.pattern2, rndGroup.execute(getRandomMock()));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(7);
        assertEquals(this.pattern2, rndGroup.execute(getRandomMock()));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(8);
        assertEquals(this.pattern3, rndGroup.execute(getRandomMock()));
    }
}
