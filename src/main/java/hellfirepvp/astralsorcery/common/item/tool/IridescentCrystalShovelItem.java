/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.util.BlockFinder;
import hellfirepvp.astralsorcery.common.util.BlockUtil;
import hellfirepvp.astralsorcery.common.util.FlagExecutor;
import hellfirepvp.astralsorcery.common.visual.type.BlockBreakEffect;
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
 * Class: IridescentCrystalPickaxeItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IridescentCrystalShovelItem extends CrystalShovelItem {

    private static final UUID MODIFIER_ID = UUID.fromString("21bfcfb8-3ed9-4543-8cc3-bf4da4277267");
    private static final DynamicAttributeModifier MINING_SIZE_MODIFIER =
            new DynamicAttributeModifier(MODIFIER_ID.toString(), PerksAS.AttributeTypes.MINING_SIZE, ModifierType.ADDITION, 1F);

    public IridescentCrystalShovelItem() {
        super(ItemsAS.CRYSTAL_TOOL_TIER, new Properties()
                .setNoRepair()
                .component(DataComponentsAS.DYNAMIC_MODIFIERS, new DynamicModifiersComponent(List.of(MINING_SIZE_MODIFIER)))
                .attributes(shovelAttributes()));
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
                if (!level.isEmptyBlock(pos)) {
                    List<BlockPos> foundBlocks = BlockFinder.findConnectedBlocksWithSameState(level, pos, true, 8, 200, true);
                    if (foundBlocks.isEmpty()) return;
                    foundBlocks.forEach(at -> {
                        BlockState otherState = level.getBlockState(at);
                        if (sPlayer.gameMode.destroyBlock(at)) {
                            BlockBreakEffect.at(at, otherState).sendToNearby(sLevel);
                        }
                    });

                    sPlayer.getCooldowns().addCooldown(stack.getItem(), 200);
                }
            });
        }
        return true;
    }
}
