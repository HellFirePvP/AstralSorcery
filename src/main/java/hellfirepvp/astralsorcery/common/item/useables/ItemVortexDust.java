package hellfirepvp.astralsorcery.common.item.useables;

import hellfirepvp.astralsorcery.common.entities.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.entities.EntityIlluminationSpark;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemVortexDust
 * Created by HellFirePvP
 * Date: 23.06.2017 / 13:11
 */
public class ItemVortexDust extends Item {

    public ItemVortexDust() {
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty() || worldIn.isRemote) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        }
        worldIn.spawnEntity(new EntityGrapplingHook(worldIn, player));
        if(!player.isCreative()) {
            stack.shrink(1);
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {}

}
