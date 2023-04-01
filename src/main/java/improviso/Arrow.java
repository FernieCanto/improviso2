package improviso;

/**
 *
 * @author Fernie Canto
 */
public class Arrow implements java.io.Serializable {
    public enum ExhaustExecutionsModes {
        endWhenFinite,
        remove,
        end
    }
    
    final private String destinationSection;
    final private int probability;
    final private int maxExecutions;
    final private ExhaustExecutionsModes exhaustExecutionsMode;
    final private boolean endCompositionAfterMax;
    
    private int executions = 0;
    
    public static class ArrowBuilder {
        private String destinationSection = null;
        private int probability = 1;
        private int maxExecutions = 100;
        private ExhaustExecutionsModes exhaustExecutionsMode = ExhaustExecutionsModes.remove;
        private boolean endCompositionAfterMax = false;

        public String getDestinationSection() {
            return destinationSection;
        }

        public ArrowBuilder setDestinationSection(String destinationSection) {
            this.destinationSection = destinationSection;
            return this;
        }

        public int getProbability() {
            return probability;
        }

        public ArrowBuilder setProbability(int probability) {
            this.probability = probability;
            return this;
        }

        public int getMaxExecutions() {
            return maxExecutions;
        }

        public ArrowBuilder setMaxExecutions(int maxExecutions) {
            this.maxExecutions = maxExecutions;
            return this;
        }
        
        public ExhaustExecutionsModes getExhaustExecutionsMode()
        {
            return exhaustExecutionsMode;
        }
        
        public ArrowBuilder setExhaustExecutionsMode(ExhaustExecutionsModes mode) {
            this.exhaustExecutionsMode = mode;
            return this;
        }

        public boolean getEndCompositionAfterMax() {
            return endCompositionAfterMax;
        }

        public ArrowBuilder setEndCompositionAfterMax(boolean endCompositionAfterMax) {
            this.endCompositionAfterMax = endCompositionAfterMax;
            return this;
        }
        
        public Arrow build() {
            return new Arrow(this);
        }
    }
    
    public Arrow(ArrowBuilder builder) {
        this.destinationSection = builder.getDestinationSection();
        this.probability = builder.getProbability();
        this.maxExecutions = builder.getMaxExecutions();
        this.exhaustExecutionsMode = builder.getExhaustExecutionsMode();
        this.endCompositionAfterMax = builder.getEndCompositionAfterMax();
    }
    
    public Arrow(Arrow arrowClone) {
        this.destinationSection = arrowClone.getDestination();
        this.probability = arrowClone.getProbability();
        this.maxExecutions = arrowClone.getMaxExecutions();
        this.exhaustExecutionsMode = arrowClone.getExhaustExecutionsMode();
        this.endCompositionAfterMax = arrowClone.getEndCompositionAfterMax();
    }
    
    public void initialize() {
        this.executions = 0;
    }

    public String getDestination() {
        return destinationSection;
    }
    
    public int getProbability() {
        return probability;
    }
    
    public int getMaxExecutions() {
        return maxExecutions;
    }
    
    public ExhaustExecutionsModes getExhaustExecutionsMode() {
        return exhaustExecutionsMode;
    }
    
    public boolean getEndCompositionAfterMax() {
        return endCompositionAfterMax;
    }
    
    public String execute() {
        executions++;
        return destinationSection;
    }

    public boolean endComposition(boolean isInfinite) {
        switch (exhaustExecutionsMode) {
            case end:
                return executions >= maxExecutions;
            case endWhenFinite:
                return !isInfinite && executions >= maxExecutions;
            case remove:
                return false;
        }
        return false;
    }
    
    public boolean isActive() {
        return this.exhaustExecutionsMode != ExhaustExecutionsModes.remove || executions < maxExecutions;
    }
}
