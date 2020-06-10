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
 * Class: ModelLens
 * Created by wiiv
 * Created using Tabula 4.1.1
 * Date: 21.09.2019 / 15:16
 */
public class ModelLens extends CustomModel {

    public ModelRenderer base;
    public ModelRenderer frame1;
    public ModelRenderer lens;
    public ModelRenderer frame2;

    public ModelLens() {
        super((resKey) -> RenderTypesAS.MODEL_LENS);
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.base = new ModelRenderer(this, 0, 13);
        this.base.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.base.addBox(-6.0F, 4.0F, -6.0F, 12, 2, 12, 0.0F);
        this.frame1 = new ModelRenderer(this, 0, 13);
        this.frame1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.frame1.addBox(-8.0F, -4.0F, -1.0F, 2, 10, 2, 0.0F);
        this.frame2 = new ModelRenderer(this, 0, 13);
        this.frame2.mirror = true;
        this.frame2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.frame2.addBox(6.0F, -4.0F, -1.0F, 2, 10, 2, 0.0F);
        this.lens = new ModelRenderer(this, 0, 0);
        this.lens.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.lens.addBox(-6.0F, -6.0F, -0.5F, 12, 12, 1, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.base.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.frame1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.frame2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.lens.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        this.lens.rotateAngleX = 0;
    }
}
