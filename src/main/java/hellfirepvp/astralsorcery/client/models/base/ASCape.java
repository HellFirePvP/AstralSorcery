/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.models.base;

import hellfirepvp.astralsorcery.client.util.ModelArmorBase;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;

import javax.annotation.Nonnull;

/**
 * armor_as_cape - wiiv
 * Created using Tabula 7.0.0
 */
public class ASCape extends ModelArmorBase {

    private ModelRenderer bodyReplacement, lArm, rArm, headReplacement;

    public ModelRenderer cowl;
    public ModelRenderer mantle_l;
    public ModelRenderer mantle_r;

    public ModelRenderer bodyAnchor;
    public ModelRenderer body;
    public ModelRenderer plate;

    public ModelRenderer armLAnchor;
    public ModelRenderer armLpauldron;
    public ModelRenderer fitting_l;

    public ModelRenderer armRAnchor;
    public ModelRenderer armRpauldron;
    public ModelRenderer fitting_r;

    public ASCape() {

        super(EntityEquipmentSlot.CHEST);

        float s = 0.01F;
        this.textureWidth = 64;
        this.textureHeight = 128;

        this.cowl = new ModelRenderer(this, 0, 33);
        this.cowl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cowl.addBox(-4.5F, -4.0F, -4.0F, 9, 5, 9, s);
        this.setRotateAngle(cowl, 0.2617993877991494F, 0.0F, 0.0F);
        this.mantle_l = new ModelRenderer(this, 0, 47);
        this.mantle_l.mirror = true;
        this.mantle_l.setRotationPoint(6.25F, 2.0F, 0.0F);
        this.mantle_l.addBox(-8.0F, -3.5F, 1.0F, 9, 21, 5, s);
        this.setRotateAngle(mantle_l, 0.08726646259971647F, 0.2617993877991494F, 0.0F);
        this.mantle_r = new ModelRenderer(this, 0, 47);
        this.mantle_r.setRotationPoint(-6.25F, 2.0F, 0.0F);
        this.mantle_r.addBox(-1.0F, -3.5F, 1.0F, 9, 21, 5, s);
        this.setRotateAngle(mantle_r, 0.08726646259971647F, -0.2617993877991494F, 0.0F);

        this.bodyAnchor = new ModelRenderer(this, 0, 41);
        this.bodyAnchor.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyAnchor.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2, s);
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.5F, -0.5F, -3.0F, 9, 6, 6, s);
        this.plate = new ModelRenderer(this, 0, 12);
        this.plate.setRotationPoint(0.0F, 1.0F, -3.0F);
        this.plate.addBox(-3.5F, -0.5F, -1.0F, 7, 7, 2, s);
        this.setRotateAngle(plate, 0.08726646259971647F, 0.0F, 0.0F);

        this.armLAnchor = new ModelRenderer(this, 0, 41);
        this.armLAnchor.mirror = true;
        this.armLAnchor.setRotationPoint(4.0F, 2.0F, 0.0F);
        this.armLAnchor.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2, s);
        this.armLpauldron = new ModelRenderer(this, 0, 21);
        this.armLpauldron.mirror = true;
        this.armLpauldron.setRotationPoint(0.0F, 0.0F, -0.0F);
        this.armLpauldron.addBox(0.55F, -3.0F, -3.0F, 5, 6, 6, s);
        this.fitting_l = new ModelRenderer(this, 18, 12);
        this.fitting_l.setRotationPoint(0.5F, -3.0F, 0.0F);
        this.fitting_l.addBox(0.0F, -1.0F, -1.0F, 4, 1, 2, s);
        this.setRotateAngle(fitting_l, 0.0F, 0.0F, 0.08726646259971647F);

        this.armRAnchor = new ModelRenderer(this, 0, 41);
        this.armRAnchor.mirror = true;
        this.armRAnchor.setRotationPoint(-4.0F, 2.0F, 0.0F);
        this.armRAnchor.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2, s);
        this.armRpauldron = new ModelRenderer(this, 0, 21);
        this.armRpauldron.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armRpauldron.addBox(-5.55F, -3.0F, -3.0F, 5, 6, 6, s);
        this.fitting_r = new ModelRenderer(this, 18, 12);
        this.fitting_r.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.fitting_r.addBox(-4.5F, -1.0F, -1.0F, 4, 1, 2, s);
        this.setRotateAngle(fitting_r, 0.0F, 0.0F, -0.08726646259971647F);

        makeRenderCascade();
    }

    //Build hierarchy so a single rendercall cascades down
    private void makeRenderCascade() {
        this.bodyReplacement = new ModelRenderer(this);
        this.bodyReplacement.addChild(this.bodyAnchor);
        this.bodyAnchor.addChild(this.body);
        this.body.addChild(this.plate);
        this.body.addChild(this.mantle_l);
        this.body.addChild(this.mantle_r);

        this.headReplacement = new ModelRenderer(this);
        this.headReplacement.addChild(this.cowl);

        this.lArm = new ModelRenderer(this);
        this.lArm.offsetX = -0.40F;
        this.lArm.addChild(this.armLAnchor);
        this.armLAnchor.addChild(this.armLpauldron);
        this.armLpauldron.addChild(this.fitting_l);

        this.rArm = new ModelRenderer(this);
        this.rArm.offsetX = 0.40F;
        this.rArm.addChild(this.armRAnchor);
        this.armRAnchor.addChild(this.armRpauldron);
        this.armRpauldron.addChild(this.fitting_r);
    }

    @Override
    public void render(@Nonnull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        bodyAnchor.showModel = true;
        armRAnchor.showModel = true;
        armLAnchor.showModel = true;
        bipedHead.showModel = true;

        bipedHeadwear.showModel = false;
        bipedRightLeg.showModel = false;
        bipedLeftLeg.showModel = false;

        bipedBody = this.bodyReplacement;
        bipedRightArm = this.rArm;
        bipedLeftArm = this.lArm;
        bipedHead = this.headReplacement;

        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    private void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}
