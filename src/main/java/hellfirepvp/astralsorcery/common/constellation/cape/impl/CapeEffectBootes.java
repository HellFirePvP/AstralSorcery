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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectBootes
 * Created by HellFirePvP
 * Date: 18.10.2017 / 00:56
 */
public class CapeEffectBootes extends CapeArmorEffect {

    public CapeEffectBootes(NBTTagCompound cmp) {
        super(cmp, "bootes");
    }

    @Override
    public void loadFromConfig(Configuration cfg) {

    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.bootes;
    }

    @Override
    public void playActiveParticleTick(EntityPlayer pl) {

    }

}
