package hellfirepvp.astralsorcery.client.models.tcn;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TCNModelLensHandle
 * Created by HellFirePvP
 * Date: 04.10.2016 / 23:36
 */
public class TCNModelLensHandle extends ModelBase {

    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;

    ModelRenderer ShapeLens;

    public TCNModelLensHandle() {
        textureWidth = 64;
        textureHeight = 32;

        Shape1 = new ModelRenderer(this, 0, 0);
        Shape1.addBox(0F, 0F, 0F, 14, 1, 14);
        Shape1.setRotationPoint(-7F, 13F, -7F);
        Shape1.setTextureSize(64, 32);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
        Shape2 = new ModelRenderer(this, 0, 15);
        Shape2.addBox(0F, 0F, 0F, 2, 8, 1);
        Shape2.setRotationPoint(-1F, 5F, 6F);
        Shape2.setTextureSize(64, 32);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);
        Shape3 = new ModelRenderer(this, 0, 15);
        Shape3.addBox(0F, 0F, 0F, 2, 8, 1);
        Shape3.setRotationPoint(-1F, 5F, -7F);
        Shape3.setTextureSize(64, 32);
        Shape3.mirror = true;
        setRotation(Shape3, 0F, 0F, 0F);
        Shape4 = new ModelRenderer(this, 6, 15);
        Shape4.addBox(0F, 0F, 0F, 2, 1, 1);
        Shape4.setRotationPoint(-1F, 5F, 5F);
        Shape4.setTextureSize(64, 32);
        Shape4.mirror = true;
        setRotation(Shape4, 0F, 0F, 0F);
        Shape5 = new ModelRenderer(this, 6, 15);
        Shape5.addBox(0F, 0F, 0F, 2, 1, 1);
        Shape5.setRotationPoint(-1F, 5F, -6F);
        Shape5.setTextureSize(64, 32);
        Shape5.mirror = true;
        setRotation(Shape5, 0F, 0F, 0F);

        ShapeLens = new ModelRenderer(this, 42, 12);
        ShapeLens.addBox(0F, -5F, 0F, 1, 10, 10);
        ShapeLens.setRotationPoint(-0.5F, 5F, -5F);
        ShapeLens.setTextureSize(64, 32);
        ShapeLens.mirror = true;
        setRotation(ShapeLens, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float modelYaw, float modelPitch, float f2, float f3, float f4, float scale) {
        ShapeLens.rotateAngleZ = modelPitch * 0.017453292F;

        Shape1.render(scale);
        Shape2.render(scale);
        Shape3.render(scale);
        Shape4.render(scale);
        Shape5.render(scale);

        ShapeLens.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
