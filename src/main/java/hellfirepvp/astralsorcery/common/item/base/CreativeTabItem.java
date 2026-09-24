/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CreativeTabItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface CreativeTabItem extends ItemLike {

    default CreativeModeTab getCreativeTab() {
        return CreativeTabsAS.CREATIVE_TAB_AS.get();
    }

    default boolean isInTab(CreativeModeTab tab) {
        return tab == getCreativeTab();
    }

    @OnlyIn(Dist.CLIENT)
    default void fillCreativeTab(Consumer<ItemStack> tabItems) {
        tabItems.accept(new ItemStack(this));
    }
}
