package bio.singa.chemistry.algorithms;

import bio.singa.chemistry.features.smiles.SmilesParser;
import bio.singa.structure.model.molecules.MoleculeAtom;
import bio.singa.structure.model.molecules.MoleculeGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;

public class RECAPFragmenter {

    private static final Logger logger = LoggerFactory.getLogger(RECAPFragmenter.class);

    private static final TreeMap<Integer, FragmentationRules> fragmentationRules;

    static {
        fragmentationRules = new TreeMap<>();
        // 1 - amide rule
        MoleculeGraph amideGraph = SmilesParser.parse("CC(=O)N(C)C");
        BiFunction<MoleculeAtom, MoleculeAtom, Boolean> amideCondition = (atom1, atom2) -> {
            return true;
        };
        fragmentationRules.put(1, new FragmentationRules(amideGraph, amideCondition));
    }

    public RECAPFragmenter(Set<MoleculeGraph> molecules) {
        fragment(molecules);
    }

    private void fragment(Set<MoleculeGraph> molecules) {

    }

    private static class FragmentationRules {

        private MoleculeGraph moleculeGraph;
        private BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition;

        public FragmentationRules(MoleculeGraph moleculeGraph, BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition) {
            this.moleculeGraph = moleculeGraph;
            this.atomCondition = atomCondition;
        }

        public MoleculeGraph getMoleculeGraph() {
            return moleculeGraph;
        }

        public void setMoleculeGraph(MoleculeGraph moleculeGraph) {
            this.moleculeGraph = moleculeGraph;
        }
    }
}
