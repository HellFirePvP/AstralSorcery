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
 * Class: ModelObservatory
 * Created by wiiv
 * Created using Tabula 7.0.0
 */
public class ModelObservatory extends CustomModel {

    private ModelRenderer base;
    private ModelRenderer tube;
    private ModelRenderer seat;
    private ModelRenderer base1;
    private ModelRenderer base2;
    private ModelRenderer base3;
    private ModelRenderer base4;
    private ModelRenderer base5;
    private ModelRenderer base6;
    private ModelRenderer base7;
    private ModelRenderer base8;

    private ModelRenderer seat1;
    private ModelRenderer seat2;
    private ModelRenderer seat3;
    private ModelRenderer seat4;
    private ModelRenderer seat5;
    private ModelRenderer seat6;
    private ModelRenderer seat7;
    private ModelRenderer seat8;
    private ModelRenderer seat9;
    private ModelRenderer seat10;
    private ModelRenderer seat11;
    private ModelRenderer seat12;
    private ModelRenderer seat13;
    private ModelRenderer seat14;

    private ModelRenderer tube1;
    private ModelRenderer tube2;
    private ModelRenderer tube3;
    private ModelRenderer tube4;
    private ModelRenderer tube5;
    private ModelRenderer tube6;
    private ModelRenderer tube7;
    private ModelRenderer tube8;
    private ModelRenderer tube9;
    private ModelRenderer tube10;
    private ModelRenderer tube11;
    private ModelRenderer tube12;
    private ModelRenderer tube13;
    private ModelRenderer tube14;
    private ModelRenderer tube15;

    public ModelObservatory() {
        super((resKey) -> RenderTypesAS.MODEL_OBSERVATORY);
        this.textureWidth = 256;
        this.textureHeight = 128;

        //base
        this.base = new ModelRenderer(this, 0, 82);
        this.base.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.base.addBox(-12.0F, 18.0F, -16.0F, 24, 6, 28, 0.1F);

        this.base1 = new ModelRenderer(this, 120, 82);
        this.base1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base1.addBox(-14.0F, 4.0F, -18.0F, 6, 18, 12, 0.0F);
        this.base2 = new ModelRenderer(this, 224, 52);
        this.base2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base2.addBox(-7.0F, 4.0F, -18.0F, 2, 18, 12, 0.0F);
        this.base3 = new ModelRenderer(this, 224, 52);
        this.base3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base3.addBox(-4.0F, 4.0F, -18.0F, 2, 18, 12, 0.0F);
        this.base4 = new ModelRenderer(this, 224, 52);
        this.base4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base4.addBox(-1.0F, 4.0F, -18.0F, 2, 18, 12, 0.0F);
        this.base5 = new ModelRenderer(this, 180, 52);
        this.base5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base5.addBox(2.0F, 4.0F, -18.0F, 10, 18, 12, 0.0F);
        this.base6 = new ModelRenderer(this, 192, 0);
        this.base6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base6.addBox(12.0F, -18.0F, -18.0F, 8, 40, 12, 0.0F);
        this.base7 = new ModelRenderer(this, 156, 82);
        this.base7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base7.addBox(8.0F, 4.0F, -6.0F, 8, 18, 20, 0.0F);
        this.base8 = new ModelRenderer(this, 192, 82);
        this.base8.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.base8.addBox(-8.0F, 28.0F, -8.0F, 16, 4, 16, 0.0F);

        //seat
        this.seat = new ModelRenderer(this, 144, 28);
        this.seat.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat.addBox(-9.0F, 16.0F, 6.0F, 12, 4, 10, 0.0F);

        this.seat1 = new ModelRenderer(this, 144, 42);
        this.seat1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.seat1.addBox(-9.0F, 16.0F, 0.0F, 12, 2, 4, 0.0F);
        this.seat2 = new ModelRenderer(this, 144, 10);
        this.seat2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.seat2.addBox(-9.0F, 6.0F, 16.0F, 12, 14, 4, 0.0F);
        this.seat3 = new ModelRenderer(this, 144, 0);
        this.seat3.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat3.addBox(-7.0F, 2.0F, 16.0F, 8, 6, 4, 0.0F);
        this.seat4 = new ModelRenderer(this, 140, 82);
        this.seat4.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat4.addBox(-1.0F, 18.0F, 12.0F, 2, 4, 8, 0.0F);
        this.seat5 = new ModelRenderer(this, 156, 82);
        this.seat5.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat5.addBox(-1.0F, 22.0F, 12.0F, 2, 4, 8, 0.0F);
        this.seat6 = new ModelRenderer(this, 156, 82);
        this.seat6.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat6.addBox(-7.0F, 22.0F, 12.0F, 2, 4, 8, 0.0F);
        this.seat7 = new ModelRenderer(this, 232, 0);
        this.seat7.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat7.addBox(-1.0F, -2.0F, 20.0F, 2, 28, 2, 0.0F);
        this.seat8 = new ModelRenderer(this, 232, 0);
        this.seat8.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat8.addBox(-7.0F, -2.0F, 20.0F, 2, 28, 2, 0.0F);
        this.seat9 = new ModelRenderer(this, 232, 2);
        this.seat9.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat9.addBox(2.0F, -2.0F, 20.0F, 2, 22, 2, 0.0F);
        this.seat10 = new ModelRenderer(this, 232, 30);
        this.seat10.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat10.addBox(-4.0F, -4.0F, 20.0F, 2, 20, 2, 0.0F);
        this.seat11 = new ModelRenderer(this, 232, 2);
        this.seat11.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat11.addBox(-10.0F, -2.0F, 20.0F, 2, 22, 2, 0.0F);
        this.seat12 = new ModelRenderer(this, 240, 0);
        this.seat12.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat12.addBox(2.0F, -6.0F, 20.0F, 2, 4, 4, 0.0F);
        this.seat13 = new ModelRenderer(this, 240, 0);
        this.seat13.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat13.addBox(-4.0F, -8.0F, 20.0F, 2, 4, 4, 0.0F);
        this.seat14 = new ModelRenderer(this, 240, 0);
        this.seat14.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.seat14.addBox(-10.0F, -6.0F, 20.0F, 2, 4, 4, 0.0F);

        //tube
        this.tube = new ModelRenderer(this, 0, 32);
        this.tube.setRotationPoint(0.0F, -12.0F, -12.0F);
        this.tube.addBox(-2.0F, -4.0F, -4.0F, 14, 8, 8, 0.0F);
        this.setRotateAngle(tube, -0.7853981633974483F, 0.0F, 0.0F);

        this.tube1 = new ModelRenderer(this, 92, 0);
        this.tube1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube1.addBox(-2.0F, -4.0F, -36.0F, 14, 6, 6, 0.0F);
        this.tube2 = new ModelRenderer(this, 78, 90);
        this.tube2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube2.addBox(2.0F, -2.0F, -30.0F, 2, 2, 26, 0.0F);
        this.tube3 = new ModelRenderer(this, 78, 90);
        this.tube3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube3.addBox(6.0F, -2.0F, -30.0F, 2, 2, 26, 0.0F);
        this.tube4 = new ModelRenderer(this, 92, 28);
        this.tube4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube4.addBox(-2.0F, -16.0F, -2.0F, 14, 8, 2, 0.0F);
        this.tube5 = new ModelRenderer(this, 92, 12);
        this.tube5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube5.addBox(-2.0F, -7.0F, -2.0F, 14, 2, 2, 0.0F);
        this.tube6 = new ModelRenderer(this, 92, 12);
        this.tube6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube6.addBox(-2.0F, -16.0F, -34.0F, 14, 8, 2, 0.0F);
        this.tube7 = new ModelRenderer(this, 92, 12);
        this.tube7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube7.addBox(-2.0F, -7.0F, -34.0F, 14, 2, 2, 0.0F);
        this.tube8 = new ModelRenderer(this, 92, 12);
        this.tube8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube8.addBox(-2.0F, -16.0F, -40.0F, 14, 14, 2, 0.0F);
        this.tube9 = new ModelRenderer(this, 0, 0);
        this.tube9.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube9.addBox(-2.0F, -16.0F, -60.0F, 14, 14, 18, 0.0F);
        this.tube10 = new ModelRenderer(this, 0, 0);
        this.tube10.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube10.addBox(0.0F, -14.0F, -56.0F, 10, 10, 68, 0.0F);
        this.tube11 = new ModelRenderer(this, 92, 50);
        this.tube11.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube11.addBox(-4.0F, -10.0F, 2.0F, 4, 4, 12, 0.0F);
        this.tube12 = new ModelRenderer(this, 44, 32);
        this.tube12.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube12.addBox(-4.0F, -10.0F, 14.0F, 2, 2, 6, 0.0F);
        this.tube13 = new ModelRenderer(this, 0, 48);
        this.tube13.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube13.addBox(2.0F, -12.0F, 12.0F, 6, 6, 4, 0.0F);
        this.tube14 = new ModelRenderer(this, 92, 0);
        this.tube14.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube14.addBox(6.0F, -18.0F, -44.0F, 2, 2, 48, 0.0F);
        this.tube15 = new ModelRenderer(this, 92, 0);
        this.tube15.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tube15.addBox(2.0F, -18.0F, -44.0F, 2, 2, 48, 0.0F);

        this.tube.addChild(this.tube6);
        this.seat.addChild(this.seat7);
        this.tube.addChild(this.tube5);
        this.seat.addChild(this.seat3);
        this.seat.addChild(this.seat9);
        this.tube.addChild(this.tube1);
        this.tube.addChild(this.tube14);
        this.seat.addChild(this.seat6);
        this.seat.addChild(this.seat11);
        this.base.addChild(this.base4);
        this.tube.addChild(this.tube11);
        this.seat.addChild(this.seat13);
        this.seat.addChild(this.seat8);
        this.seat.addChild(this.seat12);
        this.seat.addChild(this.seat14);
        this.tube.addChild(this.tube3);
        this.tube.addChild(this.tube13);
        this.tube.addChild(this.tube15);
        this.base.addChild(this.base6);
        this.tube.addChild(this.tube8);
        this.base.addChild(this.base1);
        this.seat.addChild(this.seat10);
        this.tube.addChild(this.tube12);
        this.tube.addChild(this.tube2);
        this.tube.addChild(this.tube4);
        this.seat.addChild(this.seat5);
        this.base.addChild(this.base5);
        this.base.addChild(this.base7);
        this.tube.addChild(this.tube7);
        this.seat.addChild(this.seat1);
        this.seat.addChild(this.seat2);
        this.base.addChild(this.base3);
        this.seat.addChild(this.seat4);
        this.base.addChild(this.base8);
        this.base.addChild(this.base2);
        this.tube.addChild(this.tube10);
        this.tube.addChild(this.tube9);

        this.seat.addChild(this.tube);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.seat.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.base.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void setupRotations(float iYawDegree, float iPitchDegree) {
        float yawRad = (float) Math.toRadians(iYawDegree);
        float pitchRad = (float) Math.toRadians(iPitchDegree);

        this.seat.rotateAngleY = yawRad;
        this.base.rotateAngleY = yawRad;
        this.tube.rotateAngleX = pitchRad;
    }
}
