/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import hellfirepvp.astralsorcery.common.base.MeltInteraction;
import hellfirepvp.astralsorcery.common.base.WorldMeltables;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectFornax
 * Created by HellFirePvP
 * Date: 10.10.2017 / 22:52
 */
public class CapeEffectFornax extends CapeArmorEffect {

    private static float fireMultiplier = 0F;
    private static float healMultiplier = 1F;
    private static boolean burningMelt = true;

    public CapeEffectFornax(NBTTagCompound cmp) {
        super(cmp, "fornax");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.fornax;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        float chance = 0.4F;
        if(pl.isBurning()) {
            chance = 0.9F;
        }
        playConstellationCapeSparkles(pl, chance);
    }

    public void attemptMelt(EntityPlayer pl) {
        if(burningMelt && pl.isBurning()) {
            BlockPos at = pl.getPosition().down();
            MeltInteraction mi = WorldMeltables.getMeltable(pl.getEntityWorld(), at);
            if(mi != null) {
                PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_MELT_BLOCK, at);
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(pl.getEntityWorld(), at, 16));
                if(rand.nextFloat() < 0.1) {
                    mi.placeResultAt(pl.getEntityWorld(), at);
                }
            }
        }
    }

    public float getDamageMultiplier() {
        return fireMultiplier;
    }

    public void healFor(EntityPlayer player, float amount) {
        player.heal(amount * healMultiplier);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        burningMelt = cfg.getBoolean(getKey() + "BurningMelt", getConfigurationSection(), burningMelt, "If a player burns while wearing the cape, this toggles if blocks below him then melt (true) or not. (false)");
        fireMultiplier = cfg.getFloat(getKey() + "FireDmgMultiplier", getConfigurationSection(), fireMultiplier, 0, 1, "Sets the multiplier for how much damage you take from fire damage while wearing a fornax cape");
        healMultiplier = cfg.getFloat(getKey() + "FireHealMultiplier", getConfigurationSection(), healMultiplier, 0, 5, "Sets the multiplier for how much healing the player receives from the original damage when being hit by fire damage.");
    }

}
