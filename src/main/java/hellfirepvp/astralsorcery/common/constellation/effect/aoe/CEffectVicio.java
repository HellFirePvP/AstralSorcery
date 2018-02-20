/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectStatus;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerEntity;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
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
 * Class: CEffectVicio
 * Created by HellFirePvP
 * Date: 10.01.2017 / 21:52
 */
public class CEffectVicio extends ConstellationEffect implements ConstellationEffectStatus {

    public static boolean enabled = true;
    public static int effectRange = 16, effectRangePerLens = 8;

    public CEffectVicio(@Nullable ILocatable origin) {
        super(origin, Constellations.vicio, "vicio");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(rand.nextInt(3) == 0) {
            Vector3 r = new Vector3(
                pos.getX() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                pos.getY() + rand.nextFloat() * 2 + 0.5,
                pos.getZ() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(r);
            p.motion((rand.nextFloat() * 0.07F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.07F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.07F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.45F).setColor(new Color(200, 200, 255)).gravity(0.008).setMaxAge(25);
            p = EffectHelper.genericFlareParticle(r);
            p.motion(0, rand.nextFloat() * 0.07F, 0);
            p.scale(0.45F).setColor(new Color(200, 200, 255)).gravity(0.008).setMaxAge(25);
        }
    }

    @Override
    public boolean runEffect(World world, BlockPos pos, int mirrorAmount, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        int range = effectRange + effectRangePerLens * mirrorAmount;
        List<EntityPlayerMP> entities = world.getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos).grow(range));
        for (EntityPlayerMP pl : entities) {
            if (EventHandlerEntity.ritualFlight.setOrAddTimeout(40, pl)) {
                boolean prev = pl.capabilities.allowFlying;
                pl.capabilities.allowFlying = true;
                if (!prev) {
                    pl.sendPlayerAbilities();
                }
            }
        }
        return false;
    }

    @Override
    public boolean runTraitEffect(World world, BlockPos pos, int mirrorAmount, IMinorConstellation traitType) {
        return false;
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        return false;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, IMinorConstellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        effectRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), effectRange, 1, 32, "Defines the radius (in blocks) in which the ritual will allow the players to fly in.");
        effectRangePerLens = cfg.getInt(getKey() + "RangeIncreasePerLens", getConfigurationSection(), effectRangePerLens, 1, 32, "Defines the increase in radius the ritual will get per active lens enhancing the ritual.");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
    }

}
