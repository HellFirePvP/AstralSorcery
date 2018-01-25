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
public class AStelescope extends ModelBase {

    public ModelRenderer mountpiece;
    public ModelRenderer opticalTube;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer mount;
    public ModelRenderer aperture;
    public ModelRenderer extension1;
    public ModelRenderer extension2;

    public AStelescope() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.mountpiece = new ModelRenderer(this, 40, 22);
        this.mountpiece.setRotationPoint(0.0F, -2.0F, -1.0F);
        this.mountpiece.addBox(-2.0F, 6.0F, -2.0F, 4, 4, 4, 0.0F);
        this.leg3 = new ModelRenderer(this, 52, 0);
        this.leg3.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.leg3.addBox(-0.5F, 0.0F, -1.0F, 1, 20, 2, 0.0F);
        this.setRotateAngle(leg3, -0.39269908169872414F, 0.7853981633974483F, 0.0F);
        this.leg1 = new ModelRenderer(this, 46, 0);
        this.leg1.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.leg1.addBox(-0.5F, 0.0F, -1.0F, 1, 20, 2, 0.0F);
        this.setRotateAngle(leg1, 0.39269908169872414F, 0.0F, 0.0F);
        this.extension2 = new ModelRenderer(this, 14, 20);
        this.extension2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.extension2.addBox(-1.0F, -1.0F, 8.0F, 2, 2, 4, 0.0F);
        this.aperture = new ModelRenderer(this, 24, 0);
        this.aperture.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.aperture.addBox(-2.5F, -2.5F, -13.0F, 5, 5, 6, 0.0F);
        this.opticalTube = new ModelRenderer(this, 0, 0);
        this.opticalTube.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.opticalTube.addBox(-2.0F, -2.0F, -12.0F, 4, 4, 16, 0.0F);
        this.setRotateAngle(opticalTube, -0.7853981633974483F, 0.0F, 0.0F);
        this.leg2 = new ModelRenderer(this, 52, 0);
        this.leg2.mirror = true;
        this.leg2.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.leg2.addBox(-0.5F, 0.0F, -1.0F, 1, 20, 2, 0.0F);
        this.setRotateAngle(leg2, -0.39269908169872414F, -0.7853981633974483F, 0.0F);
        this.mount = new ModelRenderer(this, 56, 22);
        this.mount.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.mount.addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2, 0.0F);
        this.extension1 = new ModelRenderer(this, 0, 20);
        this.extension1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.extension1.addBox(-1.5F, -1.5F, 4.0F, 3, 3, 4, 0.0F);
        this.mountpiece.addChild(this.leg3);
        this.mountpiece.addChild(this.leg1);
        this.opticalTube.addChild(this.extension2);
        this.opticalTube.addChild(this.aperture);
        this.mountpiece.addChild(this.leg2);
        this.mountpiece.addChild(this.mount);
        this.opticalTube.addChild(this.extension1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.mountpiece.render(f5);
        this.opticalTube.render(f5);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
