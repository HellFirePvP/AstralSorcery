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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDestructionStack
 * Created by HellFirePvP
 * Date: 29.07.2017 / 23:36
 */
public class PerkDestructionStack extends ConstellationPerk {

    private static int multiplierCap = 10;
    private static float harvestSpeedMultiplier = 0.2F;
    private static float attackDamageMultiplier = 0.3F;

    public PerkDestructionStack() {
        super("DTR_STACK", Target.ENTITY_ATTACK, Target.PLAYER_HARVEST_SPEED);
    }

    @Override
    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        int mul = MathHelper.clamp(getMultiplier(attacker) + 1, 1, multiplierCap);
        setMultiplier(attacker, mul);
        dmgIn *= (1F + attackDamageMultiplier * mul);
        return dmgIn;
    }

    @Override
    public float onHarvestSpeed(EntityPlayer harvester, IBlockState broken, @Nullable BlockPos at, float breakSpeedIn) {
        int mul = MathHelper.clamp(getMultiplier(harvester) + 1, 1, multiplierCap);
        setMultiplier(harvester, mul);
        breakSpeedIn *= (1F + harvestSpeedMultiplier * mul);
        return breakSpeedIn;
    }

    private int getMultiplier(EntityPlayer player) {
        return MathHelper.ceil(((float) getActiveCooldownForPlayer(player)) / 30F);
    }

    private void setMultiplier(EntityPlayer player, int multiplier) {
        forceSetCooldownForPlayer(player, multiplier * 30);
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        multiplierCap = cfg.getInt(getKey() + "MultiplierCap", getConfigurationSection(), multiplierCap, 1, 128, "Defines the cap for the harvestSpeed + damageIncrease multiplier-stacks.");
        harvestSpeedMultiplier = cfg.getFloat(getKey() + "HarvestSpeedMultiplier", getConfigurationSection(), harvestSpeedMultiplier, 0F, 32F, "Defines the multiplier per harvestSpeed stack. Multiple multipliers are additive.");
        attackDamageMultiplier = cfg.getFloat(getKey() + "AttackDamageMultiplier", getConfigurationSection(), attackDamageMultiplier, 0F, 32F, "Defines the multiplier per damageIncrease stack. Multiple multipliers are additive.");
    }

}
