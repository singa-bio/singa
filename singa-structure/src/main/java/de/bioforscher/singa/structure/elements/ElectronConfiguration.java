package de.bioforscher.singa.structure.elements;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ElectronConfiguration {

    private final EnumMap<AtomicOrbital, Integer> configuration;

    public ElectronConfiguration() {
        configuration = new EnumMap<>(AtomicOrbital.class);
    }

    public static ElectronConfiguration parseElectronConfigurationFromString(String orbitalsString) {
        String[] splitOrbitals = orbitalsString.toLowerCase().split("-");
        ElectronConfiguration configuration = new ElectronConfiguration();
        for (String orbitalString : splitOrbitals) {
            configuration.getConfiguration().put(AtomicOrbital.getAtomicOrbital(orbitalString.substring(0, 2)), Integer.valueOf(orbitalString.substring(2)));
        }
        return configuration;
    }

    public Map<AtomicOrbital, Integer> getConfiguration() {
        return configuration;
    }

    public int getOuterMostShell() {
        return configuration.keySet().stream()
                .mapToInt(AtomicOrbital::getShell)
                .max()
                .orElseThrow(() -> new IllegalStateException("The configuration does not contain any orbitals."));
    }

    public Map<AtomicOrbital, Integer> getOrbitalsOfShell(int shell) {
        return configuration.entrySet().stream()
                .filter(entry -> entry.getKey().getShell() == shell)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public int getTotalNumberOfElectrons() {
        return configuration.values().stream()
                .reduce((v1, v2) -> v1 + v2)
                .orElseThrow(() -> new IllegalStateException("The configuration does not contain any orbitals."));

    }

    public int getNumberOfValenceElectrons() {
        // first group = main group elements with complete d and f sub shells
        // second group = transition group element with incomplete d and f shells
        // TODO getIncompleteShells is called twice
        if (isTransitionGroup()) {
            Map<AtomicOrbital, Integer> incompleteShells = getIncompleteShells();
            Map<AtomicOrbital, Integer> outerShells = getOrbitalsOfShell(getOuterMostShell());
            Map<AtomicOrbital, Integer> distinct = new HashMap<>();
            distinct.putAll(incompleteShells);
            distinct.putAll(outerShells);
            return distinct.values().stream()
                    .reduce((v1, v2) -> v1 + v2)
                    .orElseThrow(() -> new IllegalStateException("The configuration does not contain any orbitals."));
        }
        return getNumberOfElectronsInOutermostShell();
    }

    private boolean isTransitionGroup() {
        return getIncompleteShells().keySet()
                .stream().anyMatch(orbital -> orbital.getSubShell() == 'd' || orbital.getSubShell() == 'f');
    }

    private int getNumberOfElectronsInOutermostShell() {
        return getOrbitalsOfShell(getOuterMostShell()).values().stream()
                .reduce((v1, v2) -> v1 + v2)
                .orElseThrow(() -> new IllegalStateException("The configuration does not contain any orbitals."));
    }

    public Map<AtomicOrbital, Integer> getIncompleteShells() {
        return configuration.entrySet().stream()
                .filter(entry -> entry.getKey().getMaximalElectrons() - entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public String toString() {
        return configuration.entrySet().stream()
                .map(entry -> entry.getKey().toString() + entry.getValue())
                .collect(Collectors.joining("-"));
    }

    public enum AtomicOrbital {
        // 1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f14-6s2-6p6-6d6-7s2
        S1('s', 1),
        S2('s', 2), P2('p', 2),
        S3('s', 3), P3('p', 3), D3('d', 3),
        S4('s', 4), P4('p', 4), D4('d', 4), F4('f', 4),
        S5('s', 5), P5('p', 5), D5('d', 5), F5('f', 5),
        S6('s', 6), P6('p', 6), D6('d', 6),
        S7('s', 7), P7('p', 7);

        public static final int MAX_ELECTRONS_S = 2;
        public static final int MAX_ELECTRONS_P = 6;
        public static final int MAX_ELECTRONS_D = 10;
        public static final int MAX_ELECTRONS_F = 14;
        private final int shell;
        private final char subShell;

        AtomicOrbital(char subShell, int shell) {
            this.shell = shell;
            this.subShell = subShell;
        }

        public static AtomicOrbital getAtomicOrbital(final String orbitalString) {
            if (orbitalString.length() == 2) {
                if (Character.isDigit(orbitalString.charAt(0))) {
                    return Arrays.stream(AtomicOrbital.values())
                            .filter(orbital -> orbital.shell == Character.getNumericValue(orbitalString.charAt(0)) && orbital.subShell == orbitalString.charAt(1))
                            .findAny().orElseThrow(() -> new IllegalArgumentException("The orbital " + orbitalString + " is no valid atomic orbital."));
                }
                return Arrays.stream(AtomicOrbital.values())
                        .filter(orbital -> orbital.shell == Character.getNumericValue(orbitalString.charAt(1)) && orbital.subShell == orbitalString.charAt(0))
                        .findAny().orElseThrow(() -> new IllegalArgumentException("The orbital " + orbitalString + " is no valid atomic orbital."));
            }
            throw new IllegalArgumentException("The orbital " + orbitalString + " is no valid atomic orbital.");
        }

        public int getShell() {
            return shell;
        }

        public char getSubShell() {
            return subShell;
        }

        public int getMaximalElectrons() {
            switch (subShell) {
                case 's':
                    return MAX_ELECTRONS_S;
                case 'p':
                    return MAX_ELECTRONS_P;
                case 'd':
                    return MAX_ELECTRONS_D;
                case 'f':
                    return MAX_ELECTRONS_F;
                default:
                    return 0;
            }
        }

        @Override
        public String toString() {
            return String.valueOf(shell) + subShell;
        }
    }

}
