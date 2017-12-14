/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.mocks;

import improviso.DoubleRange;
import java.util.Random;

/**
 * @author Fernie Canto
 */
public class DoubleRangeMock extends DoubleRange {
    private final double fixedVal;
    public DoubleRangeMock(double fixedVal) {
        super(fixedVal, fixedVal, fixedVal, fixedVal);
        this.fixedVal = fixedVal;
    }
    
    private double getValue() {
        return this.fixedVal;
    }
    
    @Override
    public Double getValue(Random rand) {
        return this.getValue();
    }
    
    @Override
    public Double getValue(Random rand, double position) {
        return this.getValue();
    }
}
