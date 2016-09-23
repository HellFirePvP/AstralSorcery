package hellfirepvp.astralsorcery.client.effect.fx;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXCrystalBurst
 * Created by HellFirePvP
 * Date: 19.09.2016 / 13:20
 */
public class EntityFXCrystalBurst extends EntityFXFacingSprite {

    private static final BindableResource texBurst1 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect1");
    private static final BindableResource texBurst2 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect2");
    private static final BindableResource texBurst3 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect3");
    private static final SpriteSheetResource spriteBurst1 = texBurst1.asSpriteSheet(40, 1);
    private static final SpriteSheetResource spriteBurst2 = texBurst2.asSpriteSheet(40, 1);
    private static final SpriteSheetResource spriteBurst3 = texBurst3.asSpriteSheet(40, 1);

    public EntityFXCrystalBurst(int burstId, double x, double y, double z) {
        super(getSprite(burstId), x, y, z);
        setMaxAge(40);
    }

    public EntityFXCrystalBurst(int burstId, double x, double y, double z, float scale) {
        super(getSprite(burstId), x, y, z, scale);
        setMaxAge(40);
    }

    private static SpriteSheetResource getSprite(int burstId) {
        burstId = Math.abs(burstId) % 3;
        switch (burstId) {
            case 0:
                return spriteBurst1;
            case 1:
                return spriteBurst2;
            case 2:
                return spriteBurst3;
        }
        return spriteBurst1;
    }

}
