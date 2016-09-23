package hellfirepvp.astralsorcery.client.models.tcn;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TCNModelAltarDiscovery
 * Created by HellFirePvP
 * Date: 19.09.2016 / 13:57
 */
public class TCNModelAltarDiscovery extends ModelBase {

    ModelRenderer altar;

    public TCNModelAltarDiscovery() {
        textureWidth = 64;
        textureHeight = 32;

        altar = new ModelRenderer(this, 0, 0);
        altar.addBox(0F, 0F, 0F, 16, 16, 16);
        altar.setRotationPoint(-8F, 8F, -8F);
        altar.setTextureSize(64, 32);
        altar.mirror = true;
        setRotation(altar, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        altar.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
