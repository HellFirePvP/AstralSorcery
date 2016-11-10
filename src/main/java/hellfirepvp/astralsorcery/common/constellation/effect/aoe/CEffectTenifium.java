package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import com.google.common.base.Predicate;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectEntityCollect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;
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
    public static float maxPushStrength = 2.0F;

    public CEffectTenifium() {
        super(Constellations.tenifium, "tenifium", 16, EntityLivingBase.class, (entity) -> !entity.isDead && !(entity instanceof EntityPlayer));
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }
        List<EntityLivingBase> entities = collectEntities(world, pos);
        Vector3 ped = new Vector3(pos).addY(1);
        if(!entities.isEmpty()) {
            for (EntityLivingBase entity : entities) {
                Vector3 push = ped.clone().subtract(entity);
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
        maxPushStrength = cfg.getFloat(getKey() + "PushStrength", getConfigurationSection(), 2.0F, 0.01F, 16.0F, "Set the max. push strength of the velocity that's applied to the mob to pull him towards the center");
    }

}
