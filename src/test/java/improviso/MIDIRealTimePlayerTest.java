/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.io.IOException;
import java.util.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
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
        when(composition.getMIDITrackList()).thenReturn(new MIDITrackList());
        when(composition.getIsFinished()).thenReturn(true);
        
        MIDIRealTimePlayer player = new MIDIRealTimePlayer(composition, device);
        
        player.initialize();
        verify(device).open();
        verify(composition).initialize();
        
        assertFalse(player.play());
        verifyZeroInteractions(receiver);
    }
    
    // Tempo: 125
    // 480000 ?s per quarter note
    // 4000 ?s per tick
    // 10 ticks =  40000 ?s
    // 20 ticks =  80000 ?s
    // 30 ticks = 120000 ?s
    // Next executions at 35000 ?s, 75000 ?s. 115000 ?s
    //  0 tick,     0 ?s: PROGRAM CHANGE 1 15
    //  0 tick,     0 ?s: TEMPO 125
    //  3 tick, 12000 ?s: NOTE  ON 1, 10, 100
    //  5 tick, 20000 ?s: NOTE  ON 1, 11,  99
    // 11 tick, 44000 ?s: NOTE  ON 1, 12, 100
    // 13 tick, 52000 ?s: NOTE OFF 1, 12, 100
    // 14 tick, 56000 ?s: NOTE OFF 1, 10, 100
    // 16 tick, 64000 ?s: NOTE  ON 1, 13, 100
    // 19 tick, 76000 ?s: NOTE OFF 1, 13, 100
    // 23 tick, 92000 ?s: NOTE OFF 1, 11,  99
    @Test
    public void testCompositionExecuteThree() throws MidiUnavailableException, ImprovisoException, InvalidMidiDataException {
        MIDINoteList notes1 = new MIDINoteList();
        notes1.add(new MIDITempo(0, 125));
        notes1.add(new MIDINote(11, 5, 18,  99, 1));
        notes1.add(new MIDINote(10, 3, 11, 100, 1));
        
        MIDINoteList notes2 = new MIDINoteList();
        notes1.add(new MIDINote(13, 16, 3, 100, 1));
        notes1.add(new MIDINote(12, 11, 2, 100, 1));
        
        Receiver receiver = mock(Receiver.class);
        
        MidiDevice device = mock(MidiDevice.class);
        when(device.getReceiver()).thenReturn(receiver);
        when(device.getMicrosecondPosition()).thenReturn(0l, 0l, 12000l, 45000l, 92000l);
                
        MIDITrackList trackList = new MIDITrackList();
        trackList.add(new MIDITrack(1, 15, 100, 64));
        
        Composition composition = mock(Composition.class);
        when(composition.getMIDITrackList()).thenReturn(trackList);
        when(composition.getIsFinished()).thenReturn(false, false, false, false, false, true, true, true);
        when(composition.executeTicks(eq(10))).thenReturn(notes1, notes2);
        
        MIDIRealTimePlayer player = new MIDIRealTimePlayer(composition, device);
        player.initialize();
        verify(device).open();
        verify(composition).initialize();
        
        InOrder orderComposition = inOrder(composition);
        InOrder orderReceiver = inOrder(receiver);
        
        ArgumentCaptor<MidiMessage> argumentMidiMessage1 = ArgumentCaptor.forClass(MidiMessage.class);
        ArgumentCaptor<MidiMessage> argumentMidiMessage2 = ArgumentCaptor.forClass(MidiMessage.class);
        ArgumentCaptor<MidiMessage> argumentMidiMessage3 = ArgumentCaptor.forClass(MidiMessage.class);
        ArgumentCaptor<MidiMessage> argumentMidiMessage4 = ArgumentCaptor.forClass(MidiMessage.class);
        
        assertTrue(player.play());
        orderComposition.verify(composition).getIsFinished();
        orderComposition.verify(composition).executeTicks(eq(10));
        orderComposition.verify(composition).getIsFinished();
        orderReceiver.verify(receiver, times(2)).send(argumentMidiMessage1.capture(), eq(0l));
        assertProgramChangeMessage(argumentMidiMessage1.getAllValues().get(0), 1, 15);
        assertTempoMessage(argumentMidiMessage1.getAllValues().get(1), 125);
        
        assertTrue(player.play());
        orderComposition.verify(composition, times(2)).getIsFinished();
        orderReceiver.verify(receiver).send(argumentMidiMessage2.capture(), eq(0l));
        assertNoteOnMessage(argumentMidiMessage2.getValue(), 1, 10, 100);
        
        assertTrue(player.play());
        orderComposition.verify(composition).getIsFinished();
        orderComposition.verify(composition).executeTicks(eq(10));
        orderComposition.verify(composition).getIsFinished();
        orderReceiver.verify(receiver, times(2)).send(argumentMidiMessage3.capture(), eq(0l));
        assertNoteOnMessage(argumentMidiMessage3.getAllValues().get(0), 1, 11,  99);
        assertNoteOnMessage(argumentMidiMessage3.getAllValues().get(1), 1, 12, 100);
        
        assertFalse(player.play());
        orderComposition.verify(composition, times(2)).getIsFinished();
        orderReceiver.verify(receiver, times(5)).send(argumentMidiMessage4.capture(), eq(0l));
        assertNoteOffMessage(argumentMidiMessage4.getAllValues().get(0), 1, 12, 100);
        assertNoteOffMessage(argumentMidiMessage4.getAllValues().get(1), 1, 10, 100);
        assertNoteOnMessage (argumentMidiMessage4.getAllValues().get(2), 1, 13, 100);
        assertNoteOffMessage(argumentMidiMessage4.getAllValues().get(3), 1, 13, 100);
        assertNoteOffMessage(argumentMidiMessage4.getAllValues().get(4), 1, 11,  99);
    }
    
    private void assertProgramChangeMessage(MidiMessage message, int channel, int instrument) throws InvalidMidiDataException {
        MidiMessage instrumentMessage = new ShortMessage(ShortMessage.PROGRAM_CHANGE, channel, instrument, 0);
            
        assertEquals(instrumentMessage.getStatus(), message.getStatus());
        assertEquals(instrumentMessage.getMessage()[0], message.getMessage()[0]);
        assertEquals(instrumentMessage.getMessage()[1], message.getMessage()[1]);
    }
    
    private void assertTempoMessage(MidiMessage message, int tempo) {
        int microseconds = (int)(60000000 / tempo);
        
        assertEquals(0x51, message.getMessage()[1]);
        assertEquals((byte)(microseconds >>> 16), message.getMessage()[3]);
        assertEquals((byte)(microseconds >>>  8), message.getMessage()[4]);
        assertEquals((byte)(microseconds       ), message.getMessage()[5]);
    }
    
    private void assertNoteOnMessage(MidiMessage message, int channel, int pitch, int velocity) {
        assertEquals(ShortMessage.NOTE_ON + channel, message.getStatus());
        assertEquals(pitch, message.getMessage()[1]);
        assertEquals(velocity, message.getMessage()[2]);
    }
    
    private void assertNoteOffMessage(MidiMessage message, int channel, int pitch, int velocity) {
        assertEquals(ShortMessage.NOTE_OFF + channel, message.getStatus());
        assertEquals(pitch, message.getMessage()[1]);
        assertEquals(velocity, message.getMessage()[2]);
    }
}
