/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

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
        if(pl.isElytraFlying()) {
            playVicioElytraSparkles(pl, 1F);
            playVicioElytraSparkles(pl, 0.8F);
        } else {
            playConstellationCapeSparkles(pl, 0.15F);
        }
    }

    @SideOnly(Side.CLIENT)
    private void playVicioElytraSparkles(EntityPlayer pl, float strength) {
        if(rand.nextFloat() < strength) {
            Color c = getAssociatedConstellation().getConstellationColor();
            if(c != null) {
                double x = pl.posX + rand.nextFloat() * pl.width - (pl.width / 2);
                double y = pl.posY + rand.nextFloat() * (pl.height / 2) + 0.2;
                double z = pl.posZ + rand.nextFloat() * pl.width - (pl.width / 2);
                x -= pl.motionX * 1.5F;
                y -= pl.motionY * 1.5F;
                z -= pl.motionZ * 1.5F;

                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(c).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.5F + 0.3F);
                if(rand.nextInt(3) == 0) {
                    p.setColor(IConstellation.weak);
                }
                p.setMaxAge(30 + rand.nextInt(20));

                if(rand.nextFloat() < 0.8F) {
                    p = EffectHelper.genericFlareParticle(x, y, z);
                    p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                    p.scale(rand.nextFloat() * 0.2F + 0.1F);
                    p.setMaxAge(20 + rand.nextInt(10));
                }
            }
        }
    }

}
