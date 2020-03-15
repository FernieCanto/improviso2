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
    final public static int TICKS_WHOLENOTE = 480;
    
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
    
    private Random random;
    private String currentSectionId;
    private long currentSectionInitialPosition;
    private long currentRealTimePosition;
    
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
    
    public void initialize(MIDIGeneratorInterface generator) throws InvalidMidiDataException, ImprovisoException {
        currentRealTimePosition = 0;
        currentSectionInitialPosition = 0;
        generator.setMIDITracks(this.MIDITracks);
        this.random = this.getRandom();
        
        this.initialSections.initialize();
        this.sectionDestinations.forEach((sectionId, arrowList) -> {
            arrowList.initialize();
        });

        if(initialSections.getNumArrows() > 0) {
            currentSectionId = initialSections.getNextDestination(this.random);
        } else if (!sections.isEmpty()) {
            currentSectionId = sections.keySet().iterator().next();
        } else {
            throw new ImprovisoException("Composition has no starting sections");
        }
        
        sections.get(currentSectionId).initialize(this.random);
        generator.setTempo(sections.get(currentSectionId).getTempo(), 0);
    }

    /**
     * Produces a MIDI file from the composition.
     * @param generator
     * @throws ImprovisoException
     * @throws InvalidMidiDataException
     * @throws IOException 
     * @throws javax.sound.midi.MidiUnavailableException 
     */
    public void execute(MIDIGeneratorInterface generator)
            throws ImprovisoException,
                   InvalidMidiDataException,
                   IOException,
                   MidiUnavailableException {
        Section currentSection;
        int currentPosition = offset;
        this.initialize(generator);

        do {
            currentSection = sections.get(currentSectionId);
            
            generator.setTempo(currentSection.getTempo(), currentPosition);
            generator.setTimeSignature(currentSection.getTimeSignatureNumerator(), currentSection.getTimeSignatureDenominator(), currentPosition);
            
            generator.addNotes(currentSection.execute(random).offsetNotes(currentPosition));

            currentPosition += currentSection.getActualEnd();
            
            if(sectionDestinations.get(currentSectionId).getNumArrows() > 0) {
                currentSectionId = sectionDestinations.get(currentSectionId).getNextDestination(random);
            } else {
                currentSectionId = null;
            }
        } while(currentSectionId != null);
        currentSectionInitialPosition = 0;
    }
    
    public void executeTicks(MIDIGeneratorInterface generator, int ticks) throws ImprovisoException, InvalidMidiDataException {
        int remainingTicks = ticks;
        
        MIDINoteList list = new MIDINoteList();
        
        while (remainingTicks > 0 && currentSectionId != null) {
            Section currentSection = sections.get(currentSectionId);
            long initialSectionPosition = currentSection.getCurrentRealTimePosition();
            
            list.addAll(currentSection.executeTicks(random, remainingTicks).offsetNotes(currentSectionInitialPosition));
            remainingTicks -= (currentSection.getCurrentRealTimePosition() - initialSectionPosition);
            
            if (currentSection.isFinished()) {
                currentSectionInitialPosition += currentSection.getCurrentRealTimePosition();
                if(sectionDestinations.get(currentSectionId).getNumArrows() > 0) {
                    currentSectionId = sectionDestinations.get(currentSectionId).getNextDestination(random);
                    sections.get(currentSectionId).initialize(this.random);
                    generator.setTempo(sections.get(currentSectionId).getTempo(), currentSectionInitialPosition);
                } else {
                    currentSectionId = null;
                }
            }
        }
        
        generator.addNotes(list);
    }
    
    public boolean getIsFinished()
    {
        return currentSectionId == null;
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

        generator.setTempo(currentSection.getTempo(), 0);
        generator.setTimeSignature(currentSection.getTimeSignatureNumerator(), currentSection.getTimeSignatureDenominator(), 0);

        generator.addNotes(currentSection.execute(this.getRandom()).offsetNotes(0));
    }
}