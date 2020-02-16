/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.builtin;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomModel
 * Created by HellFirePvP
 * Date: 21.09.2019 / 15:31
 */
public class CustomModel extends Model {

    protected void bindDamaged(AbstractRenderableTexture texture, int destroyStage) {
        if (destroyStage >= 0) {
            Minecraft.getInstance().getTextureManager().bindTexture(TileEntityRenderer.DESTROY_STAGES[destroyStage]);
        } else {
            texture.bindTexture();
        }
    }

    protected void setRotateAngle(RendererModel modelPart, float x, float y, float z) {
        modelPart.rotateAngleX = x;
        modelPart.rotateAngleY = y;
        modelPart.rotateAngleZ = z;
    }
}
