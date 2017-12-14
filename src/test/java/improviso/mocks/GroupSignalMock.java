/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.mocks;

import improviso.GroupSignal;
import java.util.Random;

/**
 *
 * @author User
 */
public class GroupSignalMock extends GroupSignal {
    private boolean nextResult;
    
    public GroupSignalMock() {
        super(1, 1, 1.0);
        nextResult = false;
    }
    
    public GroupSignalMock(Integer min, Integer max, Double probability) {
        super(min, max, probability);
        nextResult = false;
    }
    
    public void setNextResult(boolean nextResult) {
        this.nextResult = nextResult;
    }
    
    @Override
    public boolean signal(int executions, Random random) {
        return this.nextResult;
    }
}
