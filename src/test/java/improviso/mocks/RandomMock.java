package improviso.mocks;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Fernie Canto
 */
public class RandomMock extends Random {
    private int lastIntBound = 0;
    private final ArrayList<Double> doubles = new ArrayList<>();
    private final ArrayList<Integer> ints = new ArrayList<>();
    
    public void addDouble(Double nextDouble) {
        doubles.add(nextDouble);
    }
    
    @Override
    public double nextDouble() {
        if (doubles.isEmpty()) {
            return super.nextDouble();
        } else {
            Double nextDouble = doubles.get(0);
            doubles.remove(0);
            return nextDouble;
        }
    }
    
    public void addInteger(Integer nextInt) {
        ints.add(nextInt);
    }
    
    @Override
    public int nextInt() {
        if (ints.isEmpty()) {
            return super.nextInt();
        } else {
            Integer nextInt = ints.get(0);
            ints.remove(0);
            return nextInt;
        }
    }
    
    @Override
    public int nextInt(int bound) {
        lastIntBound = bound;
        if (ints.isEmpty()) {
            return super.nextInt(bound);
        } else if (ints.get(0) < bound) {
            Integer nextInt = ints.get(0);
            ints.remove(0);
            return nextInt;
        } else {
            return 0;
        }
    }
    
    public int getLastBound() {
        return this.lastIntBound;
    }
}
