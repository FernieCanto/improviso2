package improviso;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Implementa um grupo cujos grupos children podem repetir
 * de acordo com certos parâmetros:
 * @author fernando
 */
public abstract class RepetitionGroup extends Group {
    HashMap<Group, Integer> iterationsMap = new HashMap<>();
    HashMap<Group, Double> inertiaMap = new HashMap<>();
    int currentIterations = 0;
    protected boolean resetIterations = true; // NOT CONFIGURED
    Group selectedGroup = null;
    
    abstract public static class RepetitionGroupBuilder extends Group.GroupBuilder {
        final private HashMap<Group, Integer> iterationsMap = new HashMap<>();
        final private HashMap<Group, Double> inertiaMap = new HashMap<>();
        
        public HashMap<Group, Integer> getIterationsMap() {
            return this.iterationsMap;
        }
        
        public HashMap<Group, Double> getInertiaMap() {
            return this.inertiaMap;
        }
        
        protected RepetitionGroupBuilder addChild(Group child, Integer iterations, Double inertia) {
            this.iterationsMap.put(child, iterations != null ? iterations : 0);
            this.inertiaMap.put(child, inertia != null ? inertia : 0.0);
            super.addChild(child);
            return this;
        }
    }
    
    protected RepetitionGroup(RepetitionGroupBuilder builder) {
        super(builder);
        this.iterationsMap = builder.getIterationsMap();
        this.inertiaMap = builder.getInertiaMap();
    }
    
    public Group selectGroup(Random rand) {
        if(this.selectedGroup != null) {
            if(iterationsMap.get(this.selectedGroup) > currentIterations) {
                currentIterations++;
                return this.selectedGroup;
            }
            if(rand.nextDouble() < inertiaMap.get(this.selectedGroup)) {
                return this.selectedGroup;
            }
        }
        currentIterations = 1;
        this.selectNextGroup(rand);
        return this.selectedGroup;
    }
    
    @Override
    protected Pattern selectPattern(Random rand) {
        this.selectedGroup = this.selectGroup(rand);
        return this.selectedGroup.execute(rand);
    }
    
    @Override
    protected GroupMessage generateMessage() {
        return selectedGroup.getMessage();
    }
    
    @Override
    public void resetGroup() {
        if(resetIterations) {
            currentIterations = 0;
        }
        super.resetGroup();
    }
    
    protected abstract boolean selectNextGroup(Random rand);
}
