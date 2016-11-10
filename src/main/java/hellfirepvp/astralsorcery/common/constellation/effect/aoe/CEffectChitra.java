package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectEntityCollect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityAnimal;
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
 * Class: CEffectChitra
 * Created by HellFirePvP
 * Date: 09.11.2016 / 14:28
 */
public class CEffectChitra extends CEffectEntityCollect<EntityAnimal> {

    public static double potencyMultiplier = 1;
    public static int mateChance = 200;

    public CEffectChitra() {
        super(Constellations.chitra, "chitra", 16D, EntityAnimal.class, (entity) -> !entity.isDead && !entity.isInLove());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(!Minecraft.isFancyGraphicsEnabled() && rand.nextInt(10) != 0) return;
        if(rand.nextInt(2) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                    pos.getY() + rand.nextFloat() * 2 + 0.5,
                    pos.getZ() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            p.motion((rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.45F).setColor(new Color(255, 0, 127)).setMaxAge(55);
        }
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }
        List<EntityAnimal> entities = collectEntities(world, pos);
        if(!entities.isEmpty()) {
            EntityPlayer owner = getOwningPlayerInWorld(world, pos);
            for (EntityAnimal entity : entities) {
                if(!entity.isInLove() && rand.nextInt(mateChance) == 0) {
                    entity.setInLove(owner);
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
        mateChance = cfg.getInt(getKey() + "MateChance", getConfigurationSection(), 200, 1, 10000, "Defines the chance that an animal, once it's found, to be set 'in love' (-> Random#nextInt(MateChance) == 0)");
    }
}
