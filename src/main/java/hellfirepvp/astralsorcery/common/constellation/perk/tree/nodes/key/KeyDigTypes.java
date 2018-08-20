/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDigTypes
 * Created by HellFirePvP
 * Date: 20.07.2018 / 17:39
 */
public class KeyDigTypes extends KeyPerk {

    private static boolean checkingSpeed = false;

    public KeyDigTypes(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent
    public void checkHarvest(PlayerEvent.HarvestCheck event) {
        if (event.canHarvest()) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        Side side = event.getEntityLiving().world.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog != null && prog.hasPerkUnlocked(this)) {
            ItemStack heldMainHand = player.getHeldItemMainhand();
            if(!heldMainHand.isEmpty() && heldMainHand.getItem().getToolClasses(heldMainHand).contains("pickaxe")) {
                IBlockState tryHarvest = event.getTargetBlock();
                String toolRequired = tryHarvest.getBlock().getHarvestTool(tryHarvest);
                if(toolRequired == null || toolRequired.equalsIgnoreCase("shovel") || toolRequired.equalsIgnoreCase("axe")) {
                    event.setCanHarvest(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onHarvestSpeed(PlayerEvent.BreakSpeed event) {
        if (checkingSpeed) return;

        EntityPlayer player = event.getEntityPlayer();
        Side side = event.getEntityLiving().world.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog != null && prog.hasPerkUnlocked(this)) {
            IBlockState broken = event.getState();
            ItemStack playerMainHand = player.getHeldItemMainhand();
            if(!playerMainHand.isEmpty()) {
                if(playerMainHand.getItem().getToolClasses(playerMainHand).contains("pickaxe")) {
                    if(!broken.getBlock().isToolEffective("pickaxe", broken)) {
                        if(broken.getBlock().isToolEffective("shovel", broken) || broken.getBlock().isToolEffective("axe", broken)) {
                            checkingSpeed = true;
                            event.setNewSpeed(Math.max(event.getNewSpeed(), playerMainHand.getDestroySpeed(Blocks.STONE.getDefaultState())));
                            checkingSpeed = false;
                        }
                    }
                }
            }
        }
    }

}
