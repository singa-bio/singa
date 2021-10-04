package bio.singa.javafx.viewer;

import bio.singa.mathematics.geometry.bodies.Cube;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.mathematics.vectors.Vectors3D;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Model;
import bio.singa.structure.model.pdb.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    public static PdbStructure structure;
    public static List<bio.singa.mathematics.geometry.bodies.Sphere> spheres;
    public static List<Cube> cubes;

    public static ColorScheme colorScheme = ColorScheme.BY_CHAIN;
    private final Group displayGroup = new Group();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final XForm XYRotate = new XForm();
    private final XForm XYTranslate = new XForm();
    private final XForm ZRotate = new XForm();
    private PdbStructure displayStructure;
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
    private Vector3D centroid;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        chainMaterials = new HashMap<>();

        treeView = new TreeView<>();
        treeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        toogleDisplay(newValue.getValue());
                    }
                });

        displayGroup.getChildren().add(world);
        displayGroup.setDepthTest(DepthTest.ENABLE);

        if (structure != null) {
            if (structure.getAllModels().size() > 1) {
                // add leafs
                displayStructure = new PdbStructure();
                displayStructure.addModel((PdbModel) structure.getAllModels().get(0));
            } else {
                displayStructure = structure;
            }
            fillTree();
            translateToCentre();
            buildDisplayedStructure();
        }
        buildCamera();

        SubScene structureScene = new SubScene(displayGroup, 1500, 1500, true, SceneAntialiasing.BALANCED);
        handleKeyboard(structureScene);
        handleMouse(structureScene);
        structureScene.setFill(Color.WHITE);

        Button showSpheres = new Button("Show Spheres");
        showSpheres.setOnAction(this::buildSpheres);
        Button showCubes = new Button("Show Boxes");
        showCubes.setOnAction(this::buildCubes);

        VBox vbox = new VBox(treeView, showSpheres, showCubes);

        SplitPane pane = new SplitPane(structureScene, vbox);
        Scene mainScene = new Scene(pane);

        primaryStage.setTitle("Singa Molecule Viewer");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        structureScene.setCamera(camera);
    }

    private void buildCamera() {
        //defining the order of rotations
        displayGroup.getChildren().add(XYRotate);
        XYRotate.getChildren().add(XYTranslate);
        XYTranslate.getChildren().add(ZRotate);
        ZRotate.getChildren().add(camera);
        ZRotate.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);

        XYRotate.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        XYRotate.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void translateToCentre() {
        List<Atom> allAtoms = structure.getAllAtoms();

        centroid = Vectors3D.get3DCentroid(allAtoms.stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()))
                .multiply(3.0);

        allAtoms.forEach(atom -> atom.setPosition(atom.getPosition().multiply(3.0).subtract(centroid)));
        // cubes.forEach(cube -> cube.setCenter(cube.getCenter().multiply(3.0).subtract(centroid)));
    }

    private void buildDisplayedStructure() {
        // add leafs
        displayStructure.getAllLeafSubstructures().stream()
                .map(PdbLeafSubstructure.class::cast)
                .forEach(this::addLeafSubstructure);
        // add the created molecule to the world
        world.getChildren().addAll(moleculeGroup);

    }

    private void addLeafSubstructure(PdbLeafSubstructure leafSubstructure) {
        leafSubstructure.getAllAtoms().forEach(atom -> addAtom(leafSubstructure, atom));
        leafSubstructure.getBonds().forEach(bond -> addLeafBond(leafSubstructure, bond));
    }

    private void addAtom(LeafSubstructure origin, Atom atom) {
        Sphere atomShape = new Sphere(1.0);
        atomShape.setMaterial(getMaterial(origin, atom));
        atomShape.setTranslateX(atom.getPosition().getX());
        atomShape.setTranslateY(atom.getPosition().getY());
        atomShape.setTranslateZ(atom.getPosition().getZ());

        // add tooltip
        Tooltip tooltip = new Tooltip(atom.getElement().getName() + " (" + (atom.getAtomName()) + ":" +
                atom.getAtomIdentifier() + ") of " + origin.getFamily().getThreeLetterCode() + ":" + origin.getIdentifier());
        Tooltip.install(atomShape, tooltip);

        moleculeGroup.getChildren().add(atomShape);
    }

    private void fillTree() {
        TreeItem<String> rootItem = new TreeItem<>(structure.getPdbIdentifier());

        for (Model model : structure.getAllModels()) {
            TreeItem<String> modelNode = new TreeItem<>("Model: " + String.valueOf(model.getModelIdentifier()));
            model.getAllChains().stream()
                    .sorted(Comparator.comparing(Chain::getChainIdentifier))
                    .forEach(chain -> {
                        TreeItem<String> chainNode = new TreeItem<>("Chain: " + String.valueOf(chain.getChainIdentifier()));
                        modelNode.getChildren().add(chainNode);
                    });
            rootItem.getChildren().add(modelNode);
        }

        treeView.setRoot(rootItem);
    }

    private void toogleDisplay(final String identifier) {
        if (identifier.contains("Model")) {
            displayModel(identifier);
        } else if (identifier.contains("Chain")) {
            displayChain(identifier);
        }
    }

    private void displayModel(final String identifier) {
        displayStructure = new PdbStructure();
        world = new XForm();
        moleculeGroup = new XForm();
        displayStructure.addModel((PdbModel) structure.getModel(Integer.valueOf(identifier.replace("Model: ", ""))).get());
        buildDisplayedStructure();
        displayGroup.getChildren().retainAll();
        displayGroup.getChildren().add(world);
    }

    private void displayChain(final String identifier) {
        displayStructure = new PdbStructure();
        world = new XForm();
        moleculeGroup = new XForm();
        PdbChain chain = (PdbChain) structure.getAllChains().stream()
                .filter(aChain -> aChain.getChainIdentifier().equals(identifier.replace("Chain: ", "")))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Chould not retrieve chainIdentifier " + identifier.replace("Chain: ", "")));
        final PdbModel model = new PdbModel(1);
        model.addChain(chain);
        displayStructure.addModel(model);
        buildDisplayedStructure();
        displayGroup.getChildren().retainAll();
        displayGroup.getChildren().add(world);
    }

    private void addLeafBond(LeafSubstructure origin, PdbBond bond) {
        Cylinder bondShape = createCylinderConnecting(bond.getSource().getPosition(), bond.getTarget().getPosition());
        bondShape.setMaterial(getMaterial(origin, bond));
        moleculeGroup.getChildren().add(bondShape);
    }

    private void addChainBond(Chain origin, PdbBond bond) {
        Cylinder bondShape = createCylinderConnecting(bond.getSource().getPosition(), bond.getTarget().getPosition());
        bondShape.setMaterial(getMaterial(origin, bond));
        moleculeGroup.getChildren().add(bondShape);

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
        switch (colorScheme) {
            case BY_ELEMENT:
                return MaterialProvider.getDefaultMaterialForElement(atom.getElement());
            case BY_FAMILY:
                return MaterialProvider.getMaterialForType(origin.getFamily());
            default:
                String chain = origin.getIdentifier().getChainIdentifier();
                if (chainMaterials.containsKey(chain)) {
                    return chainMaterials.get(chain);
                } else {
                    return getMaterialForChain(origin.getIdentifier().getChainIdentifier());
                }
        }

    }

    private PhongMaterial getMaterial(LeafSubstructure origin, PdbBond edge) {
        switch (colorScheme) {
            case BY_ELEMENT:
                return MaterialProvider.CARBON;
            case BY_FAMILY:
                return MaterialProvider.getMaterialForType(origin.getFamily());
            default:
                return getMaterialForChain(origin.getIdentifier().getChainIdentifier());
        }
    }

    private PhongMaterial getMaterial(Chain origin, PdbBond edge) {
        if (colorScheme == ColorScheme.BY_ELEMENT) {
            return MaterialProvider.CARBON;
        } else {
            return getMaterialForChain(origin.getChainIdentifier());
        }
    }

    private PhongMaterial getMaterialForChain(String chain) {
        if (chainMaterials.containsKey(chain)) {
            return chainMaterials.get(chain);
        } else {
            PhongMaterial material = MaterialProvider.crateMaterialFromColor(Color.color(Math.random(), Math.random(), Math.random()));
            chainMaterials.put(chain, material);
            return material;
        }
    }

    private void buildSpheres(ActionEvent event) {
        world = new XForm();
        moleculeGroup = new XForm();
        for (bio.singa.mathematics.geometry.bodies.Sphere sphere : spheres) {

            Sphere sphereShape = new Sphere(sphere.getRadius());
            sphereShape.setMaterial(MaterialProvider.crateMaterialFromColor(Color.LIGHTSKYBLUE));
            sphereShape.setTranslateX(sphere.getCenter().getX());
            sphereShape.setTranslateY(sphere.getCenter().getY());
            sphereShape.setTranslateZ(sphere.getCenter().getZ());

            // add tooltip
            Tooltip tooltip = new Tooltip(sphere.toString());
            Tooltip.install(sphereShape, tooltip);

            moleculeGroup.getChildren().add(sphereShape);
        }

        for (Cube cube : cubes) {

            Box boxShape = new Box(cube.getSideLength(), cube.getSideLength(), cube.getSideLength());
            boxShape.setMaterial(MaterialProvider.crateMaterialFromColor(Color.LIGHTSALMON));
            boxShape.setTranslateX(cube.getCenter().getX());
            boxShape.setTranslateY(cube.getCenter().getY());
            boxShape.setTranslateZ(cube.getCenter().getZ());

            // add tooltip
            Tooltip tooltip = new Tooltip(cube.toString());
            Tooltip.install(boxShape, tooltip);

            moleculeGroup.getChildren().add(boxShape);
        }

        world.getChildren().add(moleculeGroup);
        displayGroup.getChildren().retainAll();
        displayGroup.getChildren().add(world);
    }

    private void buildCubes(ActionEvent event) {
        world = new XForm();
        moleculeGroup = new XForm();
        for (Cube cube : cubes) {

            Box boxShape = new Box(cube.getSideLength(), cube.getSideLength(), cube.getSideLength());
            boxShape.setMaterial(MaterialProvider.crateMaterialFromColor(Color.LIGHTSALMON));
            boxShape.setTranslateX(cube.getCenter().getX());
            boxShape.setTranslateY(cube.getCenter().getY());
            boxShape.setTranslateZ(cube.getCenter().getZ());

            // add tooltip
            Tooltip tooltip = new Tooltip(cube.toString());
            Tooltip.install(boxShape, tooltip);

            moleculeGroup.getChildren().add(boxShape);
        }
        world.getChildren().add(moleculeGroup);
        displayGroup.getChildren().retainAll();
        displayGroup.getChildren().add(world);
    }

    private void handleMouse(SubScene scene) {

        scene.setOnMousePressed(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });

        scene.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            double modifier = 1.0;

            if (me.isControlDown()) {
                modifier = CONTROL_MULTIPLIER;
            }
            if (me.isShiftDown()) {
                modifier = SHIFT_MULTIPLIER;
            }
            if (me.isPrimaryButtonDown()) {
                XYRotate.ry.setAngle(
                        XYRotate.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
                XYRotate.rx.setAngle(
                        XYRotate.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
            } else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
                camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                XYTranslate.translate.setX(
                        XYTranslate.translate.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
                XYTranslate.translate.setY(
                        XYTranslate.translate.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
            }
        });
    }


    private void handleKeyboard(SubScene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R: {
                    // reset to default
                    XYTranslate.translate.setX(0.0);
                    XYTranslate.translate.setY(0.0);
                    camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                    XYRotate.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                    XYRotate.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                    break;
                }
            }
        });
    }

}
