/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDestructionDigTypes
 * Created by HellFirePvP
 * Date: 27.07.2017 / 18:40
 */
public class PerkDestructionDigTypes extends ConstellationPerk {

    private static boolean checkingSpeed = false;

    public PerkDestructionDigTypes() {
        super("DTR_BREAKTYPE", Target.PLAYER_HARVEST_TYPE, Target.PLAYER_HARVEST_SPEED);
    }

    @Override
    public boolean onCanHarvest(EntityPlayer harvester, @Nonnull ItemStack playerMainHand, IBlockState tryHarvest, boolean prevSuccess) {
        if(!playerMainHand.isEmpty()) {
            if(playerMainHand.getItem().getToolClasses(playerMainHand).contains("pickaxe")) {
                String toolRequired = tryHarvest.getBlock().getHarvestTool(tryHarvest);
                if(toolRequired == null || toolRequired.equalsIgnoreCase("shovel") || toolRequired.equalsIgnoreCase("axe")) {
                    addAlignmentCharge(harvester, 0.1);
                    prevSuccess = true;
                }
            }
        }
        return prevSuccess;
    }

    @Override
    public float onHarvestSpeed(EntityPlayer harvester, IBlockState broken, @Nullable BlockPos at, float breakSpeedIn) {
        if(checkingSpeed) return breakSpeedIn;
        ItemStack playerMainHand = harvester.getHeldItemMainhand();
        if(!playerMainHand.isEmpty()) {
            if(playerMainHand.getItem().getToolClasses(playerMainHand).contains("pickaxe")) {
                if(!broken.getBlock().isToolEffective("pickaxe", broken)) {
                    if(broken.getBlock().isToolEffective("shovel", broken) || broken.getBlock().isToolEffective("axe", broken)) {
                        checkingSpeed = true;
                        breakSpeedIn = playerMainHand.getStrVsBlock(Blocks.STONE.getDefaultState());
                        checkingSpeed = false;
                    }
                }
            }
        }
        return breakSpeedIn;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {}

}
