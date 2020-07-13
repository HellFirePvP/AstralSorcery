/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.source.orbital.FXOrbitalLucerna;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectStatus;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.event.helper.EventHelperSpawnDeny;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.block.WorldBlockPos;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tick.TickTokenMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectLucerna
 * Created by HellFirePvP
 * Date: 01.02.2020 / 10:11
 */
public class CEffectLucerna extends ConstellationEffect implements ConstellationEffectStatus {

    public static LucernaConfig CONFIG = new LucernaConfig();

    private int rememberedTimeout = 0;

    public CEffectLucerna(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.lucerna);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        if (ClientScheduler.getClientTick() % 20 == 0) {
            EffectHelper.spawnSource(new FXOrbitalLucerna(new Vector3(pos).add(0.5, 0.5, 0.5))
                    .setOrbitAxis(Vector3.RotAxis.Y_AXIS)
                    .setOrbitRadius(0.8 + rand.nextFloat() * 0.7)
                    .setTicksPerRotation(20 + rand.nextInt(20)));
        }
    }

    @Override
    public boolean runStatusEffect(World world, BlockPos pos, int mirrorAmount, ConstellationEffectProperties modified, @Nullable IMinorConstellation possibleTraitEffect) {
        if (modified.isCorrupted()) {
            if (DayTimeHelper.isNight(world) && rand.nextBoolean()) {
                SkyHandler.getInstance().revertWorldTimeTick(world);
            }
            return true;
        }

        WorldBlockPos at = WorldBlockPos.wrapServer(world, pos);
        TickTokenMap.SimpleTickToken<Double> token = EventHelperSpawnDeny.spawnDenyRegions.get(at);
        if(token != null && Math.abs(token.getValue() - modified.getSize()) < 1E-3) {
            int next = token.getRemainingTimeout() + 80;
            if(next > 400) next = 400;
            token.setTimeout(next);
            rememberedTimeout = next;
        } else {
            if(token != null) {
                token.setTimeout(0);
            }
            rememberedTimeout = Math.min(400, rememberedTimeout + 80);
            EventHelperSpawnDeny.spawnDenyRegions.put(at, new TickTokenMap.SimpleTickToken<>(modified.getSize(), rememberedTimeout));
        }
        return true;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        return false;
    }

    @Override
    public void readFromNBT(CompoundNBT cmp) {
        super.readFromNBT(cmp);

        this.rememberedTimeout = cmp.getInt("rememberedTimeout");
    }

    @Override
    public void writeToNBT(CompoundNBT cmp) {
        super.writeToNBT(cmp);

        cmp.putInt("rememberedTimeout", this.rememberedTimeout);
    }

    private static class LucernaConfig extends Config {

        public LucernaConfig() {
            super("lucerna", 32D, 64D);
        }
    }
}
