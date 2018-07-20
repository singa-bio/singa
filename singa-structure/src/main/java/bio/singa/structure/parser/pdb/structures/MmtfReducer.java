package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.interfaces.Model;
import bio.singa.structure.model.mmtf.MmtfStructure;

import java.util.Optional;

/**
 * @author cl
 */
public class MmtfReducer {

    static void reduceMMTFStructure(MmtfStructure structure, StructureParser.Reducer selector) {
        reduceModels(structure, selector);
        reduceChains(structure, selector);
    }

    private static void reduceModels(MmtfStructure structure, StructureParser.Reducer selector) {
        if (!selector.allModels) {
            for (Integer modelIdentifier : structure.getAllModelIdentifiers()) {
               if (modelIdentifier != selector.modelIdentifier) {
                   structure.removeModel(modelIdentifier);
               }
            }
        }
    }

    private static void reduceChains(MmtfStructure structure, StructureParser.Reducer selector) {
        if (!selector.allChains) {
            for (Integer modelIdentifier : structure.getAllModelIdentifiers()) {
                Optional<Model> optionalModel = structure.getModel(modelIdentifier);
                if (optionalModel.isPresent()) {
                    Model model = optionalModel.get();
                    for (String chainIdentifier : model.getAllChainIdentifiers()) {
                        if (!chainIdentifier.equals(selector.chainIdentifier)) {
                            model.removeChain(chainIdentifier);
                        }
                    }
                }
            }
        }
    }



}
