package bio.singa.mathematics.geometry.model;

import bio.singa.mathematics.vectors.Vector2D;

public interface Translatable {

    /**
     * In Euclidean geometry, a translation is a function that moves every point
     * a constant distance in a specified direction. This method moves a line as
     * specified by the distance and direction of a vector.
     *
     * @param translator The vector specifies distance and direction of the
     * translation.
     */
    void translate(Vector2D translator);

}
