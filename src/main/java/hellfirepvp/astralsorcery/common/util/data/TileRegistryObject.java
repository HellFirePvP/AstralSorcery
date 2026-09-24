/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileRegistryObject
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record TileRegistryObject<T extends BlockEntity>(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> ref) {

    public BlockEntityType<T> type() {
        return this.ref.get();
    }

    public T newTile(BlockPos pos, BlockState state) {
        return this.type().create(pos, state);
    }
    @Nullable
    public <V extends TileEntityTick<?>> BlockEntityTicker<V> ticker(BlockEntityType<V> otherType, BlockEntityTicker<T> tick) {
        if (otherType != this.type()) {
            return null;
        }
        return MiscUtil.cast(tick);
    }
}
