package bio.singa.chemistry.features.smiles;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.chemistry.model.MoleculeAtom;
import bio.singa.chemistry.model.MoleculeBond;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.chemistry.model.elements.ElementProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.beam.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of SMILES string generation of {@link MoleculeGraph}s with the help of Beam (@link
 * https://github.com/johnmay/beam).
 */
public class SmilesGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SmilesGenerator.class);

    private static final Map<CovalentBondType, Bond> BEAM_BOND_TYPE_MAP;

    static {
        BEAM_BOND_TYPE_MAP = new HashMap<>();
        BEAM_BOND_TYPE_MAP.put(CovalentBondType.SINGLE_BOND, Bond.SINGLE);
        BEAM_BOND_TYPE_MAP.put(CovalentBondType.DOUBLE_BOND, Bond.DOUBLE);
        BEAM_BOND_TYPE_MAP.put(CovalentBondType.TRIPLE_BOND, Bond.TRIPLE);
        BEAM_BOND_TYPE_MAP.put(CovalentBondType.QUADRUPLE_BOND, Bond.QUADRUPLE);
        BEAM_BOND_TYPE_MAP.put(CovalentBondType.AROMATIC_BOND, Bond.AROMATIC);
        BEAM_BOND_TYPE_MAP.put(CovalentBondType.ISOMERIC_BOND_DOWN, Bond.DOWN);
        BEAM_BOND_TYPE_MAP.put(CovalentBondType.ISOMERIC_BOND_UP, Bond.UP);
        BEAM_BOND_TYPE_MAP.put(CovalentBondType.UNCONNECTED, Bond.DOT);
    }

    private Graph beamGraph;

    public SmilesGenerator(MoleculeGraph moleculeGraph) {
        toBeamGraph(moleculeGraph);
    }

    public static String generate(MoleculeGraph moleculeGraph) throws IOException {
        SmilesGenerator smilesGenerator = new SmilesGenerator(moleculeGraph);
        return smilesGenerator.beamGraph.toSmiles();
    }

    private void toBeamGraph(MoleculeGraph moleculeGraph) {
        logger.info("converting molecule graph {} to Beam graph", moleculeGraph);
        GraphBuilder beamGraphBuilder = GraphBuilder.create(moleculeGraph.getNodes().size());
        Map<Integer, Integer> atomIdentifierMap = new HashMap<>();
        int i = 0;
        // add atoms to beam graph
        for (MoleculeAtom atom : moleculeGraph.getNodes()) {
            if (atom.getElement() == ElementProvider.HYDROGEN) {
                logger.info("skipping hydrogen {}", atom);
                continue;
            }
            // TODO check for aromatic involved atoms and add support for isotopes
            AtomBuilder beamAtomBuilder = AtomBuilder.create(atom.getElement().getSymbol());
            if (atom.getElement().getCharge() != 0) {
                beamAtomBuilder.charge(atom.getElement().getCharge());
            }
            if (atom.getElement().getNeutronNumber() - atom.getElement().getProtonNumber() != 0) {
                beamAtomBuilder.isotope(atom.getElement().getNeutronNumber() - atom.getElement().getProtonNumber());
            }
            beamGraphBuilder.add(beamAtomBuilder.build());
            atomIdentifierMap.put(atom.getIdentifier(), i);
            i++;
        }
        // create bonds
        for (MoleculeBond bond : moleculeGraph.getEdges()) {
            if (!atomIdentifierMap.containsKey(bond.getSource().getIdentifier()) ||
                    !atomIdentifierMap.containsKey(bond.getTarget().getIdentifier())) {
                logger.debug("skipping hydrogen bond {}", bond);
                continue;
            }
            beamGraphBuilder.add(atomIdentifierMap.get(bond.getSource().getIdentifier()),
                    atomIdentifierMap.get(bond.getTarget().getIdentifier()), BEAM_BOND_TYPE_MAP.get(bond.getType()));
        }
        beamGraph = Functions.collapse(beamGraphBuilder.build());
    }
}
