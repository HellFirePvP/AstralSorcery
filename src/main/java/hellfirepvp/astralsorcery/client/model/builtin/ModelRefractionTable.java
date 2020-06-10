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
 * Class: ModelRefractionTable
 * AS_starmapper - wiiv
 * Created using Tabula 7.0.0
 */
public class ModelRefractionTable extends CustomModel {

    private ModelRenderer fitting_l;
    private ModelRenderer fitting_r;
    private ModelRenderer support_1;
    private ModelRenderer support_2;
    private ModelRenderer support_3;
    private ModelRenderer support_4;
    private ModelRenderer platform_l;
    private ModelRenderer platform_r;
    private ModelRenderer platform_f;
    private ModelRenderer platform_b;
    private ModelRenderer basin_l;
    private ModelRenderer basim_r;
    private ModelRenderer basin_f;
    private ModelRenderer basin_b;
    private ModelRenderer socket;
    private ModelRenderer base;
    private ModelRenderer leg_1;
    private ModelRenderer leg_2;
    private ModelRenderer leg_3;
    private ModelRenderer leg_4;

    private ModelRenderer parchment;
    private ModelRenderer black_mirror;

    private ModelRenderer treated_glass;

    public ModelRefractionTable() {
        super((resKey) -> RenderTypesAS.MODEL_REFRACTION_TABLE);
        this.textureWidth = 128;
        this.textureHeight = 128;

        this.fitting_l = new ModelRenderer(this, 0, 48);
        this.fitting_l.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.fitting_l.addBox(-14.0F, 0.0F, -12.0F, 4, 4, 24, 0.0F);
        this.fitting_r = new ModelRenderer(this, 56, 48);
        this.fitting_r.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.fitting_r.addBox(10.0F, 0.0F, -12.0F, 4, 4, 24, 0.0F);
        this.support_1 = new ModelRenderer(this, 24, 76);
        this.support_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support_1.addBox(-14.0F, 4.0F, -12.0F, 4, 6, 2, 0.0F);
        this.support_2 = new ModelRenderer(this, 24, 76);
        this.support_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support_2.addBox(10.0F, 4.0F, -12.0F, 4, 6, 2, 0.0F);
        this.support_3 = new ModelRenderer(this, 24, 76);
        this.support_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support_3.addBox(10.0F, 4.0F, 10.0F, 4, 6, 2, 0.0F);
        this.support_4 = new ModelRenderer(this, 24, 76);
        this.support_4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support_4.addBox(-14.0F, 4.0F, 10.0F, 4, 6, 2, 0.0F);
        this.platform_l = new ModelRenderer(this, 0, 0);
        this.platform_l.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.platform_l.addBox(-14.0F, -6.0F, -12.0F, 4, 2, 24, 0.0F);
        this.platform_r = new ModelRenderer(this, 0, 0);
        this.platform_r.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.platform_r.addBox(10.0F, -6.0F, -12.0F, 4, 2, 24, 0.0F);
        this.platform_f = new ModelRenderer(this, 32, 0);
        this.platform_f.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.platform_f.addBox(-10.0F, -6.0F, -12.0F, 20, 2, 2, 0.0F);
        this.platform_b = new ModelRenderer(this, 32, 0);
        this.platform_b.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.platform_b.addBox(-10.0F, -6.0F, 10.0F, 20, 2, 2, 0.0F);
        this.basin_l = new ModelRenderer(this, 84, 76);
        this.basin_l.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.basin_l.addBox(-10.0F, -8.0F, -10.0F, 2, 6, 20, 0.0F);
        this.basim_r = new ModelRenderer(this, 84, 102);
        this.basim_r.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.basim_r.addBox(8.0F, -8.0F, -10.0F, 2, 6, 20, 0.0F);
        this.basin_f = new ModelRenderer(this, 36, 84);
        this.basin_f.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.basin_f.addBox(-8.0F, -8.0F, -10.0F, 16, 6, 2, 0.0F);
        this.basin_b = new ModelRenderer(this, 36, 76);
        this.basin_b.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.basin_b.addBox(-8.0F, -8.0F, 8.0F, 16, 6, 2, 0.0F);
        this.socket = new ModelRenderer(this, 0, 76);
        this.socket.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.socket.addBox(-3.0F, -4.0F, -3.0F, 6, 2, 6, 0.0F);
        this.base = new ModelRenderer(this, 0, 26);
        this.base.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.base.addBox(-10.0F, -2.0F, -10.0F, 20, 2, 20, 0.0F);
        this.leg_1 = new ModelRenderer(this, 0, 76);
        this.leg_1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_1.addBox(-10.0F, 0.0F, -10.0F, 6, 8, 6, 0.0F);
        this.leg_2 = new ModelRenderer(this, 0, 76);
        this.leg_2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_2.addBox(4.0F, 0.0F, -10.0F, 6, 8, 6, 0.0F);
        this.leg_3 = new ModelRenderer(this, 0, 76);
        this.leg_3.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_3.addBox(4.0F, 0.0F, 4.0F, 6, 8, 6, 0.0F);
        this.leg_4 = new ModelRenderer(this, 0, 76);
        this.leg_4.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_4.addBox(-10.0F, 0.0F, 4.0F, 6, 8, 6, 0.0F);

        this.parchment = new ModelRenderer(this, 66, 28);
        this.parchment.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.parchment.addBox(-7.0F, -8.5F, -7.0F, 14, 0, 14, 0.0F);
        this.black_mirror = new ModelRenderer(this, 64, 12);
        this.black_mirror.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.black_mirror.addBox(-8.0F, -8.0F, -8.0F, 16, 0, 16, 0.0F);

        this.treated_glass = new ModelRenderer(this, 0, 107);
        this.treated_glass.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.treated_glass.addBox(-10.0F, -15.0F, -10.0F, 20, 1, 20, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {}

    public void renderFrame(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, boolean hasParchment) {
        this.fitting_l.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.fitting_r.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.support_1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.support_2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.support_3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.support_4.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.platform_l.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.platform_r.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.platform_f.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.platform_b.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.basin_l.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.basim_r.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.basin_f.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.basin_b.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.socket.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.base.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leg_1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leg_2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leg_3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leg_4.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        if (hasParchment) {
            this.parchment.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.black_mirror.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }



    public void renderGlass(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.treated_glass.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}