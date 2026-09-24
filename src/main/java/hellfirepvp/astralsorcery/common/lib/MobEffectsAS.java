/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.Mods;
import hellfirepvp.astralsorcery.common.effect.BasicMobEffect;
import hellfirepvp.astralsorcery.common.effect.RevivalMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MobEffectsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class MobEffectsAS {

    public static final DeferredRegister<MobEffect> MOB_EFFECT_REGISTER =
            DeferredRegister.create(Registries.MOB_EFFECT, AstralSorcery.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> RAMPAGE =
            register("rampage", () -> new BasicMobEffect(MobEffectCategory.BENEFICIAL, 0xBB4400)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, Mods.MINECRAFT.key("effect.rampage.damage"), 0.2F, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, Mods.MINECRAFT.key("effect.rampage.speed"), 0.03F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, Mods.MINECRAFT.key("effect.rampage.movement"), 0.03F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final DeferredHolder<MobEffect, MobEffect> PHOENIX_BLESSING =
            register("phoenix_blessing", () -> new RevivalMobEffect(MobEffectCategory.BENEFICIAL, 0xFF9944));

    private static <T extends MobEffect> DeferredHolder<MobEffect, T> register(String name, Supplier<T> effectFn) {
        return MOB_EFFECT_REGISTER.register(name, effectFn);
    }
}
