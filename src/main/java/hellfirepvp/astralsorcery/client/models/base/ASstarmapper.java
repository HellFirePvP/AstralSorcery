package hellfirepvp.astralsorcery.client.models.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * AS_starmapper - wiiv
 * Created using Tabula 4.1.1
 */
public class ASstarmapper extends ModelBase {

    public ModelRenderer treated_glass;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;
    public ModelRenderer leg_base1;
    public ModelRenderer leg_base2;
    public ModelRenderer leg_base3;
    public ModelRenderer leg_base4;
    public ModelRenderer top_fitting1;
    public ModelRenderer top_fitting2;
    public ModelRenderer parchment;
    public ModelRenderer platform;
    public ModelRenderer basin;

    public ASstarmapper() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.leg_base4 = new ModelRenderer(this, 16, 95);
        this.leg_base4.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_base4.addBox(-16.0F, 2.0F, 10.0F, 6, 6, 6, 0.0F);
        this.parchment = new ModelRenderer(this, 60, 34);
        this.parchment.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.parchment.addBox(-10.0F, -8.5F, -10.0F, 20, 0, 20, 0.0F);
        this.platform = new ModelRenderer(this, 0, 0);
        this.platform.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.platform.addBox(-16.0F, -7.0F, -16.0F, 32, 2, 32, 0.0F);
        this.leg_base2 = new ModelRenderer(this, 16, 95);
        this.leg_base2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_base2.addBox(10.0F, 2.0F, -16.0F, 6, 6, 6, 0.0F);
        this.leg4 = new ModelRenderer(this, 0, 95);
        this.leg4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.leg4.addBox(-15.0F, 2.0F, 11.0F, 4, 16, 4, 0.0F);
        this.leg_base3 = new ModelRenderer(this, 16, 95);
        this.leg_base3.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_base3.addBox(10.0F, 2.0F, 10.0F, 6, 6, 6, 0.0F);
        this.top_fitting2 = new ModelRenderer(this, 0, 60);
        this.top_fitting2.mirror = true;
        this.top_fitting2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.top_fitting2.addBox(10.0F, 0.0F, -16.0F, 6, 3, 32, 0.0F);
        this.leg2 = new ModelRenderer(this, 0, 95);
        this.leg2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.leg2.addBox(11.0F, 2.0F, -15.0F, 4, 16, 4, 0.0F);
        this.top_fitting1 = new ModelRenderer(this, 0, 60);
        this.top_fitting1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.top_fitting1.addBox(-16.0F, 0.0F, -16.0F, 6, 3, 32, 0.0F);
        this.leg1 = new ModelRenderer(this, 0, 95);
        this.leg1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.leg1.addBox(-15.0F, 2.0F, -15.0F, 4, 16, 4, 0.0F);
        this.treated_glass = new ModelRenderer(this, 0, 107);
        this.treated_glass.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.treated_glass.addBox(-10.0F, -15.0F, -10.0F, 20, 1, 20, 0.0F);
        this.leg3 = new ModelRenderer(this, 0, 95);
        this.leg3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.leg3.addBox(11.0F, 2.0F, 11.0F, 4, 16, 4, 0.0F);
        this.leg_base1 = new ModelRenderer(this, 16, 95);
        this.leg_base1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_base1.addBox(-16.0F, 2.0F, -16.0F, 6, 6, 6, 0.0F);
        this.basin = new ModelRenderer(this, 0, 34);
        this.basin.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.basin.addBox(-10.0F, -8.0F, -10.0F, 20, 6, 20, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.leg_base4.render(f5);
        if(f != 0) {
            this.parchment.render(f5);
        }
        this.platform.render(f5);
        this.leg_base2.render(f5);
        this.leg4.render(f5);
        this.leg_base3.render(f5);
        this.top_fitting2.render(f5);
        this.leg2.render(f5);
        this.top_fitting1.render(f5);
        this.leg1.render(f5);
        this.treated_glass.render(f5);
        this.leg3.render(f5);
        this.leg_base1.render(f5);
        this.basin.render(f5);
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