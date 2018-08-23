package bio.singa.simulation.model.agents.organelles;

import bio.singa.features.identifiers.GoTerm;
import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.parser.organelles.OrganelleTemplate;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

/**
 * @author cl
 */
public class Organelle {

    private final CellRegion internalRegion;
    private final CellRegion membraneRegion;
    private OrganelleTemplate template;

    public Organelle(CellRegion internalRegion, CellRegion membraneRegion, OrganelleTemplate template) {
        this.internalRegion = internalRegion;
        this.membraneRegion = membraneRegion;
        this.template = template;
        mapToSystemExtend();
    }

    private void mapToSystemExtend() {
        Quantity<Length> systemScale = Environment.convertSimulationToSystemScale(1).to(template.getScale().getUnit());
        template.scale(template.getScale().getValue().doubleValue()/systemScale.getValue().doubleValue());
    }

    private void initializeGroups() {
        if (template.getGroups().size() == 1) {
            setGroupRegion(template.getGroups().keySet().iterator().next());
        }
    }

    public void setGroupRegion(int groupIdentifier, String regionIdentifier, String regionGoTerm) {
        CellRegion region = new CellRegion(regionIdentifier, new GoTerm(regionGoTerm));
        region.addSubSection(CellTopology.INNER, membraneRegion.getInnerSubsection());
        region.addSubSection(CellTopology.MEMBRANE, membraneRegion.getMembraneSubsection());
        region.addSubSection(CellTopology.OUTER, membraneRegion.getOuterSubsection());
        template.initializeGroup(groupIdentifier, region);
    }

    public void setGroupRegion(int groupIdentifier) {
        template.initializeGroup(groupIdentifier, membraneRegion);
    }

    public CellRegion getRegion(Vector2D vector) {
        return template.getRegion(vector);
    }

    public CellRegion getInternalRegion() {
        return internalRegion;
    }

    public CellRegion getMembraneRegion() {
        return membraneRegion;
    }

    public OrganelleTemplate getTemplate() {
        return template;
    }

    public void setTemplate(OrganelleTemplate template) {
        this.template = template;
    }
}
