/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.mocks;

import improviso.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author User
 */
public class SectionMock implements ExecutableSection {
    private final String id;
    private int tempo;
    private int timeSignatureNumerator;
    private int timeSignatureDenominator;
    private MIDINoteList notes;
    private int actualEnd;
    
    private boolean initialized;
    
    public SectionMock(String id) {
        this.id = id;
        this.initialized = false;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getTempo() {
        return this.tempo;
    }
    
    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    @Override
    public int getTimeSignatureNumerator() {
        return this.timeSignatureNumerator;
    }

    @Override
    public int getTimeSignatureDenominator() {
        return this.timeSignatureDenominator;
    }
    
    public void setTimeSignature(int numerator, int denominator) {
        this.timeSignatureNumerator = numerator;
        this.timeSignatureDenominator = denominator;
    }
    
    public void setNotes(MIDINoteList notes) {
        this.notes = notes;
    }
    
    public void setNotes(MIDINote[] arrayNotes) {
        this.notes = new MIDINoteList(arrayNotes);
    }
    
    public void setActualEnd(int actualEnd) {
        this.actualEnd = actualEnd;
    }

    @Override
    public MIDINoteList execute(Random random) {
        return this.notes;
    }

    @Override
    public int getActualEnd() {
        return this.actualEnd;
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void accept(SectionVisitor visitor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Track> getTracks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
