/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import improviso.mocks.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class MIDINoteListTest {
    @Test
    public void testCreateEmptyMIDINoteList() {
        MIDINoteList list = new MIDINoteList();
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void testInsertNotesMIDINoteList() {
        MIDINote note1 = new MIDINote(10, 0, 60, 100, 0);
        MIDINote note2 = new MIDINote(11, 60, 60, 100, 0);
        MIDINote note3 = new MIDINote(12, 120, 60, 100, 0);
        
        MIDINoteList list = new MIDINoteList();
        list.add(note1);
        list.add(note2);
        list.add(note3);
        
        assertEquals(3, list.size());
        assertEquals(note1, list.get(0));
        assertEquals(note2, list.get(1));
        assertEquals(note3, list.get(2));
    }
    
    @Test
    public void testMergeMIDINoteLists() {
        MIDINote note1 = new MIDINote(10, 0, 60, 100, 0);
        MIDINote note2 = new MIDINote(11, 60, 60, 100, 0);
        MIDINote note3 = new MIDINote(12, 120, 60, 100, 0);
        MIDINote note4 = new MIDINote(13, 180, 60, 100, 0);
        
        MIDINoteList list = new MIDINoteList();
        list.add(note1);
        list.add(note2);
        
        MIDINoteList list2 = new MIDINoteList();
        list.add(note3);
        list.add(note4);
        
        list.addAll(list2);
        assertEquals(4, list.size());
    }
    
    @Test
    public void testOffsetMIDINotes() {
        MIDINote note1 = new MIDINote(10, 0, 60, 100, 0);
        MIDINote note2 = new MIDINote(11, 60, 60, 100, 0);
        MIDINote note3 = new MIDINote(12, 120, 60, 100, 0);
        MIDINote note4 = new MIDINote(13, 180, 60, 100, 0);
        
        MIDINoteList list = new MIDINoteList();
        list.add(note1);
        list.add(note2);
        list.add(note3);
        list.add(note4);
        
        MIDINoteList list2 = new MIDINoteList();
        list2.addAll(list.offsetNotes(35));
        assertEquals(4, list2.size());
        assertEquals(35, list2.get(0).getStart());
        assertEquals(95, list2.get(1).getStart());
        assertEquals(155, list2.get(2).getStart());
        assertEquals(215, list2.get(3).getStart());
    }
}
