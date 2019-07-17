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
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.effect.context.BatchRenderContext;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.util.Counter;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.order.DependencySorter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private boolean acceptsNewEffects = true;
    private List<PendingEffect> toAddBuffer = Lists.newLinkedList();
    private List<BatchRenderContext<?>> orderedEffects = null;
    private Map<BatchRenderContext<?>, List<PendingEffect>> effectMap = Maps.newHashMap();

    private EffectHandler() {}

    public static EffectHandler getInstance() {
        return INSTANCE;
    }

    public static int getDebugEffectCount() {
        final Counter c = new Counter(0);
        //for (Map<Integer, List<IComplexEffect>> effects : complexEffects.values()) {
        //    for (List<IComplexEffect> eff : effects.values()) {
        //        c.value += eff.size();
        //    }
        //}
        //c.value += fastRenderParticles.size();
        //c.value += fastRenderLightnings.size();
        //objects.values().forEach((l) -> c.value += l.size());
        return c.getValue();
    }

    public void render(RenderWorldLastEvent event) {
        if (this.orderedEffects == null) {
            return;
        }

        float pTicks = event.getPartialTicks();
        acceptsNewEffects = false;

        for (BatchRenderContext<?> ctx : this.orderedEffects) {
            List<PendingEffect> effects;
            if ((effects = this.effectMap.get(ctx)) != null && !effects.isEmpty()) {
                BufferContext buf = ctx.prepare(pTicks);
                effects.forEach(p -> p.effect.render(ctx, buf, pTicks));
                ctx.draw(pTicks);
            }
        }

        acceptsNewEffects = true;
    }

    void tick() {
        if (cleanRequested) {
            toAddBuffer.clear();
            cleanRequested = false;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (this.orderedEffects == null) {
            this.orderedEffects = DependencySorter.getSorted(EffectTemplatesAS.LIST_ALL_RENDER_CONTEXT);
            this.orderedEffects.forEach(ctx -> this.effectMap.put(ctx, new LinkedList<>()));
        }

        acceptsNewEffects = false;

        Vector3 playerPos = Vector3.atEntityCorner(player);



        acceptsNewEffects = true;
        toAddBuffer.forEach(this::registerUnsafe);
        toAddBuffer.clear();
    }

    void queueParticle(PendingEffect pendingEffect) {
        if (this.acceptsNewEffects) {
            registerUnsafe(pendingEffect);
        } else {
            toAddBuffer.add(pendingEffect);
        }
    }

    private void registerUnsafe(PendingEffect pendingEffect) {
        if (!mayAcceptParticle(pendingEffect.getProperties())) {
            return;
        }
        EntityComplexFX effect = pendingEffect.getEffect();
        BatchRenderContext ctx = pendingEffect.getProperties().getContext();
        pendingEffect.getProperties().applySpecialEffects(effect);

        effectMap.get(ctx).add(pendingEffect);
        effect.clearRemoveFlag();
    }

    private boolean mayAcceptParticle(EffectProperties properties) {
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

    static class PendingEffect {

        private final EntityComplexFX effect;
        private final EffectProperties runProperties;

        PendingEffect(EntityComplexFX effect, EffectProperties runProperties) {
            this.effect = effect;
            this.runProperties = runProperties;
        }

        EffectProperties getProperties() {
            return runProperties;
        }

        EntityComplexFX getEffect() {
            return effect;
        }
    }

}
