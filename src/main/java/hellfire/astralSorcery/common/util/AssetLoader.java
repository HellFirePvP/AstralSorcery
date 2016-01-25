package hellfire.astralSorcery.common.util;

import hellfire.astralSorcery.common.lib.LibConstants;
import net.minecraft.util.ResourceLocation;

/**
 * HellFirePvP@Admin
 * Date: 16.06.2015 / 21:27
 * on SoulSorcery
 * AssetLoader
 */
public final class AssetLoader {

    private static StringBuilder builder;

    private AssetLoader() {}

    public static ResourceLocation load(AssetLocation location, SubLocation subLocation, String name, String suffix) {
        builder = new StringBuilder();
        builder.append(LibConstants.MODID).append(':').append(location.location).append("/");
        if(subLocation != null) {
            builder.append(subLocation.getLocation()).append("/");
        }
        builder.append(name).append(suffix);
        return new ResourceLocation(builder.toString());
    }

    public static ResourceLocation loadTexture(TextureLocation location, String name) {
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

        BLOCKS("blocks"),
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
