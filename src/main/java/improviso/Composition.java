package improviso;
import java.util.*;
import java.io.*;
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
    /**
     * The number of ticks in a whole note, used for reference when calculating
     * durations and positions of notes.
     */
    public static final int TICKS_WHOLENOTE = 480;
    
    /**
     * List of MIDI tracks that shall be present in the MIDI file.
     */
    final private ArrayList<MIDITrack> MIDITracks = new ArrayList<>();
    /**
     * Map of all the sections in the composition.
     */
    final private LinkedHashMap<String, ExecutableSection> sections = new LinkedHashMap<>();
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
    }
    
    public Composition(Integer offset, Long randomSeed) {
        this.offset = offset;
        this.randomSeed = randomSeed;
    }
    
    public static String showBeatsAndTicks(int ticks) {
        String beats = Integer.toString(ticks / (TICKS_WHOLENOTE / 4));
        String remTicks = Integer.toString(ticks % (TICKS_WHOLENOTE / 4));
        
        return beats+":"+remTicks;
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
    public void addSection(String id, ExecutableSection section) {
        if (sections.containsKey(id)) {
            System.out.println("Removing section " + id);
            sections.remove(id);
            System.out.println("Replacing section " + id + " with new section " + section.toString());
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

    public ExecutableSection getSection(String selectedValue) {
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
        ExecutableSection currentSection;
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
        ExecutableSection currentSection = this.getSection(sectionId);
        generator.setMIDITracks(this.MIDITracks);
        Random random = this.getRandom();

        generator.setCurrentTick(0);
        generator.setTempo(currentSection.getTempo());
        generator.setTimeSignature(currentSection.getTimeSignatureNumerator(), currentSection.getTimeSignatureDenominator());

        generator.addNotes(currentSection.execute(random).offsetNotes(0));
    }
}