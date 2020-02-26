/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.gui;

import improviso.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

/**
 *
 * @author LENOVO G40
 */
public class CompositionExecutionThread extends Thread {
    private final Composition composition;
    private final MIDIGenerator generator;
    
    public CompositionExecutionThread(Composition composition, MIDIGenerator generator) {
        this.composition = composition;
        this.generator = generator;
    }

    @Override
    public void run() {
        try {
            this.composition.execute(this.generator);
        } catch (ImprovisoException | InvalidMidiDataException | IOException | MidiUnavailableException ex) {
            Logger.getLogger(CompositionExecutionThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        generator.playSequenceRealTime();
    }
}
