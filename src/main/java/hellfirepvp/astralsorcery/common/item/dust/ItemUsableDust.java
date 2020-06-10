/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.dust;

import hellfirepvp.astralsorcery.common.CommonProxy;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemUsableDust
 * Created by HellFirePvP
 * Date: 17.08.2019 / 08:44
 */
public abstract class ItemUsableDust extends Item implements IDispenseItemBehavior {

    public ItemUsableDust() {
        super(new Properties().group(CommonProxy.ITEM_GROUP_AS));
    }

    abstract boolean dispense(IBlockSource dispenser);

    abstract boolean rightClickAir(World world, PlayerEntity player, ItemStack dust);

    abstract boolean rightClickBlock(ItemUseContext ctx);

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        if (!ctx.getWorld().isRemote()) {
            if (this.rightClickBlock(ctx)) {
                if (!ctx.getPlayer().isCreative()) {
                    ctx.getItem().shrink(1);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty() && !world.isRemote()) {
            if (this.rightClickAir(world, player, held)) {
                if (!player.isCreative()) {
                    held.shrink(1);
                }
            }
        }

        return ActionResult.resultSuccess(held);
    }

    @Override
    public ItemStack dispense(IBlockSource src, ItemStack stack) {
        if (this.dispense(src)) {
            stack.shrink(1);
        }
        return stack;
    }
}
