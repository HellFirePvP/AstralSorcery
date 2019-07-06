/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.handler;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
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

    public static EntityComplexFX registerFX(EntityComplexFX entityComplexFX, EffectProperties properties) {
        register(entityComplexFX, properties);
        return entityComplexFX;
    }

    private static void register(IComplexEffect effect, EffectProperties properties) {
        if(AssetLibrary.isReloading() || effect == null || Minecraft.getInstance().isGamePaused()) {
            return;
        }

        if (!Thread.currentThread().getName().contains("Client thread")) {
            AstralSorcery.getProxy().scheduleClientside(() -> register(effect, properties));
            return;
        }

        EffectHandler.PendingEffect pendingEffect = new EffectHandler.PendingEffect(effect, properties);
        if (EffectHandler.acceptsNewParticles) {
            EffectHandler.getInstance().registerUnsafe(pendingEffect);
        } else {
            EffectHandler.toAddBuffer.add(pendingEffect);
        }
    }

}
