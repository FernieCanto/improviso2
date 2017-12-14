package improviso;

import java.util.*;
/**
 *
 * @author fernando
 */
public class VariableSection extends Section {
    public static class VariableSectionBuilder extends Section.SectionBuilder {
        @Override
        public VariableSection build() {
            return new VariableSection(this);
        }
    }
    
    protected VariableSection(VariableSectionBuilder builder) {
        super(builder);
    }
    
    @Override
    protected SectionEnd initialize(Random random) throws ImprovisoException {
        displayMessage("INITIALIZING");
        return new UnknownSectionEnd();
    }

    @Override
    protected SectionEnd processTrackMessage(Track track) {
        if(track.getPositionInterrupt() != null) {
            return SectionEnd.createEnd(track.getPositionInterrupt());
        } else if(track.getPositionFinished() != null) {
            Integer largestEnd = 0;
            
            displayMessage(track.getId() + " FINISHED");
            
            for(Track currentTrack : this.getTracks()) {
                if (currentTrack.getPositionFinished() == null) {
                    largestEnd = null;
                } else if (largestEnd != null && currentTrack.getPositionFinished() > largestEnd) {
                    largestEnd = currentTrack.getPositionFinished();
                }
            }
            return SectionEnd.createEnd(largestEnd);
        } else {
            return new UnknownSectionEnd();
        }
    }
    
    @Override
    public void accept(SectionVisitor visitor) {
        visitor.visit(this);
    }
}
