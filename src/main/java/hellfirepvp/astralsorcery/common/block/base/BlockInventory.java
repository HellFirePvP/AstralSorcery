/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockInventory
 * Created by HellFirePvP
 * Date: 14.08.2019 / 07:01
 */
public abstract class BlockInventory extends BlockCrystalContainer {

    protected BlockInventory(Properties builder) {
        super(builder);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity te = MiscUtils.getTileAt(worldIn, pos, TileEntity.class, true);
        if (te != null && !worldIn.isRemote) {
            LazyOptional<IItemHandler> opt = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (opt.isPresent()) {
                ItemUtils.dropInventory(opt.orElse(ItemUtils.EMPTY_INVENTORY), worldIn, pos);
            }
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
}
