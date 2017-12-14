package improviso.gui;

import improviso.*;
import java.util.ArrayList;

/**
 * @author Fernie Canto
 */
public class SectionConfiguration implements SectionVisitor {
    final static public int TYPE_FIXED = 1;
    final static public int TYPE_VARIABLE = 2;
    
    private SectionType type;
    private int lengthMin;
    private int lengthMax;
    private int tempo;
    private boolean interruptTracks;
    private ArrayList<Track> tracks;
    
    public static enum SectionType {
        TYPE_FIXED, TYPE_VARIABLE
    }
    
    public SectionConfiguration() {
        this.type = SectionType.TYPE_FIXED;
        this.lengthMin = 0;
        this.lengthMax = 0;
        this.tempo = 120;
        this.interruptTracks = true;
        this.tracks = new ArrayList<>();
    }

    @Override
    public void visit(FixedSection section) {
        this.type = SectionType.TYPE_FIXED;
        this.lengthMin = section.getLength().getValueMin();
        this.lengthMax = section.getLength().getValueMax();
        this.tempo = section.getTempo();
        this.interruptTracks = section.getInterruptTracks();
        this.tracks = section.getTracks();
    }

    @Override
    public void visit(VariableSection section) {
        this.type = SectionType.TYPE_VARIABLE;
        this.lengthMin = 0;
        this.lengthMax = 0;
        this.tempo = section.getTempo();
        this.interruptTracks = section.getInterruptTracks();
        this.tracks = section.getTracks();
    }

    ExecutableSection buildSection() {
        Section.SectionBuilder builder;
        if (this.type == SectionType.TYPE_FIXED) {
            builder = new FixedSection.FixedSectionBuilder()
                    .setLength(new IntegerRange(this.lengthMin, this.lengthMax, this.lengthMin, this.lengthMax));
        } else {
            builder = new VariableSection.VariableSectionBuilder();
        }
        builder.setTempo(this.tempo);
        builder.setInterruptTracks(this.interruptTracks);
        this.tracks.forEach((track) -> {
            System.out.println("Adding track "+track.getId());
            builder.addTrack(track);
        });
        return builder.build();
    }

    public SectionType getType() {
        return type;
    }

    public void setType(SectionType type) {
        this.type = type;
    }

    public Integer getLengthMin() {
        return lengthMin;
    }

    public void setLengthMin(int lengthMin) {
        this.lengthMin = lengthMin;
    }

    public Integer getLengthMax() {
        return lengthMax;
    }

    public void setLengthMax(int lengthMax) {
        this.lengthMax = lengthMax;
    }

    public Integer getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public boolean getInterruptTracks() {
        return interruptTracks;
    }

    public void setInterruptTracks(boolean interruptTracks) {
        this.interruptTracks = interruptTracks;
    }
}
