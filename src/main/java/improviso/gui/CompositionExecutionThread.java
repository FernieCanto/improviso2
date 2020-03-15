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
    private final MIDIRealTimePlayer generator;
    private boolean interrupt = false;
    
    public CompositionExecutionThread(Composition composition, MIDIRealTimePlayer generator) {
        this.composition = composition;
        this.generator = generator;
    }
    
    public void initialize() {
        this.interrupt = false;
    }

    @Override
    public synchronized void run() {
        try {
            System.out.println("INITIALIZING");
            this.generator.initialize(this.composition);
            while (!this.interrupt && this.generator.play(this.composition)) {
                this.wait(0, 500000);
            }
            System.out.println("Stopped.");
            this.wait(1000);
            this.generator.closeDevice();
        } catch (ImprovisoException | InvalidMidiDataException | MidiUnavailableException | InterruptedException ex) {
            Logger.getLogger(CompositionExecutionThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stopPlaying() {
        this.interrupt = true;
    }
}
