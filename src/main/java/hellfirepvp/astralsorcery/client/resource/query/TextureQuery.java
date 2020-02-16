/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource.query;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TextureQuery
 * Created by HellFirePvP
 * Date: 31.03.2017 / 14:13
 */
public class TextureQuery {

    private final AssetLoader.TextureLocation location;
    private final String[] path;

    private Supplier<?> resource;

    public TextureQuery(AssetLoader.TextureLocation location, String... path) {
        this.location = location;
        this.path = path;
    }

    @OnlyIn(Dist.CLIENT)
    public AbstractRenderableTexture resolve() {
        if (resource == null) {
            resource = AssetLibrary.loadReference(location, path);
        }
        return (AbstractRenderableTexture) resource.get();
    }

}
