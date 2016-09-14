package de.bioforscher.chemistry.descriptive.elements;

import de.bioforscher.units.quantities.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.units.UnitProvider.GRAM_PER_MOLE;

/**
 * A chemical element or element is a species of atoms having the same number of protons.
 */
public class Element {

    private final String name;
    private final String symbol;
    private final int protonNumber;
    private final int electronNumber;
    private final int neutronNumber;

    private Quantity<MolarMass> atomicMass;

    public Element(String name, String symbol, int protonNumber, Quantity<MolarMass> atomicWeight) {
        this.name = name;
        this.symbol = symbol;
        this.protonNumber = protonNumber;
        this.electronNumber = protonNumber;
        this.neutronNumber = protonNumber;
        this.atomicMass = atomicWeight;
    }

    public Element(String name, String symbol, int protonNumber, double atomicWeight) {
        this(name, symbol, protonNumber, Quantities.getQuantity(atomicWeight, GRAM_PER_MOLE));
    }

    private Element(Element element, int electronNumber, int neutronNumber) {
        this.name = element.getName();
        this.symbol = element.getSymbol();
        this.protonNumber = element.getProtonNumber();
        this.electronNumber = electronNumber;
        this.neutronNumber = neutronNumber;
        if (neutronNumber != this.protonNumber) {
            // TODO determine correctly
            this.atomicMass = Quantities.getQuantity(neutronNumber, GRAM_PER_MOLE);
        } else {
            this.atomicMass = element.getAtomicMass();
        }

    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getProtonNumber() {
        return protonNumber;
    }

    public Quantity<MolarMass> getAtomicMass() {
        return atomicMass;
    }

    public void setAtomicMass(Quantity<MolarMass> atomicMass) {
        this.atomicMass = atomicMass;
    }

    public int getElectronNumber() {
        return electronNumber;
    }

    public int getNeutronNumber() {
        return neutronNumber;
    }

    /**
     * Converts this element into an ion (cation) by decreasing its electron count.
     *
     * @param numberOfElectronsLost The number of electrons to decrease.
     * @return An cation of this element.
     */
    public Element asCation(int numberOfElectronsLost) {
        return new Element(this, this.electronNumber - numberOfElectronsLost, this.neutronNumber);
    }

    /**
     * Converts this element into an ion (anion) by increasing its electron count.
     *
     * @param numberOfElectronsGained The number of electrons gained.
     * @return An anion of this element.
     */
    public Element asAnion(int numberOfElectronsGained) {
        return new Element(this, this.electronNumber + numberOfElectronsGained, this.neutronNumber);
    }

    /**
     * Converts this element into an isotope by adjusting its neutron count.
     *
     * @param numberOfNeutrons The number of neutrons of the isotope.
     * @return An isotope of this element.
     */
    public Element asIsotope(int numberOfNeutrons) {
        return new Element(this, this.electronNumber, numberOfNeutrons);
    }

    /**
     * Returns {@code true} if this Element is an ion and {@code false} otherwise.
     *
     * @return {@code true} if this Element is an ion and {@code false} otherwise.
     */
    public boolean isIon() {
        return this.electronNumber != this.protonNumber;
    }

    /**
     * Returns {@code true} if this Element is an anion (excess of electrons) and {@code false} otherwise.
     *
     * @return {@code true} if this Element is an anion and {@code false} otherwise.
     */
    public boolean isAnion() {
        return this.protonNumber < this.electronNumber;
    }

    /**
     * Returns {@code true} if this Element is an cation (shortage of electrons) and {@code false} otherwise.
     *
     * @return {@code true} if this Element is an cation and {@code false} otherwise.
     */
    public boolean isCation() {
        return this.protonNumber > this.electronNumber;
    }

    /**
     * Returns the charge of this Element.
     *
     * @return The charge of this Element.
     */
    public int getCharge() {
        return this.protonNumber - this.electronNumber;
    }

    /**
     * Returns {@code true} if this Element is an isotope and {@code false} otherwise.
     *
     * @return {@code true} if this Element is an isotope and {@code false} otherwise.
     */
    public boolean isIsotope() {
        return this.protonNumber != neutronNumber;
    }

    @Override
    public String toString() {
        return symbol + (getCharge() != 0 ? getCharge() : "");
    }
}
