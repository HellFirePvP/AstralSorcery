package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectController;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectPhoenix;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectEntityCollect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPhoenix
 * Created by HellFirePvP
 * Date: 13.11.2016 / 14:50
 */
public class CEffectPhoenix extends CEffectEntityCollect<EntityLivingBase> {

    public static final Color PHOENIX_COLOR = new Color(0xFF5711);

    public static double potencyMultiplier = 1;
    public static float applyChance = 0.05F;
    public static boolean applyAll = false;

    public CEffectPhoenix() {
        super(Constellations.phoenix, "phoenix", 14, EntityLivingBase.class, (e) -> !e.isDead);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(pedestal.getTicksExisted() % 20 == 0) {
            OrbitalEffectPhoenix ph = new OrbitalEffectPhoenix();
            OrbitalEffectController ctrl = EffectHandler.getInstance().orbital(ph, ph, ph);
            ctrl.setOffset(new Vector3(pedestal).add(0.5, 0.5, 0.5));
            ctrl.setOrbitRadius(0.8 + rand.nextFloat() * 0.7);
            ctrl.setOrbitAxis(Vector3.RotAxis.Y_AXIS);
            ctrl.setTicksPerRotation(20 + rand.nextInt(20));
        }
        for (int i = 0; i < 2; i++) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            p.motion(rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                     0.005 + rand.nextFloat() * 0.01,
                     rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.35F).setColor(PHOENIX_COLOR).setMaxAge(45);
        }
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        p.motion(rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                 0.005 + rand.nextFloat() * 0.01,
                 rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.35F).setColor(Color.RED).setMaxAge(45);
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }
        List<EntityLivingBase> entities = collectEntities(world, pos);
        if(!entities.isEmpty()) {
            if(rand.nextFloat() <= applyChance) {
                PotionEffect toApply = new PotionEffect(RegistryPotions.potionCheatDeath, 200, 0, true, true);
                if(applyAll) {
                    for (EntityLivingBase e : entities) {
                        e.addPotionEffect(toApply);
                    }
                } else {
                    entities.get(rand.nextInt(entities.size())).addPotionEffect(toApply);
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
        applyChance = cfg.getFloat(getKey() + "ApplyChance", getConfigurationSection(), 0.05F, 0.0F, 1.0F, "Set the chance that the phoenix-potioneffect will be applied to any living entity in the ritual's vicinity.");
        applyAll = cfg.getBoolean(getKey() + "ApplyToAll", getConfigurationSection(), false, "If this is set to true, the ritual effect will (in case the applychance-check succeeds) apply the phoenix potion effect to all entities close to it, not just to one.");
    }

    @SideOnly(Side.CLIENT)
    public static void playEntityDeathEffect(PktParticleEvent event) {
        for (int i = 0; i < 25; i++) {
            Vector3 at = event.getVec();
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    at.getX(),
                    at.getY() + rand.nextFloat(),
                    at.getZ());
            p.motion((rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F).setColor(PHOENIX_COLOR);
        }
    }

}
