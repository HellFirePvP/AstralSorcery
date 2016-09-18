package hellfirepvp.astralsorcery.client.effect.fx;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXBurst
 * Created by HellFirePvP
 * Date: 17.09.2016 / 23:52
 */
public class EntityFXBurst extends EntityFXFacingSprite {

    private static final BindableResource texBurst = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "burst2");
    private static final SpriteSheetResource spriteBurst = texBurst.asSpriteSheet(16, 5);

    public EntityFXBurst(double x, double y, double z) {
        super(spriteBurst, x, y, z);
        setMaxAge(80);
    }
    public EntityFXBurst(double x, double y, double z, float scale) {
        super(spriteBurst, x, y, z, scale);
        setMaxAge(80);
    }

}
