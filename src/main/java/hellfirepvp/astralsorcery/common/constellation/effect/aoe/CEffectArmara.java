/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.source.orbital.FXOrbitalArmara;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.ConstellationEffectEntityCollect;
import hellfirepvp.astralsorcery.common.data.config.registry.TechnicalEntityRegistry;
import hellfirepvp.astralsorcery.common.event.helper.EventHelperSpawnDeny;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.block.WorldBlockPos;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tick.TickTokenMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectArmara
 * Created by HellFirePvP
 * Date: 28.07.2019 / 09:07
 */
public class CEffectArmara extends ConstellationEffectEntityCollect<LivingEntity> {

    public static ArmaraConfig CONFIG = new ArmaraConfig();

    private int rememberedTimeout = 0;

    public CEffectArmara(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.armara, LivingEntity.class, (e) -> e.isAlive() && TechnicalEntityRegistry.INSTANCE.canAffect(e));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        if (pedestal.getTicksExisted() % 20 == 0) {
            EffectHelper.spawnSource(new FXOrbitalArmara(new Vector3(pos).add(0.5, 0.5, 0.5))
                    .setOrbitRadius(0.8 + rand.nextFloat() * 0.7)
                    .setOrbitAxis(Vector3.RotAxis.Y_AXIS)
                    .setTicksPerRotation(20 + rand.nextInt(20)));
        }

        ConstellationEffectProperties prop = this.createProperties(pedestal.getMirrorCount());

        ItemStack socket = pedestal.getCurrentCrystal();
        if (!socket.isEmpty() && socket.getItem() instanceof ItemAttunedCrystalBase) {
            IMinorConstellation trait = ((ItemAttunedCrystalBase) socket.getItem()).getTraitConstellation(socket);
            prop.modify(trait);
            if (prop.isCorrupted()) {
                return;
            }
        }

        PlayerEntity owner = pedestal.getOwner();
        List<Entity> projectiles = world.getEntitiesWithinAABB(Entity.class, BOX.offset(pos).grow(prop.getSize()));
        if (!projectiles.isEmpty()) {
            for (Entity e : projectiles) {
                if (e.isAlive() && TechnicalEntityRegistry.INSTANCE.canAffect(e)) {
                    if (e instanceof IProjectile) {
                        double xRatio = (pos.getX() + 0.5) - e.getPosX();
                        double zRatio = (pos.getZ() + 0.5) - e.getPosZ();
                        float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
                        Vector3 motion = new Vector3(e.getMotion());
                        motion.multiply(new Vector3(0.5, 1, 0.5));
                        motion.subtract(xRatio / f * 0.4, 0, zRatio / f * 0.4);
                        ((IProjectile) e).shoot(motion.getX(), motion.getY(), motion.getZ(), 1.5F, 0F);
                    } else if (e instanceof MobEntity) {
                        ((LivingEntity) e).knockBack(owner == null ? e : owner, 0.4F, (pos.getX() + 0.5) - e.getPosX(), (pos.getZ() + 0.5) - e.getPosZ());
                    }
                }
            }
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        int toAdd = 2 + rand.nextInt(5);
        WorldBlockPos at = WorldBlockPos.wrapServer(world, pos);
        TickTokenMap.SimpleTickToken<Double> token = EventHelperSpawnDeny.spawnDenyRegions.get(at);
        if (token != null) {
            int next = token.getRemainingTimeout() + toAdd;
            if (next > 400) next = 400;
            token.setTimeout(next);
            rememberedTimeout = next;
        } else {
            rememberedTimeout = Math.min(400, rememberedTimeout + toAdd);
            EventHelperSpawnDeny.spawnDenyRegions.put(at, new TickTokenMap.SimpleTickToken<>(properties.getSize(), rememberedTimeout));
        }

        PlayerEntity owner = this.getOwningPlayerInWorld(world, pos);

        if (!properties.isCorrupted()) {
            List<Entity> projectiles = world.getEntitiesWithinAABB(Entity.class, BOX.offset(pos).grow(properties.getSize()));
            if (!projectiles.isEmpty()) {
                for (Entity e : projectiles) {
                    if (e.isAlive() && TechnicalEntityRegistry.INSTANCE.canAffect(e)) {
                        if (e instanceof IProjectile) {
                            double xRatio = (pos.getX() + 0.5) - e.getPosX();
                            double zRatio = (pos.getZ() + 0.5) - e.getPosZ();
                            float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
                            Vector3 motion = new Vector3(e.getMotion());
                            motion.multiply(new Vector3(0.5, 1, 0.5));
                            motion.subtract(xRatio / f * 0.4, 0, zRatio / f * 0.4);
                            ((IProjectile) e).shoot(motion.getX(), motion.getY(), motion.getZ(), 1.5F, 0F);
                        } else if (e instanceof MobEntity) {
                            ((LivingEntity) e).knockBack(owner == null ? e : owner, 0.4F, (pos.getX() + 0.5) - e.getPosX(), (pos.getZ() + 0.5) - e.getPosZ());
                        }
                    }
                }
            }
        }

        int potionAmplifier = CONFIG.potionAmplifier.get();
        List<LivingEntity> entities = this.collectEntities(world, pos, properties);
        for (LivingEntity entity : entities) {
            if (entity.isAlive() && (entity instanceof MobEntity || entity instanceof PlayerEntity)) {
                if (properties.isCorrupted()) {
                    if (entity instanceof PlayerEntity) {
                        continue;
                    }

                    entity.addPotionEffect(new EffectInstance(Effects.SPEED, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new EffectInstance(Effects.REGENERATION, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 100, potionAmplifier + 2));
                    entity.addPotionEffect(new EffectInstance(Effects.STRENGTH, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new EffectInstance(Effects.HASTE, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new EffectInstance(EffectsAS.EFFECT_DROP_MODIFIER, 100, 6));
                } else {
                    entity.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 30, Math.min(potionAmplifier, 3)));
                    if (entity instanceof PlayerEntity) {
                        entity.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 30, potionAmplifier));
                    }
                }
            }
        }

        return true;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
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

    private static class ArmaraConfig extends Config {

        private final int defaultPotionAmplifier = 1;

        public ForgeConfigSpec.IntValue potionAmplifier;

        public ArmaraConfig() {
            super("armara", 24, 12);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.potionAmplifier = cfgBuilder
                    .comment("Set the amplifier for the potion effects this ritual provides.")
                    .translation(translationKey("potionAmplifier"))
                    .defineInRange("potionAmplifier", this.defaultPotionAmplifier, 0, 10);
        }
    }
}
