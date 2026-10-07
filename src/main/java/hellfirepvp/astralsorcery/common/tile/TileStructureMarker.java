/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileStructureMarker
 * Created by HellFirePvP
 * Date: 06.10.2026 / 21:19
 */
public class TileStructureMarker extends TileEntitySynchronized<TileStructureMarker.Data> {

    private static final ResourceLocation UNASSIGNED = AstralSorcery.key("unassigned");

    public TileStructureMarker(BlockPos pos, BlockState blockState) {
        super(TileEntitiesAS.STRUCTURE_MARKER, pos, blockState);
    }

    public static ResourceLocation readMarkerId(@Nullable CompoundTag savedTag) {
        if (savedTag == null || !savedTag.contains(TileEntitySynchronized.KEY_SAVE_DATA, Tag.TAG_COMPOUND)) {
            return UNASSIGNED;
        }
        ResourceLocation id = ResourceLocation.tryParse(savedTag.getCompound(TileEntitySynchronized.KEY_SAVE_DATA).getString("marker"));
        return Optional.ofNullable(id).orElse(UNASSIGNED);
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntitySynchronized.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                CodecUtil.defaulted(ResourceLocation.CODEC, "marker", () -> UNASSIGNED, Data::getMarkerId)
        ).apply(inst, Data::new));

        private ResourceLocation markerId;

        protected Data(ResourceLocation markerId) {
            this.markerId = markerId;
        }

        public ResourceLocation getMarkerId() {
            return this.markerId;
        }

        public void setMarkerId(ResourceLocation markerId) {
            this.markerId = markerId;
        }
    }
}
