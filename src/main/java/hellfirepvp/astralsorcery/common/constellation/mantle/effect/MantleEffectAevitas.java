/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.LogicalSide;

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

        World world = player.getEntityWorld();
        BlockPos playerPos = player.getPosition();
        for (int xx = -3; xx <= 3; xx++) {
            for (int zz = -3; zz <= 3; zz++) {
                BlockPos at = playerPos.add(xx, -1, zz);
                MiscUtils.executeWithChunk(world, at, () -> {
                    if (world.getBlockState(at).isAir(world, at) && AlignmentChargeHandler.INSTANCE.hasCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerBlock.get())) {
                        if (world.setBlockState(at, BlocksAS.VANISHING.getDefaultState())) {
                            AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerBlock.get(), false);
                        }
                    }
                });
            }
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
                    stats.addStats(CONFIG.foodPerCycle.get().intValue() / 2, CONFIG.foodPerCycle.get().floatValue());
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
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    public static class AevitasConfig extends Config {

        private final int defaultHealChance = 20;
        private final int defaultFeedChance = 40;
        private final double defaultHealthPerCycle = 0.25F;
        private final double defaultFoodPerCycle = 0.5F;

        private final int defaultChargeCostPerBlock = 2;
        private final int defaultChargeCostPerHeal = 15;
        private final int defaultChargeCostPerFood = 15;

        public ForgeConfigSpec.IntValue healChance;
        public ForgeConfigSpec.IntValue feedChance;
        public ForgeConfigSpec.DoubleValue healthPerCycle;
        public ForgeConfigSpec.DoubleValue foodPerCycle;

        public ForgeConfigSpec.IntValue chargeCostPerBlock;
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

            this.chargeCostPerBlock = cfgBuilder
                    .comment("Set the amount alignment charge consumed per block placed")
                    .translation(translationKey("chargeCostPerBlock"))
                    .defineInRange("chargeCostPerBlock", this.defaultChargeCostPerBlock, 0, 1000);
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
}
