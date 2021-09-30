package bio.singa.structure.algorithms.superimposition.fit3d.representations;


import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposer;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.oak.OakAtom;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.ALANINE;
import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.GLYCINE;
import static bio.singa.structure.model.oak.StructuralEntityFilter.AtomFilter;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its beta carbon. This is only available for {@link
 * AminoAcid}s. For glycine a virtual beta carbon is calculated by superimposing alanine.
 *
 * @author fk
 */
public class BetaCarbonRepresentationScheme extends AbstractRepresentationScheme {

    @Override
    public Atom determineRepresentingAtom(LeafSubstructure leafSubstructure) {
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
        if (leafSubstructure.getFamily() == GLYCINE) {
            // superimpose alanine based on backbone
            // TODO add convenience functionality to superimpose single LeafSubstructures
            AminoAcid alanine = StructuralFamilies.AminoAcids.getPrototype(ALANINE);
            SubstructureSuperimposition superimposition = SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(
                    Stream.of(leafSubstructure).collect(Collectors.toList()),
                    Stream.of(alanine).collect(Collectors.toList()),
                    AtomFilter.isBackbone());
            // obtain virtual beta carbon
            Optional<Atom> optionalVirtualBetaCarbon = superimposition.getMappedFullCandidate().get(0).getAllAtoms().stream()
                    .filter(AtomFilter.isBetaCarbon())
                    .findAny();
            if (optionalVirtualBetaCarbon.isPresent()) {
                return new OakAtom(leafSubstructure.getAllAtoms().get(0).getAtomIdentifier(),
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
