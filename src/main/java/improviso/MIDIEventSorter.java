/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import javax.sound.midi.MidiEvent;

/**
 *
 * @author cfern
 */
public class MIDIEventSorter implements java.util.Comparator<MidiEvent> {
    @Override
    public int compare(MidiEvent o1, MidiEvent o2) {
        if(o1.getTick() < o2.getTick())
            return -1;
        else if(o1.getTick() > o2.getTick())
            return 1;
        else return 0;
    }

    public boolean equals(MidiEvent o1, MidiEvent o2) {
        return o1.getTick() == o2.getTick();
    }
}
