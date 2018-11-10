/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.models.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TCNModelGrindstone
 * Created by wiiv
 * Created using Tabula 4.1.1
 * Date: 18.09.2016
 */
public class ASgrindstone extends ModelBase {

    public ModelRenderer wheel;
    public ModelRenderer mount1;
    public ModelRenderer shape1;
    public ModelRenderer sit1;
    public ModelRenderer sit2;
    public ModelRenderer shield;
    public ModelRenderer sit1_1;
    public ModelRenderer sit1_2;
    public ModelRenderer sit2_1;
    public ModelRenderer sit2_2;
    public ModelRenderer sit1_3;
    public ModelRenderer sit2_3;
    public ModelRenderer sit2_4;
    public ModelRenderer sit1_4;
    public ModelRenderer sit1_5;
    public ModelRenderer sit2_5;
    public ModelRenderer sit2_6;
    public ModelRenderer axis;

    public ASgrindstone() {

        this.textureWidth = 64;
        this.textureHeight = 64;

        this.sit2_1 = new ModelRenderer(this, 32, 0);
        this.sit2_1.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit2_1.addBox(-5.0F, 2.0F, 10.0F, 2, 4, 2, 0.0F);
        this.mount1 = new ModelRenderer(this, 16, 24);
        this.mount1.setRotationPoint(0.0F, 13.0F, 7.0F);
        this.mount1.addBox(-5.0F, -3.0F, -3.0F, 2, 6, 6, 0.0F);
        this.sit2_3 = new ModelRenderer(this, 32, 6);
        this.sit2_3.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit2_3.addBox(-5.0F, 2.0F, -8.0F, 2, 2, 2, 0.0F);
        this.sit2 = new ModelRenderer(this, 32, 28);
        this.sit2.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit2.addBox(-5.0F, 2.0F, -2.0F, 10, 6, 2, 0.0F);
        this.sit1_1 = new ModelRenderer(this, 32, 14);
        this.sit1_1.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit1_1.addBox(3.0F, 0.0F, 0.0F, 2, 2, 12, 0.0F);
        this.sit1_3 = new ModelRenderer(this, 0, 46);
        this.sit1_3.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit1_3.addBox(-7.0F, -2.0F, -10.0F, 14, 2, 8, 0.0F);
        this.axis = new ModelRenderer(this, 36, 46);
        this.axis.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.axis.addBox(-4.0F, -1.0F, -1.0F, 8, 2, 2, 0.0F);
        this.wheel = new ModelRenderer(this, 0, 0);
        this.wheel.setRotationPoint(0.0F, 13.1F, 7.0F);
        this.wheel.addBox(-2.0F, -6.0F, -6.0F, 4, 12, 12, 0.0F);
        this.shape1 = new ModelRenderer(this, 0, 24);
        this.shape1.setRotationPoint(0.0F, 13.0F, 7.0F);
        this.shape1.addBox(3.0F, -3.0F, -3.0F, 2, 6, 6, 0.0F);
        this.sit1 = new ModelRenderer(this, 0, 36);
        this.sit1.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit1.addBox(-5.0F, 0.0F, -8.0F, 10, 2, 8, 0.0F);
        this.shield = new ModelRenderer(this, 36, 36);
        this.shield.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.shield.addBox(-3.0F, -8.0F, -2.0F, 6, 8, 2, 0.0F);
        this.sit1_2 = new ModelRenderer(this, 32, 14);
        this.sit1_2.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit1_2.addBox(-5.0F, 0.0F, 0.0F, 2, 2, 12, 0.0F);
        this.sit2_6 = new ModelRenderer(this, 0, 4);
        this.sit2_6.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit2_6.addBox(5.0F, -4.0F, 8.0F, 4, 2, 2, 0.0F);
        this.sit2_4 = new ModelRenderer(this, 32, 6);
        this.sit2_4.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit2_4.addBox(3.0F, 2.0F, -8.0F, 2, 2, 2, 0.0F);
        this.sit1_5 = new ModelRenderer(this, 32, 0);
        this.sit1_5.setRotationPoint(0.0F, 22.0F, -2.0F);
        this.sit1_5.addBox(-5.0F, 0.0F, 0.0F, 2, 2, 12, 0.0F);
        this.sit2_2 = new ModelRenderer(this, 32, 0);
        this.sit2_2.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit2_2.addBox(3.0F, 2.0F, 10.0F, 2, 4, 2, 0.0F);
        this.sit1_4 = new ModelRenderer(this, 32, 0);
        this.sit1_4.setRotationPoint(0.0F, 22.0F, -2.0F);
        this.sit1_4.addBox(3.0F, 0.0F, 0.0F, 2, 2, 12, 0.0F);
        this.sit2_5 = new ModelRenderer(this, 0, 0);
        this.sit2_5.setRotationPoint(0.0F, 16.0F, -2.0F);
        this.sit2_5.addBox(-7.0F, -4.0F, 8.0F, 2, 2, 2, 0.0F);
        this.wheel.addChild(this.axis);
    }

    @Override
    public void render(Entity entity, float degRotationWheel, float f1, float f2, float f3, float f4, float scale) {
        this.wheel.rotateAngleX = (degRotationWheel - 15) * 0.017453292F;

        this.sit2_1.render(scale);
        this.mount1.render(scale);
        this.sit2_3.render(scale);
        this.sit2.render(scale);
        this.sit1_1.render(scale);
        this.sit1_3.render(scale);
        this.wheel.render(scale);
        this.shape1.render(scale);
        this.sit1.render(scale);
        this.shield.render(scale);
        this.sit1_2.render(scale);
        this.sit2_6.render(scale);
        this.sit2_4.render(scale);
        this.sit1_5.render(scale);
        this.sit2_2.render(scale);
        this.sit1_4.render(scale);
        this.sit2_5.render(scale);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}