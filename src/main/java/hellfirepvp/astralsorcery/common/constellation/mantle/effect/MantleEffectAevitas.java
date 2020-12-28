/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.collision.CustomCollisionHandler;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.LogicalSide;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectAevitas
 * Created by HellFirePvP
 * Date: 11.03.2020 / 21:31
 */
public class MantleEffectAevitas extends MantleEffect {

    public static AevitasConfig CONFIG = new AevitasConfig();

    public MantleEffectAevitas() {
        super(ConstellationsAS.aevitas);
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    @Override
    protected void tickServer(PlayerEntity player) {
        super.tickServer(player);

        if (isStandingOnAir(player)) {
            AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerTravelTick.get().floatValue(), false);
        }

        int healChance = CONFIG.healChance.get();
        int foodChance = CONFIG.feedChance.get();
        if (healChance > 0 && rand.nextInt(healChance) == 0) {
            player.heal(CONFIG.healthPerCycle.get().floatValue());
        }
        if (foodChance > 0 && rand.nextInt(foodChance) == 0) {
            FoodStats stats = player.getFoodStats();
            if (stats.getFoodLevel() < 20 || stats.getSaturationLevel() < 5) {
                if (AlignmentChargeHandler.INSTANCE.hasCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerFood.get())) {
                    stats.addStats(CONFIG.foodPerCycle.get().intValue(), 0.5F);
                    AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerFood.get(), false);
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        this.playCapeSparkles(player, 0.1F);

        if (isStandingOnAir(player)) {
            Vector3 center = Vector3.atEntityCorner(player).addY(0.15F);
            for (int i = 0; i < 5; i++) {
                Vector3 offset = Vector3.random().setY(0).normalize()
                        .multiply(rand.nextFloat() * 5).addY(rand.nextFloat() * -0.4F)
                        .add(center);
                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(offset)
                        .setScaleMultiplier(0.25F + rand.nextFloat() * 0.15F)
                        .alpha(VFXAlphaFunction.PYRAMID)
                        .setMotion(Vector3.random().normalize().multiply(0.004F))
                        .setMaxAge(45 + rand.nextInt(20));
                if (rand.nextInt(3) == 0) {
                    p.color(VFXColorFunction.WHITE);
                } else {
                    p.color(VFXColorFunction.constant(ColorsAS.RITUAL_CONSTELLATION_AEVITAS));
                }
            }
        }
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    public static boolean canSupportEffect(PlayerEntity player) {
        LogicalSide side = player.getEntityWorld().isRemote() ? LogicalSide.CLIENT : LogicalSide.SERVER;
        PlayerProgress progress = ResearchHelper.getProgress(player, side);
        return progress.doPerkAbilities() &&
                progress.hasConstellationDiscovered(ConstellationsAS.aevitas) &&
                AlignmentChargeHandler.INSTANCE.hasCharge(player, side, CONFIG.chargeCostPerTravelTick.get().floatValue());
    }

    public static boolean isStandingOnAir(Entity entity) {
        if (entity.isOnGround()) {
            World world = entity.getEntityWorld();
            BlockPos at = entity.getPosition().down();
            return world.getBlockState(at).isAir(world, at);
        }
        return false;
    }

    public static class AevitasConfig extends Config {

        private final int defaultHealChance = 80;
        private final int defaultFeedChance = 80;
        private final double defaultHealthPerCycle = 0.5F;
        private final double defaultFoodPerCycle = 1F;

        private final double defaultChargeCostPerTravelTick = 2.5F;
        private final int defaultChargeCostPerHeal = 100;
        private final int defaultChargeCostPerFood = 100;

        public ForgeConfigSpec.IntValue healChance;
        public ForgeConfigSpec.IntValue feedChance;
        public ForgeConfigSpec.DoubleValue healthPerCycle;
        public ForgeConfigSpec.DoubleValue foodPerCycle;

        public ForgeConfigSpec.DoubleValue chargeCostPerTravelTick;
        public ForgeConfigSpec.IntValue chargeCostPerHeal;
        public ForgeConfigSpec.IntValue chargeCostPerFood;

        public AevitasConfig() {
            super("aevitas");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.healChance = cfgBuilder
                    .comment("Set the chance of '1 in <this value>' per tick to do 1 heal cycle. Amount healed per cycle is determined by 'healthPerCycle' config option. Set to 0 to disable.")
                    .translation(translationKey("healChance"))
                    .defineInRange("healChance", this.defaultHealChance, 0, Integer.MAX_VALUE);
            this.feedChance = cfgBuilder
                    .comment("Set the chance of '1 in <this value>' per tick to do 1 food cycle. Amount fed per cycle is determined by 'foodPerCycle' config option. Set to 0 to disable.")
                    .translation(translationKey("feedChance"))
                    .defineInRange("feedChance", this.defaultFeedChance, 0, Integer.MAX_VALUE);
            this.healthPerCycle = cfgBuilder
                    .comment("Set the amount of health recovered by health cycle.")
                    .translation(translationKey("healthPerCycle"))
                    .defineInRange("healthPerCycle", this.defaultHealthPerCycle, 0.0, 100.0);
            this.foodPerCycle = cfgBuilder
                    .comment("Set the amount of food recovered by food cycle.")
                    .translation(translationKey("foodPerCycle"))
                    .defineInRange("foodPerCycle", this.defaultFoodPerCycle, 0.0, 100.0);

            this.chargeCostPerTravelTick = cfgBuilder
                    .comment("Set the amount alignment charge consumed per tick when walking/standing in the air")
                    .translation(translationKey("chargeCostPerTravelTick"))
                    .defineInRange("chargeCostPerTravelTick", this.defaultChargeCostPerTravelTick, 0.0, 100.0);
            this.chargeCostPerFood = cfgBuilder
                    .comment("Set the amount alignment charge consumed per feed-cycle")
                    .translation(translationKey("chargeCostPerFood"))
                    .defineInRange("chargeCostPerFood", this.defaultChargeCostPerFood, 0, 1000);
            this.chargeCostPerHeal = cfgBuilder
                    .comment("Set the amount alignment charge consumed per heal-cycle")
                    .translation(translationKey("chargeCostPerHeal"))
                    .defineInRange("chargeCostPerHeal", this.defaultChargeCostPerHeal, 0, 1000);
        }
    }

    public static class PlayerWalkableAir implements CustomCollisionHandler {

        private static final AxisAlignedBB FULL_BOX = new AxisAlignedBB(BlockPos.ZERO);

        @Override
        public boolean shouldAddCollisionFor(Entity entity) {
            if (!(entity instanceof PlayerEntity) || ((PlayerEntity) entity).abilities.isFlying) {
                return false;
            }
            return ItemMantle.getEffect((LivingEntity) entity, ConstellationsAS.aevitas) != null &&
                    canSupportEffect((PlayerEntity) entity);
        }

        @Override
        public void addCollision(Entity entity, AxisAlignedBB testBox, List<AxisAlignedBB> additionalCollision) {
            int yOffset = 1;
            if (entity.getPose() == Pose.CROUCHING && isStandingOnAir(entity)) {
                yOffset = 2;
            }
            additionalCollision.add(FULL_BOX.offset(entity.getPosX(), Math.floor(entity.getPosY()) - yOffset, entity.getPosZ()));
        }
    }
}
