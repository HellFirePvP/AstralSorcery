/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDigTypes
 * Created by HellFirePvP
 * Date: 31.08.2019 / 17:30
 */
public class KeyDigTypes extends KeyPerk {

    public KeyDigTypes(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(this::onHarvest);
        bus.addListener(this::onHarvestSpeed);
    }

    private void onHarvest(PlayerEvent.HarvestCheck event) {
        if (event.canHarvest()) {
            return;
        }

        PlayerEntity player = event.getPlayer();
        LogicalSide side = this.getSide(player);
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.hasPerkEffect(this)) {
            ItemStack heldMainHand = player.getHeldItemMainhand();
            if (!heldMainHand.isEmpty() && heldMainHand.getItem().getToolTypes(heldMainHand).contains(ToolType.PICKAXE)) {
                ToolType requiredTool = event.getTargetBlock().getHarvestTool();
                if (requiredTool == null || requiredTool.equals(ToolType.SHOVEL) || requiredTool.equals(ToolType.AXE)) {
                    event.setCanHarvest(true);
                }
            }
        }
    }

    private void onHarvestSpeed(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        LogicalSide side = this.getSide(player);
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.hasPerkEffect(this)) {
            BlockState broken = event.getState();
            ItemStack playerMainHand = player.getHeldItemMainhand();
            if (!playerMainHand.isEmpty()) {
                if (playerMainHand.getItem().getToolTypes(playerMainHand).contains(ToolType.PICKAXE)) {
                    if (!broken.isToolEffective(ToolType.PICKAXE) &&
                            (broken.isToolEffective(ToolType.AXE) || broken.isToolEffective(ToolType.SHOVEL))) {
                        EventFlags.CHECK_BREAK_SPEED.executeWithFlag(() -> {
                            event.setNewSpeed(Math.max(event.getNewSpeed(), playerMainHand.getDestroySpeed(Blocks.STONE.getDefaultState())));
                        });
                    }
                }
            }
        }
    }
}
