/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyCalculator;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalPickaxeItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CrystalPickaxeItem extends PickaxeItem implements CrystalToolItem {

    public CrystalPickaxeItem() {
        this(ItemsAS.CRYSTAL_TOOL_TIER, new Properties()
                .setNoRepair()
                .attributes(pickaxeAttributes()));
    }

    protected CrystalPickaxeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    protected static ItemAttributeModifiers pickaxeAttributes() {
        return createAttributes(ItemsAS.CRYSTAL_TOOL_TIER, 1, -2.8F);
    }

    @Override
    public int getCrystalCount() {
        return 3;
    }

    @Override
    public void reApplyModifiers(ItemStack stack) {
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return CrystalPropertyCalculator.getToolDurability(super.getMaxDamage(stack), stack, 1);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool != null) {
            for (Tool.Rule rule : tool.rules()) {
                if (rule.speed().isPresent() && state.is(rule.blocks())) { //Testing if rule is effective.
                    return CrystalPropertyCalculator.getToolSpeed(speed, stack, 1);
                }
            }
        }
        return speed;
    }
}
