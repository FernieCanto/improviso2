package improviso;
import java.util.*;

/**
 *
 * @author Fernie Canto
 */
public class ArrowList implements java.io.Serializable {
    final private ArrayList<Arrow> originalArrows;
    private ArrayList<Arrow> currentArrows;
    final private LinkedHashMap<Arrow, Integer> accumulatedProbabilities;
    private int maxProbabilities;
    boolean verbose = false;
    
    public ArrowList() {
        originalArrows = new ArrayList<>();
        currentArrows = new ArrayList<>();
        accumulatedProbabilities = new LinkedHashMap<>();
        maxProbabilities = 0;
    }
    
    public void addArrow(Arrow arrow) {
        originalArrows.add(arrow);
        currentArrows.add(arrow);
        maxProbabilities += arrow.getProbability();
        accumulatedProbabilities.put(arrow, maxProbabilities);
    }
    
    public void verbose() {
        this.verbose = true;
    }
    
    public boolean isEmpty() {
        return this.currentArrows.isEmpty();
    }
    
    public int getNumArrows() {
        return this.currentArrows.size();
    }
    
    private void calculateProbabilities() {
        maxProbabilities = 0;
        accumulatedProbabilities.clear();
        
        this.currentArrows.forEach((arrow) -> {
            maxProbabilities += arrow.getProbability();
            accumulatedProbabilities.put(arrow, maxProbabilities);
        });
    }
    
    public void initialize() {
        this.currentArrows = new ArrayList<>();
        this.originalArrows.forEach((originalArrow) -> {
            this.currentArrows.add(new Arrow(originalArrow));
        });
        calculateProbabilities();
        this.currentArrows.forEach((arrow) -> {arrow.initialize();});
    }
    
    public String getNextDestination(Random random, boolean isInfinite) {
        if(currentArrows.isEmpty()) {
            return null;
        }
        
        Arrow selectedArrow = this.selectArrow(random.nextInt(maxProbabilities));
        String destination = selectedArrow.execute();
        
        if (selectedArrow.endComposition(isInfinite)) {
            return null;
        }
        
        if(!selectedArrow.isActive()) {
            currentArrows.remove(selectedArrow);
            this.calculateProbabilities();
        }
        return destination;
    }

    private Arrow selectArrow(int selection) {
        Arrow selectedArrow = this.currentArrows.get(0);
        
        for(Arrow arrow : this.currentArrows) {
            if(selection < this.accumulatedProbabilities.get(arrow)) {
                selectedArrow = arrow;
                break;
            }
        }
        return selectedArrow;
    }
}