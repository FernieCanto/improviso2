/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GroupSignalTest extends ImprovisoTest {
    @Test
    public void testBelowMinimumSignal() {
        GroupSignal signal = new GroupSignal(3, 5, 1.0);
        assertFalse(signal.signal(3, getRandomMock()));
    }
    
    @Test
    public void testAboveMinimumSignal() {
        GroupSignal signal = new GroupSignal(3, 5, 0.8);
        when(getRandomMock().nextDouble()).thenReturn(0.85);
        assertFalse(signal.signal(4, getRandomMock()));
        
        when(getRandomMock().nextDouble()).thenReturn(0.75);
        assertTrue(signal.signal(4, getRandomMock()));
        
        when(getRandomMock().nextDouble()).thenReturn(0.85);
        assertFalse(signal.signal(5, getRandomMock()));
        
        when(getRandomMock().nextDouble()).thenReturn(0.75);
        assertTrue(signal.signal(5, getRandomMock()));
    }
    
    @Test
    public void testAboveMaximumSignal() {
        GroupSignal signal = new GroupSignal(3, 5, 0.5);
        assertTrue(signal.signal(6, getRandomMock()));
    }
}
