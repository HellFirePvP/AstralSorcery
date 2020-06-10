/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomArmorModel
 * Created by HellFirePvP
 * Date: 17.02.2020 / 21:22
 */
//Again, another version, another ripoff of net.minecraft.client.renderer.entity.model.ArmorStandArmorModel
public class CustomArmorModel<T extends LivingEntity> extends BipedModel<T> {

    public CustomArmorModel() {
        super(0F);
    }

    @Override
    public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof ArmorStandEntity) {
            ArmorStandEntity armorStand = (ArmorStandEntity) entity;

            this.bipedHead.rotateAngleX = ((float)Math.PI / 180F) * armorStand.getHeadRotation().getX();
            this.bipedHead.rotateAngleY = ((float)Math.PI / 180F) * armorStand.getHeadRotation().getY();
            this.bipedHead.rotateAngleZ = ((float)Math.PI / 180F) * armorStand.getHeadRotation().getZ();
            this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
            this.bipedBody.rotateAngleX = ((float)Math.PI / 180F) * armorStand.getBodyRotation().getX();
            this.bipedBody.rotateAngleY = ((float)Math.PI / 180F) * armorStand.getBodyRotation().getY();
            this.bipedBody.rotateAngleZ = ((float)Math.PI / 180F) * armorStand.getBodyRotation().getZ();
            this.bipedLeftArm.rotateAngleX = ((float)Math.PI / 180F) * armorStand.getLeftArmRotation().getX();
            this.bipedLeftArm.rotateAngleY = ((float)Math.PI / 180F) * armorStand.getLeftArmRotation().getY();
            this.bipedLeftArm.rotateAngleZ = ((float)Math.PI / 180F) * armorStand.getLeftArmRotation().getZ();
            this.bipedRightArm.rotateAngleX = ((float)Math.PI / 180F) * armorStand.getRightArmRotation().getX();
            this.bipedRightArm.rotateAngleY = ((float)Math.PI / 180F) * armorStand.getRightArmRotation().getY();
            this.bipedRightArm.rotateAngleZ = ((float)Math.PI / 180F) * armorStand.getRightArmRotation().getZ();
            this.bipedLeftLeg.rotateAngleX = ((float)Math.PI / 180F) * armorStand.getLeftLegRotation().getX();
            this.bipedLeftLeg.rotateAngleY = ((float)Math.PI / 180F) * armorStand.getLeftLegRotation().getY();
            this.bipedLeftLeg.rotateAngleZ = ((float)Math.PI / 180F) * armorStand.getLeftLegRotation().getZ();
            this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
            this.bipedRightLeg.rotateAngleX = ((float)Math.PI / 180F) * armorStand.getRightLegRotation().getX();
            this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 180F) * armorStand.getRightLegRotation().getY();
            this.bipedRightLeg.rotateAngleZ = ((float)Math.PI / 180F) * armorStand.getRightLegRotation().getZ();
            this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
            this.bipedHeadwear.copyModelAngles(this.bipedHead);
        } else {
            super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    protected void setRotateAngle(ModelRenderer modelPart, float x, float y, float z) {
        modelPart.rotateAngleX = x;
        modelPart.rotateAngleY = y;
        modelPart.rotateAngleZ = z;
    }
}
