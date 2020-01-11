/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectProperties
 * Created by HellFirePvP
 * Date: 30.05.2019 / 00:07
 */
public class EffectProperties<T extends EntityVisualFX> {

    private final BatchRenderContext<T> ctx;
    private static List<Consumer<EntityVisualFX>> specialEffects = Lists.newArrayList();

    private EffectType type = null;
    private UUID owner = null;
    private Vec3i position = Vec3i.NULL_VECTOR;
    private boolean ignoreLimit = false;

    public EffectProperties(BatchRenderContext<T> ctx) {
        this.ctx = ctx;
    }

    // What player this effects belongs to, if any.
    public <I extends EffectProperties<T>> I setOwner(@Nullable UUID owner) {
        this.owner = owner;
        return (I) this;
    }

    // What type this effect is. As a means of classification/grouping
    public <I extends EffectProperties<T>> I setType(@Nullable EffectType type) {
        this.type = type;
        return (I) this;
    }

    // The position the effect originates from, if any.
    public <I extends EffectProperties<T>> I setPosition(@Nonnull Vec3i position) {
        this.position = position;
        return (I) this;
    }

    // If this effect should ignore the spawn-limit soft cap on particles
    public <I extends EffectProperties<T>> I setIgnoreLimit(boolean ignoreLimit) {
        this.ignoreLimit = ignoreLimit;
        return (I) this;
    }

    public BatchRenderContext<T> getContext() {
        return ctx;
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    @Nullable
    public EffectType getType() {
        return type;
    }

    @Nonnull
    public Vec3i getPosition() {
        return position;
    }

    public boolean ignoresSpawnLimit() {
        return ignoreLimit;
    }

    // Method in favour of not using the event system to apply special effects.
    public void applySpecialEffects(EntityVisualFX effect) {
        specialEffects.forEach(s -> s.accept(effect));
    }

    public static void addSpecialEffect(Consumer<EntityVisualFX> effect) {
        specialEffects.add(effect);
    }

}
