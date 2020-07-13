/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.handler;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.source.FXSource;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import net.minecraft.client.Minecraft;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectRegistrar
 * Created by HellFirePvP
 * Date: 30.05.2019 / 13:09
 */
public final class EffectRegistrar {

    private EffectRegistrar() {}

    static <T extends EntityVisualFX> T registerFX(T entityComplexFX, EffectProperties<T> properties) {
        register(entityComplexFX, properties);
        return entityComplexFX;
    }

    static <E extends EntityVisualFX, T extends BatchRenderContext<E>, F extends FXSource<E, T>> F registerSource(F source) {
        register(source);
        return source;
    }

    private static void register(FXSource<?, ?> src) {
        if (Minecraft.getInstance().isGamePaused() ||
                Minecraft.getInstance().player == null) {
            return;
        }

        if (!Thread.currentThread().getName().contains("Render thread")) {
            AstralSorcery.getProxy().scheduleClientside(() -> register(src));
            return;
        }

        EffectHandler.getInstance().queueSource(src);
    }

    private static <T extends EntityVisualFX> void register(T effect, EffectProperties<T> properties) {
        if (AssetLibrary.isReloading() ||
                effect == null ||
                Minecraft.getInstance().isGamePaused() ||
                Minecraft.getInstance().player == null ||
                !RenderingUtils.canEffectExist(effect)) {
            return;
        }

        if (!Thread.currentThread().getName().contains("Render thread")) {
            AstralSorcery.getProxy().scheduleClientside(() -> register(effect, properties));
            return;
        }

        EffectHandler.getInstance().queueParticle(new EffectHandler.PendingEffect(effect, properties));
    }

}
