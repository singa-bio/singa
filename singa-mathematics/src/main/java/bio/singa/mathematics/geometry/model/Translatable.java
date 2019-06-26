package bio.singa.mathematics.geometry.model;

public interface Translatable<TranstatableType, TranslatorType>  {

    /**
     * In Euclidean geometry, a translation is a function that moves every point a constant distance in a specified
     * direction.
     *
     * @param translator The vector specifies distance and direction of the translation.
     * @return The translated object.
     */
    TranstatableType translate(TranslatorType translator);

}
