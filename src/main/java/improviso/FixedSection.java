package improviso;

import java.util.Random;

/**
 *
 * @author fernando
 */
public class FixedSection extends Section {
    final private IntegerRange length;
    
    public static class FixedSectionBuilder extends Section.SectionBuilder {
        private IntegerRange length;
        
        public IntegerRange getLength() {
            return this.length;
        }
        
        public FixedSectionBuilder setLength(IntegerRange length) {
            this.length = length;
            return this;
        }
        
        @Override
        public FixedSection build() {
            return new FixedSection(this);
        }
    }
    
    protected FixedSection(FixedSectionBuilder builder) {
        super(builder);
        this.length = builder.getLength();
    }
    
    @Override
    protected SectionEnd processTrackMessage(Track track) {
        return new UnknownSectionEnd();
    }
    
    @Override
    protected SectionEnd initialize(Random random) throws ImprovisoException {
        return SectionEnd.createEnd(this.length.getValue(random));
    }
    
    public IntegerRange getLength() {
        return this.length;
    }
    
    @Override
    public void accept(SectionVisitor visitor) {
        visitor.visit(this);
    }
}