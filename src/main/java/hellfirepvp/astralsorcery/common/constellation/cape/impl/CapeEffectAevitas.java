/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectAevitas
 * Created by HellFirePvP
 * Date: 10.10.2017 / 21:14
 */
public class CapeEffectAevitas extends CapeArmorEffect {

    private static final int ticksPerRound = 30;

    private static float range = 10F;
    private static float potency = 1F;
    private static float turnChance = 0.2F;

    public CapeEffectAevitas(NBTTagCompound cmp) {
        super(cmp, "aevitas");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.aevitas;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.1F);

        Color c = getAssociatedConstellation().getConstellationColor();
        if(c != null) {
            double x = pl.posX + (pl.width / 2);
            double y = pl.posY;
            double z = pl.posZ + (pl.width / 2);
            Vector3 centerOffset = new Vector3(x, y, z);
            float tick = (float) (ClientScheduler.getClientTick() % ticksPerRound);
            Vector3 axis = Vector3.RotAxis.Y_AXIS;
            Vector3 circleVec = axis.clone().perpendicular().normalize().multiply(range * 0.9 * rand.nextFloat());
            double deg = 360D * (tick / (float) (ticksPerRound));
            Vector3 mov = circleVec.clone().rotate(Math.toRadians(deg), axis.clone());

            Vector3 at = mov.clone().add(centerOffset);

            EntityFXFacingParticle p;
            if(rand.nextFloat() < 0.2) {
                p = EffectHelper.genericFlareParticle(at);
                p.setColor(c).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.5F + 0.3F);
                if(rand.nextInt(3) == 0) {
                    p.setColor(IConstellation.weak);
                }
                p.setMaxAge(30 + rand.nextInt(20));

                if(rand.nextFloat() < 0.8F) {
                    p = EffectHelper.genericFlareParticle(at);
                    p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                    p.scale(rand.nextFloat() * 0.2F + 0.1F);
                    p.setMaxAge(20 + rand.nextInt(10));
                }
            }


            if(rand.nextFloat() < 0.2) {
                deg += 180;
                mov = circleVec.clone().rotate(Math.toRadians(deg), axis.clone());

                at = mov.clone().add(centerOffset);

                p = EffectHelper.genericFlareParticle(at);
                p.setColor(c).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.5F + 0.3F);
                if(rand.nextInt(3) == 0) {
                    p.setColor(IConstellation.weak);
                }
                p.setMaxAge(30 + rand.nextInt(20));

                if(rand.nextFloat() < 0.8F) {
                    p = EffectHelper.genericFlareParticle(at);
                    p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                    p.scale(rand.nextFloat() * 0.2F + 0.1F);
                    p.setMaxAge(20 + rand.nextInt(10));
                }
            }
        }
    }

    public float getTurnChance() {
        return turnChance;
    }

    public float getRange() {
        return range;
    }

    public float getPotency() {
        return potency;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        range = cfg.getFloat(getKey() + "Range", getConfigurationSection(), range, 1, 32, "Defines the radius (in blocks) for the aoe effect.");
        potency = cfg.getFloat(getKey() + "Potency", getConfigurationSection(), potency, 0, 1, "Defines the multiplier if the aoe will happen at all");
        turnChance = cfg.getFloat(getKey() + "PlantTransformChance", getConfigurationSection(), turnChance, 0, 1, "Defines the chance that the aoe will search for a plant to turn into another plant.");
    }

}
