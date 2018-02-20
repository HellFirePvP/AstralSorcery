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
public class ASgrindingstone extends ModelBase {

    public ModelRenderer wheel;
    public ModelRenderer frame1;
    public ModelRenderer frame2;
    public ModelRenderer frame3;
    public ModelRenderer frame4;
    public ModelRenderer mount1;
    public ModelRenderer shape1;
    public ModelRenderer sit1;
    public ModelRenderer sit2;
    public ModelRenderer shield;
    public ModelRenderer axis;

    public ASgrindingstone() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.sit1 = new ModelRenderer(this, 20, 0);
        this.sit1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.sit1.addBox(-3.0F, 1.0F, -8.0F, 6, 1, 6, 0.0F);
        this.wheel = new ModelRenderer(this, 0, 0);
        this.wheel.setRotationPoint(0.0F, 16.0F, 3.5F);
        this.wheel.addBox(-1.5F, -3.5F, -3.5F, 3, 7, 7, 0.0F);
        this.setRotateAngle(wheel, 3.141592653589793F, 0.0F, 0.0F);
        this.frame1 = new ModelRenderer(this, 0, 14);
        this.frame1.setRotationPoint(0.0F, 16.5F, 0.0F);
        this.frame1.addBox(-3.0F, -2.0F, 2.5F, 1, 10, 1, 0.0F);
        this.setRotateAngle(frame1, -0.7853981633974483F, 0.0F, 0.0F);
        this.mount1 = new ModelRenderer(this, 8, 14);
        this.mount1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.mount1.addBox(-4.0F, -2.0F, 1.5F, 2, 4, 4, 0.0F);
        this.sit2 = new ModelRenderer(this, 20, 7);
        this.sit2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.sit2.addBox(-2.0F, 2.0F, -6.0F, 4, 6, 4, 0.0F);
        this.frame2 = new ModelRenderer(this, 0, 14);
        this.frame2.setRotationPoint(0.0F, 16.5F, 0.0F);
        this.frame2.addBox(2.0F, -2.0F, 2.5F, 1, 10, 1, 0.0F);
        this.setRotateAngle(frame2, -0.7853981633974483F, 0.0F, 0.0F);
        this.axis = new ModelRenderer(this, 8, 22);
        this.axis.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.axis.addBox(-3.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
        this.frame4 = new ModelRenderer(this, 4, 14);
        this.frame4.setRotationPoint(0.0F, 17.0F, 0.0F);
        this.frame4.addBox(2.0F, 2.0F, 2.5F, 1, 8, 1, 0.0F);
        this.setRotateAngle(frame4, 0.39269908169872414F, 0.0F, 0.0F);
        this.frame3 = new ModelRenderer(this, 4, 14);
        this.frame3.setRotationPoint(0.0F, 17.0F, 0.0F);
        this.frame3.addBox(-3.0F, 2.0F, 2.5F, 1, 8, 1, 0.0F);
        this.setRotateAngle(frame3, 0.39269908169872414F, 0.0F, 0.0F);
        this.shield = new ModelRenderer(this, 36, 7);
        this.shield.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.shield.addBox(-2.0F, -4.0F, -3.0F, 4, 5, 1, 0.0F);
        this.shape1 = new ModelRenderer(this, 8, 14);
        this.shape1.mirror = true;
        this.shape1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.shape1.addBox(2.0F, -2.0F, 1.5F, 2, 4, 4, 0.0F);
        this.wheel.addChild(this.axis);
    }

    @Override
    public void render(Entity entity, float degRotationWheel, float f1, float f2, float f3, float f4, float scale) {
        this.wheel.rotateAngleX = (degRotationWheel - 15) * 0.017453292F;

        this.sit1.render(scale);
        this.wheel.render(scale);
        this.frame1.render(scale);
        this.mount1.render(scale);
        this.sit2.render(scale);
        this.frame2.render(scale);
        this.frame4.render(scale);
        this.frame3.render(scale);
        this.shield.render(scale);
        this.shape1.render(scale);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}