package improviso;

import java.util.HashMap;

/**
 *
 * @author Fernie Canto
 */
public class ElementLibrary implements java.io.Serializable {
    private final HashMap<String, Pattern> patterns = new HashMap<>();
    private final HashMap<String, Group> groups = new HashMap<>();
    private final HashMap<String, Track> tracks = new HashMap<>();
    private final HashMap<String, Section> sections = new HashMap<>();
    
    public void addPattern(String id, Pattern p) {
        this.patterns.put(id, p);
    }
    
    public boolean hasPattern(String id) {
        return this.patterns.containsKey(id);
    }
    
    public Pattern getPattern(String id) {
        return this.patterns.get(id);
    }

    public void addGroup(String id, Group g) {
        this.groups.put(id, g);
    }
    
    public boolean hasGroup(String id) {
        return this.groups.containsKey(id);
    }

    public Group getGroup(String id) {
        return this.groups.get(id);
    }

    public void addTrack(String id, Track t) {
        this.tracks.put(id, t);
    }
    
    public boolean hasTrack(String id) {
        return this.tracks.containsKey(id);
    }

    public Track getTrack(String id) {
        return this.tracks.get(id);
    }

    public void addSection(String id, Section s) {
        this.sections.put(id, s);
    }
    
    public boolean hasSection(String id) {
        return this.sections.containsKey(id);
    }

    public Section getSection(String id) {
        return this.sections.get(id);
    }
}
