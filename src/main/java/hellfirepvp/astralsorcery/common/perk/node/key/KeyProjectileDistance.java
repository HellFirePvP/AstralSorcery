/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyProjectileDistance
 * Created by HellFirePvP
 * Date: 31.08.2019 / 21:53
 */
public class KeyProjectileDistance extends KeyPerk {

    private static final float defaultCapDistance = 6400;
    private static final float defaultMaxAdditionalMultiplier = 0.75F;

    public static final Config CONFIG = new Config("key.proj_distance");

    public KeyProjectileDistance(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    @Override
    public void attachListeners(LogicalSide side, IEventBus bus) {
        super.attachListeners(side, bus);

        bus.addListener(EventPriority.HIGH, this::onProjDamage);
    }

    private void onProjDamage(LivingHurtEvent event) {
        if (event.getSource().isProjectile()) {
            DamageSource source = event.getSource();
            if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) source.getTrueSource();
                LogicalSide side = this.getSide(player);
                PlayerProgress prog = ResearchHelper.getProgress(player, side);
                if (prog.getPerkData().hasPerkEffect(this)) {
                    float added = CONFIG.maxAdditionalMultiplier.get().floatValue();
                    added *= PerkAttributeHelper.getOrCreateMap(player, side).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);

                    float capDstSq = (CONFIG.capDistance.get().floatValue());
                    float mul = ((float) (player.getDistanceSq(event.getEntityLiving()))) / capDstSq;
                    added *= (mul > 1 ? 1 : mul);

                    float amt = event.getAmount();
                    amt *= (1 + added);
                    event.setAmount(amt);
                }
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue capDistance;
        private ForgeConfigSpec.DoubleValue maxAdditionalMultiplier;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.capDistance = cfgBuilder
                    .comment("Defines the distance that must be reached to achieve the maximum damage multiplier")
                    .translation(translationKey("capDistance"))
                    .defineInRange("capDistance", defaultCapDistance, 100, 65_536);

            this.maxAdditionalMultiplier = cfgBuilder
                    .comment("Defines the maximum multiplier that can be reached if the 'capDistance' is reached or surpassed when hitting something")
                    .translation(translationKey("maxAdditionalMultiplier"))
                    .defineInRange("maxAdditionalMultiplier", defaultMaxAdditionalMultiplier, 0.05F, 5.0F);
        }
    }
}
