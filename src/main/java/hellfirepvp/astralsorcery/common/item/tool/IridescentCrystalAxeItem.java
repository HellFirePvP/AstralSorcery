/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.visual.type.BlockBreakEffect;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.util.FlagExecutor;
import hellfirepvp.astralsorcery.common.util.TreeDiscoverer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IridescentCrystalAxeItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IridescentCrystalAxeItem extends CrystalAxeItem {

    private static final UUID MODIFIER_ID = UUID.fromString("6b59ac1a-4f40-44e7-b8bc-c1197f7e4842");
    private static final DynamicAttributeModifier MINING_SPEED_MODIFIER =
            new DynamicAttributeModifier(MODIFIER_ID.toString(), PerksAS.AttributeTypes.BLOCK_REACH, ModifierType.ADDED_MULTIPLY, 0.2F);

    public IridescentCrystalAxeItem() {
        super(ItemsAS.CRYSTAL_TOOL_TIER, new Properties()
                .setNoRepair()
                .component(DataComponentsAS.DYNAMIC_MODIFIERS, new DynamicModifiersComponent(List.of(MINING_SPEED_MODIFIER)))
                .attributes(axeAttributes()));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!super.mineBlock(stack, level, state, pos, miningEntity)) {
            return false;
        }
        if (level instanceof ServerLevel sLevel &&
                miningEntity instanceof ServerPlayer sPlayer &&
                !sPlayer.isShiftKeyDown() &&
                !sPlayer.getCooldowns().isOnCooldown(this)) {

            FlagExecutor.run(FlagExecutor.Flag.CHAIN_BLOCK_BREAK, () -> {
                TreeDiscoverer.findTree(sLevel, pos, 10, true).ifPresent(tree -> {
                    tree.getContents().getContents().forEach((treePos, treeState) -> {
                        if (sPlayer.gameMode.destroyBlock(treePos)) {
                            BlockBreakEffect.at(treePos, treeState.getDescriptiveState(0)).sendToNearby(sLevel);
                        }
                    });

                    sPlayer.getCooldowns().addCooldown(this, 200);
                });
            });
        }
        return true;
    }
}
