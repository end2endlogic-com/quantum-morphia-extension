package com.end2endlogic.quantum.extension.it.models;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("animals")
public class Animal {
    @Id
    public ObjectId id;
    public String name;
}
