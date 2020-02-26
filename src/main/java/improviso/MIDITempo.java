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
public class MIDITempo extends MIDIEvent {
    private final int tempo;
    
    public MIDITempo(long start, int tempo) {
        super(start);
        this.tempo = tempo;
    }
    
    public int getTempo() {
        return this.tempo;
    }
}
