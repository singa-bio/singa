package de.bioforscher.mathematics.concepts;

public interface Ring<RingType extends Addable<RingType> & Subtractable<RingType> & Multipliable<RingType>> extends Addable<RingType>, Subtractable<RingType>, Multipliable<RingType>, AdditivelyInvertible<RingType> {

}
