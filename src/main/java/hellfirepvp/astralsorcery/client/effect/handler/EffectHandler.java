/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.source.FXSource;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import hellfirepvp.astralsorcery.common.util.Counter;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.order.DependencySorter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.*;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectHandler
 * Created by HellFirePvP
 * Date: 30.05.2019 / 00:09
 */
public final class EffectHandler {

    private static final Random STATIC_EFFECT_RAND = new Random();
    private static final EffectHandler INSTANCE = new EffectHandler();

    private boolean cleanRequested = false;
    private boolean acceptsNewEffects = false; //Wait for 1st tick to finish.
    private List<PendingEffect> toAddBuffer = Lists.newLinkedList();
    private List<FXSource<?, ?>> toAddSources = Lists.newLinkedList();

    private List<FXSource<?, ?>> activeSources = Lists.newLinkedList();
    private List<BatchRenderContext<?>> orderedEffects = null;
    private Map<BatchRenderContext<?>, List<PendingEffect>> effectMap = Maps.newHashMap();

    private EffectHandler() {}

    public static EffectHandler getInstance() {
        return INSTANCE;
    }

    public int getEffectCount() {
        final Counter c = new Counter(0);
        this.effectMap.values().stream().flatMap(Collection::stream).forEach(p -> c.increment());
        return c.getValue();
    }

    public void render(RenderWorldLastEvent event) {
        if (this.orderedEffects == null) {
            return;
        }

        float pTicks = event.getPartialTicks();
        this.acceptsNewEffects = false;

        for (BatchRenderContext<?> ctx : this.orderedEffects) {
            List<PendingEffect> effects = this.effectMap.get(ctx);
            if (!effects.isEmpty()) {
                BufferContext buf = ctx.prepare(pTicks);
                effects.forEach(p -> p.effect.render(ctx, buf, pTicks));
                ctx.draw(pTicks);
            }
        }
        TextureHelper.refreshTextureBind();

        this.acceptsNewEffects = true;
    }

    void tick() {
        if (this.cleanRequested) {
            this.toAddBuffer.clear();
            this.effectMap.values().forEach(List::clear);
            this.cleanRequested = false;
        }

        Entity rView = Minecraft.getInstance().getRenderViewEntity();
        if (rView == null) {
            rView = Minecraft.getInstance().player;
        }
        if (rView == null) {
            cleanUp();
            return;
        }

        if (this.orderedEffects == null) {
            this.orderedEffects = DependencySorter.getSorted(EffectTemplatesAS.LIST_ALL_RENDER_CONTEXT);
            this.orderedEffects.forEach(ctx -> this.effectMap.put(ctx, new LinkedList<>()));
        }

        this.acceptsNewEffects = false;

        this.effectMap.values().forEach(l -> {
            Iterator<PendingEffect> iterator = l.iterator();
            while (iterator.hasNext()) {
                PendingEffect effect = iterator.next();
                EntityComplexFX fx = effect.getEffect();

                fx.tick();
                if (fx.canRemove()) {
                    iterator.remove();
                    fx.flagAsRemoved();
                }
            }
        });

        Iterator<FXSource<?, ?>> iterator = this.activeSources.iterator();
        while (iterator.hasNext()) {
            FXSource<?, ?> src = iterator.next();

            src.tick();
            src.tickSpawnFX(new EffectRegistrar(src));
            if (src.canRemove()) {
                iterator.remove();
                src.flagAsRemoved();
            }
        }

        this.acceptsNewEffects = true;

        this.activeSources.addAll(this.toAddSources);
        this.toAddSources.clear();
        this.toAddBuffer.forEach(this::registerUnsafe);
        this.toAddBuffer.clear();
    }

    void queueSource(FXSource<?, ?> source) {
        if (this.acceptsNewEffects) {
            this.activeSources.add(source);
        } else {
            this.toAddSources.add(source);
        }
    }

    void queueParticle(PendingEffect pendingEffect) {
        if (this.acceptsNewEffects) {
            registerUnsafe(pendingEffect);
        } else {
            this.toAddBuffer.add(pendingEffect);
        }
    }

    private void registerUnsafe(PendingEffect pendingEffect) {
        if (!mayAcceptParticle(pendingEffect.getProperties())) {
            return;
        }
        EntityVisualFX effect = pendingEffect.getEffect();
        BatchRenderContext ctx = pendingEffect.getProperties().getContext();
        pendingEffect.getProperties().applySpecialEffects(effect);

        this.effectMap.get(ctx).add(pendingEffect);
    }

    private boolean mayAcceptParticle(EffectProperties<?> properties) {
        if (properties.ignoresSpawnLimit()) {
            return true;
        }
        RenderingConfig.ParticleAmount cfg = RenderingConfig.CONFIG.particleAmount.get();
        if (!Minecraft.isFancyGraphicsEnabled()) {
            cfg = cfg.less();
        }
        return cfg.shouldSpawn(STATIC_EFFECT_RAND);
    }

    public static void cleanUp() {
        getInstance().cleanRequested = true;
    }

    private static class EffectRegistrar<E extends EntityVisualFX> implements Function<Vector3, E> {

        private final FXSource<E, ?> source;

        private EffectRegistrar(FXSource<E, ?> source) {
            this.source = source;
        }

        @Override
        public E apply(Vector3 pos) {
            EffectHelper.Builder<E> prop = source.generateFX();
            E  fx = prop.getContext().makeParticle(pos);
            PendingEffect effect = new PendingEffect(fx, prop);
            getInstance().registerUnsafe(effect);
            return fx;
        }
    }

    static class PendingEffect {

        private final EntityVisualFX effect;
        private final EffectProperties<?> runProperties;

        PendingEffect(EntityVisualFX effect, EffectProperties<?> runProperties) {
            this.effect = effect;
            this.runProperties = runProperties;
        }

        EffectProperties<?> getProperties() {
            return runProperties;
        }

        EntityVisualFX getEffect() {
            return effect;
        }
    }

}
