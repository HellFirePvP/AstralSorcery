package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectEntityCollect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectInpes
 * Created by HellFirePvP
 * Date: 13.11.2016 / 17:59
 */
public class CEffectInpes extends CEffectEntityCollect<EntityLivingBase> {

    public static double potencyMultiplier = 1;
    public static float pushStrengthMultiplier = 0.1F;

    public CEffectInpes() {
        super(Constellations.inpes, "inpes", 16, EntityLivingBase.class, (entity) -> !entity.isDead && !(entity instanceof EntityPlayer));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(!Minecraft.isFancyGraphicsEnabled() && rand.nextInt(5) != 0) return;
        Vector3 to = new Vector3(
                pos.getX() + 0.5 + rand.nextFloat() * 7 * (rand.nextBoolean() ? 1 : -1),
                pos.getY() + 0.5 + rand.nextFloat() * 7,
                pos.getZ() + 0.5 + rand.nextFloat() * 7 * (rand.nextBoolean() ? 1 : -1));
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5);
        Vector3 mov = to.subtract(pos).normalize().multiply(0.1 + rand.nextFloat() * 0.2);
        p.motion(mov.getX(), mov.getY(), mov.getZ());
        p.scale(0.25F).setColor(new Color(225, 225, 255));
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }
        List<EntityLivingBase> entities = collectEntities(world, pos);
        Vector3 ped = new Vector3(pos);
        if(!entities.isEmpty()) {
            for (EntityLivingBase entity : entities) {
                Vector3 mov = new Vector3(entity).subtract(ped);
                mov.divide(mov.length());
                if(mov.length() > pushStrengthMultiplier) {
                    mov.normalize().multiply(pushStrengthMultiplier);
                }
                entity.motionX += mov.getX();
                entity.motionY += mov.getY();
                entity.motionZ += mov.getZ();
                entity.fallDistance = 0F;
            }
        }
        return false;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, Constellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        super.loadFromConfig(cfg);

        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
        pushStrengthMultiplier = cfg.getFloat(getKey() + "PushStrengthMul", getConfigurationSection(), 0.1F, 0.01F, 16.0F, "Sets the max. strength multiplier for entity push.");
    }

}
