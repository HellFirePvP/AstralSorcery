/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityEmpty;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityItemHighlighted;
import hellfirepvp.astralsorcery.common.entity.*;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import static hellfirepvp.astralsorcery.common.lib.EntityTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEntities
 * Created by HellFirePvP
 * Date: 17.08.2019 / 08:47
 */
public class RegistryEntities {

    private RegistryEntities() {}

    public static void init() {
        NOCTURNAL_SPARK = register("nocturnal_spark",
                EntityType.Builder.create(EntityNocturnalSpark.factory(), EntityClassification.MISC)
                        .disableSummoning()
                        .immuneToFire()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(32)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityNocturnalSpark(world))
                        .size(0.1F, 0.1F));
        ILLUMINATION_SPARK = register("illumination_spark",
                EntityType.Builder.create(EntityIlluminationSpark.factory(), EntityClassification.MISC)
                        .disableSummoning()
                        .immuneToFire()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(32)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityIlluminationSpark(world))
                        .size(0.1F, 0.1F));

        ITEM_HIGHLIGHT = register("item_highlighted",
                EntityType.Builder.create(EntityItemHighlighted.factoryHighlighted(), EntityClassification.MISC)
                        .disableSummoning()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .setCustomClientFactory(((spawnEntity, world) -> new EntityItemHighlighted(ITEM_HIGHLIGHT, world)))
                        .size(0.25F, 0.25F));
        ITEM_EXPLOSION_RESISTANT = register("item_explosion_resistant",
                EntityType.Builder.create(EntityItemExplosionResistant.factoryExplosionResistant(), EntityClassification.MISC)
                        .disableSummoning()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .setCustomClientFactory(((spawnEntity, world) -> new EntityItemExplosionResistant(ITEM_EXPLOSION_RESISTANT, world)))
                        .size(0.25F, 0.25F));
        ITEM_CRYSTAL = register("item_crystal",
                EntityType.Builder.create(EntityCrystal.factoryCrystal(), EntityClassification.MISC)
                        .disableSummoning()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .setCustomClientFactory(((spawnEntity, world) -> new EntityCrystal(ITEM_CRYSTAL, world)))
                        .size(0.25F, 0.25F));
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        RenderingRegistry.registerEntityRenderingHandler(EntityNocturnalSpark.class, new RenderEntityEmpty.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityIlluminationSpark.class, new RenderEntityEmpty.Factory());

        RenderingRegistry.registerEntityRenderingHandler(EntityItemHighlighted.class, new RenderEntityItemHighlighted.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityItemExplosionResistant.class, new RenderEntityItemHighlighted.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystal.class, new RenderEntityItemHighlighted.Factory());
    }

    private static <E extends Entity> EntityType<E> register(String name, EntityType.Builder<E> typeBuilder) {
        EntityType<E> type = typeBuilder.build(name);
        type.setRegistryName(AstralSorcery.key(name));
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return type;
    }

}
