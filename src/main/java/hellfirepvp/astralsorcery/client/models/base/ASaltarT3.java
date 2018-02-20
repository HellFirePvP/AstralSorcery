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
public class ASaltarT3 extends ModelBase {

    private static float jmpParts = (float) (Math.PI / 8F);

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
    public void render(Entity entity, float jump, float f1, float f2, float f3, float f4, float scale) {
        renderHovering(subhovering3, jump,                1);
        renderHovering(subhovering1, jump + jmpParts,     1);
        renderHovering(hovering4,    jump + jmpParts * 2, 1);
        renderHovering(subhovering2, jump + jmpParts * 3, 1);
        renderHovering(hovering1,    jump + jmpParts * 4, 1);
        renderHovering(hovering2,    jump + jmpParts * 5, 1);
        renderHovering(subhovering4, jump + jmpParts * 6, 1);
        renderHovering(hovering3,    jump + jmpParts * 7, 1);
    }

    private void renderHovering(ModelRenderer model, float jump, float scale) {
        float tickValSin = (float) (Math.sin(jump) * 1.25);
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