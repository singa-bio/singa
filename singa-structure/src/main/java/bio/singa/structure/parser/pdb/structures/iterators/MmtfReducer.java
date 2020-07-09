package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.structure.model.interfaces.Model;
import bio.singa.structure.model.mmtf.MmtfStructure;

import java.util.Optional;

/**
 * @author cl
 */
public class MmtfReducer {

    public static void reduceMMTFStructure(MmtfStructure structure, StructureReducer reducer) {
        reduceModels(structure, reducer);
        reduceChains(structure, reducer);
    }

    private static void reduceModels(MmtfStructure structure, StructureReducer reducer) {
        if (reducer.isReducingModels()) {
            for (Integer modelIdentifier : structure.getAllModelIdentifiers()) {
               if (modelIdentifier != reducer.getModelIdentifier()) {
                   structure.removeModel(modelIdentifier);
               }
            }
        }
    }

    private static void reduceChains(MmtfStructure structure, StructureReducer reducer) {
        if (reducer.isReducingChains()) {
            for (Integer modelIdentifier : structure.getAllModelIdentifiers()) {
                Optional<Model> optionalModel = structure.getModel(modelIdentifier);
                if (optionalModel.isPresent()) {
                    Model model = optionalModel.get();
                    for (String chainIdentifier : model.getAllChainIdentifiers()) {
                        if (!chainIdentifier.equals(reducer.getChainIdentifier())) {
                            model.removeChain(chainIdentifier);
                        }
                    }
                }
            }
        }
    }

}
