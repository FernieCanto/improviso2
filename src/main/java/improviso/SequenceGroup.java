package improviso;

import java.util.Random;

/**
 *
 * @author fernando
 */
public class SequenceGroup extends RepetitionGroup {
    protected int currentIndex = 0;
    protected boolean resetOrder = true;
    
    public static class SequenceGroupBuilder extends RepetitionGroup.RepetitionGroupBuilder {
        @Override
        public SequenceGroup build() {
            return new SequenceGroup(this);
        }
    }
    
    private SequenceGroup(SequenceGroupBuilder builder) {
        super(builder);
    }
    
    @Override
    public void resetGroup() {
        if(resetOrder) {
            currentIndex = 0;
            selectedGroup = null;
        }
        super.resetGroup();
    }
    
    @Override
    protected boolean selectNextGroup(Random rand) {
        selectedGroup = children.get(currentIndex);
        selectedGroupIndex = currentIndex;

        currentIndex++;
        if(currentIndex == children.size())
            currentIndex = 0;
        return true;
    }
}
