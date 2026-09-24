/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.common.entity.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GrapplingWandItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GrapplingWandItem extends ItemCustom {

    public GrapplingWandItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack held = player.getItemInHand(usedHand);
        if (level.isClientSide() || held.isEmpty()) return InteractionResultHolder.success(held);

        if (!player.getCooldowns().isOnCooldown(this)) {
            level.addFreshEntity(new EntityGrapplingHook(player, level));
            player.getCooldowns().addCooldown(this, 80);
        }
        return InteractionResultHolder.success(held);
    }
}
