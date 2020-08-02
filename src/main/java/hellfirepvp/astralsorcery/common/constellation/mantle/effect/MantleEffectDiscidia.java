/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectDiscidia
 * Created by HellFirePvP
 * Date: 29.03.2020 / 15:16
 */
public class MantleEffectDiscidia extends MantleEffect {

    public static DiscidiaConfig CONFIG = new DiscidiaConfig();

    public MantleEffectDiscidia() {
        super(ConstellationsAS.discidia);
    }

    @Override
    protected void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);

        bus.addListener(this::onAttack);
        bus.addListener(EventPriority.HIGHEST, this::onHurt);
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        float effChance = 0.1F;
        if (this.getLastAttackDamage(player) > 0) {
            effChance = 0.2F;
        }
        this.playCapeSparkles(player, effChance);
    }

    private void onAttack(LivingAttackEvent event) {
        LivingEntity attacked = event.getEntityLiving();
        World world = attacked.getEntityWorld();
        DamageSource source = event.getSource();
        Entity attacker = source.getTrueSource();

        if (world.isRemote()) {
            return;
        }
        if (attacker instanceof PlayerEntity) {
            if (attacked instanceof ServerPlayerEntity && MiscUtils.isPlayerFakeMP((ServerPlayerEntity) attacked)) {
                return;
            }
            PlayerEntity player = (PlayerEntity) attacker;

            MantleEffectDiscidia eff = ItemMantle.getEffect(player, ConstellationsAS.discidia);
            if (eff != null) {
                EventFlags.MANTLE_DISCIDIA_ADDED.executeWithFlag(() -> {
                    float added = this.getLastAttackDamage(player);

                    if (added > 0.1F && AlignmentChargeHandler.INSTANCE.hasCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerAttack.get())) {
                        DamageUtil.attackEntityFrom(attacked, CommonProxy.DAMAGE_SOURCE_STELLAR, added / 2F);
                        DamageUtil.attackEntityFrom(attacked, DamageSource.causePlayerDamage(player), added / 2F, player);

                        AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerAttack.get(), false);
                    }
                });
            }
        }
    }

    private void onHurt(LivingHurtEvent event) {
        World world = event.getEntity().getEntityWorld();
        LivingEntity hurt = event.getEntityLiving();

        if (world.isRemote()) {
            return;
        }
        MantleEffectDiscidia armara = ItemMantle.getEffect(hurt, ConstellationsAS.discidia);
        if (armara != null) {
            this.writeLastAttackDamage(hurt, event.getAmount());
        }
    }

    public void writeLastAttackDamage(LivingEntity entity, float dmgIn) {
        this.getData(entity).putFloat("lastAttack", dmgIn);
    }

    public float getLastAttackDamage(LivingEntity entity) {
        return this.getData(entity).getFloat("lastAttack") * CONFIG.damageMultiplier.get().floatValue();
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    public static class DiscidiaConfig extends Config {

        private final double defaultDamageMultiplier = 1.5F;

        private final int defaultChargeCostPerAttack = 40;

        public ForgeConfigSpec.DoubleValue damageMultiplier;

        public ForgeConfigSpec.IntValue chargeCostPerAttack;

        public DiscidiaConfig() {
            super("discidia");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.damageMultiplier = cfgBuilder
                    .comment("Sets the multiplier for how much of the received damage is converted into additional damage.")
                    .translation(translationKey("damageMultiplier"))
                    .defineInRange("damageMultiplier", this.defaultDamageMultiplier, 0.0, 100.0);

            this.chargeCostPerAttack = cfgBuilder
                    .comment("Set the amount alignment charge consumed per attack enhanced by the mantle")
                    .translation(translationKey("chargeCostPerAttack"))
                    .defineInRange("chargeCostPerAttack", this.defaultChargeCostPerAttack, 0, 1000);
        }
    }
}
