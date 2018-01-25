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
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectArmara
 * Created by HellFirePvP
 * Date: 15.10.2017 / 16:48
 */
public class CapeEffectArmara extends CapeArmorEffect {

    private static int immunityRechargeTicks = 80;
    private static int immunityStacks = 3;

    public CapeEffectArmara(NBTTagCompound cmp) {
        super(cmp, "armara");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.armara;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.2F);

        Vector3 offset = Vector3.atEntityCorner(pl);
        offset.addY(pl.height / 2F);
        Color c = new Color(0x706EFF);
        int stacks = getCurrentImmunityStacks();
        if(stacks > 0) {
            Random r = new Random(pl.getUniqueID().hashCode());
            for (int i = 0; i < stacks; i++) {
                Vector3 axis = Vector3.random(r);
                axis.setX(axis.getX() * 0.35F);
                axis.setZ(axis.getZ() * 0.35F);
                Vector3 perpEffect = axis.clone().perpendicular();

                int ticksPerCircle = 80 + r.nextInt(50);
                int tick = (pl.ticksExisted) % ticksPerCircle;

                Vector3 pos = perpEffect.normalize().multiply(r.nextFloat() * 0.4F + 0.9F)
                        .rotate(Math.toRadians(360 * ((float) (tick) / (float) (ticksPerCircle))), axis)
                        .add(offset);

                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos);
                p.setColor(c).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                float scale = rand.nextFloat() * 0.2F + 0.2F;
                p.scale(scale);
                p.gravity(0.004);
                p.setMaxAge(20 + rand.nextInt(20));

                p = EffectHelper.genericFlareParticle(pos);
                p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(scale * 0.45F);
                p.gravity(0.004);
                p.setMaxAge(10 + rand.nextInt(10));
            }
        }
    }

    private int getCurrentImmunityRechargeTick() {
        return this.getData().getInteger("AS_Armara_ImmunityTick");
    }

    private void setCurrentImmunityRechargeTick(int tick) {
        this.getData().setInteger("AS_Armara_ImmunityTick", tick);
    }

    private int getCurrentImmunityStacks() {
        return this.getData().getInteger("AS_Armara_ImmunityStacks");
    }

    private void setCurrentImmunityStacks(int stacks) {
        this.getData().setInteger("AS_Armara_ImmunityStacks", stacks);
    }

    public boolean shouldPreventDamage(DamageSource source, boolean simulate) {
        if(source.canHarmInCreative()) {
            return false;
        }
        int stacks = getCurrentImmunityStacks();
        if(stacks <= 0) {
            return false;
        }
        if(!simulate) {
            stacks--;
            setCurrentImmunityStacks(stacks);
        }
        return true;
    }

    public void wornTick() {
        if(getCurrentImmunityStacks() >= immunityStacks) {
            setCurrentImmunityRechargeTick(immunityRechargeTicks);
            return;
        }
        int tick = getCurrentImmunityRechargeTick();
        tick--;
        if(tick <= 0) {
            setCurrentImmunityRechargeTick(immunityRechargeTicks);
            setCurrentImmunityStacks(getCurrentImmunityStacks() + 1);
        } else {
            setCurrentImmunityRechargeTick(tick);
        }
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        immunityRechargeTicks = cfg.getInt(getKey() + "RechargeTicks", getConfigurationSection(), immunityRechargeTicks, 1, 10000, "Defines the ticks you need to wear the cape until you get a immunty-stack that prevents 1 attack/damage-hit you'd take.");
        immunityStacks = cfg.getInt(getKey() + "ImmunityStacks", getConfigurationSection(), immunityStacks, 1, 30, "Defines the maximum amount of immunity-stacks you can overall charge up.");
    }

}
