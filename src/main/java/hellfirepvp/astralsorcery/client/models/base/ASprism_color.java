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
import org.lwjgl.opengl.GL11;

/**
 * astralsorcery_prism_coloured - wiiv
 * Created using Tabula 4.1.1
 */
public class ASprism_color extends ModelBase {

    public ModelRenderer glass;
    public ModelRenderer fitting1;
    public ModelRenderer fitting2;
    public ModelRenderer fitting3;
    public ModelRenderer fitting4;
    public ModelRenderer fitting5;
    public ModelRenderer fitting6;
    public ModelRenderer fitting7;
    public ModelRenderer fitting8;

    public ASprism_color() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.fitting4 = new ModelRenderer(this, 0, 0);
        this.fitting4.mirror = true;
        this.fitting4.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.fitting4.addBox(-2.0F, 4.0F, 6.01F, 1, 2, 2, 0.0F);
        this.fitting8 = new ModelRenderer(this, 0, 4);
        this.fitting8.mirror = true;
        this.fitting8.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.fitting8.addBox(-8.01F, 4.0F, -2.0F, 2, 2, 1, 0.0F);
        this.fitting6 = new ModelRenderer(this, 0, 4);
        this.fitting6.mirror = true;
        this.fitting6.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.fitting6.addBox(6.01F, 4.0F, -2.0F, 2, 2, 1, 0.0F);
        this.fitting7 = new ModelRenderer(this, 0, 4);
        this.fitting7.mirror = true;
        this.fitting7.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.fitting7.addBox(-8.01F, 4.0F, 1.0F, 2, 2, 1, 0.0F);
        this.fitting1 = new ModelRenderer(this, 0, 0);
        this.fitting1.mirror = true;
        this.fitting1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.fitting1.addBox(1.0F, 4.0F, -8.01F, 1, 2, 2, 0.0F);
        this.fitting5 = new ModelRenderer(this, 0, 4);
        this.fitting5.mirror = true;
        this.fitting5.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.fitting5.addBox(6.01F, 4.0F, 1.0F, 2, 2, 1, 0.0F);
        this.glass = new ModelRenderer(this, 0, 0);
        this.glass.setRotationPoint(0.0F, 15.0F, 0.0F);
        this.glass.addBox(-6.0F, -7.0F, -6.0F, 12, 14, 12, 0.0F);
        this.fitting3 = new ModelRenderer(this, 0, 0);
        this.fitting3.mirror = true;
        this.fitting3.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.fitting3.addBox(1.0F, 4.0F, 6.01F, 1, 2, 2, 0.0F);
        this.fitting2 = new ModelRenderer(this, 0, 0);
        this.fitting2.mirror = true;
        this.fitting2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.fitting2.addBox(-2.0F, 4.0F, -8.01F, 1, 2, 2, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
        this.glass.render(scale);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        this.fitting4.render(scale);
        this.fitting8.render(scale);
        this.fitting6.render(scale);
        this.fitting7.render(scale);
        this.fitting1.render(scale);
        this.fitting5.render(scale);
        this.fitting3.render(scale);
        this.fitting2.render(scale);
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
