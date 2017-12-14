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
        return getValueMin() + (resolution * Math.round(new Float(getVariation(random, position)) / new Float(resolution)));
    }
    
    private int getVariation(Random random, double position) {
        return getBaseValue(position) + getRandomVariation(random, position);
    }
    
    private int getBaseValue(double position) {
        return (int)( (getValueEndMin() - getValueMin()) * position );
    }
    
    private int getRandomVariation(Random random, double position) {
        return random.nextInt(this.getIntervalStart() + getBaseInterval(position) + 1);
    }
    
    private int getIntervalStart() {
        return getValueMax() - getValueMin();
    }
    
    private int getIntervalEnd() {
        return getValueEndMax() - getValueEndMin();
    }
    
    private int getBaseInterval(double position) {
        return (int)( (this.getIntervalEnd() - this.getIntervalStart()) * position);
    }
}
