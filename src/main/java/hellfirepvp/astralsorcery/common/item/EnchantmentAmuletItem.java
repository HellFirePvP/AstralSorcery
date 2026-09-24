/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.EnumExtensions;
import hellfirepvp.astralsorcery.common.component.ColorComponent;
import hellfirepvp.astralsorcery.common.component.EnchantmentModifierComponent;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentAmuletGenerator;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.item.base.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.util.ColorReference;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EnchantmentAmuletItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EnchantmentAmuletItem extends ItemCustom implements ItemDynamicColor {

    public EnchantmentAmuletItem() {
        super(new Properties()
                .component(DataComponentsAS.ENCHANTMENT_MODIFIERS, EnchantmentModifierComponent.EMPTY)
                .rarity(EnumExtensions.RARITY_RELIC.getValue())
                .stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (!level.isClientSide()) {
            if (!stack.has(DataComponentsAS.COLOR)) {
                this.generateAmuletColor(stack);
            }
            if (stack.getOrDefault(DataComponentsAS.ENCHANTMENT_MODIFIERS, EnchantmentModifierComponent.EMPTY).isEmpty()) {
                stack.set(DataComponentsAS.ENCHANTMENT_MODIFIERS, EnchantmentAmuletGenerator.generateModifiers());
            }
        }
    }

    private void generateAmuletColor(ItemStack stack) {
        ColorComponent cmp;
        if (rand.nextInt(400) == 0) {
            if (rand.nextBoolean()) {
                cmp = ColorComponent.WHITE;
            } else {
                cmp = ColorComponent.BLACK;
            }
        } else {
            int color = ColorWrapper.ofHSB(rand.nextFloat(), 0.7F, 1.0F).getColor() | 0xFF000000;
            cmp = new ColorComponent(ColorReference.RGB.of(color));
        }
        stack.set(DataComponentsAS.COLOR, cmp);
    }


    @Override
    public int getColor(ItemStack stack, long clientTick, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFFFF;

        if (stack.has(DataComponentsAS.COLOR)) {
            return stack.getOrDefault(DataComponentsAS.COLOR, ColorComponent.WHITE).reference().color() | 0xFF000000;
        }

        int tick = (int) (clientTick % 100000L);
        int c = ColorWrapper.ofHSB(tick / 100000F, 0.7F, 1F).getColor();
        return c | 0xFF000000;
    }
}
