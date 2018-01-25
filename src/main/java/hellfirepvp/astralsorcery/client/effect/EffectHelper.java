/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingDepthParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectHelper
 * Created by HellFirePvP
 * Date: 16.10.2016 / 16:25
 */
public class EffectHelper {

    private static final BindableResource staticFlareTex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flarestatic");

    public static EntityFXFacingParticle genericFlareParticle(Vector3 v) {
        return genericFlareParticle(v.getX(), v.getY(), v.getZ());
    }

    public static EntityFXFacingParticle genericFlareParticle(double x, double y, double z) {
        EntityFXFacingParticle p = new EntityFXFacingParticle(x, y, z);
        p.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID).setAlphaMultiplier(0.75F).setColor(new Color(60, 0, 255));
        EffectHandler.getInstance().registerFX(p);
        return p;
    }

    public static EntityFXFacingDepthParticle genericDepthIgnoringFlareParticle(double x, double y, double z) {
        EntityFXFacingDepthParticle p = new EntityFXFacingDepthParticle(x, y, z);
        p.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID).setAlphaMultiplier(0.75F).setColor(new Color(60, 0, 255));
        EffectHandler.getInstance().registerFX(p);
        return p;
    }

    /*public static EntityFXFacingParticle genericFlareStarParticle(double x, double y, double z) {
        EntityFXFacingParticle p = new EntityFXFacingParticle(starFlareTex, x, y, z);
        p.enableAlphaFade().setAlphaMultiplier(0.75F).setColor(new Color(130, 0, 255)).gravity(0);
        EffectHandler.getInstance().registerFX(p);
        return p;
    }*/

}
