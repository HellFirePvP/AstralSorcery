package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AssetLoader
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:51
 */
public class AssetLoader {

    private AssetLoader() {
    }

    public static BindableResource load(AssetLocation location, SubLocation subLocation, String name, String suffix) {
        return new BindableResource(buildResourceString(location, subLocation, name, suffix));
    }

    private static String buildResourceString(AssetLocation location, SubLocation subLocation, String name, String suffix) {
        if(name.endsWith(suffix)) { //In case of derp.
            name = name.substring(0, name.length() - suffix.length());
        }

        StringBuilder builder = new StringBuilder();
        builder.append(AstralSorcery.MODID).append(':').append(location.location).append("/");
        if (subLocation != null) {
            builder.append(subLocation.getLocation()).append("/");
        }
        builder.append(name).append(suffix);
        return builder.toString();
    }

    public static BindableResource loadTexture(TextureLocation location, String name) {
        return load(AssetLocation.TEXTURES, location, name, ".png");
    }

    public static WavefrontObject loadObjModel(ModelLocation location, String name) {
        return new WavefrontObject(new ResourceLocation(buildResourceString(AssetLocation.MODELS, location, name, ".obj")));
    }

    /*public static WavefrontObject loadOBJModel(ModelLocation location, String name) {
        return load(AssetLocation.MODELS, location, name, ".obj");
    }*/

    public static interface SubLocation {

        public String getLocation();

    }

    public static enum ModelLocation implements SubLocation {

        OBJ("obj");

        private final String location;

        private ModelLocation(String location) {
            this.location = location;
        }

        @Override
        public String getLocation() {
            return location;
        }

    }

    public static enum TextureLocation implements SubLocation {

        ITEMS("items"),
        BLOCKS("blocks"),
        GUI("gui"),
        MISC("misc"),
        MODELS("models"),
        ENVIRONMENT("environment");

        private final String location;

        private TextureLocation(String location) {
            this.location = location;
        }

        @Override
        public String getLocation() {
            return location;
        }

    }

    public static enum AssetLocation {

        MODELS("models"),
        TEXTURES("textures");

        private final String location;

        private AssetLocation(String location) {
            this.location = location;
        }

    }

}
