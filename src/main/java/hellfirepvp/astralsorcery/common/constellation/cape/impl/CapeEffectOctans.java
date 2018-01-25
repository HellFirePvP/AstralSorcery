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
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectOctans
 * Created by HellFirePvP
 * Date: 17.10.2017 / 17:58
 */
public class CapeEffectOctans extends CapeArmorEffect {

    private static float healPerTick = 0.01F;

    public CapeEffectOctans(NBTTagCompound cmp) {
        super(cmp, "octans");
    }

    public void onWaterHealTick(EntityPlayer pl) {
        if(healPerTick <= 0) return;
        pl.heal(healPerTick);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        healPerTick = cfg.getFloat(getKey() + "HealPerTick", getConfigurationSection(), healPerTick, 0, 5, "Defines the amount of health that is healed while the wearer is in water. Can be set to 0 to disable this.");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.octans;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        float perc = 0.15F;
        if(pl.isInsideOfMaterial(Material.WATER)) {
            perc = 0.3F;
        }
        playConstellationCapeSparkles(pl, perc);
    }

}
