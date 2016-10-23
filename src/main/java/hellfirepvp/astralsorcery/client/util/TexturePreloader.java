package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TexturePreloader
 * Created by HellFirePvP
 * Date: 21.09.2016 / 15:44
 */
public class TexturePreloader {

    public static void preloadMandatoryTextures() {
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "arrow_left")   .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "arrow_right")  .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "underline")    .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJResBG")     .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJResOverlay").allocateGlId();
    }

    public static void preloadTextures() {
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud1")            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud2")            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud3")            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud4")            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud5")            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiConPaper")       .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJBlank")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJSpace")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJBookmark")      .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiAltar1")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiAltar2")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridDisc")          .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "lightbeam")      .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "burst1")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "burst2")         .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect1")       .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect2")       .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect3")       .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "starlight_store").allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "halo1")          .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flareStar")    .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flareStatic")    .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "overlayfont")      .allocateGlId();
        MoonPhaseRenderHelper.getMoonPhaseTexture(CelestialHandler.MoonPhase.NEW); //Loads all phase textures

        SpriteLibrary.init(); //Loads all spritesheets
    }

}
