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
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileTranslucentBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileTranslucentBlock extends TileEntitySynchronized<TileTranslucentBlock.Data> {

    public TileTranslucentBlock(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.TRANSLUCENT_BLOCK, pos, blockState);
    }

    protected TileTranslucentBlock(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntitySynchronized.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> translucentBlockFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P3<RecordCodecBuilder.Mu<T>, DyeColor, BlockState, UUID> translucentBlockFields(RecordCodecBuilder.Instance<T> instance) {
            return instance.group(
                    CodecUtil.defaulted(DyeColor.CODEC, "dye_color", () -> DyeColor.WHITE, Data::getDyeColor),
                    CodecUtil.defaulted(BlockState.CODEC, "stored_state", Blocks.AIR::defaultBlockState, Data::getStoredState),
                    CodecUtil.defaulted(CodecUtil.uuidCodec(), "owner_id", () -> Util.NIL_UUID, Data::getOwnerId)
            );
        }

        protected DyeColor dyeColor;
        protected BlockState storedState;
        protected UUID ownerId;

        protected Data(DyeColor dyeColor, BlockState storedState, UUID ownerId) {
            this.dyeColor = dyeColor;
            this.storedState = storedState;
            this.ownerId = ownerId;
        }

        public DyeColor getDyeColor() {
            return this.dyeColor;
        }

        public void setDyeColor(DyeColor dyeColor) {
            Objects.requireNonNull(dyeColor);
            this.dyeColor = dyeColor;
        }

        public BlockState getStoredState() {
            return this.storedState;
        }

        public void setStoredState(BlockState storedState) {
            Objects.requireNonNull(storedState);
            this.storedState = storedState;
        }

        public UUID getOwnerId() {
            return this.ownerId;
        }

        public void setOwnerId(UUID ownerId) {
            Objects.requireNonNull(ownerId);
            this.ownerId = ownerId;
        }

        public boolean hasOwner() {
            return !this.getOwnerId().equals(Util.NIL_UUID);
        }
    }
}
