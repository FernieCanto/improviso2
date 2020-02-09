/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

/**
 *
 * @author cfern
 */
public class MyReceiver implements Receiver {

    @Override
    public void send(MidiMessage message, long timeStamp) {
        System.out.println(message.getMessage());
    }

    @Override
    public void close() {
        return;
    }
    
}
