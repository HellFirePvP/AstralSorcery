/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileTranslucentTree
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileTranslucentTree extends TileEntityTick<TileTranslucentTree.Data> {

    public TileTranslucentTree(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.TRANSLUCENT_TREE, pos, blockState);
    }

    protected TileTranslucentTree(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        if (this.getTileData().getTicksExisted() % 5 == 0 && rand.nextInt(50) == 0) {
            BlockPos treePos = this.getTileData().getTreeBeaconPos();
            if (treePos == null) {
                this.restoreBlock(level);
                return;
            }

            if (!MiscUtil.getTileExists(level, treePos, TileTreeBeacon.class, false)) {
                this.restoreBlock(level);
            }
        }
    }

    protected void restoreBlock(Level level) {
        level.setBlock(this.getBlockPos(), this.getTileData().getStoredState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntityTick.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> translucentTreeFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P5<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, BlockState, Optional<BlockPos>> translucentTreeFields(RecordCodecBuilder.Instance<T> instance) {
            return tickFields(instance).and(instance.group(
                    CodecUtil.defaulted(BlockState.CODEC, "stored_state", Blocks.AIR::defaultBlockState, Data::getStoredState),
                    CodecUtil.optional(BlockPos.CODEC, "tree_beacon", Data::getTreeBeaconPos)
            ));
        }

        protected BlockState storedState;
        protected BlockPos treeBeaconPos;

        protected Data(long ticksExisted, boolean hasStructure, Map<BlockPos, Boolean> skyObstructions,  BlockState storedState, Optional<BlockPos> treeBeaconPos) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.storedState = storedState;
            this.treeBeaconPos = treeBeaconPos.orElse(null);
        }

        public BlockState getStoredState() {
            return this.storedState;
        }

        public void setStoredState(BlockState storedState) {
            Objects.requireNonNull(storedState);
            this.storedState = storedState;
        }

        @Nullable
        public BlockPos getTreeBeaconPos() {
            return this.treeBeaconPos;
        }

        public void setTreeBeaconPos(BlockPos treeBeaconPos) {
            this.treeBeaconPos = treeBeaconPos;
        }
    }
}
