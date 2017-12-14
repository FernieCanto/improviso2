/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.mocks;

import improviso.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author User
 */
public class TrackMock extends Track {
    private int numExecutions = 0;
    final private ArrayList<GroupMessage> groupMessages = new ArrayList<>();
    private GroupMessage currentMessage;
    
    private ArrayList<Integer> positionsFinished = new ArrayList<>();
    private ArrayList<Integer> positionsInterrupt = new ArrayList<>();
    
    public static class TrackMockBuilder extends Track.TrackBuilder {
        @Override
        public TrackMock build() {
            return new TrackMock(this);
        }
    }
    
    protected TrackMock(TrackMockBuilder builder) {
        super(builder);
    }
    
    public void addMessage(boolean finished, boolean interrupt) {
        GroupMessage message = new GroupMessage("Test");
        if (finished) {
            message.setFinished();
        }
        if (interrupt) {
            message.setInterrupt();
        }
        this.groupMessages.add(message);
    }
    
    @Override
    public GroupMessage getMessage() {
        this.numExecutions++;
        return this.currentMessage;
    }
    
    @Override
    public MIDINoteList execute(Random random, Section.SectionEnd sectionEnd, boolean interruptTracks) {
        if (this.groupMessages.size() > 0) {
            this.currentMessage = this.groupMessages.get(0);
            this.groupMessages.remove(0);
        } else {
            this.currentMessage = new GroupMessage("test");
        }
        return super.execute(random, sectionEnd, interruptTracks);
    }
    
    public void addPositionFinished(Integer position) {
        this.positionsFinished.add(position);
    }
    
    public void addPositionInterrupt(Integer position) {
        this.positionsInterrupt.add(position);
    }
    
    @Override
    public Integer getPositionFinished() {
        Integer result = null;
        if (positionsFinished.size() > 0) {
            result = positionsFinished.get(0);
            if (result == null) {
                positionsFinished.remove(0);
            }
        }
        return result;
    }
    
    @Override
    public Integer getPositionInterrupt() {
        Integer result = null;
        if (positionsInterrupt.size() > 0) {
            result = positionsInterrupt.get(0);
            if (result == null) {
                positionsInterrupt.remove(0);
            }
        }
        return result;
    }
}
