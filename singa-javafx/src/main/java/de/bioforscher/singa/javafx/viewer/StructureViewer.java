package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.branches.StructuralModel;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Bond;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.ChainFilter;

/**
 * @author cl
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

    public static Structure structure;
    public static ColorScheme colorScheme = ColorScheme.BY_CHAIN;
    private final Group displayGroup = new Group();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final XForm XYRotate = new XForm();
    private final XForm XYTranslate = new XForm();
    private final XForm ZRotate = new XForm();
    private Structure displayStructure;
    private Map<String, PhongMaterial> chainMaterials;
    private XForm world = new XForm();
    private XForm moleculeGroup = new XForm();
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    private TreeView<String> treeView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.chainMaterials = new HashMap<>();

        this.treeView = new TreeView<>();
        this.treeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        toogleDisplay(newValue.getValue());
                    }
                });

        this.displayGroup.getChildren().add(this.world);
        this.displayGroup.setDepthTest(DepthTest.ENABLE);

        if (structure.getAllModels().size() > 1) {
            // add leafs
            this.displayStructure = new Structure();
            this.displayStructure.addSubstructure(structure.getAllModels().get(0));
        } else {
            this.displayStructure = structure;
        }

        fillTree();
        translateToCentre();
        buildCamera();
        buildDisplayedStructure();

        SubScene structureScene = new SubScene(this.displayGroup, 800, 600, true, SceneAntialiasing.BALANCED);
        handleKeyboard(structureScene);
        handleMouse(structureScene);
        structureScene.setFill(Color.WHITE);

        SplitPane pane = new SplitPane(structureScene, this.treeView);
        Scene mainScene = new Scene(pane);

        primaryStage.setTitle("Singa Molecule Viewer");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        structureScene.setCamera(this.camera);
    }

    private void buildCamera() {
        //defining the order of rotations
        this.displayGroup.getChildren().add(this.XYRotate);
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

    private void translateToCentre() {
        List<Atom> allAtoms = structure.getAllAtoms();

        final Vector3D centroid = Vectors.getCentroid(allAtoms.stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()))
                .as(Vector3D.class).multiply(3.0);

        allAtoms.forEach(atom -> atom.setPosition(
                atom.getPosition().multiply(3.0).subtract(centroid)));
    }

    private void buildDisplayedStructure() {
        // add leafs
        this.displayStructure.getAllLeaves().forEach(this::addLeaf);
        // edges in chains (backbone connections)
        this.displayStructure.getAllChains().forEach(this::addChainConnections);
        // add the created molecule to the world
        this.world.getChildren().addAll(this.moleculeGroup);

    }

    private void addLeaf(LeafSubstructure<?, ?> leafSubstructure) {
        leafSubstructure.getNodes().forEach(atom -> addAtom(leafSubstructure, atom));
        leafSubstructure.getEdges().forEach(bond -> addLeafBond(leafSubstructure, bond));
    }

    private void addChainConnections(Chain chain) {
        chain.getEdges().forEach(bond -> addChainBond(chain, bond));
    }

    private void addAtom(LeafSubstructure<?, ?> origin, Atom atom) {
        Sphere atomShape = new Sphere(1.0);
        atomShape.setMaterial(getMaterial(origin, atom));
        atomShape.setTranslateX(atom.getPosition().getX());
        atomShape.setTranslateY(atom.getPosition().getY());
        atomShape.setTranslateZ(atom.getPosition().getZ());

        // add tooltip
        Tooltip tooltip = new Tooltip(atom.getElement().getName() + " (" + (atom.getAtomNameString()) + ":" +
                atom.getIdentifier() + ") of " + origin.getName() + ":" + origin.getIdentifier());
        Tooltip.install(atomShape, tooltip);

        this.moleculeGroup.getChildren().add(atomShape);
    }

    private void fillTree() {
        TreeItem<String> rootItem = new TreeItem<>(structure.getPdbIdentifier());

        for (StructuralModel model : structure.getAllModels()) {
            TreeItem<String> modelNode = new TreeItem<>("Model: " + String.valueOf(model.getIdentifier()));
            model.getAllChains().stream()
                    .sorted(Comparator.comparing(Chain::getChainIdentifier))
                    .forEach(chain -> {
                        TreeItem<String> chainNode = new TreeItem<>("Chain: " + String.valueOf(chain.getChainIdentifier()));
                        modelNode.getChildren().add(chainNode);
                    });
            rootItem.getChildren().add(modelNode);
        }

        this.treeView.setRoot(rootItem);
    }

    private void toogleDisplay(final String identifier) {
        if (identifier.contains("Model")) {
            displayModel(identifier);
        } else if (identifier.contains("Chain")) {
            displayChain(identifier);
        }
    }

    private void displayModel(final String identifier) {
        this.displayStructure = new Structure();
        this.world = new XForm();
        this.moleculeGroup = new XForm();
        this.displayStructure.addSubstructure(structure.getAllModels().get(Integer.valueOf(identifier.replace("Model: ", ""))));
        buildDisplayedStructure();
        this.displayGroup.getChildren().retainAll();
        this.displayGroup.getChildren().add(this.world);
    }

    private void displayChain(final String identifier) {
        this.displayStructure = new Structure();
        this.world = new XForm();
        this.moleculeGroup = new XForm();
        Chain chain = structure.getAllChains().stream()
                .filter(ChainFilter.isInChain(identifier.replace("Chain: ", "")))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Chould not retrieve chain " + identifier.replace("Chain: ", "")));
        this.displayStructure.addSubstructure(chain);
        buildDisplayedStructure();
        this.displayGroup.getChildren().retainAll();
        this.displayGroup.getChildren().add(this.world);
    }

    private void addLeafBond(LeafSubstructure origin, Bond bond) {
        Cylinder bondShape = createCylinderConnecting(bond.getSource().getPosition(), bond.getTarget().getPosition());
        bondShape.setMaterial(getMaterial(origin, bond));
        this.moleculeGroup.getChildren().add(bondShape);
    }

    private void addChainBond(Chain origin, Bond bond) {
        Cylinder bondShape = createCylinderConnecting(bond.getSource().getPosition(), bond.getTarget().getPosition());
        bondShape.setMaterial(getMaterial(origin, bond));
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
        bond.getTransforms().add(new Rotate(90 + Math.toDegrees(Math.atan2(delta.getY(), delta.getX())), Rotate.Z_AXIS));
        // theta
        bond.getTransforms().add(new Rotate(90 + Math.toDegrees(Math.acos(delta.getZ() / distance)), Rotate.X_AXIS));

        return bond;
    }

    private PhongMaterial getMaterial(LeafSubstructure origin, Atom atom) {
        if (colorScheme == ColorScheme.BY_ELEMENT) {
            return MaterialProvider.getDefaultMaterialForElement(atom.getElement());
        } else if (colorScheme == ColorScheme.BY_FAMILY) {
            return MaterialProvider.getMaterialForType(origin.getFamily());
        } else {
                String chain = origin.getLeafIdentifier().getChainIdentifer();
                if (this.chainMaterials.containsKey(chain)) {
                    return this.chainMaterials.get(chain);
                } else {
                    return getMaterialForChain(origin.getLeafIdentifier().getChainIdentifer());
                }
            }

    }

    private PhongMaterial getMaterial(LeafSubstructure origin, Bond edge) {
        if (colorScheme == ColorScheme.BY_ELEMENT) {
            return MaterialProvider.CARBON;
        } else if (colorScheme == ColorScheme.BY_FAMILY) {
            return MaterialProvider.getMaterialForType(origin.getFamily());
        } else {
            return getMaterialForChain(origin.getLeafIdentifier().getChainIdentifer());
        }
    }

    private PhongMaterial getMaterial(Chain origin, Bond edge) {
        if (colorScheme == ColorScheme.BY_ELEMENT) {
            return MaterialProvider.CARBON;
        } else {
            return getMaterialForChain(origin.getChainIdentifier());
        }
    }

    private PhongMaterial getMaterialForChain(String chain) {
        if (this.chainMaterials.containsKey(chain)) {
            return this.chainMaterials.get(chain);
        } else {
            PhongMaterial material = MaterialProvider.crateMaterialFromColor(Color.color(Math.random(), Math.random(), Math.random()));
            this.chainMaterials.put(chain, material);
            return material;
        }
    }

    private void handleMouse(SubScene scene) {

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


    private void handleKeyboard(SubScene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R: {
                    // reset to default
                    this.XYTranslate.translate.setX(0.0);
                    this.XYTranslate.translate.setY(0.0);
                    this.camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                    this.XYRotate.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                    this.XYRotate.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                    break;
                }
            }
        });
    }

}
