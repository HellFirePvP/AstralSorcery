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
 * Class: CEffectTenifium
 * Created by HellFirePvP
 * Date: 09.11.2016 / 20:28
 */
public class CEffectTenifium extends CEffectEntityCollect<EntityLivingBase> {

    public static double potencyMultiplier = 1;
    public static float pushStrengthMultiplier = 1.0F;

    public CEffectTenifium() {
        super(Constellations.tenifium, "tenifium", 16, EntityLivingBase.class, (entity) -> !entity.isDead && !(entity instanceof EntityPlayer));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(!Minecraft.isFancyGraphicsEnabled() && rand.nextInt(5) != 0) return;
        Vector3 at = new Vector3(
                pos.getX() + 0.5 + rand.nextFloat() * 7 * (rand.nextBoolean() ? 1 : -1),
                pos.getY() + 0.5 + rand.nextFloat() * 7,
                pos.getZ() + 0.5 + rand.nextFloat() * 7 * (rand.nextBoolean() ? 1 : -1));
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
        Vector3 to = new Vector3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5).subtract(at).normalize().multiply(0.1 + rand.nextFloat() * 0.2);
        p.motion(to.getX(), to.getY(), to.getZ());
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
                double var3 = (ped.getX() + 0.5D - entity.posX) / 15.0D;
                double var5 = (ped.getY() + 1.5D - entity.posY) / 15.0D;
                double var7 = (ped.getZ() + 0.5D - entity.posZ) / 15.0D;
                double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
                double var11 = 1.0D - var9;
                if (var11 > 0.0D) {
                    var11 *= var11;
                    entity.motionX += var3 / var9 * var11 * 0.1D *  pushStrengthMultiplier;
                    entity.motionY += var5 / var9 * var11 * 0.14D * pushStrengthMultiplier;
                    entity.motionZ += var7 / var9 * var11 * 0.1D *  pushStrengthMultiplier;
                    entity.fallDistance = 0;
                }
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
        pushStrengthMultiplier = cfg.getFloat(getKey() + "PushStrengthMul", getConfigurationSection(), 1.0F, 0.01F, 16.0F, "Sets the max. strength multiplier for entity pull.");
    }

}
