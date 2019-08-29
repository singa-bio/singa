package bio.singa.chemistry.entities;

import bio.singa.chemistry.entities.complex.GraphComplex;
import bio.singa.chemistry.entities.complex.GraphComplexNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ChemicalEntities {

    private ChemicalEntities() {

    }

    public static List<ChemicalEntity> sortByComplexDependencies(List<ChemicalEntity> entities) {

        // creation of entities have to be done in a certain order, if they depend on the entities required by complexes
        // initialize entities and their requirements
        Map<ChemicalEntity, Set<ChemicalEntity>> creationRequirements = new HashMap<>();
        // and the priority of the entity
        Map<ChemicalEntity, Integer> priorityMap = new HashMap<>();
        for (ChemicalEntity entity : entities) {
            creationRequirements.put(entity, new HashSet<>());
            priorityMap.put(entity, Integer.MAX_VALUE);
        }

        // collect requirements
        for (ChemicalEntity targetEntity : entities) {
            if (targetEntity instanceof GraphComplex) {
                // check if it is required elsewhere
                List<ChemicalEntity> allData = ((GraphComplex) targetEntity).getNodes().stream()
                        .map(GraphComplexNode::getEntity)
                        .collect(Collectors.toList());
                allData.remove(targetEntity);
                creationRequirements.get(targetEntity).addAll(allData);
            }
        }

        // sort entities without requirements to the top
        List<ChemicalEntity> handledEntities = new ArrayList<>();
        List<ChemicalEntity> suppliedEntities = new ArrayList<>();
        for (Map.Entry<ChemicalEntity, Set<ChemicalEntity>> entry : creationRequirements.entrySet()) {
            if (entry.getValue().isEmpty()) {
                // if no requirements are needed assign priority 0
                priorityMap.put(entry.getKey(), 0);
                suppliedEntities.add(entry.getKey());
                handledEntities.add(entry.getKey());
            }
        }

        // sort the other entries thereafter
        boolean allAssigned = false;
        int level = 0;
        while (!allAssigned) {
            allAssigned = true;
            level++;
            for (Map.Entry<ChemicalEntity, Set<ChemicalEntity>> entry : creationRequirements.entrySet()) {
                if (!handledEntities.contains(entry.getKey())) {
                    if (suppliedEntities.containsAll(entry.getValue())) {
                        priorityMap.put(entry.getKey(), level);
                        suppliedEntities.add(entry.getKey());
                        handledEntities.add(entry.getKey());
                        allAssigned = false;
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

