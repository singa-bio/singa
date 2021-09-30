package bio.singa.javafx.renderer;

import bio.singa.mathematics.algorithms.superimposition.Superimposition;
import bio.singa.mathematics.algorithms.superimposition.VectorSuperimposer;
import bio.singa.mathematics.algorithms.superimposition.VectorSuperimposition;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.vectors.Vector;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.OakAminoAcid;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static bio.singa.mathematics.vectors.Vectors2D.getDirectionalAngle;
import static bio.singa.mathematics.vectors.Vectors3D.calculateReflection;
import static bio.singa.mathematics.vectors.Vectors3D.calculateRotation;
import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.ALANINE;

/**
 * @author cl
 */
public class MirrorRenderer extends Application implements Renderer {

    private Canvas canvas;

    public static void main(String[] args) {
        launch();
    }

    public static List<Atom> newmanProjection(List<Atom> atoms, String firstAxisAtom, String secondAxisAtom) {
        Optional<Atom> firstAxisOptional = atoms.stream().filter(atom -> atom.getAtomName().equals(firstAxisAtom)).findAny();
        Optional<Atom> secondAxisOptional = atoms.stream().filter(atom -> atom.getAtomName().equals(secondAxisAtom)).findAny();
        List<Atom> projected = new ArrayList<>();
        for (Atom atom : atoms) {
            projected.add(atom.getCopy());
        }
        if (firstAxisOptional.isPresent() && secondAxisOptional.isPresent()) {
            Atom first = firstAxisOptional.get();
            Atom second = secondAxisOptional.get();

            // center at first
            for (Atom atom : projected) {
                atom.setPosition(atom.getPosition().subtract(first.getPosition()));
            }

            // determine rotation
            Matrix rotation = calculateRotation(second.getPosition().subtract(first.getPosition()), new Vector3D(0, 0, 1));
            for (Atom atom : projected) {
                atom.setPosition(new Vector3D(rotation.multiply(atom.getPosition()).getElements()));
            }

            return projected;
        }
        return Collections.emptyList();
    }

    public static Vector3D applyTransformation(Vector3D vector, VectorSuperimposition<Vector3D> superimposition) {
        return new Vector3D(superimposition.getRotation().transpose().multiply(vector).add(superimposition.getTranslation()).getElements());
    }

    public static Superimposition<Vector> alignPyramid(List<Atom> reference, List<Atom> candidate, String first, String second, String third) {
        Optional<Atom> firstRefernceOptional = reference.stream().filter(atom -> atom.getAtomName().equals(first)).findAny();
        Optional<Atom> secondReferenceOptional = reference.stream().filter(atom -> atom.getAtomName().equals(second)).findAny();
        Optional<Atom> thirdReferenceOptional = reference.stream().filter(atom -> atom.getAtomName().equals(third)).findAny();

        Optional<Atom> firstCandidateOptional = candidate.stream().filter(atom -> atom.getAtomName().equals(first)).findAny();
        Optional<Atom> secondCandidateOptional = candidate.stream().filter(atom -> atom.getAtomName().equals(second)).findAny();
        Optional<Atom> thirdCandidateOptional = candidate.stream().filter(atom -> atom.getAtomName().equals(third)).findAny();

        if (firstRefernceOptional.isPresent() && secondReferenceOptional.isPresent() && thirdReferenceOptional.isPresent() &&
                firstCandidateOptional.isPresent() && secondCandidateOptional.isPresent() && thirdCandidateOptional.isPresent()) {

            List<Vector> refVectors = new ArrayList<>();
            refVectors.add(firstRefernceOptional.get().getPosition());
            refVectors.add(secondReferenceOptional.get().getPosition());
            refVectors.add(thirdReferenceOptional.get().getPosition());

            List<Vector> canVectors = new ArrayList<>();
            canVectors.add(firstCandidateOptional.get().getPosition());
            canVectors.add(secondCandidateOptional.get().getPosition());
            canVectors.add(thirdCandidateOptional.get().getPosition());

            VectorSuperimposition<Vector> vectorSuperimposition = VectorSuperimposer.calculateVectorSuperimposition(refVectors, canVectors);
            return vectorSuperimposition;
        }
        return null;
    }


    @Override
    public void start(Stage primaryStage) {

        canvas = new Canvas(1000, 1000);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

//        Path testMirrorPath = Paths.get("/home/leberech/Downloads/comprec_class_A/2class_a/d1a9xa1");
//        List<Path> collect = null;
//        try {
//            collect = Files.list(testMirrorPath).collect(Collectors.toList());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        List<Structure> structures = StructureParser.local()
//                .paths(collect)
//                .everything()
//                .setOptions(StructureParserOptions.withSettings(StructureParserOptions.Setting.GET_IDENTIFIER_FROM_FILENAME))
//                .parse();

        Vector3D mirror = new Vector3D(1, 0, 0);

//        for (Structure structure : structures) {


        int closerNative = 0;
        int closerMirror = 0;
        Path sp = Paths.get("/home/leberech/Downloads/comprec_class_A/2class_a/d1a9xa1/d1a9xa1_20_FT_REMO_SCWRL.pdb");
        Structure structure = StructureParser.local()
                .path(sp)
                .everything()
                .parse();

        for (AminoAcid aminoAcid : structure.getAllAminoAcids()) {
            OakAminoAcid nativeAminoAcid = (OakAminoAcid) aminoAcid;

            StructuralFamily family = nativeAminoAcid.getFamily();
            // family.equals(UNKNOWN) || family.equals(GLYCINE)
            if (!family.equals(ALANINE)) {
                continue;
            }

            List<Atom> nativeAtoms = nativeAminoAcid.getAllAtoms();
            List<Atom> projectedNative = newmanProjection(nativeAtoms, "CA", "C");

            nativeAtoms.forEach(atom -> atom.setPosition(calculateReflection(atom.getPosition(), mirror)));
            List<Atom> projectedMirror = newmanProjection(nativeAtoms, "CA", "C");

            List<Atom> referenceAtoms = StructuralFamilies.AminoAcids.getPrototype(family).getAllAtoms();
            List<Atom> projectedReference = newmanProjection(referenceAtoms, "CA", "C");

            Superimposition<Vector> nativeSuperimposition = alignPyramid(projectedReference, projectedNative, "CA", "C", "CB");
            Matrix nativeRotation = nativeSuperimposition.getRotation().transpose();
            for (Atom atom : projectedNative) {
                atom.setPosition(new Vector3D(nativeRotation.multiply(atom.getPosition()).getElements()));
            }

            Superimposition<Vector> mirrorSuperimposition = alignPyramid(projectedReference, projectedMirror, "CA", "C", "CB");
            Matrix mirrorRotation = mirrorSuperimposition.getRotation().transpose();
            for (Atom atom : projectedMirror) {
                atom.setPosition(new Vector3D(mirrorRotation.multiply(atom.getPosition()).getElements()));
            }

            getGraphicsContext().setFill(Color.GREEN);
            Vector2D c1 = new Vector2D(100, 200);
            drawProjection(projectedNative, c1);

            getGraphicsContext().setFill(Color.RED);
            Vector2D c2 = new Vector2D(400, 200);
            drawProjection(projectedMirror, c2);

            getGraphicsContext().setFill(Color.BLUE);
            Vector2D c3 = new Vector2D(700, 200);
            drawProjection(projectedReference, c3);

            Vector3D nativeC = projectedNative.stream().filter(atom -> atom.getAtomName().equals("CB")).findAny().get().getPosition();
            Vector3D nativeN = projectedNative.stream().filter(atom -> atom.getAtomName().equals("N")).findAny().get().getPosition();

            Vector3D mirrorC = projectedMirror.stream().filter(atom -> atom.getAtomName().equals("CB")).findAny().get().getPosition();
            Vector3D mirrorN = projectedMirror.stream().filter(atom -> atom.getAtomName().equals("N")).findAny().get().getPosition();

            Vector3D referenceC = projectedReference.stream().filter(atom -> atom.getAtomName().equals("CB")).findAny().get().getPosition();
            Vector3D referenceN = projectedReference.stream().filter(atom -> atom.getAtomName().equals("N")).findAny().get().getPosition();

            double nativeAngle = getDirectionalAngle(new Vector2D(nativeC.getX(), nativeC.getY()), new Vector2D(nativeN.getX(), nativeN.getY()));
            double mirrorAngle = getDirectionalAngle(new Vector2D(mirrorC.getX(), mirrorC.getY()), new Vector2D(mirrorN.getX(), mirrorN.getY()));
            double referenceAngle = getDirectionalAngle(new Vector2D(referenceC.getX(), referenceC.getY()), new Vector2D(referenceN.getX(), referenceN.getY()));

//                System.out.println(family);
//                System.out.println(nativeAngle);
//                System.out.println(mirrorAngle);
//                System.out.println(referenceAngle);
//                System.out.println();

            if (Math.abs(nativeAngle - referenceAngle) < Math.abs(mirrorAngle - referenceAngle)) {
                closerNative++;
            } else {
                closerMirror++;
            }

        }

        System.out.println("native was closer: " + closerNative);
        System.out.println("mirror was closer: " + closerMirror);
        System.out.println();

//        }

        // show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void drawProjection(List<Atom> atoms, Vector2D offset) {
        for (Atom nativeAtom : atoms) {
            Vector3D position = nativeAtom.getPosition();
            strokeTextCenteredOnPoint(nativeAtom.getAtomName(), new Vector2D(position.getX(), position.getY()).multiply(50).add(offset));
        }
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return canvas.getGraphicsContext2D();
    }

    @Override
    public double getDrawingWidth() {
        return canvas.getWidth();
    }

    @Override
    public double getDrawingHeight() {
        return canvas.getHeight();
    }
}
