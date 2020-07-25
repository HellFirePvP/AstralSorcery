/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.source.orbital.FXOrbitalPelotrio;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.CEffectAbstractList;
import hellfirepvp.astralsorcery.common.constellation.effect.base.ListEntries;
import hellfirepvp.astralsorcery.common.data.config.registry.EntityTransmutationRegistry;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPelotrio
 * Created by HellFirePvP
 * Date: 01.02.2020 / 14:44
 */
public class CEffectPelotrio extends CEffectAbstractList<ListEntries.EntitySpawnEntry> {

    private static AxisAlignedBB PROXIMITY_BOX = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public static PelotrioConfig CONFIG = new PelotrioConfig();

    public CEffectPelotrio(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.pelotrio, CONFIG.maxAmount.get(),
                (world, pos, state) -> ListEntries.EntitySpawnEntry.createEntry(world, pos, SpawnReason.SPAWNER) != null);
    }

    @Nullable
    @Override
    public ListEntries.EntitySpawnEntry recreateElement(CompoundNBT tag, BlockPos pos) {
        return null;
    }

    @Nullable
    @Override
    public ListEntries.EntitySpawnEntry createElement(World world, BlockPos pos) {
        return ListEntries.EntitySpawnEntry.createEntry(world, pos, SpawnReason.SPAWNER);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        ConstellationEffectProperties prop = this.createProperties(pedestal.getMirrorCount());

        if (rand.nextFloat() < 0.2F) {
            Vector3 at = Vector3.random().normalize().multiply(rand.nextFloat() * prop.getSize()).add(pos).add(0.5, 0.5, 0.5);

            EffectHelper.spawnSource(new FXOrbitalPelotrio(at)
                    .setOrbitAxis(Vector3.random())
                    .setOrbitRadius(0.8 + rand.nextFloat() * 0.7)
                    .setTicksPerRotation(20 + rand.nextInt(20)));
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        if (!(world instanceof ServerWorld)) {
            return false;
        }

        boolean update = false;

        List<LivingEntity> nearbyEntities = world.getEntitiesWithinAABB(LivingEntity.class, PROXIMITY_BOX.offset(pos).grow(properties.getSize()));

        if (properties.isCorrupted()) {
            for (LivingEntity entity : nearbyEntities) {
                if (entity != null && entity.isAlive() && rand.nextInt(300) == 0) {
                    LivingEntity transmuted = EntityTransmutationRegistry.INSTANCE.transmuteEntity((ServerWorld) world, entity);
                    if (transmuted != null) {
                        transmuted.addPotionEffect(new EffectInstance(EffectsAS.EFFECT_DROP_MODIFIER, Integer.MAX_VALUE, 2));
                        AstralSorcery.getProxy().scheduleDelayed(() -> world.addEntity(transmuted));
                        update = true;
                    }
                }
            }
            return update;
        }

        ListEntries.EntitySpawnEntry entry = this.getRandomElementChanced();
        if (entry != null) {
            int count = entry.getCounter();
            count++;
            entry.setCounter(count);
            if (count >= 40) {
                entry.spawn(world, SpawnReason.SPAWNER);
                removeElement(entry);
            }

            update = true;
        }

        if (nearbyEntities.size() > CONFIG.proximityAmount.get()) {
            return update; //Flood prevention
        }

        if (rand.nextFloat() < CONFIG.spawnChance.get()) {
            if (findNewPosition(world, pos, properties) != null) {
                update = true;
            }
        }
        return update;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    private static class PelotrioConfig extends CountConfig {

        private final double defaultSpawnChance = 0.05D;
        private final int defaultProximityAmount = 24;

        public ForgeConfigSpec.DoubleValue spawnChance;
        public ForgeConfigSpec.IntValue proximityAmount;

        public PelotrioConfig() {
            super("pelotrio", 12D, 0D, 5);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.spawnChance = cfgBuilder
                    .comment("Defines the per-tick chance that a new position for a entity-spawn will be searched for.")
                    .translation(translationKey("spawnChance"))
                    .defineInRange("spawnChance", this.defaultSpawnChance, 0, 1);

            this.proximityAmount = cfgBuilder
                    .comment("Defines the threshold at which the ritual will stop spawning mobs. If there are more or equal amount of mobs near this ritual, the ritual will not spawn more mobs. Mainly to reduce potential server lag.")
                    .translation(translationKey("proximityAmount"))
                    .defineInRange("proximityAmount", this.defaultProximityAmount, 0, 256);
        }
    }
}
