/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.client.helper.RenderAstrolabeOverlay;
import hellfirepvp.astralsorcery.common.component.AstrolabeAngleComponent;
import hellfirepvp.astralsorcery.common.item.base.CreativeTabItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpyglassItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstrolabeItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstrolabeItem extends SpyglassItem implements CreativeTabItem {

    public static final float FOV_MODIFIER = 0.2F;

    public AstrolabeItem() {
        super(new Properties()
                .stacksTo(1)
                .component(DataComponentsAS.ASTROLABE_ANGLE, AstrolabeAngleComponent.DEFAULT));
    }

    public static boolean isUsingAstrolabe(@Nullable Player player) {
        if (player == null) return false;
        if (player.level().isClientSide()) {
            if (isClientDrawing()) {
                return true;
            }
        }
        return player.isUsingItem() && player.getUseItem().is(ItemsAS.ASTROLABE);
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean isClientDrawing() {
        return RenderAstrolabeOverlay.isDrawing();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(newStack.getItem());
    }
}
