/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.builtin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomModel
 * Created by HellFirePvP
 * Date: 21.09.2019 / 15:31
 */
public abstract class CustomModel extends Model {

    public CustomModel(Function<ResourceLocation, RenderType> renderTypeIn) {
        super(renderTypeIn);
    }

    public final RenderType getGeneralType() {
        return this.getRenderType(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    }

    public final void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn) {
        this.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1F, 1F, 1F, 1F);
    }

    protected void setRotateAngle(ModelRenderer modelPart, float x, float y, float z) {
        modelPart.rotateAngleX = x;
        modelPart.rotateAngleY = y;
        modelPart.rotateAngleZ = z;
    }
}
