package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.BindableResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AssetLoader
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:51
 */
public class AssetLoader {

    private static StringBuilder builder;

    private AssetLoader() {
    }

    public static BindableResource load(AssetLocation location, SubLocation subLocation, String name, String suffix) {
        builder = new StringBuilder();
        builder.append(AstralSorcery.MODID).append(':').append(location.location).append("/");
        if (subLocation != null) {
            builder.append(subLocation.getLocation()).append("/");
        }
        builder.append(name).append(suffix);
        return new BindableResource(builder.toString());
    }

    public static BindableResource loadTexture(TextureLocation location, String name) {
        return load(AssetLocation.TEXTURES, location, name, ".png");
    }

    /*public static ResourceLocation loadOBJModel(ModelLocation location, String name) {
        return load(AssetLocation.MODELS, location, name, ".obj");
    }*/

    /*public static WorldObject loadWorldObject(String name) {
        if(name == null) return null;

        ResourceLocation location = load(AssetLocation.WORLDOBJECTS, null, name, ".BO2");
        try {
            InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
            return WorldObject.parseBO2(stream);
        } catch (Exception ignored) {}
        return null;
    }*/

    public static interface SubLocation {

        public String getLocation();

    }

    public static enum ModelLocation implements SubLocation {

        ;

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
        GUI("gui"),
        MISC("misc"),
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

        TEXTURES("textures");

        private final String location;

        private AssetLocation(String location) {
            this.location = location;
        }

    }

}
