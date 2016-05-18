package de.bioforscher.simulation.application.components;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.model.NextEpochEvent;
import de.bioforscher.simulation.util.SingaPerferences;
import de.bioforscher.units.quantities.MolarConcentration;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

import javax.measure.Quantity;
import java.util.Map;

/**
 * The chart is used for visualization of BioNode concentrations changes over
 * the course of a simulation.
 *
 * @author Christoph Leberecht
 */
public class SpeciesObserverChart extends LineChart<Number, Number> implements UpdateEventListener<NextEpochEvent> {

    private BioNode observedNode;
    private Map<String, ChemicalEntity> observedSpecies;

    private int maximalPoints;

    public SpeciesObserverChart(NumberAxis xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis);
        this.maximalPoints = SingaPerferences.Plot.MAXIMAL_DATA_POINTS_VALUE;
    }

    public BioNode getObservedNode() {
        return observedNode;
    }

    public void setObservedNode(BioNode observedNode) {
        this.observedNode = observedNode;
    }

    public Map<String, ChemicalEntity> getObservedSpecies() {
        return observedSpecies;
    }

    public void setObservedSpecies(Map<String, ChemicalEntity> observedSpecies) {
        this.observedSpecies = observedSpecies;
        for (ChemicalEntity compound : observedSpecies.values()) {
            Series<Number, Number> series = new Series<>();
            series.setName(compound.getName());
            this.getData().add(series);
        }
    }

    public void addSpecies(Species compound) {
        this.observedSpecies.put(compound.getName(), compound);
        Series<Number, Number> series = new Series<>();
        series.setName(compound.getName());
        this.getData().add(series);
    }

    public void removeSpecies(Species species) {
        this.observedSpecies.remove(species.getName());
        ObservableList<Series<Number, Number>> seriesList = this.getData();
        seriesList.stream().filter(series -> series.getName().equals(species.getName())).forEach(seriesList::remove);
    }

    @Override
    protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
        // suppress printing of points
    }

    @Override
    public void onEventRecieved(NextEpochEvent event) {

        Map<ChemicalEntity, Quantity<MolarConcentration>> concentrations = event.getNode().getConcentrations();

        for (Series<Number, Number> series : this.getData()) {
            String name = series.getName();
            ChemicalEntity species = this.getObservedSpecies().get(name);
            double concentration = concentrations.get(species).getValue().doubleValue();

            // all changes to the scene graph must occur on the FX Application
            // Thread - therefore modifications to the observable list have to
            // be delegated to avoid concurrent modification
            // FIXME possibly a cleaner solution is available
            Platform.runLater(() -> {
                series.getData().add(new Data<>(event.getEpoch(), concentration));
                if (series.getData().size() > maximalPoints) {
                    series.getData().remove(series.getData().size() - maximalPoints);
                }
            });

        }

        ((NumberAxis) this.getXAxis()).setLowerBound(event.getEpoch() - maximalPoints);
        ((NumberAxis) this.getXAxis()).setUpperBound(event.getEpoch() - 1);

    }

}
