/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.item.base.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenCrystalItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenCrystalItem extends ItemCustom implements ItemDynamicColor {

    public LumenCrystalItem() {
        super(new Properties()
                .component(DataComponentsAS.LUMEN, LumenComponent.EMPTY));
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return CreativeTabsAS.CREATIVE_TAB_AS_LUMEN.get();
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        RegistriesAS.REGISTRY_LUMEN.holders().forEach(lumenRef -> {
            if (lumenRef.is(LumenAS.NONE)) return;
            tabItems.accept(getCrystal(lumenRef));
        });
    }

    public static ItemStack getCrystal(Holder<Lumen> lumen) {
        ItemStack stack = ItemsAS.LUMEN_CRYSTAL.toStack();
        stack.set(DataComponentsAS.LUMEN, new LumenComponent(lumen));
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.has(DataComponentsAS.LUMEN)) {
            Lumen lumen = stack.getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen().value();
            if (lumen != LumenAS.NONE.get()) {
                Component lumenName = lumen.getName();
                return Component.translatable("item.astralsorcery.lumen_crystal.typed", lumenName);
            }
        }
        return super.getName(stack);
    }

    @Override
    public int getColor(ItemStack stack, long tick, int tintIndex) {
        if (tintIndex == 0 && stack.has(DataComponentsAS.LUMEN)) {
            Holder<Lumen> lumen = stack.getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen();
            int color = lumen.value().getColor(tick).getColor();
            if (lumen.is(LumenAS.PRISMATIC.getKey())) {
                color = ColorUtil.blendColors(color, 0xFFFFFFFF, 0.33F);
            }
            return color & 0xFFFFFF | 0xFF000000;
        }
        return 0xFFFFFFFF;
    }
}
