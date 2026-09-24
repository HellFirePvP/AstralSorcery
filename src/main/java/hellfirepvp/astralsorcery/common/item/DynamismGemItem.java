/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.perk.DynamismGemModifierHelper;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.perk.socket.GemSocketItem;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DynamismGemItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DynamismGemItem extends ItemCustom implements GemSocketItem {

    private final GemType gemType;

    public DynamismGemItem(GemType gemType) {
        super(new Properties()
                .component(DataComponentsAS.DYNAMIC_MODIFIERS, DynamicModifiersComponent.EMPTY)
                .stacksTo(1));
        this.gemType = gemType;
    }

    public GemType getGemType() {
        return this.gemType;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(level instanceof ServerLevel)) return;

        if (stack.getOrDefault(DataComponentsAS.DYNAMIC_MODIFIERS, DynamicModifiersComponent.EMPTY).modifiers().isEmpty()) {
            DynamismGemModifierHelper.rollGem(stack);
        }
    }

    public enum GemType {

        SKY(0.15F, 1.0F),
        DAY(0.6F, 0.4F),
        NIGHT(0F, 2.0F);

        private final float countModifier;
        private final float amplifierModifier;

        GemType(float countModifier, float amplifierModifier) {
            this.countModifier = countModifier;
            this.amplifierModifier = amplifierModifier;
        }

        public float getCountModifier() {
            return this.countModifier;
        }

        public float getAmplifierModifier() {
            return this.amplifierModifier;
        }
    }
}
