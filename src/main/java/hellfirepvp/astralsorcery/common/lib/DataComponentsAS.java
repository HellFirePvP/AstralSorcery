/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.component.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DataComponentsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DataComponentsAS {

    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPE_REGISTER =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, AstralSorcery.MODID);

    public static DeferredHolder<DataComponentType<?>, DataComponentType<CrystalAttributesComponent>> CRYSTAL_ATTRIBUTES =
            COMPONENT_TYPE_REGISTER.register("crystal_attributes", () -> DataComponentType.<CrystalAttributesComponent>builder()
                    .persistent(CrystalAttributesComponent.CODEC)
                    .networkSynchronized(CrystalAttributesComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<AstrolabeAngleComponent>> ASTROLABE_ANGLE =
            COMPONENT_TYPE_REGISTER.register("astrolabe_angle", () -> DataComponentType.<AstrolabeAngleComponent>builder()
                    .persistent(AstrolabeAngleComponent.CODEC)
                    .networkSynchronized(AstrolabeAngleComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<ArtifactComponent>> ARTIFACT =
            COMPONENT_TYPE_REGISTER.register("artifact", () -> DataComponentType.<ArtifactComponent>builder()
                    .persistent(ArtifactComponent.CODEC)
                    .build());
    public static DeferredHolder<DataComponentType<?>, DataComponentType<ArtifactTypeComponent>> ARTIFACT_TYPE =
            COMPONENT_TYPE_REGISTER.register("artifact_type", () -> DataComponentType.<ArtifactTypeComponent>builder()
                    .persistent(ArtifactTypeComponent.CODEC)
                    .networkSynchronized(ArtifactTypeComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<ConstellationPaperComponent>> CONSTELLATION_PAPER =
            COMPONENT_TYPE_REGISTER.register("constellation_paper", () -> DataComponentType.<ConstellationPaperComponent>builder()
                    .persistent(ConstellationPaperComponent.CODEC)
                    .networkSynchronized(ConstellationPaperComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<AttunedConstellationComponent>> ATTUNED_CONSTELLATION =
            COMPONENT_TYPE_REGISTER.register("attuned_constellation", () -> DataComponentType.<AttunedConstellationComponent>builder()
                    .persistent(AttunedConstellationComponent.CODEC)
                    .networkSynchronized(AttunedConstellationComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<LumenComponent>> LUMEN =
            COMPONENT_TYPE_REGISTER.register("lumen", () -> DataComponentType.<LumenComponent>builder()
                    .persistent(LumenComponent.CODEC)
                    .networkSynchronized(LumenComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<StoredLumenComponent>> STORED_LUMEN =
            COMPONENT_TYPE_REGISTER.register("stored_lumen", () -> DataComponentType.<StoredLumenComponent>builder()
                    .persistent(StoredLumenComponent.CODEC)
                    .networkSynchronized(StoredLumenComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<IdentifierComponent>> IDENTIFIER =
            COMPONENT_TYPE_REGISTER.register("identifier", () -> DataComponentType.<IdentifierComponent>builder()
                    .persistent(IdentifierComponent.CODEC)
                    .networkSynchronized(IdentifierComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<ColorComponent>> COLOR =
            COMPONENT_TYPE_REGISTER.register("color", () -> DataComponentType.<ColorComponent>builder()
                    .persistent(ColorComponent.CODEC)
                    .networkSynchronized(ColorComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<DynamicModifiersComponent>> DYNAMIC_MODIFIERS =
            COMPONENT_TYPE_REGISTER.register("dynamic_modifiers", () -> DataComponentType.<DynamicModifiersComponent>builder()
                    .persistent(DynamicModifiersComponent.CODEC)
                    .networkSynchronized(DynamicModifiersComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<EnchantmentModifierComponent>> ENCHANTMENT_MODIFIERS =
            COMPONENT_TYPE_REGISTER.register("enchantment_modifiers", () -> DataComponentType.<EnchantmentModifierComponent>builder()
                    .persistent(EnchantmentModifierComponent.CODEC)
                    .networkSynchronized(EnchantmentModifierComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<WeakPlayerReferenceComponent>> WEAK_PLAYER_REFERENCE =
            COMPONENT_TYPE_REGISTER.register("weak_player_reference", () -> DataComponentType.<WeakPlayerReferenceComponent>builder()
                    .networkSynchronized(WeakPlayerReferenceComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<BlockStateStorageComponent>> BLOCK_STATE_STORAGE =
            COMPONENT_TYPE_REGISTER.register("block_state_storage", () -> DataComponentType.<BlockStateStorageComponent>builder()
                    .persistent(BlockStateStorageComponent.CODEC)
                    .networkSynchronized(BlockStateStorageComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<StoredItemsComponent>> STORED_ITEMS =
            COMPONENT_TYPE_REGISTER.register("stored_items", () -> DataComponentType.<StoredItemsComponent>builder()
                    .persistent(StoredItemsComponent.CODEC)
                    .networkSynchronized(StoredItemsComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<IntegerModeComponent>> MODE =
            COMPONENT_TYPE_REGISTER.register("mode", () -> DataComponentType.<IntegerModeComponent>builder()
                    .persistent(IntegerModeComponent.CODEC)
                    .networkSynchronized(IntegerModeComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<FlagsComponent>> FLAGS =
            COMPONENT_TYPE_REGISTER.register("flags", () -> DataComponentType.<FlagsComponent>builder()
                    .persistent(FlagsComponent.CODEC)
                    .networkSynchronized(FlagsComponent.STREAM_CODEC)
                    .build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<StoredPlayerProgressComponent>> STORED_PLAYER_PROGRESS =
            COMPONENT_TYPE_REGISTER.register("stored_player_progress", () -> DataComponentType.<StoredPlayerProgressComponent>builder()
                    .persistent(StoredPlayerProgressComponent.CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodecWithRegistriesTrusted(StoredPlayerProgressComponent.CODEC))
                    .build());
}
