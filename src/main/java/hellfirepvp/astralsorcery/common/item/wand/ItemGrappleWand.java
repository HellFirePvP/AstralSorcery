/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.entity.technical.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemGrappleWand
 * Created by HellFirePvP
 * Date: 29.02.2020 / 18:15
 */
public class ItemGrappleWand extends Item implements AlignmentChargeConsumer {

    private static final float COST_PER_GRAPPLE = 450F;

    public ItemGrappleWand() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    public float getAlignmentChargeCost(PlayerEntity player, ItemStack stack) {
        return player.getCooldownTracker().hasCooldown(this) ? 0 : COST_PER_GRAPPLE;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack held = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote() || held.isEmpty()) {
            return new ActionResult<>(ActionResultType.SUCCESS, held);
        }
        if (!playerIn.getCooldownTracker().hasCooldown(this) &&
                AlignmentChargeHandler.INSTANCE.drainCharge(playerIn, LogicalSide.SERVER, COST_PER_GRAPPLE, false)) {
            worldIn.addEntity(new EntityGrapplingHook(playerIn, worldIn));
            playerIn.getCooldownTracker().setCooldown(this, 40);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, held);
    }
}
