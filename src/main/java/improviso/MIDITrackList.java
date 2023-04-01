/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.ArrayList;

/**
 *
 * @author User
 */
public class MIDITrackList extends ArrayList<MIDITrack> {
    public int getChannel(int index){
        return this.get(index - 1).getChannel();
    }
}
