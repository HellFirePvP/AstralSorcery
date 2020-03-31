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
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectArmara
 * Created by HellFirePvP
 * Date: 29.03.2020 / 13:53
 */
public class MantleEffectArmara extends MantleEffect {

    public static ArmaraConfig CONFIG = new ArmaraConfig();

    public MantleEffectArmara() {
        super(ConstellationsAS.armara);
    }

    @Override
    protected void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);

        bus.addListener(EventPriority.HIGH, this::onHurt);
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    @Override
    protected void tickServer(PlayerEntity player) {
        super.tickServer(player);

        if (getCurrentImmunityStacks(player) >= CONFIG.immunityStacks.get()) {
            setCurrentImmunityRechargeTick(player, CONFIG.immunityRechargeTicks.get());
            return;
        }
        int tick = getCurrentImmunityRechargeTick(player);
        tick--;
        if (tick <= 0) {
            setCurrentImmunityRechargeTick(player, CONFIG.immunityRechargeTicks.get());
            setCurrentImmunityStacks(player, getCurrentImmunityStacks(player) + 1);
        } else {
            setCurrentImmunityRechargeTick(player, tick);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        this.playCapeSparkles(player, 0.15F);

        Vector3 at = Vector3.atEntityCorner(player);
        at.addY(player.getHeight() / 2F);

        int stacks = getCurrentImmunityStacks(player);
        if (stacks > 0) {
            Random sRand = new Random(player.getUniqueID().hashCode());
            for (int i = 0; i < stacks; i++) {
                Vector3 axis = Vector3.random(sRand);
                axis.setX(axis.getX() * 0.35F);
                axis.setZ(axis.getZ() * 0.35F);
                Vector3 perpEffect = axis.clone().perpendicular();

                float scale = rand.nextFloat() * 0.2F + 0.2F;
                int ticksPerCircle = 80 + sRand.nextInt(50);
                int tick = (player.ticksExisted) % ticksPerCircle;

                Vector3 pos = perpEffect.normalize().multiply(sRand.nextFloat() * 0.4F + 0.9F)
                        .rotate(Math.toRadians(360 * ((float) (tick) / (float) (ticksPerCircle))), axis)
                        .add(at);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .color(VFXColorFunction.constant(ColorsAS.MANTLE_ARMARA_STACKS))
                        .setScaleMultiplier(scale)
                        .setMaxAge(20 + rand.nextInt(20));

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .color(VFXColorFunction.WHITE)
                        .setScaleMultiplier(scale * 0.4F)
                        .setMaxAge(10 + rand.nextInt(10));
            }
        }
    }

    private void onHurt(LivingHurtEvent event) {
        World world = event.getEntity().getEntityWorld();
        LivingEntity hurt = event.getEntityLiving();

        if (world.isRemote()) {
            return;
        }
        MantleEffectArmara armara = ItemMantle.getEffect(hurt, ConstellationsAS.armara);
        if (armara != null && this.shouldPreventDamage(hurt, event.getSource(), false)) {
            event.setCanceled(true);
        }
    }

    private boolean shouldPreventDamage(LivingEntity hurt, DamageSource source, boolean simulate) {
        if (source.canHarmInCreative()) {
            return false;
        }
        int stacks = getCurrentImmunityStacks(hurt);
        if (stacks <= 0) {
            return false;
        }
        if (!simulate) {
            stacks--;
            setCurrentImmunityStacks(hurt, stacks);
        }
        return true;
    }

    private int getCurrentImmunityRechargeTick(LivingEntity entity) {
        return this.getData(entity).getInt("ITick");
    }

    private void setCurrentImmunityRechargeTick(LivingEntity entity, int tick) {
        this.getData(entity).putInt("ITick", tick);
    }

    private int getCurrentImmunityStacks(LivingEntity entity) {
        return this.getData(entity).getInt("IStacks");
    }

    private void setCurrentImmunityStacks(LivingEntity entity, int stacks) {
        this.getData(entity).putInt("IStacks", stacks);
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    public static class ArmaraConfig extends Config {

        private final int defaultImmunityStacks = 300;
        private final int defaultImmunityRechargeTicks = 300;

        public ForgeConfigSpec.IntValue immunityStacks;
        public ForgeConfigSpec.IntValue immunityRechargeTicks;

        public ArmaraConfig() {
            super("armara");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.immunityStacks = cfgBuilder
                    .comment("Set the max amount of immunity stacks.")
                    .translation(translationKey("immunityStacks"))
                    .defineInRange("immunityStacks", this.defaultImmunityStacks, 0, 10);
            this.immunityRechargeTicks = cfgBuilder
                    .comment("Sets the amount of ticks between immunity stack recharges.")
                    .translation(translationKey("immunityRechargeTicks"))
                    .defineInRange("immunityRechargeTicks", this.defaultImmunityRechargeTicks, 20, 1_000_000);
        }
    }
}
