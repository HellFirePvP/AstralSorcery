package hellfirepvp.astralsorcery.client.models.tcn;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TCNModelGrindstone
 * Created by HellFirePvP
 * Date: 18.09.2016 / 16:39
 */
public class TCNModelGrindstone extends ModelBase {

    ModelRenderer grF1;
    ModelRenderer grF1Mirrored;
    ModelRenderer grF2Mirrored;
    ModelRenderer grF2;
    ModelRenderer handle;

    public TCNModelGrindstone() {
        textureWidth = 32;
        textureHeight = 32;

        grF1 = new ModelRenderer(this, 0, 0);
        grF1.addBox(0F, 0F, 0F, 1, 12, 1);
        grF1.setRotationPoint(-1F, 13F, -6F);
        grF1.setTextureSize(32, 32);
        grF1.mirror = true;
        setRotation(grF1, 0F, 0F, 0.3490659F);
        grF1Mirrored = new ModelRenderer(this, 0, 0);
        grF1Mirrored.addBox(0F, 0F, 0F, 1, 12, 1);
        grF1Mirrored.setRotationPoint(-1F, 13F, 5F);
        grF1Mirrored.setTextureSize(32, 32);
        grF1Mirrored.mirror = true;
        setRotation(grF1Mirrored, 0F, 0F, 0.3490659F);
        grF2Mirrored = new ModelRenderer(this, 0, 0);
        grF2Mirrored.addBox(0F, 0F, 0F, 1, 12, 1);
        grF2Mirrored.setRotationPoint(-1F, 13F, 5F);
        grF2Mirrored.setTextureSize(32, 32);
        grF2Mirrored.mirror = true;
        setRotation(grF2Mirrored, 0F, 0F, -0.3490659F);
        grF2 = new ModelRenderer(this, 0, 0);
        grF2.addBox(0F, 0F, 0F, 1, 12, 1);
        grF2.setRotationPoint(-1F, 13F, -6F);
        grF2.setTextureSize(32, 32);
        grF2.mirror = true;
        setRotation(grF2, 0F, 0F, -0.3490659F);
        handle = new ModelRenderer(this, 4, 0);
        handle.addBox(0F, 0F, 0F, 1, 1, 12);
        handle.setRotationPoint(-1F, 13.3F, -6F);
        handle.setTextureSize(32, 32);
        handle.mirror = true;
        setRotation(handle, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        grF1.render(scale);
        grF1Mirrored.render(scale);
        grF2Mirrored.render(scale);
        grF2.render(scale);
        handle.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
