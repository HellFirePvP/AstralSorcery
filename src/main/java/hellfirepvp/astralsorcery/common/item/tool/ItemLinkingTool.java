/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.auxiliary.link.LinkHandler;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemLinkingTool
 * Created by HellFirePvP
 * Date: 03.08.2016 / 17:16
 */
public class ItemLinkingTool extends Item implements LinkHandler.IItemLinkingTool, ISpecialInteractItem {

    public ItemLinkingTool() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    /*@Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if(!world.isRemote) {
            LinkHandler.RightClickResult result = LinkHandler.onRightClick(player, world, pos, player.isSneaking());
            LinkHandler.propagateClick(result, player, world, pos);
            return EnumActionResult.SUCCESS;
        } else {
            player.swingArm(hand);
            return EnumActionResult.PASS;
        }
    }*/

    @Override
    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean onRightClick(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side, EnumHand hand, ItemStack stack) {
        if(!world.isRemote) {
            LinkHandler.RightClickResult result = LinkHandler.onRightClick(entityPlayer, world, pos, entityPlayer.isSneaking());
            LinkHandler.propagateClick(result, entityPlayer, world, pos);
        } else {
            entityPlayer.swingArm(hand);
        }
        return true;
    }
}
