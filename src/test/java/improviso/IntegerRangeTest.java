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
 * @author FernieCanto
 */
public class IntegerRangeTest {
    @Test
    public void testFixedValueRange() {
        RandomMock random = new RandomMock();
        IntegerRange fixedValue = new IntegerRange(50);
        assertEquals(new Integer(50), fixedValue.getValue(random));
        assertEquals(new Integer(50), fixedValue.getValue(random, 0.0));
        assertEquals(new Integer(50), fixedValue.getValue(random, 0.5));
        assertEquals(new Integer(50), fixedValue.getValue(random, 1.0));
    }
    
    @Test
    public void testMinMaxValueRange() {
        RandomMock random = new RandomMock();
        random.addInteger(0);
        random.addInteger(1);
        random.addInteger(49);
        random.addInteger(50);
        IntegerRange fixedValue = new IntegerRange(50, 100);
        assertEquals(new Integer(50), fixedValue.getValue(random));
        assertEquals(new Integer(51), fixedValue.getValue(random));
        assertEquals(new Integer(99), fixedValue.getValue(random));
        assertEquals(new Integer(100), fixedValue.getValue(random));
    }
    
    @Test
    public void testVariableValueRange() {
        RandomMock random = new RandomMock();
        IntegerRange fixedValue = new IntegerRange(50, 50, 100, 100);
        assertEquals(new Integer(50), fixedValue.getValue(random));
        assertEquals(new Integer(50), fixedValue.getValue(random, 0.0));
        assertEquals(new Integer(75), fixedValue.getValue(random, 0.5));
        assertEquals(new Integer(100), fixedValue.getValue(random, 1.0));
    }
    
    @Test
    public void testVariableMinMaxValueRange() {
        RandomMock random = new RandomMock();
        random.addInteger(1);
        random.addInteger(50);
        random.addInteger(2);
        random.addInteger(75);
        random.addInteger(3);
        random.addInteger(100);
        IntegerRange fixedValue = new IntegerRange(0, 50, 100, 200);
        assertEquals(new Integer(1), fixedValue.getValue(random, 0.0));
        assertEquals(51, random.getLastBound());
        assertEquals(new Integer(50), fixedValue.getValue(random, 0.0));
        
        assertEquals(new Integer(52), fixedValue.getValue(random, 0.5));
        assertEquals(76, random.getLastBound());
        assertEquals(new Integer(125), fixedValue.getValue(random, 0.5));
        
        assertEquals(new Integer(103), fixedValue.getValue(random, 1.0));
        assertEquals(101, random.getLastBound());
        assertEquals(new Integer(200), fixedValue.getValue(random, 1.0));
    }
    
    @Test
    public void testVariableMinMaxValueResolutionRange() {
        RandomMock random = new RandomMock();
        IntegerRange fixedValue = new IntegerRange(0, 50, 100, 200, 10);
        
        random.addInteger(1);
        random.addInteger(4);
        random.addInteger(5);
        random.addInteger(9);
        assertEquals(new Integer(0), fixedValue.getValue(random, 0.0));
        assertEquals(new Integer(0), fixedValue.getValue(random, 0.0));
        assertEquals(new Integer(10), fixedValue.getValue(random, 0.0));
        assertEquals(new Integer(10), fixedValue.getValue(random, 0.0));
        
        random.addInteger(2);
        random.addInteger(75);
        assertEquals(new Integer(50), fixedValue.getValue(random, 0.5));
        assertEquals(new Integer(130), fixedValue.getValue(random, 0.5));
        
        random.addInteger(3);
        random.addInteger(100);
        assertEquals(new Integer(100), fixedValue.getValue(random, 1.0));
        assertEquals(new Integer(200), fixedValue.getValue(random, 1.0));
    }
}
