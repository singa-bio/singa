package de.bioforscher.chemistry.descriptive.elements;

import de.bioforscher.units.quantities.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.units.UnitProvider.GRAM_PER_MOLE;

/**
 * A chemical element or element is a species of atoms having the same number of protons. Generally no elements need
 * to be created since all common elements are deposited statically in the {@link ElementProvider}. Each element is
 * basically final. The isotopes and ions can easily be created with the provided methods.
 *
 * @author cl
 */
public class Element {

    // ................................................1s 2s 2p 3s 3p 4s  3d 4p 5s  4d 5p 6s  4f  5d 6p 7s  5f  6d 7p
    private static final int[] orbitalElectrons = {2, 2, 6, 2, 6, 2, 10, 6, 2, 10, 6, 2, 14, 10, 6, 2, 14, 10, 6};


    /**
     * The element's name.
     */
    private final String name;

    /**
     * The element's symbol.
     */
    private final String symbol;

    /**
     * The element's number of protons.
     */
    private final int protonNumber;

    /**
     * The element's number of electrons.
     */
    private final int electronNumber;

    /**
     * The element's number of neutrons.
     */
    private final int neutronNumber;

    /**
     * The atomic mass of the element.
     */
    private Quantity<MolarMass> atomicMass;

    /**
     * Creates a new Element with name, symbol, proton number and atomic weight.
     *
     * @param name         The name.
     * @param symbol       The symbol.
     * @param protonNumber The proton number.
     * @param atomicWeight The atomic weight.
     */
    public Element(String name, String symbol, int protonNumber, Quantity<MolarMass> atomicWeight) {
        this.name = name;
        this.symbol = symbol;
        this.protonNumber = protonNumber;
        this.electronNumber = protonNumber;
        this.neutronNumber = protonNumber;
        this.atomicMass = atomicWeight;
    }

    /**
     * Creates a new Element with name, symbol, proton number and atomic weight in
     * {@link de.bioforscher.units.UnitProvider#GRAM_PER_MOLE g/mol}.
     *
     * @param name         The name.
     * @param symbol       The symbol.
     * @param protonNumber The proton number.
     * @param atomicWeight The atomic weight.
     */
    public Element(String name, String symbol, int protonNumber, double atomicWeight) {
        this(name, symbol, protonNumber, Quantities.getQuantity(atomicWeight, GRAM_PER_MOLE));
    }

    /**
     * Creates a new Element with the possibility to specify electron and neutron number.
     *
     * @param element        A previously defined element.
     * @param electronNumber The electron number.
     * @param neutronNumber  The neutron number.
     */
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

    private static int calculateValenceElectronNumber(int remainingElectrons) {
        if (remainingElectrons == 1) {
            return 1;
        }
        if (remainingElectrons == 2) {
            return 2;
        }
        int nextOrbital = 0;
        while (remainingElectrons > orbitalElectrons[nextOrbital]){
            remainingElectrons -= orbitalElectrons[nextOrbital];
            nextOrbital++;
        }
        System.out.println(orbitalElectrons[nextOrbital]);
        System.out.println(remainingElectrons);
        return orbitalElectrons[nextOrbital]-remainingElectrons;
    }

    public static void main(String[] args) {
        Element.calculateValenceElectronNumber(ElementProvider.PHOSPHORUS.getElectronNumber());
    }


    /**
     * Returns the name.
     *
     * @return The name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the symbol.
     *
     * @return The symbol.
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * Returns the proton number.
     *
     * @return The proton number.
     */
    public int getProtonNumber() {
        return this.protonNumber;
    }

    /**
     * Returns the atomic mass.
     *
     * @return The atomic mass.
     */
    public Quantity<MolarMass> getAtomicMass() {
        return this.atomicMass;
    }

    /**
     * Sets the atomic mass.
     *
     * @param atomicMass The atomic mass.
     */
    public void setAtomicMass(Quantity<MolarMass> atomicMass) {
        this.atomicMass = atomicMass;
    }

    /**
     * Returns the electron number.
     *
     * @return The electron number.
     */
    public int getElectronNumber() {
        return this.electronNumber;
    }

    /**
     * Returns the neutron number.
     *
     * @return The neutron number.
     */
    public int getNeutronNumber() {
        return this.neutronNumber;
    }

    /**
     * Converts this element into an ion (cation) by decreasing its electron count.
     *
     * @param charge The total charge of this element
     * @return An cation of this element.
     */
    public Element asIon(int charge) {
        if (charge != 0) {
            return new Element(this, this.electronNumber + charge, this.neutronNumber);
        }
        return this;
    }

    /**
     * Converts this element into an ion (cation) by decreasing its electron count.
     *
     * @param numberOfElectronsLost The number of electrons to decrease.
     * @return An cation of this element.
     */
    public Element asCation(int numberOfElectronsLost) {
        return asIon(-numberOfElectronsLost);
    }

    /**
     * Converts this element into an ion (anion) by increasing its electron count.
     *
     * @param numberOfElectronsGained The number of electrons gained.
     * @return An anion of this element.
     */
    public Element asAnion(int numberOfElectronsGained) {
        return asIon(numberOfElectronsGained);
    }

    /**
     * Converts this element into an isotope by adjusting its neutron count.
     *
     * @param numberOfNeutrons The number of neutrons of the isotope.
     * @return An isotope of this element.
     */
    public Element asIsotope(int numberOfNeutrons) {
        if (numberOfNeutrons != this.neutronNumber) {
            return new Element(this, this.electronNumber, numberOfNeutrons);
        }
        return this;
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
        return this.electronNumber - this.protonNumber;
    }

    /**
     * Returns {@code true} if this Element is an isotope and {@code false} otherwise.
     *
     * @return {@code true} if this Element is an isotope and {@code false} otherwise.
     */
    public boolean isIsotope() {
        return this.protonNumber != this.neutronNumber;
    }

    @Override
    public String toString() {
        return (this.neutronNumber != this.protonNumber ? this.neutronNumber : "") + this.symbol + (getCharge() != 0 ? getCharge() : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Element element = (Element) o;

        if (this.protonNumber != element.protonNumber) return false;
        if (this.electronNumber != element.electronNumber) return false;
        return this.neutronNumber == element.neutronNumber;
    }

    @Override
    public int hashCode() {
        int result = this.protonNumber;
        result = 31 * result + this.electronNumber;
        result = 31 * result + this.neutronNumber;
        return result;
    }
}
