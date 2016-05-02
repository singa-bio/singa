package de.bioforscher.simulation.reactions;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.simulation.model.BioNode;
import tec.units.ri.quantity.Quantities;

import static de.bioforscher.units.UnitDictionary.PER_SECOND;

/**
 * Created by Christoph on 23.04.2016.
 */
public class CompareReactionOrders {

    public static void main(String[] args) {

        Species nitrogenDioxide = new Species.Builder("CHEBI:33101")
                .name("Nitrogen Dioxide").build();
        Species nitricOxide = new Species.Builder("CHEBI:16480")
                .name("Nitric oxide").build();
        Species diOxygen = new Species.Builder("CHEBI:15379")
                .name("Dioxygen").build();

        // nth order
        NthOrderReaction nthOderReaction = new NthOrderReaction.Builder()
                .addSubstrate(nitrogenDioxide, 1)
                .addProduct(nitricOxide)
                .addProduct(diOxygen)
                .rateConstant(Quantities.getQuantity(4.2, PER_SECOND))
                .build();

        BioNode nodeNthOrder = new BioNode(0);
        nodeNthOrder.setConcentration(nitrogenDioxide, 100.0);
        nodeNthOrder.setConcentration(nitricOxide, 0.0);
        nodeNthOrder.setConcentration(diOxygen, 0.0);

        nthOderReaction.updateConcentrations(nodeNthOrder);
        System.out.println(nodeNthOrder.getConcentration(nitrogenDioxide));
        System.out.println(nodeNthOrder.getConcentration(nitricOxide));
        System.out.println(nodeNthOrder.getConcentration(diOxygen));

        // frist order
        FirstOrderReaction firstOrderReaction = new FirstOrderReaction.Builder()
                .addSubstrate(nitrogenDioxide, 2)
                .addProduct(nitricOxide)
                .addProduct(diOxygen)
                .rateConstant(Quantities.getQuantity(4.2, PER_SECOND))
                .build();

        BioNode nodeFirst = new BioNode(0);
        nodeFirst.setConcentration(nitrogenDioxide, 100.0);
        nodeFirst.setConcentration(nitricOxide, 0.0);
        nodeFirst.setConcentration(diOxygen, 0.0);

        firstOrderReaction.updateConcentrations(nodeFirst);
        System.out.println(nodeFirst.getConcentration(nitrogenDioxide));
        System.out.println(nodeFirst.getConcentration(nitricOxide));
        System.out.println(nodeFirst.getConcentration(diOxygen));


    }


}
