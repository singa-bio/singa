package de.bioforscher.singa.simulation.model.rules;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public final class AssignmentRules {

    private AssignmentRules() {

    }

    /**
     * It is possible, that {@link AssignmentRule}s depend on each other. So they have to be executed in a certain
     * order. This method checks the dependencies of the AssignmentRules in the supplied list and returns a sorted list
     * that is ordered by calculation priority. So the rules on top of the list should be calculated first.
     *
     * @param assignmentRules The AssignmentRules to sort.
     * @return A list of AssignmentRules sorted by the calculation priority.
     */
    public static List<AssignmentRule> sortAssignmentRulesByPriority(List<AssignmentRule> assignmentRules) {
        // assignments have to be done in a certain order, if they depend on other assignment rules
        // initialize assignment rules and their requirements
        Map<AssignmentRule, Set<ChemicalEntity<?>>> assignmentRequirements = new HashMap<>();
        // and the priority of the rule
        Map<AssignmentRule, Integer> priorityMap = new HashMap<>();
        for (AssignmentRule rule : assignmentRules) {
            assignmentRequirements.put(rule, new HashSet<>());
            priorityMap.put(rule, Integer.MAX_VALUE);
        }

        // collect requirements
        for (AssignmentRule targetRule : assignmentRules) {
            // rule provides
            ChemicalEntity<?> targetEntity = targetRule.getTargetEntity();
            // check if it is required elsewhere
            for (AssignmentRule sourceRule : assignmentRules) {
                if (sourceRule != targetRule) {
                    if (sourceRule.getEntityReference().keySet().contains(targetEntity)) {
                        assignmentRequirements.get(sourceRule).add(targetEntity);
                    }
                }
            }
        }

        // sort rules without requirements to the top
        List<AssignmentRule> handledRules = new ArrayList<>();
        List<ChemicalEntity<?>> suppliedEntities = new ArrayList<>();
        for (Map.Entry<AssignmentRule, Set<ChemicalEntity<?>>> entry : assignmentRequirements.entrySet()) {
            if (entry.getValue().isEmpty()) {
                // if no requirements are needed assign priority 0
                priorityMap.put(entry.getKey(), 0);
                suppliedEntities.add(entry.getKey().getTargetEntity());
                handledRules.add(entry.getKey());
            }
        }

        // sort the other entries thereafter
        boolean allAssigned = false;
        int level = 0;
        while (!allAssigned) {
            allAssigned = true;
            level++;
            for (Map.Entry<AssignmentRule, Set<ChemicalEntity<?>>> entry : assignmentRequirements.entrySet()) {
                if (!handledRules.contains(entry.getKey())) {
                    if (suppliedEntities.containsAll(entry.getValue())) {
                        priorityMap.put(entry.getKey(), level);
                        suppliedEntities.add(entry.getKey().getTargetEntity());
                        handledRules.add(entry.getKey());
                        allAssigned = false;
                        break;
                    }
                }
            }
        }

        return priorityMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


}
