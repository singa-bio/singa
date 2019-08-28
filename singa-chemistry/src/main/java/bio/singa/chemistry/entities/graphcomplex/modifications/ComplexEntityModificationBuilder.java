package bio.singa.chemistry.entities.graphcomplex.modifications;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;

public class ComplexEntityModificationBuilder {

    public static ComplexEntityModification bind(BindingSite bindingSite) {
        return new BindModification(bindingSite);
    }

    public static ComplexEntityModification add(BindingSite bindingSite, ChemicalEntity complex) {
        return new AddModification(bindingSite, complex);
    }

    public static ComplexEntityModification release(BindingSite bindingSite) {
        return new ReleaseModification(bindingSite);
    }

    public static ComplexEntityModification remove(BindingSite bindingSite, ChemicalEntity entityToRemove) {
        return new RemoveModification(bindingSite, entityToRemove);
    }

}
