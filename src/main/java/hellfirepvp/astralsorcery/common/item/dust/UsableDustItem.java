/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.dust;

import hellfirepvp.astralsorcery.common.item.base.CreativeTabItem;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: UsableDustItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class UsableDustItem extends ItemCustom implements DispenseItemBehavior, CreativeTabItem {

    public UsableDustItem(Properties properties) {
        super(properties);
    }

    public abstract boolean dispenseItem(BlockSource dispenser);

    public abstract boolean useAir(ServerLevel sLevel, ServerPlayer sPlayer, ItemStack dust);

    public abstract boolean useBlock(ServerLevel sLevel, ServerPlayer sPlayer, UseOnContext ctx);

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel() instanceof ServerLevel sLevel && context.getPlayer() instanceof ServerPlayer sPlayer) {
            if (this.useBlock(sLevel, sPlayer, context)) {
                if (!sPlayer.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack held = player.getItemInHand(usedHand);
        if (level instanceof ServerLevel sLevel && player instanceof ServerPlayer sPlayer) {
            if (!held.isEmpty() && this.useAir(sLevel, sPlayer, held)) {
                if (!sPlayer.isCreative()) {
                    held.shrink(1);
                }
            }
        }
        return InteractionResultHolder.success(held);
    }

    @Override
    public ItemStack dispense(BlockSource blockSource, ItemStack item) {
        if (this.dispenseItem(blockSource)) {
            item.shrink(1);
        }
        return item;
    }
}
