/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMarble;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAltar
 * Created by HellFirePvP
 * Date: 12.08.2019 / 20:00
 */
public abstract class BlockAltar extends BlockStarlightNetwork implements CustomItemBlock {

    private final AltarType type;

    public BlockAltar(AltarType type) {
        super(PropertiesMarble.defaultMarble()
                .harvestLevel(2)
                .harvestTool(ToolType.PICKAXE));

        this.type = type;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        } else {
            AltarType thisType = ((BlockAltar)    state.getBlock()).type;
            AltarType thatType = ((BlockAltar) newState.getBlock()).type;
            if (thisType != thatType) {
                TileAltar ta = MiscUtils.getTileAt(worldIn, pos, TileAltar.class, true);
                if (ta != null) {
                    ta.updateType(thatType, false);
                }
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileAltar().updateType(this.type, true);
    }
}
