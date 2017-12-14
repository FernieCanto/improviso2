/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

/**
 *
 * @author fernando
 */
public class NoteSorter implements java.util.Comparator<MIDINote> {
    @Override
    public int compare(MIDINote n1, MIDINote n2) {
        if(n1.getStart() < n2.getStart())
            return -1;
        else if(n1.getStart() > n2.getStart())
            return 1;
        else return 0;
    }
    
    public boolean equals(MIDINote n1, MIDINote n2) {
        return n1.getStart() == n2.getStart();
    }
}
