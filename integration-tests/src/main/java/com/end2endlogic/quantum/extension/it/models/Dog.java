package com.end2endlogic.quantum.extension.it.models;

import dev.morphia.annotations.Indexed;

/**
 * Carries no mapping annotation of its own: it is an entity only because {@link Animal} is. Its index
 * exists only if the subclass itself is mapped at startup.
 */
public class Dog extends Animal {
    @Indexed
    public String breed;
}
