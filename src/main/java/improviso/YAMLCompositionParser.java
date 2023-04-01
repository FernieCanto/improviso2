/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author cfern
 */
public class YAMLCompositionParser {
    final private String fileName;
    final private ElementLibrary elementLibrary = new ElementLibrary();
    final private NoteNameInterpreter interpreter = new NoteNameInterpreter();

    public YAMLCompositionParser(String fileName) {
        this.fileName = fileName;
    }
    
    public void process() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        FileInputStream io = new FileInputStream(this.fileName);
        yaml.load(io);
    }
}
