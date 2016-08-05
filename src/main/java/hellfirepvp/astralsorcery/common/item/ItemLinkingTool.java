package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.auxiliary.link.LinkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
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
public class ItemLinkingTool extends Item implements LinkHandler.IItemLinkingTool {

    public ItemLinkingTool() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            LinkHandler.RightClickResult result = LinkHandler.onRightClick(playerIn, worldIn, pos, playerIn.isSneaking());
            LinkHandler.propagateClick(result, playerIn, worldIn, pos);
        }
        return EnumActionResult.SUCCESS; //We don't pass it onwards.
    }
}
