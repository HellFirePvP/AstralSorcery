/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomTileEntityRenderer
 * Created by HellFirePvP
 * Date: 21.09.2019 / 15:29
 */
public abstract class CustomTileEntityRenderer<T extends TileEntity> extends TileEntityRenderer<T> {

    @Override
    public abstract void render(T tile, double x, double y, double z, float pTicks, int destroyStage);

    protected void bind(AbstractRenderableTexture texture) {
        this.bindDamaged(texture, -1);
    }

    protected void bindDamaged(AbstractRenderableTexture texture, int destroyStage) {
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
        } else {
            texture.bindTexture();
        }
    }

}
