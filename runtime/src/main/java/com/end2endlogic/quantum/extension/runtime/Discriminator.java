package com.end2endlogic.quantum.extension.runtime;

import static dev.morphia.mapping.DiscriminatorFunction.className;
import static dev.morphia.mapping.DiscriminatorFunction.lowerClassName;
import static dev.morphia.mapping.DiscriminatorFunction.lowerSimpleName;
import static dev.morphia.mapping.DiscriminatorFunction.simpleName;

import dev.morphia.mapping.DiscriminatorFunction;

public enum Discriminator {
    className {
        @Override
        public DiscriminatorFunction convert() {
            return className();
        }
    },
    lowerClassName {
        @Override
        public DiscriminatorFunction convert() {
            return lowerClassName();
        }
    },
    lowerSimpleName {
        @Override
        public DiscriminatorFunction convert() {
            return lowerSimpleName();
        }
    },
    simpleName {
        @Override
        public DiscriminatorFunction convert() {
            return simpleName();
        }
    };

    public abstract DiscriminatorFunction convert();
}
