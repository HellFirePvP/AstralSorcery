/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AssetLoader
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@OnlyIn(Dist.CLIENT)
public class AssetLoader {

    private AssetLoader() {}

    @OnlyIn(Dist.CLIENT)
    protected static RenderTexture load(AssetLocation location, String name, String suffix) {
        return new RenderTexture(buildResourceKey(location, name, suffix));
    }

    @OnlyIn(Dist.CLIENT)
    private static ResourceLocation buildResourceKey(AssetLocation location, String name, String suffix) {
        if (name.endsWith(suffix)) { //In case of derp.
            name = name.substring(0, name.length() - suffix.length());
        }

        StringBuilder path = new StringBuilder();
        path.append("textures").append("/");
        if (location != null) {
            path.append(location.getLocation()).append("/");
        }
        path.append(name).append(suffix);
        return AstralSorcery.key(path.toString());
    }

    @OnlyIn(Dist.CLIENT)
    protected static RenderTexture loadTexture(AssetLocation location, String name) {
        return load(location, name, ".png");
    }
}
