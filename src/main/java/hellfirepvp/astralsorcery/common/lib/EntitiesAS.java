/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.entity.*;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityAltarInput;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityCrystal;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityStarmetal;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntitiesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntitiesAS {

    public static final DeferredRegister<EntityType<?>> ENTITY_REGISTER =
            DeferredRegister.create(Registries.ENTITY_TYPE, AstralSorcery.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<ItemEntityHighlighted>> ITEM_HIGHLIGHTED =
            ENTITY_REGISTER.register("item_highlighted", () ->
                    EntityType.Builder.of(ItemEntityHighlighted.factoryHighlighted(), MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .eyeHeight(0.25F)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(10)
                            .build(AstralSorcery.key("item_highlighted").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ItemEntityStarmetal>> ITEM_STARMETAL =
            ENTITY_REGISTER.register("item_starmetal", () ->
                    EntityType.Builder.of(ItemEntityStarmetal.factory(), MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .eyeHeight(0.25F)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(10)
                            .build(AstralSorcery.key("item_starmetal").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ItemEntityCrystal>> ITEM_CRYSTAL =
            ENTITY_REGISTER.register("item_crystal", () ->
                    EntityType.Builder.of(ItemEntityCrystal.factory(), MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .eyeHeight(0.25F)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(10)
                            .build(AstralSorcery.key("item_crystal").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ItemEntityArtifact>> ITEM_ARTIFACT =
            ENTITY_REGISTER.register("item_artifact", () ->
                    EntityType.Builder.of(ItemEntityArtifact.factory(), MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .eyeHeight(0.25F)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(10)
                            .build(AstralSorcery.key("item_artifact").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ItemEntityAltarInput>> ITEM_ALTAR_INPUT =
            ENTITY_REGISTER.register("item_altar_input", () ->
                    EntityType.Builder.of(ItemEntityAltarInput.factory(), MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .eyeHeight(0.2125F)
                            .clientTrackingRange(6)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(2)
                            .build(AstralSorcery.key("item_altar_input").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAltarFluidInput>> FLUID_ALTAR_INPUT =
            ENTITY_REGISTER.register("fluid_altar_input", () ->
                    EntityType.Builder.of(EntityAltarFluidInput.factory(), MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .eyeHeight(0.2125F)
                            .clientTrackingRange(6)
                            .fireImmune()
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(2)
                            .build(AstralSorcery.key("fluid_altar_input").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityFlare>> FLARE =
            ENTITY_REGISTER.register("flare", () ->
                    EntityType.Builder.of(EntityFlare.factory(), MobCategory.AMBIENT)
                            .sized(0.4F, 0.4F)
                            .fireImmune()
                            .clientTrackingRange(64)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(1)
                            .build(AstralSorcery.key("flare").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityIlluminationSpark>> ILLUMINATION_SPARK =
            ENTITY_REGISTER.register("illumination_spark", () ->
                    EntityType.Builder.of(EntityIlluminationSpark.factory(), MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .fireImmune()
                            .noSummon()
                            .noSave()
                            .clientTrackingRange(32)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(1)
                            .build(AstralSorcery.key("illumination_spark").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityNocturnalSpark>> NOCTURNAL_SPARK =
            ENTITY_REGISTER.register("nocturnal_spark", () ->
                    EntityType.Builder.of(EntityNocturnalSpark.factory(), MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .fireImmune()
                            .noSummon()
                            .noSave()
                            .clientTrackingRange(32)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(1)
                            .build(AstralSorcery.key("nocturnal_spark").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVividSpark>> VIVID_SPARK =
            ENTITY_REGISTER.register("vivid_spark", () ->
                    EntityType.Builder.of(EntityVividSpark.factory(), MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .fireImmune()
                            .noSummon()
                            .noSave()
                            .clientTrackingRange(32)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(1)
                            .build(AstralSorcery.key("vivid_spark").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityGrapplingHook>> GRAPPLING_HOOK =
            ENTITY_REGISTER.register("grappling_hook", () ->
                    EntityType.Builder.of(EntityGrapplingHook.factory(), MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .fireImmune()
                            .noSummon()
                            .clientTrackingRange(64)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(1)
                            .build(AstralSorcery.key("grappling_hook").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityShootingStar>> SHOOTING_STAR =
            ENTITY_REGISTER.register("shooting_star", () ->
                    EntityType.Builder.of(EntityShootingStar.factory(), MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .fireImmune()
                            .noSave()
                            .clientTrackingRange(96)
                            .setShouldReceiveVelocityUpdates(true)
                            .updateInterval(20)
                            .build(AstralSorcery.key("shooting_star").toString()));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FLARE.get(), EntityFlare.createAttributes().build());
    }
}
