package bio.singa.core.parameters;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParameterSamplingTests {

    @Test
    void testMixedParameterRetrieval() {
        DoubleParameter doubleParameter = new DoubleParameter("A", 0.0, 10.0);
        ParameterValue<Double> doubleParameterValue = new ParameterValue<>(doubleParameter, 3.0);
        BooleanParameter booleanParameter = new BooleanParameter("B");
        ParameterValue<Boolean> booleanParameterValue = new ParameterValue<>(booleanParameter, true);

        MixedParameterList pList = new MixedParameterList();
        pList.add(doubleParameterValue);
        pList.add(booleanParameterValue);

        ParameterValue<Double> retrievedDoubleParameterValue = pList.getValue(0, Double.class);
        ParameterValue<Boolean> retrievedBooleanParameterValue = pList.getValue(1, Boolean.class);

        assertEquals(retrievedDoubleParameterValue.toString(), doubleParameterValue.toString());
        assertEquals(retrievedBooleanParameterValue.toString(), booleanParameterValue.toString());
    }

    @Test
    void testDoubleParameterValueSampling() {
        DoubleParameter parameter = new DoubleParameter("A", 0.0, 10.0);
        UniqueParameterList<Double> actual = ParameterSampler.sample(parameter, 11);
        List<Double> expected = Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.getValues().get(i).getValue());
        }
    }

    @Test
    void testIntegerParameterValueSampling() {
        IntegerParameter parameter = new IntegerParameter("B", 0, 10);
        UniqueParameterList<Integer> actual = ParameterSampler.sample(parameter, 11);
        List<Integer> expected = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.getValues().get(i).getValue());
        }
    }

    @Test
    void testBooleanParameterValueSampling() {
        BooleanParameter parameter = new BooleanParameter("C");
        UniqueParameterList<Boolean> actual = ParameterSampler.sample(parameter);
        List<Boolean> expected = Arrays.asList(true, false);
        assertSame(actual.getValues().get(0).getValue(), expected.get(0));
        assertSame(actual.getValues().get(1).getValue(), expected.get(1));
    }

}
