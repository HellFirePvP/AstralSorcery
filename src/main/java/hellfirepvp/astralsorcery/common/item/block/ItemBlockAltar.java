/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockAltar
 * Created by HellFirePvP
 * Date: 10.11.2016 / 10:37
 */
public class ItemBlockAltar extends ItemBlockCustomName {

    public ItemBlockAltar() {
        super(BlocksAS.blockAltar);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        BlockAltar.AltarType type = newState.getValue(BlockAltar.ALTAR_TYPE);
        switch (type) {
            case ALTAR_1:
                break;
            case ALTAR_2:
            case ALTAR_3:
            case ALTAR_4:
            case ALTAR_5:
                BlockPos.PooledMutableBlockPos mut = BlockPos.PooledMutableBlockPos.retain();
                for (int xx = -1; xx <= 1; xx++) {
                    for (int zz = -1; zz <= 1; zz++) {
                        mut.setPos(pos.getX() + xx, pos.getY(), pos.getZ() + zz);
                        if (!world.isAirBlock(mut) && !world.getBlockState(mut).getBlock().isReplaceable(world, mut)) {
                            mut.release();
                            return false;
                        }
                    }
                }
                mut.release();
                break;
        }

        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }
}
