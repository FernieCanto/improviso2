/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.mocks;

import improviso.*;
import java.util.Random;

/**
 *
 * @author fernando
 */
public class NoteMock extends Note {
    private MIDINote note;
    
    public static class NoteMockBuilder extends NoteBuilder {
        private MIDINote note;
        
        public NoteMockBuilder setNote(MIDINote note) {
            this.note = note;
            return this;
        }
        
        public MIDINote getNote() {
            return this.note;
        }
        
        @Override
        public NoteMock build() {
            return new NoteMock(this);
        }
    }
    
    public NoteMock(NoteMock.NoteMockBuilder builder) {
        super(builder);
        this.note = builder.getNote();
    }
    
    public void setNote(MIDINote note) {
        this.note = note;
    }
    
    @Override
    public MIDINoteList execute(Random rand, int patternLength, double position, int maximumLength) {
        MIDINoteList list = new MIDINoteList();
        if (note.getStart() <= maximumLength) {
            list.add(new MIDINote(
                    note.getPitch(),
                    note.getStart(),
                    note.getStart() + note.getLength() > maximumLength
                            ? maximumLength - note.getStart()
                            : note.getLength(),
                    note.getVelocity(),
                    note.getMIDITrack()
            ));
        }
        return list;
    }
}
