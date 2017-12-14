package improviso;

public class MIDITrack implements java.io.Serializable {
    final private int channel;
    final private int instrument;
    final private int volume;
    final private int pan;
    
    public MIDITrack(int channel, int instrument, int volume, int pan) {
        this.channel = channel;
        this.instrument = instrument;
        this.volume = volume;
        this.pan = pan;
    }
    
    /**
     * @return the channel
     */
    public int getChannel() {
        return channel;
    }

    /**
     * @return the instrument
     */
    public int getInstrument() {
        return instrument;
    }

    /**
     * @return the volume
     */
    public int getVolume() {
        return volume;
    }

    /**
     * @return the pan
     */
    public int getPan() {
        return pan;
    }
}