package improviso;

import java.util.Random;

/**
 *
 * @author fernando
 */
public class SequenceGroup extends RepetitionGroup {
    private int currentIndex = 0;
    private boolean resetOrder = true;
    
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
    protected Group selectNextGroup(Random rand) {
        selectedGroup = getChildren().get(currentIndex);

        currentIndex++;
        if(currentIndex == getChildren().size()) {
            currentIndex = 0;
        }
        return selectedGroup;
    }
}
