package de.bioforscher.chemistry.physical.viewer;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.Bond;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.Vectors;
import javafx.application.Application;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 27.09.2016.
 */
public class StructureViewer extends Application {

    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    private final Group root = new Group();
    private final XForm world = new XForm();

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final XForm XYRotate = new XForm();
    private final XForm XYTranslate = new XForm();
    private final XForm ZRotate = new XForm();

    private final XForm moleculeGroup = new XForm();

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    public static Structure structure;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.root.getChildren().add(this.world);
        this.root.setDepthTest(DepthTest.ENABLE);

        // loadTestStructure();
        translateToCentre();
        buildCamera();
        buildMolecule();

        Scene scene = new Scene(this.root, 800, 600, true);
        handleKeyboard(scene);
        handleMouse(scene);
        scene.setFill(Color.WHITE);

        primaryStage.setTitle("Singa Molecule Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setCamera(this.camera);
    }

    private void buildCamera() {
        //defining the order of rotations
        this.root.getChildren().add(this.XYRotate);
        this.XYRotate.getChildren().add(this.XYTranslate);
        this.XYTranslate.getChildren().add(this.ZRotate);
        this.ZRotate.getChildren().add(this.camera);
        this.ZRotate.setRotateZ(180.0);

        this.camera.setNearClip(CAMERA_NEAR_CLIP);
        this.camera.setFarClip(CAMERA_FAR_CLIP);
        this.camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);

        this.XYRotate.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        this.XYRotate.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void buildMolecule() {
        // add atoms
        this.structure.getAllResidues().stream()
                .map(Residue::getNodes)
                .flatMap(Collection::stream)
                .forEach(this::addAtom);
        // edges in residues
        this.structure.getAllResidues().stream()
                .map(Residue::getEdges)
                .flatMap(Collection::stream)
                .forEach(this::addCovalentBond);
        // edges in chains (backbone connections)
        this.structure.getAllChains().stream()
                .map(Chain::getEdges)
                .flatMap(Collection::stream)
                .forEach(this::addCovalentBond);
        // add the created molecule to the world
        this.world.getChildren().addAll(this.moleculeGroup);
    }

    private void addAtom(Atom atom) {
        Sphere atomShape = new Sphere(1.0);
        atomShape.setMaterial(MaterialProvider.getDefaultMaterialForElement(atom.getElement()));
        atomShape.setTranslateX(atom.getPosition().getX());
        atomShape.setTranslateY(atom.getPosition().getY());
        atomShape.setTranslateZ(atom.getPosition().getZ());
        this.moleculeGroup.getChildren().add(atomShape);
    }

    private void addCovalentBond(Bond bond) {
        Cylinder bondShape = createCylinderConnecting(bond.getSource().getPosition(), bond.getTarget().getPosition());
        bondShape.setMaterial(MaterialProvider.CARBON);
        this.moleculeGroup.getChildren().add(bondShape);
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
        List<Atom> allAtoms = this.structure.getAllAtoms();

        final Vector3D centroid = Vectors.getCentroid(allAtoms.stream()
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
                this.XYRotate.ry.setAngle(
                        this.XYRotate.ry.getAngle() - this.mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
                this.XYRotate.rx.setAngle(
                        this.XYRotate.rx.getAngle() + this.mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
            } else if (me.isSecondaryButtonDown()) {
                double z = this.camera.getTranslateZ();
                double newZ = z + this.mouseDeltaX * MOUSE_SPEED * modifier;
                this.camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                this.XYTranslate.translate.setX(
                        this.XYTranslate.translate.getX() + this.mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
                this.XYTranslate.translate.setY(
                        this.XYTranslate.translate.getY() + this.mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
            }
        });
    }

    private void handleKeyboard(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    // reset to default
                    this.XYTranslate.translate.setX(0.0);
                    this.XYTranslate.translate.setY(0.0);
                    this.camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                    this.XYRotate.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                    this.XYRotate.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                    break;
            }
        });
    }

    private void loadTestStructure() {
        try {
            this.structure = PDBParserService.parseProteinById("5E9R");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
