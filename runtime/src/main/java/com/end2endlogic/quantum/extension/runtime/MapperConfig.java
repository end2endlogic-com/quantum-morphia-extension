package com.end2endlogic.quantum.extension.runtime;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import dev.morphia.mapping.DateStorage;
import dev.morphia.mapping.DiscriminatorFunction;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.smallrye.config.WithDefault;
import java.util.List;
import java.util.Optional;

@ConfigGroup
public interface MapperConfig {

    /**
     * If critter is present, auto import the generated model information created by
     * <a href="https://morphia.dev/critter/4.4/index.html">critter</a>.
     */
   @WithDefault("false")
   boolean autoImportModels();

    /**
     * The strategy to use when calculating collection names for entities without an explicitly mapped collection name.
     *
     * @see Entity
     */
   @WithDefault("camelCase")
   NamingStrategy collectionNaming();

    /**
     * Create collection caps.
     */
    @WithDefault("false")
    boolean createCaps();

    /**
     * Create mapped indexes.
     */
   @WithDefault("false")
   boolean createIndexes();

    /**
     * Enable mapped document validation.
     */
   @WithDefault("false")
   boolean createValidators();

    /**
     * The database to use
     */
    String database();

    /**
     * Specifies how dates should be stored in the database. This value should only be changed to support legacy systems which
     * use the {@link DateStorage#SYSTEM_DEFAULT} setting. New projects should use the default value.
     *
     * @see DateStorage
     */
    @WithDefault("utc")
    DateStorage dateStorage();

    /**
     * The function to use when calculating an entity's discriminator value. Possible values include:
     * built-in functions defined on {@link DiscriminatorFunction}
     * the class names of a type extending {@link DiscriminatorFunction}
     *
     * @see DiscriminatorFunction
     */
    @WithDefault("simpleName")
    Discriminator discriminator();

    /**
     * The key to use when storing an entity's discriminator value
     */
    @WithDefault("_t")
    String discriminatorKey();

    /**
     * Should queries be updated to include subtypes when querying for a specific type
     */
   @WithDefault("true")
   boolean enablePolymorphicQueries();

    /**
     * Should final properties be serialized
     */
    @WithDefault("false")
    boolean ignoreFinals();

    /**
     * List the packages to automatically map. To map any subpackages, simply include {@code .*} on the end of the name. e.g.
     * otherwise the package name will be matched exactly against the declared package for a class. If this item is
     * missing/empty, no automatic mapping will be performed.
     *
     * @see Entity
     */
    List<String> packages();

    /**
     * Should "subpackages" also be mapped when mapping a specific package
     */
    @WithDefault("true")
    boolean mapSubPackages();



    /**
     * The strategy to use when calculating collection names for entities without an explicitly mapped property name.
     *
     * @see Property
     */
    @WithDefault("identity")
    public NamingStrategy propertyNaming();

    /**
     * Should empty Lists/Maps/Sets be serialized
     */
    @WithDefault("false")
    public boolean storeEmpties();

    /**
     * Should null properties be serialized
     */
    @WithDefault("false")
    public boolean storeNulls();

   /* public MorphiaConfig toMorphiaConfig() {
        return ManualMorphiaConfig.configure()
            .collectionNaming(collectionNaming.convert())
            .dateStorage(dateStorage)
            .discriminator(discriminator.convert())
            .discriminatorKey(discriminatorKey)
            .enablePolymorphicQueries(enablePolymorphicQueries)
            .ignoreFinals(ignoreFinals)
            .propertyNaming(propertyNaming.convert())
            .storeEmpties(storeEmpties)
            .storeNulls(storeNulls);

    } */
}
