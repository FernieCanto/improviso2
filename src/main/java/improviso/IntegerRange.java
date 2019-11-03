package improviso;

import java.util.Random;

/**
 * Randomly generates an integer valueMin within a set interval. This interval
 * can vary linearly between an initial and a final interval, and the generated
 * valueMin will depend on the position of the section currently being executed.
 * @author Fernie Canto
 */
public class IntegerRange extends NumberRange<Integer> {
    final private Integer resolution;
    
    public IntegerRange(int val) {
        super(val, val);
        this.resolution = 1;
    }
    
    public IntegerRange(int valMin, int valMax) {
        super(valMin, valMax);
        this.resolution = 1;
    }
    
    public IntegerRange(int valMin, int valMax, int valEndMin, int valEndMax) {
        super(valMin, valMax, valEndMin, valEndMax);
        this.resolution = 1;
    }
    
    public IntegerRange(int valMin, int valMax, int valEndMin, int valEndMax, int resolution) {
        super(valMin, valMax, valEndMin, valEndMax);
        this.resolution = resolution;
    }
    
    @Override
    public Integer getValue(Random random, double position) {
        return getValueMin() + (this.resolution * Math.round((float)getRandomValueAtPosition(random, position) / (float)this.resolution));
    }
    
    private int getRandomValueAtPosition(Random random, double position) {
        return getMinValueAtPosition(position) + getRandomValueWithinInterval(random, position);
    }
    
    private int getMinValueAtPosition(double position) {
        return (int)( (getValueEndMin() - getValueMin()) * position );
    }
    
    private int getRandomValueWithinInterval(Random random, double position) {
        return random.nextInt(this.getValueIntervalAtStart() + getValueIntervalAtPosition(position) + 1);
    }
    
    private int getValueIntervalAtStart() {
        return getValueMax() - getValueMin();
    }
    
    private int getValueIntervalAtEnd() {
        return getValueEndMax() - getValueEndMin();
    }
    
    private int getValueIntervalAtPosition(double position) {
        return (int)( (this.getValueIntervalAtEnd() - this.getValueIntervalAtStart()) * position);
    }
}
