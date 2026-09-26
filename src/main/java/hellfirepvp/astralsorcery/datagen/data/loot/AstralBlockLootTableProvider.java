/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import hellfirepvp.astralsorcery.common.block.tile.CelestialCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.block.tile.GemCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.loot.*;
import hellfirepvp.astralsorcery.common.loot.condition.LockableTileEntityCondition;
import hellfirepvp.astralsorcery.common.loot.condition.PlayerNearbyCondition;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralBlockLootTableProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralBlockLootTableProvider extends BlockLootSubProvider {

    protected AstralBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        this.dropSelf(BlocksAS.MARBLE_ARCH);
        this.dropSelf(BlocksAS.MARBLE_BRICKS);
        this.dropSelf(BlocksAS.MARBLE_CHISELED);
        this.dropSelf(BlocksAS.MARBLE_ENGRAVED);
        this.dropSelf(BlocksAS.MARBLE_PILLAR);
        this.dropSelf(BlocksAS.MARBLE_RAW);
        this.dropSelf(BlocksAS.MARBLE_RUNED);
        this.dropSelf(BlocksAS.MARBLE_STAIRS);
        this.add(BlocksAS.MARBLE_SLAB.get(), this::createSlabItemTable);
        this.dropSelf(BlocksAS.SOOTY_MARBLE_ARCH);
        this.dropSelf(BlocksAS.SOOTY_MARBLE_BRICKS);
        this.dropSelf(BlocksAS.SOOTY_MARBLE_CHISELED);
        this.dropSelf(BlocksAS.SOOTY_MARBLE_ENGRAVED);
        this.dropSelf(BlocksAS.SOOTY_MARBLE_PILLAR);
        this.dropSelf(BlocksAS.SOOTY_MARBLE_RAW);
        this.dropSelf(BlocksAS.SOOTY_MARBLE_RUNED);
        this.dropSelf(BlocksAS.SOOTY_MARBLE_STAIRS);
        this.add(BlocksAS.SOOTY_MARBLE_SLAB.get(), this::createSlabItemTable);
        this.dropSelf(BlocksAS.INFUSED_WOOD_RAW);
        this.dropSelf(BlocksAS.INFUSED_WOOD_ARCH);
        this.dropSelf(BlocksAS.INFUSED_WOOD_COLUMN);
        this.dropSelf(BlocksAS.INFUSED_WOOD_ENGRAVED);
        this.dropSelf(BlocksAS.INFUSED_WOOD_ENRICHED);
        this.dropSelf(BlocksAS.INFUSED_WOOD_INFUSED);
        this.dropSelf(BlocksAS.INFUSED_WOOD_PLANKS);
        this.dropSelf(BlocksAS.INFUSED_WOOD_STAIRS);
        this.add(BlocksAS.INFUSED_WOOD_SLAB.get(), this::createSlabItemTable);

        this.add(BlocksAS.AQUAMARINE_SHALE.get(), block -> {
            return this.createSilkTouchDispatchTable(block,
                    LootItem.lootTableItem(ItemsAS.AQUAMARINE)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                            .apply(LinearLuckFunction.luckAndEnchantments(List.of(
                                    enchantments.getOrThrow(Enchantments.FORTUNE)
                            )))
                            .apply(ApplyExplosionDecay.explosionDecay())
            );
        });
        this.add(BlocksAS.ROCK_CRYSTAL_ORE.get(), block -> {
            return LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(UniformGenerator.between(1F, 3F))
                            .when(PlayerNearbyCondition.nearby(10, true))
                            .add(LootItem.lootTableItem(ItemsAS.ROCK_CRYSTAL)
                                    .apply(GenerateCrystalPropertiesFunction.randomProperties())
                                    .apply(ApplyExplosionDecay.explosionDecay())));
        });
        this.add(BlocksAS.STARMETAL_ORE.get(), b -> this.createOreDrop(b, ItemsAS.RAW_STARMETAL.asItem()));
        this.dropSelf(BlocksAS.RAW_STARMETAL_BLOCK);

        this.add(BlocksAS.GLIMMER_AMARANTH.get(), block -> {
            return createShearsDispatchTable(block,
                    LootItem.lootTableItem(Items.GLOWSTONE_DUST)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5)))
                            .apply(LinearLuckFunction.luckAndEnchantments(List.of(
                                    enchantments.getOrThrow(Enchantments.FORTUNE),
                                    enchantments.getOrThrow(Enchantments.LOOTING)
                            )))
                            .apply(ApplyExplosionDecay.explosionDecay()));
        });
        this.dropSelf(BlocksAS.HYACINTH);
        this.dropSelf(BlocksAS.IRIS);
        this.dropSelf(BlocksAS.ORCHID);
        this.dropSelf(BlocksAS.PROTEA);
        this.dropSelf(BlocksAS.THISTLE);
        this.dropPottedContents(BlocksAS.POTTED_GLIMMER_AMARANTH.get());
        this.dropPottedContents(BlocksAS.POTTED_HYACINTH.get());
        this.dropPottedContents(BlocksAS.POTTED_IRIS.get());
        this.dropPottedContents(BlocksAS.POTTED_ORCHID.get());
        this.dropPottedContents(BlocksAS.POTTED_PROTEA.get());
        this.dropPottedContents(BlocksAS.POTTED_THISTLE.get());

        this.dropSelf(BlocksAS.ALTAR_ILLUMINATION);
        this.dropSelf(BlocksAS.ALTAR_RESONANCE);
        this.dropSelf(BlocksAS.ALTAR_LUMINANCE);
        this.dropSelf(BlocksAS.ALTAR_RADIANCE);

        this.dropSelf(BlocksAS.FOCUS_RELAY);
        this.add(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.get(), block -> {
            return LootTable.lootTable()
                    .apply(ApplyExplosionDecay.explosionDecay())
                    .apply(CopyCrystalPropertiesFunction.copyProperties())
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsAS.CELESTIAL_CRYSTAL)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.get())
                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                    .hasProperty(CelestialCrystalClusterBlock.STAGE, 4)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsAS.STARDUST)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.get())
                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                    .hasProperty(CelestialCrystalClusterBlock.STAGE, 1)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(UniformGenerator.between(1F, 2F))
                            .add(LootItem.lootTableItem(ItemsAS.STARDUST)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.get())
                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                    .hasProperty(CelestialCrystalClusterBlock.STAGE, 2)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(UniformGenerator.between(1F, 2F))
                            .add(LootItem.lootTableItem(ItemsAS.STARDUST)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.get())
                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                    .hasProperty(CelestialCrystalClusterBlock.STAGE, 3)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(2))
                            .add(LootItem.lootTableItem(ItemsAS.STARDUST)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.get())
                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                    .hasProperty(CelestialCrystalClusterBlock.STAGE, 4)))
                            )
                    );
        });
        this.add(BlocksAS.GEM_CRYSTAL_CLUSTER.get(), block -> {
            return LootTable.lootTable()
                    .apply(ApplyExplosionDecay.explosionDecay())
                    .apply(GenerateDynamismGemRollsFunction.randomProperties())
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsAS.DYNAMISM_GEM_DAY)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                    .hasProperty(GemCrystalClusterBlock.STAGE, GemCrystalClusterBlock.GrowthStageType.STAGE_2_DAY)))))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsAS.DYNAMISM_GEM_NIGHT)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                    .hasProperty(GemCrystalClusterBlock.STAGE, GemCrystalClusterBlock.GrowthStageType.STAGE_2_NIGHT)))))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsAS.DYNAMISM_GEM_SKY)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                    .hasProperty(GemCrystalClusterBlock.STAGE, GemCrystalClusterBlock.GrowthStageType.STAGE_2_SKY)))));
        });
        this.add(BlocksAS.LUMEN_CRYSTAL_CLUSTER.get(), block -> {
            return LootTable.lootTable()
                    .apply(ApplyExplosionDecay.explosionDecay())
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemsAS.LUMEN_CRYSTAL)
                                    .apply(CopyLumenFunction.copyLumen())));
        });

        this.dropSelf(BlocksAS.LUMEN_ARRAY);
        this.dropSelf(BlocksAS.LUMEN_ALCHEMY_ARRAY);
        this.dropSelf(BlocksAS.LUMEN_FILAMENT);
        this.dropSelf(BlocksAS.LUMEN_CRYSTALLIZER);
        this.dropSelf(BlocksAS.LIGHTWELL);
        this.dropSelf(BlocksAS.INFUSER);
        this.dropSelf(BlocksAS.CHALICE);
        this.dropSelf(BlocksAS.ATTUNEMENT_ALTAR);
        this.dropSelf(BlocksAS.TREE_BEACON);
        this.add(BlocksAS.CELESTIAL_GATEWAY.get(),
                LootTable.lootTable().withPool(
                        this.applyExplosionCondition(
                                BlocksAS.CELESTIAL_GATEWAY.get(),
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(BlocksAS.CELESTIAL_GATEWAY.get())
                                                .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                        .include(DataComponents.CUSTOM_NAME)
                                                        .include(DataComponentsAS.COLOR.get())))
                                        .add(LootItem.lootTableItem(ItemsAS.AQUAMARINE.get())
                                                .when(LockableTileEntityCondition.lockable()))
                        )
                ));

        this.add(BlocksAS.LENS.get(),
                this.createSingleItemTable(BlocksAS.LENS)
                        .apply(CopyCrystalPropertiesFunction.copyProperties()));
        this.add(BlocksAS.PRISM.get(),
                this.createSingleItemTable(BlocksAS.PRISM)
                        .apply(CopyCrystalPropertiesFunction.copyProperties()));
        this.add(BlocksAS.STARLIGHT_FOCUS_ROCK_CRYSTAL.get(),
                this.createSingleItemTable(BlocksAS.STARLIGHT_FOCUS_ROCK_CRYSTAL)
                        .apply(CopyCrystalPropertiesFunction.copyProperties())
                        .apply(CopyConstellationFunction.copyConstellation()));
        this.add(BlocksAS.STARLIGHT_FOCUS_CELESTIAL_CRYSTAL.get(),
                this.createSingleItemTable(BlocksAS.STARLIGHT_FOCUS_CELESTIAL_CRYSTAL)
                        .apply(CopyCrystalPropertiesFunction.copyProperties())
                        .apply(CopyConstellationFunction.copyConstellation()));
        this.dropSelf(BlocksAS.STELLAR_FILAMENT);
        this.add(BlocksAS.CAVE_ILLUMINATOR.get(),
                this.createSingleItemTable(BlocksAS.CAVE_ILLUMINATOR)
                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                .include(DataComponentsAS.COLOR.get())));
    }

    private void dropSelf(DeferredBlock<? extends Block> deferredBlock) {
        this.dropSelf(deferredBlock.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BlocksAS.BLOCK_REGISTER.getEntries().stream()
                .map(DeferredHolder::get)
                .collect(Collectors.toUnmodifiableList());
    }
}
