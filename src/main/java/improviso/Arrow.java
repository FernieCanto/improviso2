package improviso;

/**
 *
 * @author fernando
 */
public class Arrow {
    final private String destinationSection;
    final private int probability;
    final private int maxExecutions;
    final private boolean endCompositionAfterMax;
    
    private int executions = 0;
    
    public static class ArrowBuilder {
        private String destinationSection = null;
        private int probability = 1;
        private int maxExecutions = 100;
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
        this.endCompositionAfterMax = builder.getEndCompositionAfterMax();
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
    
    public String execute() {
        if(executions < maxExecutions) {
            executions++;
            return destinationSection;
        } else {
            return null;
        }
    }
    
    public boolean isActive() {
        return endCompositionAfterMax || (executions < maxExecutions);
    }
}
