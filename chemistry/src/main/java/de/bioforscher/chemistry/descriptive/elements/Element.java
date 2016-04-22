package de.bioforscher.chemistry.descriptive.elements;


import de.bioforscher.units.quantities.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.units.UnitDictionary.GRAM_PER_MOLE;

public class Element {

    private String name;
    private String sybmol;
    private int protonNumber;
    private Quantity<MolarMass> atomicWeight;

    public Element(String name, String sybmol, int protonNumber, Quantity<MolarMass> atomicWeight) {
        super();
        this.name = name;
        this.sybmol = sybmol;
        this.protonNumber = protonNumber;
        this.atomicWeight = atomicWeight;
    }

    public Element(String name, String sybmol, int protonNumber, double atomicWeight) {
        super();
        this.name = name;
        this.sybmol = sybmol;
        this.protonNumber = protonNumber;
        this.atomicWeight = Quantities.getQuantity(atomicWeight, GRAM_PER_MOLE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSybmol() {
        return sybmol;
    }

    public void setSybmol(String sybmol) {
        this.sybmol = sybmol;
    }

    public int getProtonNumber() {
        return protonNumber;
    }

    public void setProtonNumber(int protonNumber) {
        this.protonNumber = protonNumber;
    }

    public Quantity<MolarMass> getAtomicWeight() {
        return atomicWeight;
    }

    public void setAtomicWeight(Quantity<MolarMass> atomicWeight) {
        this.atomicWeight = atomicWeight;
    }

}
