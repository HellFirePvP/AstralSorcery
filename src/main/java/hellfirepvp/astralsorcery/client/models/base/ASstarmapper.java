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
 * AS_starmapper - wiiv
 * Created using Tabula 7.0.0
 */
public class ASstarmapper extends ModelBase {

    public ModelRenderer fitting_l;
    public ModelRenderer fitting_r;
    public ModelRenderer support_1;
    public ModelRenderer support_2;
    public ModelRenderer support_3;
    public ModelRenderer support_4;
    public ModelRenderer platform_l;
    public ModelRenderer platform_r;
    public ModelRenderer platform_f;
    public ModelRenderer platform_b;
    public ModelRenderer basin_l;
    public ModelRenderer basim_r;
    public ModelRenderer basin_f;
    public ModelRenderer basin_b;
    public ModelRenderer socket;
    public ModelRenderer base;
    public ModelRenderer leg_1;
    public ModelRenderer leg_2;
    public ModelRenderer leg_3;
    public ModelRenderer leg_4;

    public ModelRenderer parchment;
    public ModelRenderer black_mirror;

    public ModelRenderer treated_glass;

    public ASstarmapper() {

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
    public void render(Entity entity, float renderParchment, float renderGlass, float f2, float f3, float f4, float f5) {

        this.fitting_l.render(f5);
        this.fitting_r.render(f5);
        this.support_1.render(f5);
        this.support_2.render(f5);
        this.support_3.render(f5);
        this.support_4.render(f5);
        this.platform_l.render(f5);
        this.platform_r.render(f5);
        this.platform_f.render(f5);
        this.platform_b.render(f5);
        this.basin_l.render(f5);
        this.basim_r.render(f5);
        this.basin_f.render(f5);
        this.basin_b.render(f5);
        this.socket.render(f5);
        this.base.render(f5);
        this.leg_1.render(f5);
        this.leg_2.render(f5);
        this.leg_3.render(f5);
        this.leg_4.render(f5);

        if(renderParchment != 0) {
            this.parchment.render(f5);
            this.black_mirror.render(f5);
        }
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