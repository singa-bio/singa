package bio.singa.simulation.export.format;

import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.model.Feature;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static bio.singa.features.units.UnitProvider.PICO_MOLE_PER_LITRE;
import static tech.units.indriya.unit.Units.MINUTE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class FormatFeatureTest {

    @Test
    void formatRates() {

        RateConstant c1 = RateConstant.create(1.0)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        RateConstant c2 = RateConstant.create(2.0)
                .backward().secondOrder()
                .concentrationUnit(PICO_MOLE_PER_LITRE)
                .timeUnit(MINUTE)
                .build();

        List<Feature> features = new ArrayList<>();
        features.add(c1);
        features.add(c2);

        System.out.println(FormatFeature.formatFeatures(features));

    }
}