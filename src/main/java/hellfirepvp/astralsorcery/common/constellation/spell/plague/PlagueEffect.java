/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell.plague;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.spell.ISpellEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlagueEffect
 * Created by HellFirePvP
 * Date: 07.07.2017 / 11:17
 */
public class PlagueEffect {

    private IConstellation parent;
    private int ticksApplied = 0;
    private int duration;
    private boolean expired = false;

    public PlagueEffect(IConstellation parentConstellation, int expectedDuration) {
        this.parent = parentConstellation;
        this.duration = expectedDuration;
    }

    public PlagueEffect(NBTTagCompound compound) {
        readNBT(compound);
    }

    public void onTick(EntityLivingBase affected) {
        if(hasExpired()) return;

        ISpellEffect effect = parent.getSpellEffect();
        if(effect == null) {
            this.expired = true;
            return;
        }
        effect.affect(affected, ISpellEffect.EffectType.ENTITY_SPELL_PLAGUE);
        ticksApplied++;
        if(ticksApplied >= duration) {
            this.expired = true;
        }
    }

    public boolean hasExpired() {
        return this.expired;
    }

    private void readNBT(NBTTagCompound compound) {
        this.ticksApplied = compound.getInteger("ticks");
        this.duration = compound.getInteger("duration");
        this.expired = compound.getBoolean("expired");
        String cstName = compound.getString("constellation");
        if(!cstName.isEmpty()) {
            IConstellation cst = ConstellationRegistry.getConstellationByName(cstName);
            if(cst != null) {
                this.parent = cst;
            } else {
                this.expired = true;
            }
        } else {
            this.expired = true;
        }
    }

    public void writeNBT(NBTTagCompound compound) {
        compound.setInteger("ticks", this.ticksApplied);
        compound.setInteger("duration", this.duration);
        compound.setBoolean("expired", this.expired);
        compound.setString("constellation", parent.getUnlocalizedName());
    }

}
