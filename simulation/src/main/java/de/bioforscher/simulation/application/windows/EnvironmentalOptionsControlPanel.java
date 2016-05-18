package de.bioforscher.simulation.application.windows;

import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitName;
import de.bioforscher.units.UnitPrefix;
import de.bioforscher.units.UnitUtilities;
import de.bioforscher.units.quantities.DynamicViscosity;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;
import java.util.Observable;
import java.util.Observer;

import static de.bioforscher.units.UnitDictionary.PASCAL_SECOND;
import static tec.units.ri.unit.MetricPrefix.*;
import static tec.units.ri.unit.Units.*;

public class EnvironmentalOptionsControlPanel extends GridPane implements Observer {

    private Spinner<Double> spNodeDistance;
    private Spinner<Double> spTimeStep;
    private Spinner<Double> spTemperature;
    private Spinner<Double> spViscosity;

    private ComboBox<Unit<Length>> cbNodeDistance;
    private ComboBox<Unit<Time>> cbTimeStep;

    private Tab owner;

    public EnvironmentalOptionsControlPanel(Tab owner) {
        this.owner = owner;
        initialize();
        EnvironmentalVariables.getInstance().addObserver(this);
    }

    private void initialize() {

        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10, 10, 10, 10));

        // node distance
        Label labNodeDistance = new Label("Distance between two nodes:");
        this.add(labNodeDistance, 0, 0, 1, 1);

        this.spNodeDistance = new Spinner<>(1.0, 1000.0, 250.0);
        this.spNodeDistance.setEditable(true);
        this.spNodeDistance.valueProperty()
                .addListener((observable, oldValue, newValue) -> this.markChangesAsUnApplied());
        this.add(this.spNodeDistance, 1, 0, 1, 1);

        this.cbNodeDistance = new ComboBox<>();
        this.cbNodeDistance.getItems().addAll(UnitUtilities.generateUnitsForPrefixes(UnitPrefix
                .getDefaultSpacePrefixes(), METRE));
        this.cbNodeDistance.setValue(NANO(METRE));
        this.cbNodeDistance.valueProperty()
                .addListener((observable, oldValue, newValue) -> this.markChangesAsUnApplied());
        this.add(this.cbNodeDistance, 2, 0, 1, 1);

        // time step
        Label labTimeStep = new Label("Duration of a time step:");
        this.add(labTimeStep, 0, 1, 1, 1);

        this.spTimeStep = new Spinner<>(1.0, 1000.0, 1.0);
        this.spTimeStep.setEditable(true);
        this.spTimeStep.valueProperty().addListener((observable, oldValue, newValue) -> this.markChangesAsUnApplied());
        this.add(this.spTimeStep, 1, 1, 1, 1);

        this.cbTimeStep = new ComboBox<>();
        this.cbTimeStep.getItems().addAll(UnitUtilities.generateUnitsForPrefixes(UnitPrefix.getDefaultTimePrefixes(),
                SECOND));
        this.cbTimeStep.setValue(MICRO(SECOND));
        this.cbTimeStep.valueProperty().addListener((observable, oldValue, newValue) -> this.markChangesAsUnApplied());
        this.add(this.cbTimeStep, 2, 1, 1, 1);

        // temperature
        Label labTemperature = new Label("System temperature:");
        this.add(labTemperature, 0, 2, 1, 1);

        this.spTemperature = new Spinner<>(0, 100, 23.0, 0.1);
        this.spTemperature.setEditable(true);
        this.spTemperature.valueProperty()
                .addListener((observable, oldValue, newValue) -> this.markChangesAsUnApplied());
        this.add(this.spTemperature, 1, 2, 1, 1);

        Label labTemperatureUnit = new Label(UnitName.CELSIUS.getSymbol());
        this.add(labTemperatureUnit, 2, 2, 1, 1);

        // viscosity
        Label labViscosity = new Label("System viscosity:");
        this.add(labViscosity, 0, 3, 1, 1);

        this.spViscosity = new Spinner<>(0, 100, 1.0, 0.1);
        this.spViscosity.setEditable(true);
        this.spViscosity.valueProperty().addListener((observable, oldValue, newValue) -> this.markChangesAsUnApplied());
        this.add(this.spViscosity, 1, 3, 1, 1);

        Label labViscosityUnit = new Label(UnitName.PASCAL.getSymbol() + UnitName.SECOND.getSymbol());
        this.add(labViscosityUnit, 2, 3, 1, 1);

        Button btnDefaults = new Button("Resore Defaults");
        btnDefaults.setMaxWidth(Double.MAX_VALUE);
        btnDefaults.setOnAction(this::restoreDefault);
        this.add(btnDefaults, 0, 4, 1, 1);

        Button btnApply = new Button("Apply");
        btnApply.setMaxWidth(Double.MAX_VALUE);
        btnApply.setOnAction(this::applyChanges);
        this.add(btnApply, 1, 4, 1, 1);
    }

    private void applyChanges(ActionEvent event) {
        Quantity<Length> nodeDistance = Quantities.getQuantity(this.spNodeDistance.getValue(), this.cbNodeDistance
                .getValue());
        Quantity<Time> timeStep = Quantities.getQuantity(this.spTimeStep.getValue(), this.cbTimeStep.getValue());
        Quantity<Temperature> systemTemperature = Quantities.getQuantity(this.spTemperature.getValue(),
                CELSIUS);
        Quantity<DynamicViscosity> systemViscosity = Quantities.getQuantity(this.spViscosity.getValue(),
                MILLI(PASCAL_SECOND));

        EnvironmentalVariables.getInstance().setNodeDistance(nodeDistance);
        EnvironmentalVariables.getInstance().setTimeStep(timeStep);
        EnvironmentalVariables.getInstance().setSystemTemperature(systemTemperature);
        EnvironmentalVariables.getInstance().setSystemViscosity(systemViscosity);
        EnvironmentalVariables.getInstance().setCellularEnvironment(false);

        markChangesAsApplied();
    }

    private void restoreDefault(ActionEvent event) {
        EnvironmentalVariables.getInstance().resetToDefaultValues();
        markChangesAsApplied();
    }

    @Override
    public void update(Observable o, Object arg) {
        EnvironmentalVariables changedVariables = (EnvironmentalVariables) o;

        Quantity<Length> nodeDistance = changedVariables.getNodeDistance();
        this.cbNodeDistance.setValue(nodeDistance.getUnit());
        this.spNodeDistance.getValueFactory().setValue(nodeDistance.getValue().doubleValue());

        Quantity<Time> timeStep = changedVariables.getTimeStep();
        this.cbTimeStep.setValue(timeStep.getUnit());
        this.spTimeStep.getValueFactory().setValue(timeStep.getValue().doubleValue());

        Quantity<Temperature> temperature = changedVariables.getSystemTemperature().to(CELSIUS);
        this.spTemperature.getValueFactory().setValue(temperature.getValue().doubleValue());

        Quantity<DynamicViscosity> viscosity = changedVariables.getSystemViscosity();
        this.spViscosity.getValueFactory().setValue(viscosity.getValue().doubleValue());

        markChangesAsApplied();
    }

    private void markChangesAsUnApplied() {
        String title = this.owner.getText();
        if (!title.contains("*")) {
            this.owner.setText(title + " *");
        }
    }

    private void markChangesAsApplied() {
        this.owner.setText(this.owner.getText().replace(" *", ""));
    }

}
