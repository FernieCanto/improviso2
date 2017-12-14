/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.Random;

/**
 * Implementa um Group Folha, que contém um Pattern que será devolvido
 * quando ele for selecionado.
 * @author fernando
 */
public class LeafGroup extends Group {
    private final Pattern leafPattern;
    
    public static class LeafGroupBuilder extends Group.GroupBuilder {
        private Pattern leafPattern;
        
        public Pattern getLeafPattern() {
            return leafPattern;
        }
        
        public LeafGroupBuilder setLeafPattern(Pattern leafPattern) {
            this.leafPattern = leafPattern;
            return this;
        }
        
        @Override
        public LeafGroup build() {
            return new LeafGroup(this);
        }
    }
    
    private LeafGroup(LeafGroupBuilder builder) {
        super(builder);
        this.leafPattern = builder.getLeafPattern();
    }
    
    @Override
    protected Pattern selectPattern(Random rand) {
        return this.leafPattern;
    }
    
    @Override
    protected GroupMessage generateMessage() {
        return new GroupMessage(this.getId());
    }
}