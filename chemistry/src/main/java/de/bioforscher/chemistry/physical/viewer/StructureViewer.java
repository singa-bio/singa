package de.bioforscher.chemistry.physical.viewer;

import de.bioforscher.chemistry.algorithms.superimposition.SubStructureSuperimposer;
import de.bioforscher.chemistry.algorithms.superimposition.SubStructureSuperimposition;
import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.VectorUtilities;
import javafx.application.Application;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.bioforscher.chemistry.descriptive.elements.ElementProvider.*;

/**
 * Created by Christoph on 27.09.2016.
 */
public class StructureViewer extends Application {

    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;
    private final Group root = new Group();
    private final Xform world = new Xform();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Xform cameraXform = new Xform();
    private final Xform cameraXform2 = new Xform();
    private final Xform cameraXform3 = new Xform();
    private final Xform axisGroup = new Xform();
    private final Xform moleculeGroup = new Xform();
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private Structure structure;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.root.getChildren().add(this.world);
        this.root.setDepthTest(DepthTest.ENABLE);
        // this.root.getChildren().add(new Button("My test button"));

        loadTestStructure();
        translateToCentre();
        buildCamera();
        buildAxes();
        buildMolecule();

        Scene scene = new Scene(this.root, 800, 600, true);
        handleKeyboard(scene);
        handleMouse(scene);
        scene.setFill(Color.WHITE);

        primaryStage.setTitle("Molecule Sample Application");
        primaryStage.setScene(scene);
        primaryStage.show();

        this.axisGroup.setVisible(false);

        scene.setCamera(this.camera);

    }

    private void buildCamera() {
        this.root.getChildren().add(this.cameraXform);
        this.cameraXform.getChildren().add(this.cameraXform2);
        this.cameraXform2.getChildren().add(this.cameraXform3);
        this.cameraXform3.getChildren().add(this.camera);
        this.cameraXform3.setRotateZ(180.0);

        this.camera.setNearClip(CAMERA_NEAR_CLIP);
        this.camera.setFarClip(CAMERA_FAR_CLIP);
        this.camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        this.cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        this.cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        this.axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        this.axisGroup.setVisible(true);
        this.world.getChildren().addAll(this.axisGroup);
    }

    private void buildMolecule() {

        final PhongMaterial carbonMaterial = new PhongMaterial();
        carbonMaterial.setDiffuseColor(Color.DARKGREY);
        carbonMaterial.setSpecularColor(Color.GREY);

        final PhongMaterial nitrogenMaterial = new PhongMaterial();
        nitrogenMaterial.setDiffuseColor(Color.CORNFLOWERBLUE.darker());
        nitrogenMaterial.setSpecularColor(Color.CORNFLOWERBLUE.brighter());

        final PhongMaterial oxygenMaterial = new PhongMaterial();
        oxygenMaterial.setDiffuseColor(Color.INDIANRED.darker());
        oxygenMaterial.setSpecularColor(Color.INDIANRED.brighter());

        final PhongMaterial hydrogenMaterial = new PhongMaterial();
        hydrogenMaterial.setDiffuseColor(Color.GREENYELLOW.darker());
        hydrogenMaterial.setSpecularColor(Color.GREENYELLOW.brighter());


        List<Xform> forms = new ArrayList<>();

        Xform atoms = new Xform();

        // draw atoms
        this.structure.getAllResidues().forEach(residue -> {
                    residue.getNodes().forEach(atom -> {
                        Sphere atomSphere = new Sphere(1.0);
                        if (atom.getElement().equals(CARBON)) {
                            atomSphere.setMaterial(carbonMaterial);
                        } else if (atom.getElement().equals(NITROGEN)) {
                            atomSphere.setMaterial(nitrogenMaterial);
                        } else if (atom.getElement().equals(OXYGEN)) {
                            atomSphere.setMaterial(oxygenMaterial);
                        } else {
                            atomSphere.setMaterial(hydrogenMaterial);
                        }

                        atomSphere.setTranslateX(atom.getPosition().getX());
                        atomSphere.setTranslateY(atom.getPosition().getY());
                        atomSphere.setTranslateZ(atom.getPosition().getZ());

                        Xform atomForm = new Xform();
                        atomForm.getChildren().add(atomSphere);
                        forms.add(atomForm);

                        residue.getEdges().forEach(edge -> {

                            Vector3D source = edge.getSource().getPosition();
                            Vector3D target = edge.getTarget().getPosition();

                            Cylinder bond = createCylinderConnecting(source, target);
                            bond.setMaterial(carbonMaterial);

                            Xform bondFrom = new Xform();
                            bondFrom.getChildren().add(bond);
                            forms.add(bondFrom);
                        });

                    });
                }
        );

        // backbone edges between residues
        this.structure.getAllResidues().forEach(residue ->
                residue.getEdges().forEach(edge -> {

                    Vector3D source = edge.getSource().getPosition();
                    Vector3D target = edge.getTarget().getPosition();

                    Cylinder bond = createCylinderConnecting(source, target);
                    bond.setMaterial(carbonMaterial);

                    Xform bondFrom = new Xform();
                    bondFrom.getChildren().add(bond);
                    forms.add(bondFrom);
                }));

        atoms.getChildren().addAll(forms);
        this.moleculeGroup.getChildren().add(atoms);
        this.world.getChildren().addAll(this.moleculeGroup);
    }

    private Cylinder createCylinderConnecting(Vector3D source, Vector3D target) {
        Vector3D delta = target.subtract(source);
        double distance = source.distanceTo(target);

        Cylinder bond = new Cylinder(0.4, distance, 10);
        Vector3D newLocation = delta.divide(2).add(source);

        bond.setTranslateX(newLocation.getX());
        bond.setTranslateY(newLocation.getY());
        bond.setTranslateZ(newLocation.getZ());

        // phi
        bond.getTransforms().add(new Rotate(90 + Math.toDegrees(Math.atan2(delta.getY(), delta.getX())), Rotate
                .Z_AXIS));
        // theta
        bond.getTransforms().add(new Rotate(90 + Math.toDegrees(Math.acos(delta.getZ() / distance)), Rotate.X_AXIS));

        return bond;
    }

    private void translateToCentre() {
        List<Atom> allAtoms = this.structure.getSubstructures().stream()
                .map(SubStructure::getAllAtoms)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Vector3D centroid = VectorUtilities.getCentroid(allAtoms.stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()))
                .as(Vector3D.class).multiply(3.0);
        allAtoms.forEach(atom -> atom.setPosition(
                atom.getPosition().multiply(3.0).subtract(centroid)));
    }

    private void handleMouse(Scene scene) {
        scene.setOnMousePressed(me -> {
            this.mousePosX = me.getSceneX();
            this.mousePosY = me.getSceneY();
            this.mouseOldX = me.getSceneX();
            this.mouseOldY = me.getSceneY();
        });

        scene.setOnMouseDragged(me -> {
            this.mouseOldX = this.mousePosX;
            this.mouseOldY = this.mousePosY;
            this.mousePosX = me.getSceneX();
            this.mousePosY = me.getSceneY();
            this.mouseDeltaX = (this.mousePosX - this.mouseOldX);
            this.mouseDeltaY = (this.mousePosY - this.mouseOldY);

            double modifier = 1.0;

            if (me.isControlDown()) {
                modifier = CONTROL_MULTIPLIER;
            }
            if (me.isShiftDown()) {
                modifier = SHIFT_MULTIPLIER;
            }
            if (me.isPrimaryButtonDown()) {
                this.cameraXform.ry.setAngle(
                        this.cameraXform.ry.getAngle() - this.mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
                this.cameraXform.rx.setAngle(
                        this.cameraXform.rx.getAngle() + this.mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
            } else if (me.isSecondaryButtonDown()) {
                double z = this.camera.getTranslateZ();
                double newZ = z + this.mouseDeltaX * MOUSE_SPEED * modifier;
                this.camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                this.cameraXform2.t.setX(
                        this.cameraXform2.t.getX() + this.mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
                this.cameraXform2.t.setY(
                        this.cameraXform2.t.getY() + this.mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
            }
        });
    }

    private void handleKeyboard(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Z:
                    this.cameraXform2.t.setX(0.0);
                    this.cameraXform2.t.setY(0.0);
                    this.camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                    this.cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                    this.cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                    break;
                case X:
                    this.axisGroup.setVisible(!this.axisGroup.isVisible());
                    break;
                case V:
                    this.moleculeGroup.setVisible(!this.moleculeGroup.isVisible());
                    break;
            }
        });
    }

    private void loadTestStructure() {
        try {
            // this.structure = PDBParserService.parsePDBFile("D:\\intellij\\singa\\chemistry\\src\\test\\resources\\5hwx.pdb");
//            ResidueFactory.setToOmitHydrogens(true);
//            this.structure = PDBParserService.parseProteinById("1pqs");
            // this.structure = PDBParserService.parsePDBFile("D:\\intellij\\singa\\chemistry\\src\\test\\resources\\5hwx.pdb");
//            ResidueFactory.setToOmitHydrogens(true);
            Structure motif1 = PDBParserService.parsePDBFile("/home/fkaiser/Workspace/IdeaProjects/singa/chemistry/src/test/resources/motif_HDS_01.pdb");
            Structure motif2 = PDBParserService.parsePDBFile("/home/fkaiser/Workspace/IdeaProjects/singa/chemistry/src/test/resources/motif_HDS_02.pdb");
            SubStructure reference = motif1.getSubstructures().stream().collect(Collectors.toList()).get(0);
            SubStructure candidate = motif2.getSubstructures().stream().collect(Collectors.toList()).get(0);
            SubStructureSuperimposition superimposition =
                    SubStructureSuperimposer.calculateSubstructureSuperimposition(reference, candidate, AtomFilter.isBackbone());

            Structure structure = new Structure();
            superimposition.applyTo(candidate.getAtomContainingSubstructures()).forEach(structure::addSubstructure);
            structure.addSubstructure(reference);
            this.structure = structure;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // StructureAssembler.parseAminoAcidAtoms(atomLines);
        // this.structure = StructureAssembler.parseResidues(atomLines);
    }
}
