/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.Random;

/**
 * @author Fernie Canto
 * @param <T>
 */
abstract public class NumberRange<T extends Number> {
    final private T valueMin;
    final private T valueMax;
    final private T valueEndMin;
    final private T valueEndMax;
    
    public NumberRange(T valueMin, T valueMax) {
        this.valueMin = valueMin;
        this.valueMax = valueMax;
        this.valueEndMin = valueMin;
        this.valueEndMax = valueMax;
    }
    
    public NumberRange(T valueMin, T valueMax, T valueEndMin, T valueEndMax) {
        this.valueMin = valueMin;
        this.valueMax = valueMax;
        this.valueEndMin = valueEndMin;
        this.valueEndMax = valueEndMax;
    }
    
    public T getValue(Random rand) {
        return this.getValue(rand, 0.0);
    }
    
    abstract public T getValue(Random rand, double position);

    public T getValueMin() {
        return valueMin;
    }

    public T getValueMax() {
        return valueMax;
    }

    public T getValueEndMin() {
        return valueEndMin;
    }

    public T getValueEndMax() {
        return valueEndMax;
    }
}