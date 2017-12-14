/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import improviso.mocks.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class GroupSignalTest {
    private RandomMock random;
    
    @Before
    public void SetUp() {
        this.random = new RandomMock();
    }
    
    @Test
    public void testBelowMinimumSignal() {
        GroupSignal signal = new GroupSignal(3, 5, 1.0);
        assertFalse(signal.signal(3, this.random));
    }
    
    @Test
    public void testAboveMinimumSignal() {
        GroupSignal signal = new GroupSignal(3, 5, 0.8);
        this.random.addDouble(0.85);
        assertFalse(signal.signal(4, this.random));
        
        this.random.addDouble(0.75);
        assertTrue(signal.signal(4, this.random));
        
        this.random.addDouble(0.85);
        assertFalse(signal.signal(5, this.random));
        
        this.random.addDouble(0.75);
        assertTrue(signal.signal(5, this.random));
    }
    
    @Test
    public void testAboveMaximumSignal() {
        GroupSignal signal = new GroupSignal(3, 5, 0.5);
        assertTrue(signal.signal(6, this.random));
    }
}
