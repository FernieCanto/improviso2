package improviso;
import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;


/**
 * This class implements the a full Improviso Composition, with all its
 * elements. A Composition consists of graph of Sections, with Arrows
 * linking them. The Composition, when executed, will choose one of the
 * starting sections and execute it, then follow one of its Arrows to the
 * next Section, repeating the process until it reaches a Section with no
 * outward Arrows, or follows an explicit ending Arrow. All other elements
 * are placed inside the Sections, so the Composition does not handle
 * them directly.
 * @author Fernie Canto
 */
public class Composition implements java.io.Serializable {
    final private static java.util.regex.Pattern NOTE_NAME_PATTERN = java.util.regex.Pattern.compile("^([A-G])([#b])?(-2|-1|\\d)$");
    final private HashMap<Character, Integer> noteMap;
    /**
     * The number of ticks in a whole note, used for reference when calculating
     * durations and positions of notes.
     */
    public static final int TICKS_WHOLENOTE = 480;
    
    final private ElementLibrary elementLibrary = new ElementLibrary();
    /**
     * List of MIDI tracks that shall be present in the MIDI file.
     */
    final private MIDITrackList MIDITracks = new MIDITrackList();
    /**
     * Map of all the sections in the composition.
     */
    final private LinkedHashMap<String, Section> sections = new LinkedHashMap<>();
    /**
     * List of Arrows that point to the possible initial Sections of the
     * composition. One of these will be chosen when the composition is
     * executed.
     */
    final private ArrowList initialSections = new ArrowList();
    /**
     * Map of the lists of Arrows that point out of each Section.
     */
    final private HashMap<String, ArrowList> sectionDestinations = new HashMap<>();
    
    /**
     * Number of ticks at the start of the composition, before the first section
     * is executed.
     */
    final private Integer offset;
    
    final private Long randomSeed;
    
    public Composition(Integer offset) {
        this.offset = offset;
        this.randomSeed = null;
        this.noteMap = createNoteNumberMap();
    }
    
    public Composition(Integer offset, Long randomSeed) {
        this.offset = offset;
        this.randomSeed = randomSeed;
        this.noteMap = createNoteNumberMap();
    }
    
    public static String showBeatsAndTicks(int ticks) {
        String beats = Integer.toString(ticks / (TICKS_WHOLENOTE / 4));
        String remTicks = Integer.toString(ticks % (TICKS_WHOLENOTE / 4));
        
        return beats+":"+remTicks;
    }
    
    public ElementLibrary getElementLibrary() {
        return this.elementLibrary;
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
        
        if(elementLibrary.hasNoteAlias(stringNoteName)) {
            return elementLibrary.getNoteAlias(stringNoteName);
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
    
    /**
     * Adds a MIDITrack to the composition.
     * @param track 
     */
    public void addMIDITrack(MIDITrack track) {
        MIDITracks.add(track);
    }
    
    /**
     * Adds a Section to the composition
     * @param id The section identifier
     * @param section 
     */
    public void addSection(String id, Section section) {
        if (sections.containsKey(id)) {
            sections.remove(id);
            sections.put(id, section);
        } else {
            sections.put(id, section);
            sectionDestinations.put(id, new ArrowList());
        }
    }
    
    /**
     * Adds an Arrow to the composition, with a determined origin Section.
     * @param origin The identifier of the origin Section
     * @param arrow 
     * @throws improviso.ImprovisoException 
     */
    public void addArrow(String origin, Arrow arrow) throws ImprovisoException {
        if(origin == null) {
            initialSections.addArrow(arrow);
        } else if(sectionDestinations.get(origin) != null) {
            sectionDestinations.get(origin).addArrow(arrow);
        } else {
            throw new ImprovisoException("Section not found: "+origin);
        }
    }
    
    public Random getRandom() {
        Random random = new Random();
        if (this.randomSeed != null) {
            random.setSeed(this.randomSeed);
        }
        return random;
    }
    
    public String[] getSectionIds() {
        String[] ids = new String[this.sections.size()];
        this.sections.keySet().toArray(ids);
        
        return ids;
    }

    public Section getSection(String selectedValue) {
        return this.sections.get(selectedValue);
    }

    /**
     * Produces a MIDI file from the composition.
     * @param generator
     * @throws ImprovisoException
     * @throws InvalidMidiDataException
     * @throws IOException 
     * @throws javax.sound.midi.MidiUnavailableException 
     */
    public void execute(MIDIGenerator generator)
            throws ImprovisoException,
                   InvalidMidiDataException,
                   IOException,
                   MidiUnavailableException {
        String currentSectionId;
        Section currentSection;
        int currentPosition = offset;
        generator.setMIDITracks(this.MIDITracks);
        Random random = this.getRandom();
        
        this.initialSections.initialize();
        this.sectionDestinations.forEach((sectionId, arrowList) -> {
            arrowList.initialize();
        });

        if(initialSections.getNumArrows() > 0) {
            currentSectionId = initialSections.getNextDestination(random);
        } else if (!sections.isEmpty()) {
            currentSectionId = sections.keySet().iterator().next();
        } else {
            throw new ImprovisoException("Composition has no starting sections");
        }

        do {
            currentSection = sections.get(currentSectionId);
            
            generator.setCurrentTick(currentPosition);
            generator.setTempo(currentSection.getTempo());
            generator.setTimeSignature(currentSection.getTimeSignatureNumerator(), currentSection.getTimeSignatureDenominator());
            
            generator.addNotes(currentSection.execute(random).offsetNotes(currentPosition));

            currentPosition += currentSection.getActualEnd();
            
            if(sectionDestinations.get(currentSectionId).getNumArrows() > 0) {
                currentSectionId = sectionDestinations.get(currentSectionId).getNextDestination(random);
            } else {
                currentSectionId = null;
            }
        } while(currentSectionId != null);
    }
    
    /**
     * Produces a MIDI file from one of the sections.
     * @param generator
     * @param sectionId
     * @throws ImprovisoException
     * @throws InvalidMidiDataException
     * @throws IOException 
     * @throws javax.sound.midi.MidiUnavailableException 
     */
    public void executeSection(MIDIGenerator generator, String sectionId)
            throws ImprovisoException,
                   InvalidMidiDataException,
                   IOException,
                   MidiUnavailableException {
        Section currentSection = this.getSection(sectionId);
        generator.setMIDITracks(this.MIDITracks);
        Random random = this.getRandom();

        generator.setCurrentTick(0);
        generator.setTempo(currentSection.getTempo());
        generator.setTimeSignature(currentSection.getTimeSignatureNumerator(), currentSection.getTimeSignatureDenominator());

        generator.addNotes(currentSection.execute(random).offsetNotes(0));
    }
}