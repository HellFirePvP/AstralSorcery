/*package model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * astralsorcery_altarT3 - wiiv
 * Created using Tabula 4.1.1
 */
/*public class ASaltarT3 extends ModelBase {
    public ModelRenderer hovering1;
    public ModelRenderer hovering2;
    public ModelRenderer hovering3;
    public ModelRenderer hovering4;
    public ModelRenderer subhovering1;
    public ModelRenderer subhovering2;
    public ModelRenderer subhovering3;
    public ModelRenderer subhovering4;

    public ASaltarT3() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.hovering1 = new ModelRenderer(this, 0, 0);
        this.hovering1.setRotationPoint(-9.0F, 8.0F, -9.0F);
        this.hovering1.addBox(-9.0F, 0.0F, -9.0F, 9, 6, 9, 0.0F);
        this.setRotateAngle(hovering1, -0.39269908169872414F, 0.0F, 0.39269908169872414F);
		this.hovering2 = new ModelRenderer(this, 0, 15);
        this.hovering2.setRotationPoint(9.0F, 8.0F, -9.0F);
        this.hovering2.addBox(0.0F, 0.0F, -9.0F, 9, 6, 9, 0.0F);
        this.setRotateAngle(hovering2, -0.39269908169872414F, 0.0F, -0.39269908169872414F);
        this.hovering3 = new ModelRenderer(this, 0, 30);
        this.hovering3.setRotationPoint(9.0F, 8.0F, 9.0F);
        this.hovering3.addBox(0.0F, 0.0F, 0.0F, 9, 6, 9, 0.0F);
        this.setRotateAngle(hovering3, 0.39269908169872414F, 0.0F, -0.39269908169872414F);
        this.hovering4 = new ModelRenderer(this, 0, 45);
        this.hovering4.setRotationPoint(-9.0F, 8.0F, 9.0F);
        this.hovering4.addBox(-9.0F, 0.0F, 0.0F, 9, 6, 9, 0.0F);
        this.setRotateAngle(hovering4, 0.39269908169872414F, 0.0F, 0.39269908169872414F);
        this.subhovering1 = new ModelRenderer(this, 40, 0);
        this.subhovering1.setRotationPoint(0.0F, 8.0F, -12.0F);
        this.subhovering1.addBox(-3.0F, 0.0F, -6.0F, 6, 6, 6, 0.0F);
        this.setRotateAngle(subhovering1, -0.39269908169872414F, 0.0F, 0.0F);
        this.subhovering2 = new ModelRenderer(this, 40, 12);
        this.subhovering2.setRotationPoint(12.0F, 8.0F, 0.0F);
        this.subhovering2.addBox(0.0F, 0.0F, -3.0F, 6, 6, 6, 0.0F);
        this.setRotateAngle(subhovering2, 0.0F, 0.0F, -0.39269908169872414F);
        this.subhovering3 = new ModelRenderer(this, 40, 24);
        this.subhovering3.setRotationPoint(0.0F, 8.0F, 12.0F);
        this.subhovering3.addBox(-3.0F, 0.0F, 0.0F, 6, 6, 6, 0.0F);
        this.setRotateAngle(subhovering3, 0.39269908169872414F, 0.0F, 0.0F);
		this.subhovering4 = new ModelRenderer(this, 40, 36);
        this.subhovering4.setRotationPoint(-12.0F, 8.0F, 0.0F);
        this.subhovering4.addBox(-6.0F, 0.0F, -3.0F, 6, 6, 6, 0.0F);
        this.setRotateAngle(subhovering4, 0.0F, 0.0F, 0.39269908169872414F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.subhovering3.render(f5);
        this.hovering2.render(f5);
        this.subhovering4.render(f5);
        this.hovering3.render(f5);
        this.hovering4.render(f5);
        this.subhovering1.render(f5);
        this.subhovering2.render(f5);
        this.hovering1.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    /*public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
*/