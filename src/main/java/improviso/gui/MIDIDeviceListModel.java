/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.gui;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.DefaultListModel;

/**
 *
 * @author cfern
 */
public class MIDIDeviceListModel extends DefaultListModel<String> {
    private ArrayList<MidiDevice.Info> availableDevicesInfo = new ArrayList<>();

    public MIDIDeviceListModel() {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info i : infos) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(i);
                Receiver receiver = device.getReceiver();
                availableDevicesInfo.add(i);
            } catch (MidiUnavailableException ex) {
                
            }
        }
    }

    @Override
    public int getSize() {
        return availableDevicesInfo.size();
    }

    @Override
    public String getElementAt(int index) {
        return availableDevicesInfo.get(index).getName();
    }
    
    public MidiDevice.Info getMidiInfoAt(int index) {
        return availableDevicesInfo.get(index);
    }
}
