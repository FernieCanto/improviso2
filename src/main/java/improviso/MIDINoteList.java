package improviso;

import java.util.ArrayList;
import java.util.Arrays;

public class MIDINoteList extends ArrayList<MIDINote> {
    public MIDINoteList() {
        super();
    }
    
    public MIDINoteList(MIDINote note) {
        super();
        this.add(note);
    }
    
    public MIDINoteList(MIDINote[] arrayNotes) {
        super();
        this.addAll(Arrays.asList(arrayNotes));
    }
    
    public MIDINoteList offsetNotes(int offset) {
        MIDINoteList newList = new MIDINoteList();
        this.forEach((note) -> {
            newList.add(new MIDINote(note, offset));
        });
        return newList;
    }
}
