/*package model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * astralsorcery_lens - wiiv
 * Created using Tabula 4.1.1
 */
/*public class ASlens extends ModelBase {
    public ModelRenderer Base;
    public ModelRenderer frame1;
    public ModelRenderer lens;
    public ModelRenderer frame2;

    public ASlens() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Base = new ModelRenderer(this, 0, 13);
        this.Base.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.Base.addBox(-6.0F, 4.0F, -6.0F, 12, 2, 12, 0.0F);
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
        this.setRotateAngle(lens, 0.2617993877991494F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.Base.render(f5);
        this.frame1.render(f5);
        this.frame2.render(f5);
        this.lens.render(f5);
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