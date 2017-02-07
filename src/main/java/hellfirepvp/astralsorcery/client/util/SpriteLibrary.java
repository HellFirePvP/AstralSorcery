/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpriteLibrary
 * Created by HellFirePvP
 * Date: 26.09.2016 / 00:03
 */
public class SpriteLibrary {

    private static final BindableResource texBurst1 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect1");
    private static final BindableResource texBurst2 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect2");
    private static final BindableResource texBurst3 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect3");
    private static final BindableResource flareActive = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flarePerkActive");
    private static final BindableResource flareInactive = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flarePerkInactive");
    private static final BindableResource flareActivateable = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flarePerkActivateable");
    private static final BindableResource flareActivate = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "unlock_perk");
    private static final BindableResource texCraftBurst = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "burst1");
    private static final BindableResource texCollectorBurst = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "burst2");
    private static final BindableResource texLightbeam = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "lightbeam");
    private static final BindableResource texHalo1 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "halo1");
    private static final BindableResource texHalo2 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "halo2");
    private static final BindableResource texStar1 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "star1");
    private static final BindableResource texStar2 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "star2");
    private static final BindableResource texFlare1 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flare1");
    private static final BindableResource texSpriteStarlight = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "starlight_store");
    private static final BindableResource texLiquidStarlight = AssetLibrary.loadTexture(AssetLoader.TextureLocation.BLOCKS, "fluid/starlight_still");

    public static final SpriteSheetResource spriteStarlight = texSpriteStarlight.asSpriteSheet(16, 4);
    public static final SpriteSheetResource spriteLightbeam = texLightbeam.asSpriteSheet(4, 16);
    public static final SpriteSheetResource spriteHalo1 = texHalo1.asSpriteSheet(6, 8);
    public static final SpriteSheetResource spriteHalo2 = texHalo2.asSpriteSheet(8, 8);
    public static final SpriteSheetResource spriteStar1 = texStar1.asSpriteSheet(6, 8);
    public static final SpriteSheetResource spriteStar2 = texStar2.asSpriteSheet(6, 8);
    public static final SpriteSheetResource spritePerkInactive = flareInactive.asSpriteSheet(1, 40);
    public static final SpriteSheetResource spritePerkActive = flareActive.asSpriteSheet(1, 40);
    public static final SpriteSheetResource spritePerkActivateable = flareActivateable.asSpriteSheet(1, 40);
    public static final SpriteSheetResource spritePerkActivate = flareActivate.asSpriteSheet(5, 16);
    public static final SpriteSheetResource spriteCelestialBurst1 = texBurst1.asSpriteSheet(1, 40);
    public static final SpriteSheetResource spriteCelestialBurst2 = texBurst2.asSpriteSheet(1, 40);
    public static final SpriteSheetResource spriteCelestialBurst3 = texBurst3.asSpriteSheet(1, 40);
    public static final SpriteSheetResource spriteCraftBurst = texCraftBurst.asSpriteSheet(6, 8);
    public static final SpriteSheetResource spriteFlare1 = texFlare1.asSpriteSheet(6, 8);
    public static final SpriteSheetResource spriteCollectorBurst = texCollectorBurst.asSpriteSheet(5, 16);
    public static final SpriteSheetResource spriteLiquidStarlight = texLiquidStarlight.asSpriteSheet(64, 1);

    public static void init() {}

}
