package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations;


import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposer;
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.oak.OakAtom;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.bioforscher.singa.structure.model.oak.StructuralEntityFilter.AtomFilter;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its beta carbon. This is only available for {@link
 * AminoAcid}s. For glycine a virtual beta carbon is calculated by superimposing alanine.
 *
 * @author fk
 */
public class BetaCarbonRepresentationScheme extends AbstractRepresentationScheme {

    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?> leafSubstructure) {
        // immediately return atom if part of structure
        final Optional<Atom> optionalBetaCarbon = leafSubstructure.getAtomByName("CB");
        if (optionalBetaCarbon.isPresent()) {
            return optionalBetaCarbon.get();
        }
        if (!(leafSubstructure instanceof AminoAcid)) {
            logger.warn("fallback for {} because it is no amino acid", leafSubstructure);
            return determineCentroid(leafSubstructure);
        }
        // create virtual beta carbon for glycine
        if (leafSubstructure.getFamily() == AminoAcidFamily.GLYCINE) {
            // superimpose alanine based on backbone
            // TODO add convenience functionality to superimpose single LeafSubstructures
            AminoAcid alanine = AminoAcidFamily.ALANINE.getPrototype();
            SubstructureSuperimposition superimposition = SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(
                    Stream.of(leafSubstructure).collect(Collectors.toList()),
                    Stream.of(alanine).collect(Collectors.toList()),
                    AtomFilter.isBackbone());
            // obtain virtual beta carbon
            Optional<Atom> optionalVirtualBetaCarbon = superimposition.getMappedFullCandidate().get(0).getAllAtoms().stream()
                    .filter(AtomFilter.isBetaCarbon())
                    .findAny();
            if (optionalVirtualBetaCarbon.isPresent()) {
                return new OakAtom(leafSubstructure.getAllAtoms().get(0).getIdentifier(),
                        ElementProvider.CARBON,
                        RepresentationSchemeType.BETA_CARBON.getAtomNameString(),
                        optionalVirtualBetaCarbon.get().getPosition().getCopy());
            }
        }
        return leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isBetaCarbon())
                .findAny()
                .orElseGet(() -> determineCentroid(leafSubstructure));
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.BETA_CARBON;
    }
}
