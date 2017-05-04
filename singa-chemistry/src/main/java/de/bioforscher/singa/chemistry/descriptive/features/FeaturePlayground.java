package de.bioforscher.singa.chemistry.descriptive.features;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.chemistry.parser.chebi.ChEBIParserService;

import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.DIFFUSIVITY;

/**
 * @author cl
 */
public class FeaturePlayground {

    public static void main(String[] args) {
        Species atp = ChEBIParserService.parse("CHEBI:15422");
        atp.assignFeature(DIFFUSIVITY);

        System.out.println(atp.getFeature(DIFFUSIVITY).toString());

    }

}
