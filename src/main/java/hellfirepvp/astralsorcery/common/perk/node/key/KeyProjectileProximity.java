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
 * Class: KeyProjectileProximity
 * Created by HellFirePvP
 * Date: 31.08.2019 / 21:59
 */
public class KeyProjectileProximity extends KeyPerk {

    private static final float defaultCapDistance = 100;
    private static final float defaultMaxAdditionalMultiplier = 0.75F;

    public static final Config CONFIG = new Config("key.proj_proximity");

    public KeyProjectileProximity(ResourceLocation name, float x, float y) {
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
                if (prog.hasPerkEffect(this)) {
                    float added = (float) this.applyMultiplierD(CONFIG.maxAdditionalMultiplier.get());
                    added *= PerkAttributeHelper.getOrCreateMap(player, side).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);

                    float capDstSq = CONFIG.capDistance.get().floatValue();
                    float dst = -(((float) (player.getDistanceSq(event.getEntityLiving()))) - capDstSq);
                    dst /= capDstSq;
                    if (dst < 0) {
                        dst /= 10; //To make it drop a bit slower though... like. that damage reduction is... not fun :P
                    }
                    dst = Math.max(0, 1 + dst);
                    added *= dst;

                    float amt = event.getAmount();
                    amt *= Math.max(0, added); //Might become negative if too far away; prevent that :P
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
                    .comment("Defines the distance at which no additional damage is awarded.")
                    .translation(translationKey("capDistance"))
                    .defineInRange("capDistance", defaultCapDistance, 4, 65_536);

            this.maxAdditionalMultiplier = cfgBuilder
                    .comment("Defines the maximum multiplier that can be reached if the distance when hitting something with projectiles is basically nothing.")
                    .translation(translationKey("maxAdditionalMultiplier"))
                    .defineInRange("maxAdditionalMultiplier", defaultMaxAdditionalMultiplier, 0.05F, 5.0F);
        }
    }
}
