package bio.singa.chemistry.model.elements;

import bio.singa.features.quantities.MolarMass;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static bio.singa.features.units.UnitProvider.ANGSTROEM;

/**
 * A chemical element or element is a species of atoms having the same number of protons. Generally no elements need to
 * be created since all common elements are deposited statically in the {@link ElementProvider}. Each element is
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

    private Quantity<Length> vanDerWaalsRadius;

    /**
     * Creates a new Element with name, symbol, proton number and atomic weight.
     *
     * @param name The name.
     * @param symbol The symbol.
     * @param protonNumber The proton number.
     * @param atomicWeight The atomic weight.
     * @param electronConfiguration The electron configuration.
     */
    public Element(String name, String symbol, int protonNumber, Quantity<MolarMass> atomicWeight, String electronConfiguration) {
        this.name = name;
        this.symbol = symbol;
        this.protonNumber = protonNumber;
        electronNumber = protonNumber;
        valenceElectronNumber = ElectronConfiguration.parseElectronConfigurationFromString(electronConfiguration)
                .getNumberOfValenceElectrons() - getCharge();
        neutronNumber = protonNumber;
        atomicMass = atomicWeight;
    }

    /**
     * Creates a new Element with name, symbol, proton number and atomic weight in {@link MolarMass#GRAM_PER_MOLE
     * g/mol}.
     *
     * @param name The name.
     * @param symbol The symbol.
     * @param protonNumber The proton number.
     * @param atomicWeight The atomic weight.
     * @param electronConfiguration The electron configuration.
     * @param vanDerWaalsRadius The van der Waals radius.
     */
    public Element(String name, String symbol, int protonNumber, double atomicWeight, String electronConfiguration, double vanDerWaalsRadius) {
        this(name, symbol, protonNumber, Quantities.getQuantity(atomicWeight, MolarMass.GRAM_PER_MOLE), electronConfiguration);
        this.vanDerWaalsRadius = Quantities.getQuantity(vanDerWaalsRadius, ANGSTROEM);
    }

    public Element(String name, String symbol, int protonNumber, double atomicWeight, String electronConfiguration) {
        this(name, symbol, protonNumber, Quantities.getQuantity(atomicWeight, MolarMass.GRAM_PER_MOLE), electronConfiguration);
    }

    private Element(int charge, Element element) {
        name = element.getName();
        symbol = element.getSymbol();
        protonNumber = element.getProtonNumber();
        neutronNumber = element.getNeutronNumber();
        electronNumber = element.electronNumber - charge;
        valenceElectronNumber = element.valenceElectronNumber - charge;
    }

    private Element(Element element, int neutronNumber) {
        name = element.getName();
        symbol = element.getSymbol();
        protonNumber = element.getProtonNumber();
        electronNumber = element.getElectronNumber();
        valenceElectronNumber = element.getValenceElectronNumber();
        this.neutronNumber = neutronNumber - protonNumber;
        if (neutronNumber != protonNumber) {
            // TODO determine correctly
            atomicMass = Quantities.getQuantity(neutronNumber, MolarMass.GRAM_PER_MOLE);
        } else {
            atomicMass = element.getAtomicMass();
        }
    }

    /**
     * Returns the name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the symbol.
     *
     * @return The symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the proton number.
     *
     * @return The proton number.
     */
    public int getProtonNumber() {
        return protonNumber;
    }

    /**
     * Returns the atomic mass.
     *
     * @return The atomic mass.
     */
    public Quantity<MolarMass> getAtomicMass() {
        return atomicMass;
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
        return electronNumber;
    }

    /**
     * Returns the number of valence electrons.
     *
     * @return The number of valence electrons.
     */
    public int getValenceElectronNumber() {
        return valenceElectronNumber;
    }

    /**
     * Estimates the Number of potential bonds this element forms.
     *
     * @return The number of potential bonds this element forms
     */
    public int getNumberOfPotentialBonds() {
        // very preliminary
        if (electronNumber <= 2) {
            return 2 - electronNumber;
        }
        // octet rule
        if (valenceElectronNumber <= 4) {
            return valenceElectronNumber;
        }
        return 8 - valenceElectronNumber;
    }

    /**
     * Returns the neutron number.
     *
     * @return The neutron number.
     */
    public int getNeutronNumber() {
        return neutronNumber;
    }

    /**
     * Converts this element into an ion (cation) by decreasing its electron count.
     *
     * @param charge The total charge of this element
     * @return An cation of this element.
     */
    public Element asIon(int charge) {
        if (charge != 0) {
            return new Element(charge, this);
        }
        return this;
    }

    /**
     * Converts this element into an cation by decreasing its electron count.
     *
     * @param numberOfElectronsLost The number of electrons to decrease.
     * @return An cation of this element.
     */
    public Element asCation(int numberOfElectronsLost) {
        return asIon(numberOfElectronsLost);
    }

    /**
     * Converts this element into an anion by increasing its electron count.
     *
     * @param numberOfElectronsGained The number of electrons gained.
     * @return An anion of this element.
     */
    public Element asAnion(int numberOfElectronsGained) {
        return asIon(-numberOfElectronsGained);
    }

    /**
     * Converts this element into an isotope by adjusting its neutron count.
     *
     * @param massNumber The mass number (number of protons + neutrons).
     * @return An isotope of this element.
     */
    public Element asIsotope(int massNumber) {
        return new Element(this, massNumber);
    }

    /**
     * Returns {@code true} if this Element is an ion and {@code false} otherwise.
     *
     * @return {@code true} if this Element is an ion and {@code false} otherwise.
     */
    public boolean isIon() {
        return electronNumber != protonNumber;
    }

    /**
     * Returns {@code true} if this Element is an anion (excess of electrons) and {@code false} otherwise.
     *
     * @return {@code true} if this Element is an anion and {@code false} otherwise.
     */
    public boolean isAnion() {
        return protonNumber < electronNumber;
    }

    /**
     * Returns {@code true} if this Element is an cation (shortage of electrons) and {@code false} otherwise.
     *
     * @return {@code true} if this Element is an cation and {@code false} otherwise.
     */
    public boolean isCation() {
        return protonNumber > electronNumber;
    }

    /**
     * Returns the charge of this Element.
     *
     * @return The charge of this Element.
     */
    public int getCharge() {
        return protonNumber - electronNumber;
    }

    public int getMassNumber() {
        return neutronNumber + protonNumber;
    }

    public Quantity<Length> getVanDerWaalsRadius() {
        return vanDerWaalsRadius;
    }

    /**
     * Returns {@code true} if this Element is an isotope and {@code false} otherwise.
     *
     * @return {@code true} if this Element is an isotope and {@code false} otherwise.
     */
    public boolean isIsotope() {
        return protonNumber != neutronNumber;
    }

    @Override
    public String toString() {
        return symbol + (getCharge() != 0 ? getCharge() : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Element element = (Element) o;

        if (protonNumber != element.protonNumber) return false;
        if (electronNumber != element.electronNumber) return false;
        return neutronNumber == element.neutronNumber;
    }

    @Override
    public int hashCode() {
        int result = protonNumber;
        result = 31 * result + electronNumber;
        result = 31 * result + neutronNumber;
        return result;
    }
}
