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
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectDiscidia
 * Created by HellFirePvP
 * Date: 10.10.2017 / 20:03
 */
public class CapeEffectDiscidia extends CapeArmorEffect {

    @Override
    public float getActiveParticleChance() {
        return 0.25F; //..t
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.discidia;
    }

    public void writeLastAttackDamage(float dmgIn) {
        getData().setFloat("lastAttack", dmgIn);
    }

    public float getLastAttackDamage() {
        return NBTHelper.getFloat(getData(), "lastAttack", 0);
    }

}
