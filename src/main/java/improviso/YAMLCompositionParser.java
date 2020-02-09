/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author cfern
 */
public class YAMLCompositionParser {
    private final String fileName;

    public YAMLCompositionParser(String fileName) {
        this.fileName = fileName;
    }
    
    public void process() {
        Yaml yaml = new Yaml();
    }
}
