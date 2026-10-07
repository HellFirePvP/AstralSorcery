/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure.marker;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.StructureMarkerReplacementTypesAS;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.LootTable;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MarkerReplacementLootContainer
 * Created by HellFirePvP
 * Date: 06.10.2026 / 19:33
 */
public class MarkerReplacementLootContainer extends StructureMarkerReplacement {

    public static final Type<MarkerReplacementLootContainer> TYPE = new Type<>(RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockStateProvider.CODEC.optionalFieldOf("block", BlockStateProvider.simple(Blocks.CHEST)).forGetter(MarkerReplacementLootContainer::getBlockProvider),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(MarkerReplacementLootContainer::getLootTable)
    ).apply(inst, MarkerReplacementLootContainer::new)));

    private final BlockStateProvider blockProvider;
    private final ResourceKey<LootTable> lootTable;

    public MarkerReplacementLootContainer(BlockStateProvider blockProvider, ResourceKey<LootTable> lootTable) {
        this.blockProvider = blockProvider;
        this.lootTable = lootTable;
    }

    public static MarkerReplacementLootContainer chest(ResourceKey<LootTable> lootTable) {
        return of(Blocks.CHEST, lootTable);
    }

    public static MarkerReplacementLootContainer suspiciousGravel(ResourceKey<LootTable> lootTable) {
        return of(Blocks.SUSPICIOUS_GRAVEL, lootTable);
    }

    public static MarkerReplacementLootContainer suspiciousSand(ResourceKey<LootTable> lootTable) {
        return of(Blocks.SUSPICIOUS_SAND, lootTable);
    }

    public static MarkerReplacementLootContainer of(Block block, ResourceKey<LootTable> lootTable) {
        return new MarkerReplacementLootContainer(BlockStateProvider.simple(block), lootTable);
    }

    public BlockStateProvider getBlockProvider() {
        return this.blockProvider;
    }

    public ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    @Override
    public Type<?> getType() {
        return StructureMarkerReplacementTypesAS.LOOT_CONTAINER.get();
    }

    @Override
    public StructureTemplate.StructureBlockInfo replace(LevelReader level, StructureTemplate.StructureBlockInfo marker, RandomSource rand) {
        BlockState state = this.getBlockProvider().getState(rand, marker.pos());
        BlockEntity tile = this.createContainer(state, marker);
        if (tile instanceof RandomizableContainer || tile instanceof BrushableBlockEntity) {
            CompoundTag tag = new CompoundTag();
            tag.putString(RandomizableContainer.LOOT_TABLE_TAG, this.getLootTable().location().toString());
            return new StructureTemplate.StructureBlockInfo(marker.pos(), state, tag);
        }

        return new StructureTemplate.StructureBlockInfo(marker.pos(), state, null);
    }

    @Nullable
    private BlockEntity createContainer(BlockState containerState, StructureTemplate.StructureBlockInfo marker) {
        if (containerState.getBlock() instanceof EntityBlock entityBlock) {
            return entityBlock.newBlockEntity(marker.pos(), containerState);
        }
        return null;
    }
}
