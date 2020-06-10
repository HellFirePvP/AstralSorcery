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
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelLensColored
 * Created by wiiv
 * Created using Tabula 4.1.1
 * Date: 21.09.2019 / 15:18
 */
public class ModelLensColored extends CustomModel {

    public ModelRenderer glass;
    public ModelRenderer detail1;
    public ModelRenderer detail1_1;
    public ModelRenderer fitting2;
    public ModelRenderer fitting1;

    public ModelLensColored() {
        super((resKey) -> RenderTypesAS.MODEL_LENS_COLORED);
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.glass = new ModelRenderer(this, 0, 0);
        this.glass.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.glass.addBox(-5.0F, -5.0F, -1.51F, 10, 10, 1, 0.0F);
        this.fitting1 = new ModelRenderer(this, 22, 0);
        this.fitting1.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.fitting1.addBox(-5.0F, -7.0F, -1.5F, 2, 1, 2, 0.0F);
        this.detail1_1 = new ModelRenderer(this, 22, 3);
        this.detail1_1.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.detail1_1.addBox(3.0F, -6.0F, -1.5F, 2, 1, 1, 0.0F);
        this.fitting2 = new ModelRenderer(this, 22, 0);
        this.fitting2.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.fitting2.addBox(3.0F, -7.0F, -1.5F, 2, 1, 2, 0.0F);
        this.detail1 = new ModelRenderer(this, 22, 3);
        this.detail1.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.detail1.addBox(-5.0F, -6.0F, -1.5F, 2, 1, 1, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.fitting1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.detail1_1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.fitting2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.detail1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void renderGlass(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.glass.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
