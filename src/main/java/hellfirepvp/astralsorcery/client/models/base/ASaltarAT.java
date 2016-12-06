package hellfirepvp.astralsorcery.client.models.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * astralsorcery_altarat2 - wiiv
 * Created using Tabula 4.1.1
 */
public class ASaltarAT extends ModelBase {
    public ModelRenderer base;
    public ModelRenderer subhovering1;
    public ModelRenderer subhovering2;
    public ModelRenderer subhovering3;
    public ModelRenderer subhovering4;
    public ModelRenderer subhovering5;
    public ModelRenderer subhovering6;
    public ModelRenderer subhovering7;
    public ModelRenderer subhovering8;

    public ASaltarAT() {
        this.textureWidth = 128;
        this.textureHeight = 32;
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.base.addBox(-10.0F, -14.0F, -10.0F, 20, 6, 20, 0.0F);
        this.subhovering2 = new ModelRenderer(this, 0, 0);
        this.subhovering2.setRotationPoint(0.0F, -16.0F, -18.0F);
        this.subhovering2.addBox(-2.0F, 0.0F, -4.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(subhovering2, -0.39269908169872414F, 0.0F, 0.0F);
        this.subhovering4 = new ModelRenderer(this, 0, 0);
        this.subhovering4.setRotationPoint(18.0F, -16.0F, 0.0F);
        this.subhovering4.addBox(0.0F, 0.0F, -2.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(subhovering4, 0.0F, 0.0F, -0.39269908169872414F);
        this.subhovering1 = new ModelRenderer(this, 0, 0);
        this.subhovering1.setRotationPoint(-14.0F, -16.0F, -14.0F);
        this.subhovering1.addBox(-2.0F, 0.0F, -4.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(subhovering1, -0.39269908169872414F, 0.7853981633974483F, 0.0F);
        this.subhovering3 = new ModelRenderer(this, 0, 0);
        this.subhovering3.setRotationPoint(14.0F, -16.0F, -14.0F);
        this.subhovering3.addBox(-2.0F, 0.0F, -4.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(subhovering3, -0.39269908169872414F, -0.7853981633974483F, 0.0F);
        this.subhovering6 = new ModelRenderer(this, 0, 0);
        this.subhovering6.setRotationPoint(0.0F, -16.0F, 18.0F);
        this.subhovering6.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(subhovering6, 0.39269908169872414F, 0.0F, 0.0F);
        this.subhovering8 = new ModelRenderer(this, 0, 0);
        this.subhovering8.setRotationPoint(-18.0F, -16.0F, 0.0F);
        this.subhovering8.addBox(-4.0F, 0.0F, -2.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(subhovering8, 0.0F, 0.0F, 0.39269908169872414F);
        this.subhovering7 = new ModelRenderer(this, 0, 0);
        this.subhovering7.setRotationPoint(-14.0F, -16.0F, 14.0F);
        this.subhovering7.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(subhovering7, 0.39269908169872414F, -0.7853981633974483F, 0.0F);
        this.subhovering5 = new ModelRenderer(this, 0, 0);
        this.subhovering5.setRotationPoint(14.0F, -16.0F, 14.0F);
        this.subhovering5.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(subhovering5, 0.39269908169872414F, 0.7853981633974483F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.base.render(f5);
        this.subhovering2.render(f5);
        this.subhovering4.render(f5);
        this.subhovering1.render(f5);
        this.subhovering3.render(f5);
        this.subhovering6.render(f5);
        this.subhovering8.render(f5);
        this.subhovering7.render(f5);
        this.subhovering5.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
