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
        assertEquals(50, (int)fixedValue.getValue(getRandomMock()));
        assertEquals(50, (int)fixedValue.getValue(getRandomMock(), 0.0));
        assertEquals(50, (int)fixedValue.getValue(getRandomMock(), 0.5));
        assertEquals(50, (int)fixedValue.getValue(getRandomMock(), 1.0));
    }
    
    @Test
    public void testMinMaxValueRange() {
        IntegerRange fixedValue = new IntegerRange(50, 100);
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(0);
        assertEquals(50, (int)fixedValue.getValue(getRandomMock()));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(1);
        assertEquals(51, (int)fixedValue.getValue(getRandomMock()));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(49);
        assertEquals(99, (int)fixedValue.getValue(getRandomMock()));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(50);
        assertEquals(100, (int)fixedValue.getValue(getRandomMock()));
    }
    
    @Test
    public void testVariableValueRange() {
        IntegerRange fixedValue = new IntegerRange(50, 50, 100, 100);
        assertEquals(50, (int)fixedValue.getValue(getRandomMock()));
        assertEquals(50, (int)fixedValue.getValue(getRandomMock(), 0.0));
        assertEquals(75, (int)fixedValue.getValue(getRandomMock(), 0.5));
        assertEquals(100, (int)fixedValue.getValue(getRandomMock(), 1.0));
    }
    
    @Test
    public void testVariableMinMaxValueRange() {
        IntegerRange fixedValue = new IntegerRange(0, 50, 100, 200);
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(1);
        assertEquals(1, (int)fixedValue.getValue(getRandomMock(), 0.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(50);
        assertEquals(50, (int)fixedValue.getValue(getRandomMock(), 0.0));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(2);
        assertEquals(52, (int)fixedValue.getValue(getRandomMock(), 0.5));
        when(getRandomMock().nextInt(anyInt())).thenReturn(75);
        assertEquals(125, (int)fixedValue.getValue(getRandomMock(), 0.5));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(3);
        assertEquals(103, (int)fixedValue.getValue(getRandomMock(), 1.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(100);
        assertEquals(200, (int)fixedValue.getValue(getRandomMock(), 1.0));
    }

    @Test
    public void testVariableMinMaxValueResolutionRange() {
        IntegerRange fixedValue = new IntegerRange(0, 50, 100, 200, 10);
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(1);
        assertEquals(0, (int)fixedValue.getValue(getRandomMock(), 0.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(4);
        assertEquals(0, (int)fixedValue.getValue(getRandomMock(), 0.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(5);
        assertEquals(10, (int)fixedValue.getValue(getRandomMock(), 0.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(9);
        assertEquals(10, (int)fixedValue.getValue(getRandomMock(), 0.0));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(2);
        assertEquals(50, (int)fixedValue.getValue(getRandomMock(), 0.5));
        when(getRandomMock().nextInt(anyInt())).thenReturn(75);
        assertEquals(130, (int)fixedValue.getValue(getRandomMock(), 0.5));
        
        when(getRandomMock().nextInt(anyInt())).thenReturn(3);
        assertEquals(100, (int)fixedValue.getValue(getRandomMock(), 1.0));
        when(getRandomMock().nextInt(anyInt())).thenReturn(100);
        assertEquals(200, (int)fixedValue.getValue(getRandomMock(), 1.0));
    }
}
