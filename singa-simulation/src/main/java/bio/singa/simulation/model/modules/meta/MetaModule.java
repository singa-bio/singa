package bio.singa.simulation.model.modules.meta;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class MetaModule {

    private final Simulation simulation;

    private Map<Identifier, ChemicalEntity> entityMap;
    private Map<String, Feature> featureMap;

    public MetaModule(Simulation simulation) {
        this.simulation = simulation;
        entityMap = new HashMap<>();
        featureMap = new HashMap<>();
    }

    public Simulation getSimulation() {
        return simulation;
    }


    public void addFeature(String identifier, Feature feature) {
        featureMap.put(identifier, feature);
    }

    public void addEntity(Identifier identifier, ChemicalEntity entity) {
        entityMap.put(identifier, entity);
    }

    public void addEntity(ChemicalEntity entity) {
        addEntity(entity.getIdentifier(), entity);
    }

}
