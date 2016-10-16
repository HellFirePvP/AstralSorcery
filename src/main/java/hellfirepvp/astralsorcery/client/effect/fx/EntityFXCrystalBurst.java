package hellfirepvp.astralsorcery.client.effect.fx;

import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
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

    public EntityFXCrystalBurst(int burstId, double x, double y, double z) {
        super(getSprite(burstId), x, y, z);
    }

    public EntityFXCrystalBurst(int burstId, double x, double y, double z, float scale) {
        super(getSprite(burstId), x, y, z, scale);
    }

    private static SpriteSheetResource getSprite(int burstId) {
        burstId = Math.abs(burstId) % 3;
        switch (burstId) {
            case 0:
                return SpriteLibrary.spriteCelestialBurst1;
            case 1:
                return SpriteLibrary.spriteCelestialBurst2;
            case 2:
                return SpriteLibrary.spriteCelestialBurst3;
        }
        return SpriteLibrary.spriteCelestialBurst1;
    }

}
