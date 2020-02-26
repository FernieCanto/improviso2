/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

/**
 *
 * @author LENOVO G40
 */
public class MIDIEvent {
    final private long start;
    
    public MIDIEvent(long start) {
        this.start = start;
    }

    /**
     * Gets the starting position of the note in ticks.
     * @return The starting position
     */
    public long getStart() {
        return start;
    }
}
