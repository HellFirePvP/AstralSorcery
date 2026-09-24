/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.base;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BaseTickTileBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class BaseTickTileBlock<T extends TileEntityTick<?>> extends BaseTileBlock<T> {

    protected BaseTickTileBlock(Properties properties, TileRegistryObject<T> tileType) {
        super(properties, tileType);
    }

    @Override
    @Nullable
    public <V extends BlockEntity> BlockEntityTicker<V> getTicker(Level level, BlockState state, BlockEntityType<V> blockEntityType) {
        return createTickerHelper(blockEntityType, this.getTileType().type(), this.createTicker());
    }

    protected abstract BlockEntityTicker<T> createTicker();

    protected BlockEntityTicker<T> ticker() {
        return (level, pos, state, tile) -> {
            if (level.isClientSide()) {
                tile.clientTick(level);
            } else if (level instanceof ServerLevel sLevel) {
                tile.serverTick(sLevel);
            }
        };
    }

    protected BlockEntityTicker<T> serverTicker() {
        return (level, pos, state, tile) -> {
            if (level instanceof ServerLevel sLevel) {
                tile.serverTick(sLevel);
            }
        };
    }

    protected BlockEntityTicker<T> clientTicker() {
        return (level, pos, state, tile) -> {
            if (level.isClientSide()) {
                tile.clientTick(level);
            }
        };
    }
}
