/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
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
 * Class: CapeEffectVicio
 * Created by HellFirePvP
 * Date: 15.10.2017 / 20:29
 */
public class CapeEffectVicio extends CapeArmorEffect {

    public CapeEffectVicio(NBTTagCompound cmp) {
        super(cmp, "vicio");
    }

    @Override
    public void loadFromConfig(Configuration cfg) {}

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.vicio;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        float chance = 0.15F;
        if(pl.isElytraFlying()) {
            chance = 0.8F;
            playConstellationCapeSparkles(pl, 1F);
        }
        playConstellationCapeSparkles(pl, chance);
    }

}
