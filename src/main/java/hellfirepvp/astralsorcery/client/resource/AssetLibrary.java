/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.sky.astral.AstralSkyRenderer;
import hellfirepvp.astralsorcery.common.util.object.CacheReference;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AssetLibrary
 * Created by HellFirePvP
 * Date: 09.08.2016 / 11:00
 */
public class AssetLibrary implements ISelectiveResourceReloadListener {

    public static AssetLibrary INSTANCE = new AssetLibrary();
    private static boolean reloading = false;

    private static Map<AssetLoader.SubLocation, Map<String, BindableResource>> loadedTextures = new HashMap<>();

    private AssetLibrary() {}

    public static Supplier<AbstractRenderableTexture> loadReference(AssetLoader.TextureLocation location, String... path) {
        return new CacheReference<>(() -> loadTexture(location, path));
    }

    public static AbstractRenderableTexture loadTexture(AssetLoader.TextureLocation location, String... path) {
        String name = String.join("/", path);
        if (name.endsWith(".png")) {
            throw new IllegalArgumentException("Tried to loadTexture with appended .png from the AssetLibrary!");
        }
        return loadedTextures.computeIfAbsent(location, l -> new HashMap<>())
                .computeIfAbsent(name, str -> AssetLoader.loadTexture(location, str));
    }

    public static boolean isReloading() {
        return reloading;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if (reloading || !resourcePredicate.test(VanillaResourceType.TEXTURES)) {
            return;
        }
        reloading = true;
        AstralSorcery.log.info("[AssetLibrary] Refreshing and Invalidating Resources");
        for (Map<String, BindableResource> map : loadedTextures.values()) {
            map.values().forEach(BindableResource::invalidateAndReload);
        }
        reloading = false;

        //Reload buffer during next render
        AstralSkyRenderer.INSTANCE.reset();

        AstralSorcery.log.info("[AssetLibrary] Successfully reloaded library.");
    }

}
