package improviso;

import java.util.Random;

/**
 *
 * @author Usuário
 */
public class DoubleRange extends NumberRange<Double> {
    public DoubleRange(double val) {
        super(val, val);
    }
    
    public DoubleRange(double valMin, double valMax) {
        super(valMin, valMax);
    }
    
    public DoubleRange(double valMin, double valMax, double valEndMin, double valEndMax) {
        super(valMin, valMax, valEndMin, valEndMax);
    }
    
    @Override
    public Double getValue(Random rand, double position) {
        double returnVal;
        
        returnVal  = getValueMin() + ( (getValueEndMin() - getValueMin()) * position );
        returnVal += rand.nextDouble() * (getValueMax() + ( (getValueEndMax() - getValueMax()) * position));
        
        return returnVal;
    }
}
