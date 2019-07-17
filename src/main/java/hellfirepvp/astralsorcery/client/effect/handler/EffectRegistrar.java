/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.handler;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectRegistrar
 * Created by HellFirePvP
 * Date: 30.05.2019 / 13:09
 */
public final class EffectRegistrar {

    private EffectRegistrar() {}

    public static <T extends EntityComplexFX> T registerFX(T entityComplexFX, EffectProperties properties) {
        register(entityComplexFX, properties);
        return entityComplexFX;
    }

    private static void register(EntityComplexFX effect, EffectProperties properties) {
        if(AssetLibrary.isReloading() ||
                effect == null ||
                Minecraft.getInstance().isGamePaused() ||
                Minecraft.getInstance().player == null ||
                !RenderingUtils.canEffectExist(effect)) {
            return;
        }

        if (!Thread.currentThread().getName().contains("Client thread")) {
            AstralSorcery.getProxy().scheduleClientside(() -> register(effect, properties));
            return;
        }

        EffectHandler.getInstance().queueParticle(new EffectHandler.PendingEffect(effect, properties));
    }

}
