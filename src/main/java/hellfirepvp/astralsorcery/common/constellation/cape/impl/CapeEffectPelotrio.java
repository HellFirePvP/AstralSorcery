/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectPelotrio
 * Created by HellFirePvP
 * Date: 13.10.2017 / 23:48
 */
public class CapeEffectPelotrio extends CapeArmorEffect {

    private static float chanceSpawnPick = 0.5F;
    private static float chanceSpawnAxe = 0.65F;
    private static float chanceSpawnSword = 0.2F;

    private static float swordAttackDamage = 4F;

    private static int ticksSwordAttacks = 6;
    private static int ticksBreakBlockPick = 4;
    private static int ticksBreakBlockAxe = 2;

    public CapeEffectPelotrio(NBTTagCompound cmp) {
        super(cmp, "pelotrio");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.pelotrio;
    }

    public float getChanceSpawnPick() {
        return chanceSpawnPick;
    }

    public float getChanceSpawnAxe() {
        return chanceSpawnAxe;
    }

    public float getChanceSpawnSword() {
        return chanceSpawnSword;
    }

    public static float getSwordAttackDamage() {
        return swordAttackDamage;
    }

    public static int getTicksSwordAttacks() {
        return ticksSwordAttacks;
    }

    public static int getTicksBreakBlockPick() {
        return ticksBreakBlockPick;
    }

    public static int getTicksBreakBlockAxe() {
        return ticksBreakBlockAxe;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.2F);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        chanceSpawnPick = cfg.getFloat(getKey() + "Chance_Pickaxe", getConfigurationSection(), chanceSpawnPick, 0, 1, "Defines the chance of a spectral pickaxe spawning that's mining for you for a bit when you mine a block.");
        chanceSpawnAxe = cfg.getFloat(getKey() + "Chance_Axe", getConfigurationSection(), chanceSpawnAxe, 0, 1, "Defines the chance of a spectral axe spawning that's chopping logs and leaves for you for a bit when you break a log or leaf.");
        chanceSpawnSword = cfg.getFloat(getKey() + "Chance_Sword", getConfigurationSection(), chanceSpawnSword, 0, 1, "Defines the chance of a spectral sword spawning that fights mobs nearby for a bit when you attack a mob.");

        swordAttackDamage = cfg.getFloat(getKey() + "Sword_Attack", getConfigurationSection(), swordAttackDamage, 0.1F, 32F, "Defines the damage the sword does per attack");

        ticksSwordAttacks = cfg.getInt(getKey() + "Ticks_Sword", getConfigurationSection(), ticksSwordAttacks, 1, 100, "Definies how many ticks are at least between sword attacks the sword makes");
        ticksBreakBlockAxe = cfg.getInt(getKey() + "Ticks_Axe", getConfigurationSection(), ticksBreakBlockAxe, 1, 100, "Definies how long an axe is going to need to break a leaf or log");
        ticksBreakBlockPick = cfg.getInt(getKey() + "Ticks_Pickaxe", getConfigurationSection(), ticksBreakBlockPick, 1, 100, "Definies how long a pickaxe needs to break a block");
    }

}
