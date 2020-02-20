/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.HashMap;
import java.util.regex.Matcher;

/**
 *
 * @author cfern
 */
public class NoteNameInterpreter {
    final private static java.util.regex.Pattern NOTE_NAME_PATTERN = java.util.regex.Pattern.compile("^([A-G])([#b])?(-2|-1|\\d)$");
    final private HashMap<String, Integer> noteAliases = new HashMap<>();
    final private HashMap<Character, Integer> noteMap = createNoteNumberMap();
    
    /**
     * Produces the MIDI note number corresponding to the note name. Note names
     * have to include a letter from A to G, an optional accidental ("b" for
     * flat or "#" for sharp) and the octave number. Additionally, the note name
     * can be an alias included in the composition file.
     * @param stringNoteName The note name to be interpreted
     * @return The numerical value of the note
     * @throws ImprovisoException 
     */
    public int interpretNoteName(String stringNoteName)
        throws ImprovisoException {
        Matcher noteMatcher = NOTE_NAME_PATTERN.matcher(stringNoteName);
        
        if(this.hasNoteAlias(stringNoteName)) {
            return this.getNoteAlias(stringNoteName);
        } else if(noteMatcher.matches()) {
            int note = this.noteMap.get(noteMatcher.group(1).charAt(0));
            if(noteMatcher.group(2) != null) {
                if(noteMatcher.group(2).equals("b")) {
                    note--;
                } else{
                    note++;
                }
            }

            if(!noteMatcher.group(3).equals("-2")) {
                int octave = Integer.parseInt(noteMatcher.group(3));
                note += (octave+2) * 12;
            }
            return note;
        } else {
            try {
                return Integer.parseInt(stringNoteName);
            } catch(NumberFormatException e) {
                ImprovisoException exception = new ImprovisoException("Invalid note name: "+stringNoteName);
                exception.addSuppressed(e);
                throw exception;
            }
        }
    }
    
    public void addNoteAlias(String id, Integer n) {
        this.noteAliases.put(id, n);
    }
    
    public boolean hasNoteAlias(String id) {
        return this.noteAliases.containsKey(id);
    }
    
    public Integer getNoteAlias(String id) {
        return this.noteAliases.get(id);
    }
    
    private HashMap<Character, Integer> createNoteNumberMap() {
        HashMap<Character, Integer> map = new HashMap<>();
        map.put('C', 0);
        map.put('D', 2);
        map.put('E', 4);
        map.put('F', 5);
        map.put('G', 7);
        map.put('A', 9);
        map.put('B', 11);
        return map;
    }
}
