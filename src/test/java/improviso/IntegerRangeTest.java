/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
/**
 *
 * @author FernieCanto
 */
public class IntegerRangeTest extends ImprovisoTest {
    @Test
    public void testFixedValueRange() {
        IntegerRange fixedValue = new IntegerRange(50);
        assertEquals(new Integer(50), fixedValue.getValue(getRandomMock()));
        assertEquals(new Integer(50), fixedValue.getValue(getRandomMock(), 0.0));
        assertEquals(new Integer(50), fixedValue.getValue(getRandomMock(), 0.5));
        assertEquals(new Integer(50), fixedValue.getValue(getRandomMock(), 1.0));
    }
    
    @Test
    public void testMinMaxValueRange() {
        IntegerRange fixedValue = new IntegerRange(50, 100);
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(0);
        assertEquals(new Integer(50), fixedValue.getValue(getRandomMock()));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(1);
        assertEquals(new Integer(51), fixedValue.getValue(getRandomMock()));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(49);
        assertEquals(new Integer(99), fixedValue.getValue(getRandomMock()));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(50);
        assertEquals(new Integer(100), fixedValue.getValue(getRandomMock()));
    }
    
    @Test
    public void testVariableValueRange() {
        IntegerRange fixedValue = new IntegerRange(50, 50, 100, 100);
        assertEquals(new Integer(50), fixedValue.getValue(getRandomMock()));
        assertEquals(new Integer(50), fixedValue.getValue(getRandomMock(), 0.0));
        assertEquals(new Integer(75), fixedValue.getValue(getRandomMock(), 0.5));
        assertEquals(new Integer(100), fixedValue.getValue(getRandomMock(), 1.0));
    }
    
    @Test
    public void testVariableMinMaxValueRange() {
        IntegerRange fixedValue = new IntegerRange(0, 50, 100, 200);
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(1);
        assertEquals(new Integer(1), fixedValue.getValue(getRandomMock(), 0.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(50);
        assertEquals(new Integer(50), fixedValue.getValue(getRandomMock(), 0.0));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(2);
        assertEquals(new Integer(52), fixedValue.getValue(getRandomMock(), 0.5));
        when(getRandomMock().nextInt(anyInt())).thenReturn(75);
        assertEquals(new Integer(125), fixedValue.getValue(getRandomMock(), 0.5));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(3);
        assertEquals(new Integer(103), fixedValue.getValue(getRandomMock(), 1.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(100);
        assertEquals(new Integer(200), fixedValue.getValue(getRandomMock(), 1.0));
    }
    
    @Test
    public void testVariableMinMaxValueResolutionRange() {
        IntegerRange fixedValue = new IntegerRange(0, 50, 100, 200, 10);
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(1);
        assertEquals(new Integer(0), fixedValue.getValue(getRandomMock(), 0.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(4);
        assertEquals(new Integer(0), fixedValue.getValue(getRandomMock(), 0.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(5);
        assertEquals(new Integer(10), fixedValue.getValue(getRandomMock(), 0.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(9);
        assertEquals(new Integer(10), fixedValue.getValue(getRandomMock(), 0.0));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(2);
        assertEquals(new Integer(50), fixedValue.getValue(getRandomMock(), 0.5));
        when(getRandomMock().nextInt(anyInt())).thenReturn(75);
        assertEquals(new Integer(130), fixedValue.getValue(getRandomMock(), 0.5));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(3);
        assertEquals(new Integer(100), fixedValue.getValue(getRandomMock(), 1.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(100);
        assertEquals(new Integer(200), fixedValue.getValue(getRandomMock(), 1.0));
    }
}
