package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientGuiHandler;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemJournal
 * Created by HellFirePvP
 * Date: 11.08.2016 / 18:33
 */
public class ItemJournal extends Item {

    public ItemJournal() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if(worldIn.isRemote) {
            playerIn.openGui(AstralSorcery.instance, ClientGuiHandler.EnumClientGui.JOURNAL.ordinal(), worldIn, 0, 0, 0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }
}
