/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.artifact;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.artifact.condition.data.ArtifactConditionProvider;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.advancements.critereon.BlockPredicate.Builder.*;
import static hellfirepvp.astralsorcery.common.util.data.DescribedEntityPredicate.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralArtifactConditionProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralArtifactConditionProvider extends ArtifactConditionProvider {

    public AstralArtifactConditionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(AstralSorcery.MODID, output, registries);
    }

    @Override
    public void registerConditions() {
        this.registerBlockConditions();
        this.registerEntityConditions();
        this.registerDimensionConditions();
        this.registerStructureConditions();
        this.registerDamageTypeConditions();
        this.registerDamageItemConditions();
    }

    private void registerBlockConditions() {
        this.newCondition("spawner")
                .build(this.blocksNear(block().of(Blocks.SPAWNER, Blocks.TRIAL_SPAWNER))
                        .setRange(4).build());
        this.newCondition("observer")
                .build(this.blocksNear(block().of(Blocks.OBSERVER))
                        .setRange(4).build());
        this.newCondition("cartography_table")
                .build(this.blocksNear(block().of(Blocks.CARTOGRAPHY_TABLE))
                        .setRange(4).build());
        this.newCondition("sound_block")
                .build(this.blocksNear(block().of(Blocks.NOTE_BLOCK, Blocks.JUKEBOX, Blocks.BELL))
                        .setRange(4).build());
        this.newCondition("banner")
                .build(this.blocksNear(block().of(BlockTags.BANNERS))
                        .setRange(5).build());
        this.newCondition("anvil")
                .build(this.blocksNear(block().of(BlockTags.ANVIL))
                        .setRange(5).build());
        this.newCondition("sculk_sensor")
                .build(this.blocksNear(block().of(Blocks.SCULK_SENSOR))
                        .setRange(5).build());

        this.newCondition("cobweb")
                .build(this.blocksNear(block().of(Blocks.COBWEB))
                        .setBlockCountNeeded(IntRange.of(3)).setRange(5).build());
        this.newCondition("amethyst_clusters")
                .build(this.blocksNear(block().of(Blocks.SMALL_AMETHYST_BUD, Blocks.MEDIUM_AMETHYST_BUD,
                                Blocks.LARGE_AMETHYST_BUD, Blocks.AMETHYST_CLUSTER))
                        .setBlockCountNeeded(IntRange.of(2)).setRange(6).build());
        this.newCondition("flowers")
                .build(this.blocksNear(block().of(BlockTags.FLOWERS))
                        .setBlockCountNeeded(IntRange.of(4)).setBlockConsumptionChance(0.4F).setRange(7).build());
        this.newCondition("ores")
                .build(this.blocksNear(block().of(Tags.Blocks.ORES))
                        .setBlockCountNeeded(IntRange.of(2)).setRange(6).build());
        this.newCondition("logs")
                .build(this.blocksNear(block().of(BlockTags.LOGS))
                        .setBlockCountNeeded(IntRange.of(4)).setBlockConsumptionChance(0.4F).setRange(7).build());
        this.newCondition("leaves")
                .build(this.blocksNear(block().of(BlockTags.LEAVES))
                        .setBlockCountNeeded(IntRange.of(4)).setBlockConsumptionChance(0.4F).setRange(7).build());
        this.newCondition("redstone_parts")
                .build(this.blocksNear(block().of(Blocks.REDSTONE_WIRE, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH,
                                Blocks.REPEATER, Blocks.COMPARATOR, Blocks.DAYLIGHT_DETECTOR))
                        .setBlockCountNeeded(IntRange.of(2)).setRange(6).build());
        this.newCondition("ice")
                .build(this.blocksNear(block().of(BlockTags.ICE))
                        .setBlockCountNeeded(IntRange.of(2)).setRange(6).build());
        this.newCondition("gem_blocks")
                .build(this.blocksNear(block().of(Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK))
                        .setBlockCountNeeded(IntRange.of(2)).setRange(6).build());
        this.newCondition("candles")
                .build(this.blocksNear(block().of(BlockTags.CANDLES))
                        .setBlockCountNeeded(IntRange.of(3)).setRange(6).build());
        this.newCondition("tnt")
                .build(this.blocksNear(block().of(Blocks.TNT))
                        .setBlockCountNeeded(IntRange.of(3)).setRange(6).build());
        this.newCondition("corals")
                .build(this.blocksNear(block().of(BlockTags.CORAL_BLOCKS))
                        .setBlockCountNeeded(IntRange.of(2)).setRange(7).build());
    }

    private void registerEntityConditions() {
        this.newCondition("zombies")
                .build(this.entitiesNear(entityFilter().tag(EntityTypeTags.ZOMBIES), "zombies")
                        .setEntityCountNeeded(IntRange.of(2)).setRange(6).build());
        this.newCondition("skeletons")
                .build(this.entitiesNear(entityFilter().tag(EntityTypeTags.SKELETONS), "skeletons")
                        .setEntityCountNeeded(IntRange.of(2)).setRange(8).build());
        this.newCondition("aquatic")
                .build(this.entitiesNear(entityFilter().tag(EntityTypeTags.AQUATIC), "aquatic")
                        .setEntityCountNeeded(IntRange.of(2)).setRange(10).build());
        this.newCondition("raiders")
                .build(this.entitiesNear(entityFilter().tag(EntityTypeTags.RAIDERS), "raiders")
                        .setEntityCountNeeded(IntRange.of(2)).setRange(4).build());
    }

    private void registerDimensionConditions() {
        this.newCondition("in_nether")
                .addConflict(AstralSorcery.key("in_stronghold"))
                .addConflict(AstralSorcery.key("in_mineshaft"))
                .build(this.inDimension(BuiltinDimensionTypes.NETHER));
        this.newCondition("in_end")
                .addConflict(AstralSorcery.key("in_fortress"))
                .addConflict(AstralSorcery.key("in_stronghold"))
                .addConflict(AstralSorcery.key("in_mineshaft"))
                .build(this.inDimension(BuiltinDimensionTypes.END));
    }

    private void registerStructureConditions() {
        this.newCondition("in_fortress")
                .addConflict(AstralSorcery.key("in_end"))
                .addConflict(AstralSorcery.key("in_stronghold"))
                .addConflict(AstralSorcery.key("in_mineshaft"))
                .build(this.inAnyStructure(BuiltinStructures.FORTRESS));
        this.newCondition("in_stronghold")
                .addConflict(AstralSorcery.key("in_end"))
                .addConflict(AstralSorcery.key("in_nether"))
                .addConflict(AstralSorcery.key("in_fortress"))
                .addConflict(AstralSorcery.key("in_mineshaft"))
                .build(this.inAnyStructure(BuiltinStructures.STRONGHOLD));
        this.newCondition("in_mineshaft")
                .addConflict(AstralSorcery.key("in_end"))
                .addConflict(AstralSorcery.key("in_nether"))
                .addConflict(AstralSorcery.key("in_fortress"))
                .addConflict(AstralSorcery.key("in_stronghold"))
                .build(this.inAnyStructure(BuiltinStructures.MINESHAFT, BuiltinStructures.MINESHAFT_MESA));
    }

    private void registerDamageTypeConditions() {
        this.newCondition("hit_by_fire")
                .build(this.hitByDamageType("hit_by_fire")
                        .tag(DamageTypeTags.IS_FIRE)
                        .build());
        this.newCondition("hit_by_projectile")
                .build(this.hitByDamageType("hit_by_projectile")
                        .tag(DamageTypeTags.IS_PROJECTILE)
                        .build());
        this.newCondition("hit_by_explosion")
                .build(this.hitByDamageType("hit_by_explosion")
                        .tag(DamageTypeTags.IS_EXPLOSION)
                        .build());
    }

    private void registerDamageItemConditions() {
        this.newCondition("hit_by_sword")
                .build(this.hitByItem("hit_by_sword")
                        .item(Ingredient.of(ItemTags.SWORDS))
                        .build());
    }
}
