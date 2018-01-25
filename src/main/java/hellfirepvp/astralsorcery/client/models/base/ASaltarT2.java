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
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TCNModelGrindstone
 * Created by wiiv
 * Created using Tabula 4.1.1
 * Date: 18.09.2016
 */
public class ASaltarT2 extends ModelBase {

    private static float jmpParts = (float) (Math.PI / 4F);

    public ModelRenderer hovering1;
    public ModelRenderer hovering2;
    public ModelRenderer hovering3;
    public ModelRenderer hovering4;

    public ASaltarT2() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.hovering1 = new ModelRenderer(this, 0, 42);
        this.hovering1.setRotationPoint(-9.0F, 8.0F, -9.0F);
        this.hovering1.addBox(-6.0F, 0.0F, -6.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(hovering1, -0.39269908169872414F, 0.0F, 0.39269908169872414F);
		this.hovering2 = new ModelRenderer(this, 0, 28);
        this.hovering2.setRotationPoint(9.0F, 8.0F, -9.0F);
        this.hovering2.addBox(0.0F, 0.0F, -6.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(hovering2, -0.39269908169872414F, 0.0F, -0.39269908169872414F);
        this.hovering3 = new ModelRenderer(this, 0, 14);
        this.hovering3.setRotationPoint(9.0F, 8.0F, 9.0F);
        this.hovering3.addBox(0.0F, 0.0F, 0.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(hovering3, 0.39269908169872414F, 0.0F, -0.39269908169872414F);
        this.hovering4 = new ModelRenderer(this, 0, 0);
        this.hovering4.setRotationPoint(-9.0F, 8.0F, 9.0F);
        this.hovering4.addBox(-6.0F, 0.0F, 0.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(hovering4, 0.39269908169872414F, 0.0F, 0.39269908169872414F);
    }

    @Override
    public void render(Entity entity, float jump, float f1, float f2, float f3, float f4, float scale) {
        renderHovering(hovering1, jump,                1);
        renderHovering(hovering3, jump + jmpParts,     1);
        renderHovering(hovering4, jump + jmpParts * 2, 1);
        renderHovering(hovering2, jump + jmpParts * 3, 1);
    }

    private void renderHovering(ModelRenderer model, float jump, float scale) {
        float tickValSin = (float) (Math.sin(jump) * 0.625);
        GL11.glTranslatef(0, tickValSin, 0);
        model.render(scale);
        GL11.glTranslatef(0, -tickValSin, 0);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}