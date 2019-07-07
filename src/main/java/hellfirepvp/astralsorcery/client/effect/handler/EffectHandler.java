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
import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.effect.context.BatchRenderContext;
import hellfirepvp.astralsorcery.common.util.Counter;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;

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

    public static final Random STATIC_EFFECT_RAND = new Random();
    public static final EffectHandler INSTANCE = new EffectHandler();

    private boolean cleanRequested = false;
    private boolean acceptsNewEffects = true;
    List<PendingEffect> toAddBuffer = Lists.newLinkedList();
    Map<BatchRenderContext, List<PendingEffect>> effectMap = Maps.newHashMap();

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
        float pTicks = event.getPartialTicks();
        acceptsNewEffects = false;

        acceptsNewEffects = true;
    }

    void tick() {
        if(cleanRequested) {
            toAddBuffer.clear();
            cleanRequested = false;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        acceptsNewEffects = false;

        Vector3 playerPos = Vector3.atEntityCorner(player);

        acceptsNewEffects = true;
        toAddBuffer.forEach(this::registerUnsafe);
        toAddBuffer.clear();
    }

    void registerUnsafe(PendingEffect pendingEffect) {
        if (!mayAcceptParticle(pendingEffect.getProperties())) {
            return;
        }
        IComplexEffect effect = pendingEffect.getEffect();
        BatchRenderContext ctx = pendingEffect.getProperties().getContext();
        pendingEffect.getProperties().applySpecialEffects(effect);

        effectMap.computeIfAbsent(ctx, c -> Lists.newLinkedList())
                .add(pendingEffect);

        //TODO effect registering
        //if(effect instanceof EffectLightning) {
        //    fastRenderLightnings.add((EffectLightning) effect);
        //} else if(effect instanceof EntityFXFacingParticle.Gateway) {
        //    fastRenderGatewayParticles.add((EntityFXFacingParticle) effect);
        //} else if(effect instanceof EntityFXFacingDepthParticle) {
        //    fastRenderDepthParticles.add((EntityFXFacingDepthParticle) effect);
        //} else if(effect instanceof EntityFXFacingParticle) {
        //    fastRenderParticles.add((EntityFXFacingParticle) effect);
        //} else if(effect instanceof CompoundObjectEffect) {
        //    CompoundObjectEffect.ObjectGroup group = ((CompoundObjectEffect) effect).getGroup();
        //    if(!objects.containsKey(group)) objects.put(group, new LinkedList<>());
        //    objects.get(group).add((CompoundObjectEffect) effect);
        //} else {
        //    complexEffects.get(effect.getRenderTarget()).get(effect.getLayer()).add(effect);
        //}

        effect.clearRemoveFlag();
    }

    private boolean mayAcceptParticle(EffectProperties properties) {
        if (properties.ignoresSpawnLimit()) {
            return true;
        }
        RenderingConfig.ParticleAmount cfg = RenderingConfig.CONFIG.particleAmount.get();
        if(!Minecraft.isFancyGraphicsEnabled()) {
            cfg = cfg.less();
        }
        return cfg.shouldSpawn(STATIC_EFFECT_RAND);
    }

    public static boolean acceptsNewEffects() {
        return getInstance().acceptsNewEffects;
    }

    public static void cleanUp() {
        getInstance().cleanRequested = true;
    }

    static class PendingEffect {

        private final IComplexEffect effect;
        private final EffectProperties runProperties;

        PendingEffect(IComplexEffect effect, EffectProperties runProperties) {
            this.effect = effect;
            this.runProperties = runProperties;
        }

        EffectProperties getProperties() {
            return runProperties;
        }

        IComplexEffect getEffect() {
            return effect;
        }
    }

}
