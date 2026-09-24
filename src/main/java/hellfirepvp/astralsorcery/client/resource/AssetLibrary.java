/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.sky.AstralSkyRenderer;
import hellfirepvp.astralsorcery.common.util.data.CacheSupplier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AssetLibrary
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@OnlyIn(Dist.CLIENT)
public class AssetLibrary implements ResourceManagerReloadListener {

    private static final AssetLibrary INSTANCE = new AssetLibrary();
    private static boolean reloading = false;

    private static final Map<AssetLocation, Map<String, AbstractRenderTexture>> loadedTextures = new HashMap<>();
    private static final List<ReloadableResource> reloadableResources = new ArrayList<>();

    private AssetLibrary() {}

    public static AssetLibrary getInstance() {
        return INSTANCE;
    }

    public static boolean isReloading() {
        return reloading;
    }

    @Nonnull
    public static Supplier<AbstractRenderTexture> loadReference(AssetLocation location, String... path) {
        return new CacheSupplier<>(() -> loadTexture(location, path));
    }

    @Nonnull
    public static AbstractRenderTexture loadTexture(AssetLocation location, String... path) {
        String name = String.join("/", path);
        if (name.endsWith(".png")) {
            throw new IllegalArgumentException("Tried to loadTexture with appended .png from the AssetLibrary!");
        }
        AbstractRenderTexture resource = loadedTextures.computeIfAbsent(location, l -> new HashMap<>())
                .computeIfAbsent(name, str -> AssetLoader.loadTexture(location, str));
        reloadableResources.add(resource);
        return resource;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        if (reloading) {
            return;
        }
        reloading = true;
        AstralSorcery.LOG.info("[AssetLibrary] Refreshing and Invalidating Resources");
        reloadableResources.forEach(ReloadableResource::invalidateAndReload);
        reloading = false;

        //Reload buffer during next render
        AstralSkyRenderer.getInstance().reset();

        AstralSorcery.LOG.info("[AssetLibrary] Successfully reloaded library.");
    }
}
