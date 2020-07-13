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
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityGrapplingHook;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityItemHighlighted;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntitySpectralTool;
import hellfirepvp.astralsorcery.common.entity.EntityFlare;
import hellfirepvp.astralsorcery.common.entity.EntityIlluminationSpark;
import hellfirepvp.astralsorcery.common.entity.EntityNocturnalSpark;
import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.entity.item.EntityCrystal;
import hellfirepvp.astralsorcery.common.entity.item.EntityItemExplosionResistant;
import hellfirepvp.astralsorcery.common.entity.item.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.entity.item.EntityStarmetal;
import hellfirepvp.astralsorcery.common.entity.technical.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.entity.technical.EntityObservatoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
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
        FLARE = register("flare",
                EntityType.Builder.create(EntityFlare.factory(), EntityClassification.MISC)
                        .immuneToFire()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityFlare(world))
                        .size(0.4F, 0.4F));
        SPECTRAL_TOOL = register("spectral_tool",
                EntityType.Builder.create(EntitySpectralTool.factory(), EntityClassification.MISC)
                        .disableSummoning()
                        .immuneToFire()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(32)
                        .setCustomClientFactory((spawnEntity, world) -> new EntitySpectralTool(world))
                        .size(0.6F, 0.8F));

        ITEM_HIGHLIGHT = register("item_highlighted",
                EntityType.Builder.create(EntityItemHighlighted.factoryHighlighted(), EntityClassification.MISC)
                        .disableSummoning()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityItemHighlighted(ITEM_HIGHLIGHT, world))
                        .size(0.25F, 0.25F));
        ITEM_EXPLOSION_RESISTANT = register("item_explosion_resistant",
                EntityType.Builder.create(EntityItemExplosionResistant.factoryExplosionResistant(), EntityClassification.MISC)
                        .disableSummoning()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityItemExplosionResistant(ITEM_EXPLOSION_RESISTANT, world))
                        .size(0.25F, 0.25F));
        ITEM_CRYSTAL = register("item_crystal",
                EntityType.Builder.create(EntityCrystal.factoryCrystal(), EntityClassification.MISC)
                        .disableSummoning()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityCrystal(ITEM_CRYSTAL, world))
                        .size(0.5F, 0.5F));
        ITEM_STARMETAL_INGOT = register("item_starmetal",
                EntityType.Builder.create(EntityStarmetal.factoryStarmetalIngot(), EntityClassification.MISC)
                        .disableSummoning()
                        .setUpdateInterval(1)
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(16)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityStarmetal(ITEM_STARMETAL_INGOT, world))
                        .size(0.5F, 0.5F));
        OBSERVATORY_HELPER = register("observatory_helper",
                EntityType.Builder.create(EntityObservatoryHelper.factory(), EntityClassification.MISC)
                        .disableSummoning()
                        .setUpdateInterval(1)
                        .immuneToFire()
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityObservatoryHelper(world))
                        .size(0, 0));
        GRAPPLING_HOOK = register("grappling_hook",
                EntityType.Builder.create(EntityGrapplingHook.factory(), EntityClassification.MISC)
                        .disableSummoning()
                        .setUpdateInterval(1)
                        .immuneToFire()
                        .setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64)
                        .setCustomClientFactory((spawnEntity, world) -> new EntityGrapplingHook(world))
                        .size(0.1F, 0.1F));
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        RenderingRegistry.registerEntityRenderingHandler(NOCTURNAL_SPARK, new RenderEntityEmpty.Factory());
        RenderingRegistry.registerEntityRenderingHandler(ILLUMINATION_SPARK, new RenderEntityEmpty.Factory());
        RenderingRegistry.registerEntityRenderingHandler(FLARE, new RenderEntityEmpty.Factory());
        RenderingRegistry.registerEntityRenderingHandler(SPECTRAL_TOOL, new RenderEntitySpectralTool.Factory());

        RenderingRegistry.registerEntityRenderingHandler(ITEM_HIGHLIGHT, new RenderEntityItemHighlighted.Factory());
        RenderingRegistry.registerEntityRenderingHandler(ITEM_EXPLOSION_RESISTANT, new RenderEntityItemHighlighted.Factory());
        RenderingRegistry.registerEntityRenderingHandler(ITEM_CRYSTAL, new RenderEntityItemHighlighted.Factory());
        RenderingRegistry.registerEntityRenderingHandler(ITEM_STARMETAL_INGOT, manager -> new ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));

        RenderingRegistry.registerEntityRenderingHandler(OBSERVATORY_HELPER, new RenderEntityEmpty.Factory());
        RenderingRegistry.registerEntityRenderingHandler(GRAPPLING_HOOK, new RenderEntityGrapplingHook.Factory());
    }

    private static <E extends Entity> EntityType<E> register(String name, EntityType.Builder<E> typeBuilder) {
        EntityType<E> type = typeBuilder.build(AstralSorcery.key(name).toString());
        type.setRegistryName(AstralSorcery.key(name));
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return type;
    }

}
