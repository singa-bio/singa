package de.bioforscher.singa.chemistry.descriptive.elements;

import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass.GRAM_PER_MOLE;

/**
 * A chemical element or element is a species of atoms having the same number of protons. Generally no elements need
 * to be created since all common elements are deposited statically in the {@link ElementProvider}. Each element is
 * basically final. The isotopes and ions can easily be created with the provided methods.
 *
 * @author cl
 */
public class Element {

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
     * The number of valence electrons.
     */
    private final int valenceElectronNumber;

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
     * @param name                  The name.
     * @param symbol                The symbol.
     * @param protonNumber          The proton number.
     * @param atomicWeight          The atomic weight.
     * @param electronConfiguration The electron configuration.
     */
    public Element(String name, String symbol, int protonNumber, Quantity<MolarMass> atomicWeight, String electronConfiguration) {
        this.name = name;
        this.symbol = symbol;
        this.protonNumber = protonNumber;
        this.electronNumber = protonNumber;
        this.valenceElectronNumber = ElectronConfiguration.parseElectronConfigurationFromString(electronConfiguration).getNumberOfValenceElectrons();
        this.neutronNumber = protonNumber;
        this.atomicMass = atomicWeight;
    }

    /**
     * Creates a new Element with name, symbol, proton number and atomic weight in
     * {@link MolarMass#GRAM_PER_MOLE g/mol}.
     *
     * @param name                  The name.
     * @param symbol                The symbol.
     * @param protonNumber          The proton number.
     * @param atomicWeight          The atomic weight.
     * @param electronConfiguration The electron configuration.
     */
    public Element(String name, String symbol, int protonNumber, double atomicWeight, String electronConfiguration) {
        this(name, symbol, protonNumber, Quantities.getQuantity(atomicWeight, GRAM_PER_MOLE), electronConfiguration);
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
        this.valenceElectronNumber = element.valenceElectronNumber;
        this.neutronNumber = neutronNumber;
        if (neutronNumber != this.protonNumber) {
            // TODO determine correctly
            this.atomicMass = Quantities.getQuantity(neutronNumber, GRAM_PER_MOLE);
        } else {
            this.atomicMass = element.getAtomicMass();
        }

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
     * Returns the number of valence electrons.
     *
     * @return The number of valence electrons.
     */
    public int getValenceElectronNumber() {
        return this.valenceElectronNumber;
    }

    /**
     * Estimates the Number of potential bonds this element forms.
     *
     * @return The number of potential bonds this element forms
     */
    public int getNumberOfPotentialBonds() {
        // very preliminary
        if (this.electronNumber <= 2) {
            return 2 - this.electronNumber;
        }
        // octet rule
        if (this.valenceElectronNumber <= 4) {
            return this.valenceElectronNumber;
        }
        return 8 - this.valenceElectronNumber;
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
     * @param massNumber The mass number (number of protons + neutrons).
     * @return An isotope of this element.
     */
    public Element asIsotope(int massNumber) {
        int neutronNumber = massNumber - this.protonNumber;
        if (neutronNumber != this.neutronNumber) {
            return new Element(this, this.electronNumber, neutronNumber);
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

    public int getMassNumber() {
        return this.neutronNumber + this.protonNumber;
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
        return (this.neutronNumber != this.protonNumber ? getMassNumber() : "") + this.symbol + (getCharge() != 0 ? getCharge() : "");
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
