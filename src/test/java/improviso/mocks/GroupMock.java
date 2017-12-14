/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.mocks;

import improviso.Group;
import improviso.GroupMessage;
import improviso.Pattern;
import java.util.Random;

/**
 *
 * @author User
 */
public class GroupMock extends Group {
    private Pattern nextPattern;
    private GroupMessage nextMessage;
    
    public static class GroupMockBuilder extends Group.GroupBuilder {
        @Override
        public GroupMock build() {
            return new GroupMock(this);
        }
    }
    
    private GroupMock(GroupMockBuilder builder) {
        super(builder);
    }
    
    public void setNextPattern(Pattern nextPattern) {
        this.nextPattern = nextPattern;
    }
    
    public void setNextMessage(GroupMessage nextMessage) {
        this.nextMessage = nextMessage;
    }
    
    @Override
    protected Pattern selectPattern(Random rand) {
        return this.nextPattern;
    }

    @Override
    protected GroupMessage generateMessage() {
        return this.nextMessage;
    }
}
