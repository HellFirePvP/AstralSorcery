/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectFornax
 * Created by HellFirePvP
 * Date: 23.02.2020 / 08:15
 */
public class MantleEffectFornax extends MantleEffect {

    public static FornaxConfig CONFIG = new FornaxConfig();

    public MantleEffectFornax() {
        super(ConstellationsAS.fornax);
    }

    @Override
    protected void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);
        bus.addListener(this::onHurt);
    }

    private void onHurt(LivingHurtEvent event) {
        World world = event.getEntityLiving().getEntityWorld();
        if (world.isRemote()) {
            return;
        }

        LivingEntity attacked = event.getEntityLiving();
        Entity attacker = event.getSource().getTrueSource();
        if (attacker instanceof LivingEntity) {
            if (attacked instanceof ServerPlayerEntity && MiscUtils.isPlayerFakeMP((ServerPlayerEntity) attacked)) {
                return;
            }

            if (attacker.isBurning() && ItemMantle.getEffect((LivingEntity) attacker, ConstellationsAS.fornax) != null) {
                event.setAmount((float) (event.getAmount() * CONFIG.damageIncreaseInFire.get()));
            }
        }

        if (event.getSource().isFireDamage() && ItemMantle.getEffect(attacked, ConstellationsAS.fornax) != null) {
            if (CONFIG.healPercentFromFireDamage.get() > 0) {
                attacked.heal((float) (event.getAmount() * CONFIG.healPercentFromFireDamage.get()));
            }

            event.setAmount((float) (event.getAmount() * CONFIG.damageReductionInFire.get()));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        if (player.isBurning()) {
            this.playCapeSparkles(player, 0.75F);
        } else {
            this.playCapeSparkles(player, 0.25F);
        }
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    public static class FornaxConfig extends Config {

        private final double defaultDamageReductionInFire = 0.4F;
        private final double defaultDamageIncreaseInFire = 1.6F;
        private final double defaultHealPercentFromFireDamage = 0.6F;

        public ForgeConfigSpec.DoubleValue damageReductionInFire;
        public ForgeConfigSpec.DoubleValue damageIncreaseInFire;
        public ForgeConfigSpec.DoubleValue healPercentFromFireDamage;

        public FornaxConfig() {
            super("fornax");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.damageReductionInFire = cfgBuilder
                    .comment("Sets the multiplier for how much damage you take from fire damage while wearing a fornax mantle.")
                    .translation(translationKey("damageReductionInFire"))
                    .defineInRange("damageReductionInFire", this.defaultDamageReductionInFire, 0.0, 1.0);
            this.damageIncreaseInFire = cfgBuilder
                    .comment("Sets the multiplier for how much more damage the player deals when ignited while wearing a fornax mantle.")
                    .translation(translationKey("damageIncreaseInFire"))
                    .defineInRange("damageIncreaseInFire", this.defaultDamageIncreaseInFire, 1.0, 3.0);
            this.healPercentFromFireDamage = cfgBuilder
                    .comment("Sets the multiplier for how much healing the player receives from the original damage when being hit by fire damage.")
                    .translation(translationKey("healPercentFromFireDamage"))
                    .defineInRange("healPercentFromFireDamage", this.defaultHealPercentFromFireDamage, 0.0, 3.0);
        }
    }
}
