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
public class LeafGroupTest extends ImprovisoTest {
    /**
     * Test of generateGroupXML method, of class Group.
     */
    @Test
    public void testBuildLeafGroup() {
        Pattern pattern = mock(Pattern.class);
        
        LeafGroup.LeafGroupBuilder builder = new LeafGroup.LeafGroupBuilder();
        LeafGroup group = builder.setLeafPattern(pattern).build();
        
        assertNotNull(group);
        assertEquals(pattern, group.selectPattern(getRandomMock()));
    }
}
