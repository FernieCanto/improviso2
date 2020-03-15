/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.io.IOException;
import java.util.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import org.junit.*;
import static org.junit.Assert.*;
import org.mockito.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author FernieCanto
 */
public class MIDIRealTimePlayerTest {
    
    @Test
    public void testCompositionFinished() throws ImprovisoException, InvalidMidiDataException, MidiUnavailableException {
        Receiver receiver = mock(Receiver.class);
        
        MidiDevice device = mock(MidiDevice.class);
        when(device.getReceiver()).thenReturn(receiver);
        when(device.getMicrosecondPosition()).thenReturn(0l);
        
        Composition composition = mock(Composition.class);
        when(composition.getIsFinished()).thenReturn(true);
        
        MIDIRealTimePlayer player = new MIDIRealTimePlayer(device);
        
        player.initialize(composition);
        verify(device).open();
        verify(composition).initialize(player);
        
        assertFalse(player.play(composition));
        verifyZeroInteractions(receiver);
    }
}
