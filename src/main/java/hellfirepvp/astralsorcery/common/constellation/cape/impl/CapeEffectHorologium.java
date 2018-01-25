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
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.effect.time.TimeStopController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectHorologium
 * Created by HellFirePvP
 * Date: 17.10.2017 / 23:38
 */
public class CapeEffectHorologium extends CapeArmorEffect {

    private static float effectRange = 20F;
    private static int duration = 180;

    private static int cooldown = 1000;
    private static float chanceProc = 0.4F;

    public CapeEffectHorologium(NBTTagCompound cmp) {
        super(cmp, "horologium");
    }

    public void onHurt(EntityPlayer player) {
        if(player.getCooldownTracker().hasCooldown(ItemsAS.armorImbuedCape)) return;

        if(rand.nextFloat() < chanceProc) {
            TimeStopController.freezeWorldAt(player, player.world, player.getPosition(), effectRange, duration);
            player.getCooldownTracker().setCooldown(ItemsAS.armorImbuedCape, cooldown);
        }
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.horologium;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        float perc = 0.2F;
        if(!pl.getCooldownTracker().hasCooldown(ItemsAS.armorImbuedCape)) {
            perc = 0.35F;
        }
        playConstellationCapeSparkles(pl, perc);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        effectRange = cfg.getFloat(getKey() + "FreezeRange", getConfigurationSection(), effectRange, 4F, 64F, "Defines the range of the time-freeze effect");
        duration = cfg.getInt(getKey() + "Duration", getConfigurationSection(), duration, 40, 50_000, "Defines the duration of the time-freeze bubble");

        cooldown = cfg.getInt(getKey() + "Cooldown", getConfigurationSection(), cooldown, 40, 70_000, "Defines the cooldown for the time-freeze effect after it triggered (should be longer than duration!)");
        chanceProc = cfg.getFloat(getKey() + "TriggerChance", getConfigurationSection(), chanceProc, 0F, 1F, "Defines the chance for the time-freeze effect to trigger when being hit");
    }

}
