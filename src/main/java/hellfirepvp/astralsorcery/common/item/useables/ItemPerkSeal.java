/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.useables;

import hellfirepvp.astralsorcery.common.entities.EntityShootingStar;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemPerkSeal
 * Created by HellFirePvP
 * Date: 18.09.2018 / 19:38
 */
public class ItemPerkSeal extends Item {

    public ItemPerkSeal() {
        setMaxStackSize(16);
        setMaxDamage(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
        if (!worldIn.isRemote) {
            Vector3 movement = Vector3.positiveYRandom().setY(0).normalize().multiply(0.2);
            EntityShootingStar star = new EntityShootingStar(player.getEntityWorld(), player.posX, 200, player.posZ, movement);
            player.getEntityWorld().spawnEntity(star);
        }
        return super.onItemRightClick(worldIn, player, handIn);
    }

    public static int getPlayerSealCount(EntityPlayer player) {
        return getPlayerSealCount(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
    }

    public static int getPlayerSealCount(IItemHandler inv) {
        return ItemUtils.findItemsInInventory(inv, new ItemStack(ItemsAS.perkSeal), false)
                .stream().mapToInt(ItemStack::getCount).sum();
    }

    public static boolean useSeal(EntityPlayer player, boolean simulate) {
        return useSeal((IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), simulate);
    }

    public static boolean useSeal(IItemHandlerModifiable inv, boolean simulate) {
        return ItemUtils.consumeFromInventory(inv, new ItemStack(ItemsAS.perkSeal), simulate);
    }

}
