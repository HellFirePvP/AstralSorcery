/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertySource;
import hellfirepvp.astralsorcery.common.crystal.property.*;
import hellfirepvp.astralsorcery.common.crystal.source.Crystal;
import hellfirepvp.astralsorcery.common.crystal.source.Ritual;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverRitualPedestal;

import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.*;
import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Properties.*;
import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Sources.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryCrystalProperties
 * Created by HellFirePvP
 * Date: 20.08.2019 / 21:51
 */
public class RegistryCrystalProperties {

    private RegistryCrystalProperties() {}

    public static void init() {
        PROPERTY_SIZE = registerProperty(new PropertySize());
        PROPERTY_PURITY = registerProperty(new PropertyPurity());
        PROPERTY_SHAPE = registerProperty(new PropertyShape());

        PROPERTY_TOOL_DURABILITY = registerProperty(new PropertyToolDurability());
        PROPERTY_TOOL_EFFICIENCY = registerProperty(new PropertyToolEfficiency());
        PROPERTY_RITUAL_EFFECT = registerProperty(new PropertyRitualEffect());
        PROPERTY_RITUAL_RANGE = registerProperty(new PropertyRitualRange());
        PROPERTY_COLLECTOR_COLLECTION_RATE = registerProperty(new PropertyCollectionRate());

        PROPERTY_CST_AEVITAS =    registerProperty(new PropertyConstellation(ConstellationsAS.aevitas));
        PROPERTY_CST_DISCIDIA =   registerProperty(new PropertyConstellation(ConstellationsAS.discidia));
        PROPERTY_CST_ARMARA =     registerProperty(new PropertyConstellation(ConstellationsAS.armara));
        PROPERTY_CST_VICIO =      registerProperty(new PropertyConstellation(ConstellationsAS.vicio));
        PROPERTY_CST_EVORSIO =    registerProperty(new PropertyConstellation(ConstellationsAS.evorsio));
        PROPERTY_CST_LUCERNA =    registerProperty(new PropertyConstellation(ConstellationsAS.lucerna));
        PROPERTY_CST_MINERALIS =  registerProperty(new PropertyConstellation(ConstellationsAS.mineralis));
        PROPERTY_CST_OCTANS =     registerProperty(new PropertyConstellation(ConstellationsAS.octans));
        PROPERTY_CST_BOOTES =     registerProperty(new PropertyConstellation(ConstellationsAS.bootes));
        PROPERTY_CST_HOROLOGIUM = registerProperty(new PropertyConstellation(ConstellationsAS.horologium));
        PROPERTY_CST_FORNAX =     registerProperty(new PropertyConstellation(ConstellationsAS.fornax));
        PROPERTY_CST_PELOTRIO =   registerProperty(new PropertyConstellation(ConstellationsAS.pelotrio));
    }

    public static void initDefaultAttributes() {
        SOURCE_RITUAL_PEDESTAL = new PropertySource<StarlightReceiverRitualPedestal, Ritual>(AstralSorcery.key("ritual_network")) {
            @Override
            public Ritual createInstance(StarlightReceiverRitualPedestal obj) {
                return new Ritual(this, obj.getChannelingType(), obj.getChannelingTrait());
            }
        };
        SOURCE_TILE_RITUAL_PEDESTAL = new PropertySource<TileRitualPedestal, Ritual>(AstralSorcery.key("ritual_tile")) {
            @Override
            public Ritual createInstance(TileRitualPedestal obj) {
                return new Ritual(this, obj.getRitualConstellation(), obj.getRitualTrait());
            }
        };
        SOURCE_COLLECTOR_CRYSTAL = new PropertySource<IndependentCrystalSource, Crystal>(AstralSorcery.key("crystal_network")) {
            @Override
            public Crystal createInstance(IndependentCrystalSource obj) {
                return new Crystal(this, obj.getStarlightType());
            }
        };
        SOURCE_TILE_COLLECTOR_CRYSTAL = new PropertySource<TileCollectorCrystal, Crystal>(AstralSorcery.key("crystal_tile")) {
            @Override
            public Crystal createInstance(TileCollectorCrystal obj) {
                return new Crystal(this, obj.getAttunedConstellation());
            }
        };

        CREATIVE_CRYSTAL_TOOL_ATTRIBUTES =
                CrystalAttributes.Builder.newBuilder(false)
                        .addProperty(PROPERTY_SIZE, 3)
                        .addProperty(PROPERTY_SHAPE, 3)
                        .addProperty(PROPERTY_TOOL_DURABILITY, 3)
                        .addProperty(PROPERTY_TOOL_EFFICIENCY, 3)
                        .build();
        WORLDGEN_SHRINE_COLLECTOR_ATTRIBUTES =
                CrystalAttributes.Builder.newBuilder(false)
                        .addProperty(PROPERTY_SIZE, 2)
                        .addProperty(PROPERTY_SHAPE, 2)
                        .addProperty(PROPERTY_PURITY, 2)
                        .addProperty(PROPERTY_COLLECTOR_COLLECTION_RATE, 2)
                        .build();

        CREATIVE_ROCK_COLLECTOR_ATTRIBUTES =
                CrystalAttributes.Builder.newBuilder(false)
                        .addProperty(PROPERTY_SIZE, 3)
                        .addProperty(PROPERTY_SHAPE, 3)
                        .addProperty(PROPERTY_PURITY, 2)
                        .addProperty(PROPERTY_COLLECTOR_COLLECTION_RATE, 3)
                        .build();
        CREATIVE_CELESTIAL_COLLECTOR_ATTRIBUTES =
                CrystalAttributes.Builder.newBuilder(false)
                        .addProperty(PROPERTY_SIZE, 3)
                        .addProperty(PROPERTY_SHAPE, 3)
                        .addProperty(PROPERTY_PURITY, 2)
                        .addProperty(PROPERTY_COLLECTOR_COLLECTION_RATE, 3)
                        .build();

        LENS_PRISM_CREATIVE_ATTRIBUTES =
                CrystalAttributes.Builder.newBuilder(false)
                        .addProperty(PROPERTY_PURITY, 2)
                        .build();
    }

    private static <T extends CrystalProperty> T registerProperty(T property) {
        AstralSorcery.getProxy().getRegistryPrimer().register(property);
        return property;
    }
}
