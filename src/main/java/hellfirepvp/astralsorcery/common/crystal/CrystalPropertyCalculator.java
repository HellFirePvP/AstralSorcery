/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.item.tool.CrystalAxeItem;
import hellfirepvp.astralsorcery.common.item.tool.CrystalSwordItem;
import hellfirepvp.astralsorcery.common.item.tool.CrystalToolItem;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalPropertyCalculator
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CrystalPropertyCalculator {

    private CrystalPropertyCalculator() {}

    private static <T> T withProperties(ItemStack stack, T _default, Function<CrystalAttributesComponent, T> run) {
        CrystalAttributesComponent cmp = stack.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());
        if (!cmp.isEmpty()) {
            return run.apply(cmp);
        }
        return _default;
    }

    private static void withProperties(ItemStack stack, Consumer<CrystalAttributesComponent> run) {
        CrystalAttributesComponent cmp = stack.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());
        if (!cmp.isEmpty()) {
            run.accept(cmp);
        }
    }

    public static float getStarlightFocusRate(CrystalAttributesComponent cmp) {
        int sizeTier = cmp.getAttributeTier(CrystalPropertiesAS.SIZE);
        int cutTier = cmp.getAttributeTier(CrystalPropertiesAS.CUT);
        return 1F + (sizeTier * 0.1F + cutTier * 0.15F);
    }

    public static float getStarlightTransmissionLoss(CrystalAttributesComponent cmp) {
        int purityTier = cmp.getAttributeTier(CrystalPropertiesAS.PURITY);
        int missingTiers = CrystalPropertiesAS.PURITY.get().getMaxTier() - purityTier;
        return 1F - (missingTiers * 0.15F);
    }

    public static int getToolDurability(int durability, ItemStack stack, float multiplier) {
        return withProperties(stack, durability, cmp -> {
            int sizeTier = cmp.getAttributeTier(CrystalPropertiesAS.SIZE);
            int toolDurabilityTier = cmp.getAttributeTier(CrystalPropertiesAS.TOOL_DURABILITY);
            return durability + Mth.floor((sizeTier * 793 + toolDurabilityTier * 1283) * multiplier);
        });
    }

    public static float getToolSpeed(float speed, ItemStack stack, float multiplier) {
        return withProperties(stack, speed, cmp -> {
            int cutTier = cmp.getAttributeTier(CrystalPropertiesAS.CUT);
            int toolEfficiencyTier = cmp.getAttributeTier(CrystalPropertiesAS.TOOL_EFFICIENCY);
            return speed + (cutTier * 1.2F + toolEfficiencyTier * 2F) * multiplier;
        });
    }

    public static float getToolDamage(float damage, ItemStack stack, float multiplier) {
        return withProperties(stack, damage, cmp -> {
            int cutTier = cmp.getAttributeTier(CrystalPropertiesAS.CUT);
            int toolEfficiencyTier = cmp.getAttributeTier(CrystalPropertiesAS.TOOL_EFFICIENCY);
            return damage + (cutTier * 1.5F + toolEfficiencyTier * 2) * multiplier;
        });
    }
}
