/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectStatus;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectVicio
 * Created by HellFirePvP
 * Date: 11.06.2019 / 20:46
 */
public class CEffectVicio extends ConstellationEffect implements ConstellationEffectStatus {

    private static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public CEffectVicio(@Nullable ILocatable origin) {
        super(origin, ConstellationsAS.vicio);
    }

    @Override
    public ConstellationEffectProperties createProperties(int mirrors) {
        return new ConstellationEffectProperties(VicioConfig.RANGE.get() + mirrors * VicioConfig.RANGE_PER_LENS.get());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
    }

    @Override
    public boolean runEffect(World world, BlockPos pos, int mirrorAmount, ConstellationEffectProperties modified, @Nullable IMinorConstellation possibleTraitEffect) {
        boolean foundPlayer = false;
        double range = modified.getSize();
        if (modified.isCorrupted()) {
            List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, BOX.offset(pos).grow(range));
            for (LivingEntity entity : entities) {
                if (entity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity pl = (ServerPlayerEntity) entity;
                    if (pl.interactionManager.getGameType().isSurvivalOrAdventure()) {
                        boolean prev = pl.abilities.allowFlying;
                        pl.abilities.allowFlying = false;
                        if (prev) {
                            pl.sendPlayerAbilities();
                        }
                    }
                }
                foundPlayer = true;
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 9));
                entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 9));
            }
        } else {
            List<ServerPlayerEntity> entities = world.getEntitiesWithinAABB(ServerPlayerEntity.class, BOX.offset(pos).grow(range));
            for (ServerPlayerEntity pl : entities) {
                if (EventHandlerEntity.ritualFlight.setOrAddTimeout(40, pl)) {
                    boolean prev = pl.abilities.allowFlying;
                    pl.abilities.allowFlying = true;
                    foundPlayer = true;
                    if (!prev) {
                        pl.sendPlayerAbilities();
                    }
                }
            }
        }
        return foundPlayer;
    }

    public static class VicioConfig extends Config {

        public static ForgeConfigSpec.DoubleValue RANGE;
        public static ForgeConfigSpec.DoubleValue RANGE_PER_LENS;

        public VicioConfig() {
            super("vicio");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            RANGE = cfgBuilder
                    .comment("Defines the radius (in blocks) in which the ritual will allow the players to fly in.")
                    .translation(translationKey("range"))
                    .defineInRange("range", 24D, 1, 512);

            RANGE_PER_LENS = cfgBuilder
                    .comment("Defines the increase in radius the ritual will get per active lens enhancing the ritual.")
                    .translation(translationKey("rangePerLens"))
                    .defineInRange("rangePerLens", 16D, 1, 128);
        }

    }

}
