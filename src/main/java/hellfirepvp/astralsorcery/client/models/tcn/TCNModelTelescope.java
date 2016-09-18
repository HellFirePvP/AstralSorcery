package hellfirepvp.astralsorcery.client.models.tcn;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TCNModelTelescope
 * Created by HellFirePvP
 * Date: 16.09.2016 / 15:08
 */
public class TCNModelTelescope extends ModelBase {

    ModelRenderer ttr1;
    ModelRenderer ttr2;
    ModelRenderer ttr3;
    ModelRenderer tfound;
    ModelRenderer tbody;
    ModelRenderer tset;
    ModelRenderer tep;

    public TCNModelTelescope() {
        textureWidth = 32;
        textureHeight = 32;

        ttr1 = new ModelRenderer(this, 0, 21);
        ttr1.addBox(0F, 0F, 0F, 1, 10, 1);
        ttr1.setRotationPoint(-0.7F, 15F, -0.5F);
        ttr1.setTextureSize(32, 32);
        ttr1.mirror = true;
        setRotation(ttr1, 0.4363323F, 0F, 0F);
        ttr2 = new ModelRenderer(this, 4, 21);
        ttr2.addBox(0F, 0F, 0F, 1, 10, 1);
        ttr2.setRotationPoint(0F, 15F, 0.5F);
        ttr2.setTextureSize(32, 32);
        ttr2.mirror = true;
        setRotation(ttr2, 0.4363323F, 2.094395F, 0F);
        ttr3 = new ModelRenderer(this, 8, 21);
        ttr3.addBox(0F, 0F, 0F, 1, 10, 1);
        ttr3.setRotationPoint(0.5F, 15F, -0.2F);
        ttr3.setTextureSize(32, 32);
        ttr3.mirror = true;
        setRotation(ttr3, 0.4363323F, -2.094395F, 0F);
        tfound = new ModelRenderer(this, 24, 0);
        tfound.addBox(0F, 0F, 0F, 2, 2, 2);
        tfound.setRotationPoint(-1F, 13F, -1F);
        tfound.setTextureSize(32, 32);
        tfound.mirror = true;
        setRotation(tfound, 0F, 0F, 0F);
        tbody = new ModelRenderer(this, 24, 20);
        tbody.addBox(0F, 0F, 0F, 2, 10, 2);
        tbody.setRotationPoint(-1F, 7.5F, -5.5F);
        tbody.setTextureSize(32, 32);
        tbody.mirror = true;
        setRotation(tbody, 0.9599311F, 0F, 0F);
        tset = new ModelRenderer(this, 28, 4);
        tset.addBox(0F, 0F, 0F, 1, 2, 1);
        tset.setRotationPoint(-0.5F, 11F, -0.5F);
        tset.setTextureSize(32, 32);
        tset.mirror = true;
        setRotation(tset, 0F, 0F, 0F);
        tep = new ModelRenderer(this, 20, 20);
        tep.addBox(0F, 0F, 0F, 1, 3, 1);
        tep.setRotationPoint(-0.5F, 12.2F, 2F);
        tep.setTextureSize(32, 32);
        tep.mirror = true;
        setRotation(tep, 0.9599311F, 0F, 0F);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ttr1.render(scale);
        ttr2.render(scale);
        ttr3.render(scale);
        tfound.render(scale);
        tbody.render(scale);
        tset.render(scale);
        tep.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
