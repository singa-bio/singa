package de.bioforscher.singa.chemistry.physical.atoms.representations;


import de.bioforscher.singa.chemistry.algorithms.superimposition.SubStructureSuperimposer;
import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.UncertainAtom;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leafes.LeafSubstructure;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its beta carbon. This is only available for
 * {@link AminoAcid}s. For glycine a virtual beta carbon is calculated by superimposing alanine.
 *
 * @author fk
 */
public class BetaCarbonRepresentationScheme extends AbstractRepresentationScheme {

    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?, ?> leafSubstructure) {
        if (!(leafSubstructure instanceof AminoAcid)) {
            logger.warn("fallback for ", leafSubstructure);
            return determineCentroid(leafSubstructure);
        }
        // create virtual beta carbon for glycine
        if (leafSubstructure.getFamily() == AminoAcidFamily.GLYCINE) {
            // superimpose alanine based on backbone
            // TODO add convenience functionality to superimpose single LeafSubstructures
            AminoAcid alanine = AminoAcidFamily.ALANINE.getPrototype();
            SubstructureSuperimposition superimposition = SubStructureSuperimposer.calculateIdealSubstructureSuperimposition(
                    Stream.of(leafSubstructure).collect(Collectors.toList()),
                    Stream.of(alanine).collect(Collectors.toList()),
                    AtomFilter.isBackbone());
            // obtain virtual beta carbon
            Optional<Atom> optionalBetaCarbon = superimposition.getMappedFullCandidate().get(0).getAllAtoms().stream()
                    .filter(AtomFilter.isBetaCarbon())
                    .findAny();
            if (optionalBetaCarbon.isPresent()) {
                return new UncertainAtom(leafSubstructure.getAllAtoms().get(0).getIdentifier(),
                        ElementProvider.CARBON,
                        RepresentationSchemeType.CB.getAtomNameString(),
                        optionalBetaCarbon.get().getPosition().getCopy());
            }
        }
        return leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isBetaCarbon())
                .findAny()
                .orElse(determineCentroid(leafSubstructure));
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.CB;
    }
}
