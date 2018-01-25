/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalShovel
 * Created by HellFirePvP
 * Date: 14.03.2017 / 12:43
 */
public class ItemChargedCrystalShovel extends ItemCrystalShovel implements ChargedCrystalToolBase {

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        World world = player.getEntityWorld();
        if (!world.isRemote && !player.getCooldownTracker().hasCooldown(ItemsAS.chargedCrystalShovel)) {
            IBlockState at = world.getBlockState(pos);
            if(at.getBlock().isToolEffective("shovel", at)) {
                BlockArray shovelables = BlockDiscoverer.discoverBlocksWithSameStateAround(world, pos, false, 8, 100, true);
                if (shovelables != null) {
                    Map<BlockPos, BlockArray.BlockInformation> pattern = shovelables.getPattern();
                    for (Map.Entry<BlockPos, BlockArray.BlockInformation> blocks : pattern.entrySet()) {
                        world.setBlockState(blocks.getKey(), BlocksAS.blockFakeTree.getDefaultState());
                        TileFakeTree tt = MiscUtils.getTileAt(world, blocks.getKey(), TileFakeTree.class, true);
                        if(tt != null) {
                            tt.setupTile(player, itemstack, blocks.getValue().state);
                        } else {
                            world.setBlockState(blocks.getKey(), blocks.getValue().state);
                        }
                    }
                    if(!ChargedCrystalToolBase.tryRevertMainHand(player, itemstack)) {
                        player.getCooldownTracker().setCooldown(ItemsAS.chargedCrystalShovel, 150);
                    }
                    return true;
                }
            }
        }
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalShovel;
    }

}
