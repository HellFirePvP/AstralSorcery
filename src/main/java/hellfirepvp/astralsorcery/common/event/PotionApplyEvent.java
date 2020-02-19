/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionApplyEvent
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:37
 */
public class PotionApplyEvent {

    public static class New extends LivingEvent {

        private final EffectInstance applied;

        public New(LivingEntity entity, EffectInstance applied) {
            super(entity);
            this.applied = applied;
        }

        public EffectInstance getPotionEffect() {
            return applied;
        }
    }

    public static class Changed extends LivingEvent {

        private final EffectInstance addedEffect, newCombinedEffect;

        public Changed(LivingEntity entity, EffectInstance newlyAddedEffect, EffectInstance newCombinedEffect) {
            super(entity);
            this.addedEffect = newlyAddedEffect;
            this.newCombinedEffect = newCombinedEffect;
        }

        public EffectInstance getAddedEffect() {
            return addedEffect;
        }

        public EffectInstance getNewCombinedEffect() {
            return newCombinedEffect;
        }
    }
}
