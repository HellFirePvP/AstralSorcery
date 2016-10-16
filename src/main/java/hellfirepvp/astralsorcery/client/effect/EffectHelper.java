package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectHelper
 * Created by HellFirePvP
 * Date: 16.10.2016 / 16:25
 */
public class EffectHelper {

    private static final BindableResource staticFlareTex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flareStatic");

    public static EntityFXFacingParticle genericFlareParticle(double x, double y, double z) {
        EntityFXFacingParticle p = new EntityFXFacingParticle(staticFlareTex, x, y, z);
        p.enableAlphaFade().setAlphaMultiplier(0.75F).setColor(new Color(130, 0, 255)).gravity(0);
        EffectHandler.getInstance().registerFX(p);
        return p;
    }

}
