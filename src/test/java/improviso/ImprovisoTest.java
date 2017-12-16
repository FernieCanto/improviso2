/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.Random;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author User
 */
abstract public class ImprovisoTest {
    final private Random randomMock = mock(Random.class);
    
    protected Random getRandomMock() {
        return this.randomMock;
    }
    
    protected IntegerRange getIntegerRangeMock(int value) {
        IntegerRange range = mock(IntegerRange.class);
        when(range.getValue(any(Random.class))).thenReturn(value);
        when(range.getValue(any(Random.class), anyDouble())).thenReturn(value);
        return range;
    }
    
    protected DoubleRange getDoubleRangeMock(double value) {
        DoubleRange range = mock(DoubleRange.class);
        when(range.getValue(any(Random.class))).thenReturn(value);
        when(range.getValue(any(Random.class), anyDouble())).thenReturn(value);
        return range;
    }
    
    protected Pattern.PatternExecution getPatternExecutionMock(int length) {
        Pattern.PatternExecution execution = mock(Pattern.PatternExecution.class);
        when(execution.getLength()).thenReturn(length);
        when(execution.execute(any(Random.class), anyDouble(), anyInt())).thenReturn(new MIDINoteList());
        return execution;
    }
    
    protected Group getGroupMock(Pattern.PatternExecution execution, GroupMessage message) {
        Pattern pattern1 = mock(Pattern.class);
        when(pattern1.getNextExecution(any(Random.class))).thenReturn(execution);
        
        Group groupMock = mock(Group.class);
        when(groupMock.getMessage()).thenReturn(message);
        when(groupMock.execute(any(Random.class))).thenReturn(execution);
        return groupMock;
    }
    
    protected Group getGroupMock(Pattern.PatternExecution execution) {
        return getGroupMock(execution, mock(GroupMessage.class));
    }
}
