package de.bioforscher.simulation.application.components;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.util.SingaPerferences;
import javafx.scene.chart.NumberAxis;

import java.util.Map;

/**
 * A factory class that creates a SpeciesObserverChart with the specified
 * parameters.
 *
 * @author Christoph Leberecht
 */
public class SpeciesObserverChartFactory {

    public static SpeciesObserverChart setupSpeciesObserverChart(BioNode observedNode,
                                                                 Map<String, ChemicalEntity> observedSpecies) {

        SingaPerferences preferences = new SingaPerferences();
        int maxPoints = preferences.preferences.getInt(SingaPerferences.Plot.MAXIMAL_DATA_POINTS,
                SingaPerferences.Plot.MAXIMAL_DATA_POINTS_VALUE);
        int ticks = preferences.preferences.getInt(SingaPerferences.Plot.TICK_SPACING,
                SingaPerferences.Plot.TICK_SPACING_VALUE);

        NumberAxis xAxis = new NumberAxis(0, maxPoints, ticks);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);

        NumberAxis yAxis = new NumberAxis();

        SpeciesObserverChart chart = new SpeciesObserverChart(xAxis, yAxis);

        chart.setAnimated(false);
        chart.setObservedNode(observedNode);
        chart.setObservedSpecies(observedSpecies);
        chart.setId("Node_" + observedNode.getIdentifier());
        chart.setTitle("Concentrations of node " + observedNode.getIdentifier());
        chart.setMinHeight(250);
        chart.setMinWidth(300);

        return chart;

    }

}
