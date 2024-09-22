package com.end2endlogic.quantum.extension.runtime;

import static dev.morphia.mapping.NamingStrategy.camelCase;
import static dev.morphia.mapping.NamingStrategy.identity;
import static dev.morphia.mapping.NamingStrategy.kebabCase;
import static dev.morphia.mapping.NamingStrategy.lowerCase;
import static dev.morphia.mapping.NamingStrategy.snakeCase;

public enum NamingStrategy {
    identity {
        @Override
        public dev.morphia.mapping.NamingStrategy convert() {
            return identity();
        }
    },
    lowerCase {
        @Override
        public dev.morphia.mapping.NamingStrategy convert() {
            return lowerCase();
        }
    },
    snakeCase {
        @Override
        public dev.morphia.mapping.NamingStrategy convert() {
            return snakeCase();
        }
    },
    camelCase {
        @Override
        public dev.morphia.mapping.NamingStrategy convert() {
            return camelCase();
        }
    },
    kebabCase {
        @Override
        public dev.morphia.mapping.NamingStrategy convert() {
            return kebabCase();
        }
    };

    public abstract dev.morphia.mapping.NamingStrategy convert();
}
