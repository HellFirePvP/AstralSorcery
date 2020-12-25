/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.constellation;

import com.google.common.base.Preconditions;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import net.minecraft.client.renderer.RenderType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationBackgroundInfo
 * Created by HellFirePvP
 * Date: 25.12.2020 / 20:42
 */
public class ConstellationBackgroundInfo {

    private final RenderType renderType;
    private final AbstractRenderableTexture texture;

    ConstellationBackgroundInfo(RenderType renderType, AbstractRenderableTexture texture) {
        Preconditions.checkNotNull(renderType);
        Preconditions.checkNotNull(texture);

        this.renderType = renderType;
        this.texture = texture;
    }

    public RenderType getRenderType() {
        return this.renderType;
    }

    public AbstractRenderableTexture getBackgroundTexture() {
        return this.texture;
    }
}
