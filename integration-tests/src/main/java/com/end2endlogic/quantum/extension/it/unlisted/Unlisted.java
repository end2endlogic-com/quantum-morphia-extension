package com.end2endlogic.quantum.extension.it.unlisted;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

/**
 * An entity in a package that quarkus.morphia.packages does not list. It is in the build-time index, so it
 * must be filtered out by the package list rather than mapped.
 */
@Entity("unlisted")
public class Unlisted {
    @Id
    public ObjectId id;
    @Indexed
    public String name;
}
