/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.block.tile.BlockCelestialCrystalCluster;
import hellfirepvp.astralsorcery.common.block.tile.BlockGemCrystalCluster;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.loot.CopyConstellation;
import hellfirepvp.astralsorcery.common.loot.CopyCrystalProperties;
import hellfirepvp.astralsorcery.common.loot.LinearLuckBonus;
import hellfirepvp.astralsorcery.common.loot.RandomCrystalProperty;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Items;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.functions.ExplosionDecay;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;

import static hellfirepvp.astralsorcery.common.lib.BlocksAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockLootTableProvider
 * Created by HellFirePvP
 * Date: 06.03.2020 / 21:50
 */
public class BlockLootTableProvider extends BlockLootTables {

    @Override
    protected void addTables() {
        this.registerDropSelfLootTable(MARBLE_ARCH);
        this.registerDropSelfLootTable(MARBLE_BRICKS);
        this.registerDropSelfLootTable(MARBLE_CHISELED);
        this.registerDropSelfLootTable(MARBLE_ENGRAVED);
        this.registerDropSelfLootTable(MARBLE_PILLAR);
        this.registerDropSelfLootTable(MARBLE_RAW);
        this.registerDropSelfLootTable(MARBLE_RUNED);
        this.registerDropSelfLootTable(MARBLE_STAIRS);
        this.registerLootTable(MARBLE_SLAB, BlockLootTables::droppingSlab);
        this.registerDropSelfLootTable(BLACK_MARBLE_ARCH);
        this.registerDropSelfLootTable(BLACK_MARBLE_BRICKS);
        this.registerDropSelfLootTable(BLACK_MARBLE_CHISELED);
        this.registerDropSelfLootTable(BLACK_MARBLE_ENGRAVED);
        this.registerDropSelfLootTable(BLACK_MARBLE_PILLAR);
        this.registerDropSelfLootTable(BLACK_MARBLE_RAW);
        this.registerDropSelfLootTable(BLACK_MARBLE_RUNED);
        this.registerDropSelfLootTable(BLACK_MARBLE_STAIRS);
        this.registerLootTable(BLACK_MARBLE_SLAB, BlockLootTables::droppingSlab);
        this.registerDropSelfLootTable(INFUSED_WOOD);
        this.registerDropSelfLootTable(INFUSED_WOOD_ARCH);
        this.registerDropSelfLootTable(INFUSED_WOOD_COLUMN);
        this.registerDropSelfLootTable(INFUSED_WOOD_ENGRAVED);
        this.registerDropSelfLootTable(INFUSED_WOOD_ENRICHED);
        this.registerDropSelfLootTable(INFUSED_WOOD_INFUSED);
        this.registerDropSelfLootTable(INFUSED_WOOD_PLANKS);
        this.registerDropSelfLootTable(INFUSED_WOOD_STAIRS);
        this.registerLootTable(INFUSED_WOOD_SLAB, BlockLootTables::droppingSlab);

        this.registerLootTable(AQUAMARINE_SAND_ORE, (block) -> {
            return droppingWithSilkTouch(block,
                    ItemLootEntry.builder(ItemsAS.AQUAMARINE)
                            .acceptFunction(SetCount.builder(RandomValueRange.of(1F, 3F)))
                            .acceptFunction(LinearLuckBonus.builder())
                            .acceptFunction(ExplosionDecay.builder())
            );
        });
        this.registerLootTable(ROCK_CRYSTAL_ORE, (block) -> {
            return LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(RandomValueRange.of(2F, 5F))
                            .addEntry(ItemLootEntry.builder(ItemsAS.ROCK_CRYSTAL)
                                    .acceptFunction(RandomCrystalProperty.builder())
                                    .acceptFunction(ExplosionDecay.builder())
                            )
                    );
        });
        this.registerDropSelfLootTable(STARMETAL_ORE);
        this.registerLootTable(GLOW_FLOWER, (block) -> {
            return droppingWithShears(block,
                    ItemLootEntry.builder(Items.GLOWSTONE_DUST)
                            .acceptFunction(SetCount.builder(RandomValueRange.of(2F, 4F)))
                            .acceptFunction(LinearLuckBonus.builder())
                            .acceptFunction(ExplosionDecay.builder())
            );
        });

        this.registerDropSelfLootTable(SPECTRAL_RELAY);
        this.registerDropSelfLootTable(ALTAR_DISCOVERY);
        this.registerDropSelfLootTable(ALTAR_ATTUNEMENT);
        this.registerDropSelfLootTable(ALTAR_CONSTELLATION);
        this.registerDropSelfLootTable(ALTAR_RADIANCE);
        this.registerDropSelfLootTable(ATTUNEMENT_ALTAR);

        this.registerLootTable(CELESTIAL_CRYSTAL_CLUSTER, (block) -> {
            return LootTable.builder()
                    .acceptFunction(ExplosionDecay.builder())
                    .acceptFunction(CopyCrystalProperties.builder())
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ItemsAS.CELESTIAL_CRYSTAL)
                                    .acceptCondition(BlockStateProperty.builder(CELESTIAL_CRYSTAL_CLUSTER)
                                            .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                                                    .withIntProp(BlockCelestialCrystalCluster.STAGE, 4)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ItemsAS.STARDUST)
                                    .acceptCondition(BlockStateProperty.builder(CELESTIAL_CRYSTAL_CLUSTER)
                                            .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                                                    .withIntProp(BlockCelestialCrystalCluster.STAGE, 1)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(RandomValueRange.of(1F, 2F))
                            .addEntry(ItemLootEntry.builder(ItemsAS.STARDUST)
                                    .acceptCondition(BlockStateProperty.builder(CELESTIAL_CRYSTAL_CLUSTER)
                                            .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                                                    .withIntProp(BlockCelestialCrystalCluster.STAGE, 2)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(RandomValueRange.of(1F, 2F))
                            .addEntry(ItemLootEntry.builder(ItemsAS.STARDUST)
                                    .acceptCondition(BlockStateProperty.builder(CELESTIAL_CRYSTAL_CLUSTER)
                                            .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                                                    .withIntProp(BlockCelestialCrystalCluster.STAGE, 3)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(2))
                            .addEntry(ItemLootEntry.builder(ItemsAS.STARDUST)
                                    .acceptCondition(BlockStateProperty.builder(CELESTIAL_CRYSTAL_CLUSTER)
                                            .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                                                    .withIntProp(BlockCelestialCrystalCluster.STAGE, 4)))
                            )
                    );
        });
        this.registerLootTable(GEM_CRYSTAL_CLUSTER, (block) -> {
            return LootTable.builder()
                    .acceptFunction(ExplosionDecay.builder())
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ItemsAS.PERK_GEM_DAY)
                                    .acceptCondition(BlockStateProperty.builder(GEM_CRYSTAL_CLUSTER)
                                            .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                                                    .withProp(BlockGemCrystalCluster.STAGE, BlockGemCrystalCluster.GrowthStageType.STAGE_2_DAY)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ItemsAS.PERK_GEM_NIGHT)
                                    .acceptCondition(BlockStateProperty.builder(GEM_CRYSTAL_CLUSTER)
                                            .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                                                    .withProp(BlockGemCrystalCluster.STAGE, BlockGemCrystalCluster.GrowthStageType.STAGE_2_NIGHT)))
                            )
                    )
                    .addLootPool(LootPool.builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(ItemsAS.PERK_GEM_SKY)
                                    .acceptCondition(BlockStateProperty.builder(GEM_CRYSTAL_CLUSTER)
                                            .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                                                    .withProp(BlockGemCrystalCluster.STAGE, BlockGemCrystalCluster.GrowthStageType.STAGE_2_SKY)))
                            )
                    );
        });
        this.registerLootTable(ROCK_COLLECTOR_CRYSTAL, dropping(ROCK_COLLECTOR_CRYSTAL)
                .acceptFunction(CopyCrystalProperties.builder())
                .acceptFunction(CopyConstellation.builder()));
        this.registerLootTable(CELESTIAL_COLLECTOR_CRYSTAL, dropping(CELESTIAL_COLLECTOR_CRYSTAL)
                .acceptFunction(CopyCrystalProperties.builder())
                .acceptFunction(CopyConstellation.builder()));
        this.registerLootTable(LENS, dropping(LENS)
                .acceptFunction(CopyCrystalProperties.builder()));
        this.registerLootTable(PRISM, dropping(PRISM)
                .acceptFunction(CopyCrystalProperties.builder()));
        this.registerDropSelfLootTable(RITUAL_LINK);
        this.registerDropSelfLootTable(RITUAL_PEDESTAL);
        this.registerDropSelfLootTable(ILLUMINATOR);
        this.registerDropSelfLootTable(INFUSER);
        this.registerDropSelfLootTable(CHALICE);
        this.registerDropSelfLootTable(WELL);
        this.registerDropSelfLootTable(TELESCOPE);
        this.registerDropSelfLootTable(OBSERVATORY);
        this.registerDropSelfLootTable(REFRACTION_TABLE);

        this.registerLootTable(FLARE_LIGHT, LootTable.builder());
        this.registerLootTable(TRANSLUCENT_BLOCK, LootTable.builder());
        this.registerLootTable(VANISHING, LootTable.builder());
        this.registerLootTable(STRUCTURAL, LootTable.builder());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(Mods.ASTRAL_SORCERY::owns)
                .collect(Collectors.toList());
    }
}
