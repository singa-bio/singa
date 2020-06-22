package bio.singa.simulation.model.concentrations;

import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.simulation.model.simulation.error.TimeStepManager;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Time;

/**
 * @author cl
 */
public class TimedCondition extends AbstractConcentrationCondition {

    public enum Relation {
        LESS, LESS_EQUALS, GREATER_EQUALS, GREATER;
    }

    private ComparableQuantity<Time> time;
    private Relation relation;

    public TimedCondition(Relation relation, ComparableQuantity<Time> time) {
        super(30);
        this.relation = relation;
        this.time = time;
    }

    public static TimedCondition of(Relation relation, ComparableQuantity<Time> time) {
        return new TimedCondition(relation, time);
    }

    public ComparableQuantity<Time> getTime() {
        return time;
    }

    public Relation getRelation() {
        return relation;
    }

    @Override
    public boolean test(Updatable updatable) {
        switch (relation) {
            case LESS:
                return TimeStepManager.getElapsedTime().isLessThanOrEqualTo(time);
            case LESS_EQUALS:
                return TimeStepManager.getElapsedTime().isLessThan(time);
            case GREATER_EQUALS:
                return TimeStepManager.getElapsedTime().isGreaterThanOrEqualTo(time);
            case GREATER:
                return TimeStepManager.getElapsedTime().isGreaterThan(time);
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "elapsed time is "+getRelation().name()+" "+ UnitRegistry.humanReadable(time);
    }
}
