/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.armor;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelArmorMantle
 * Created by HellFirePvP
 * Date: 17.02.2020 / 21:21
 */
public class ModelArmorMantle extends CustomArmorModel {

    //TODO adjust arms at some point

    private RendererModel bodyReplacement, lArm, rArm, headReplacement;

    public RendererModel cowl;
    public RendererModel mantle_l;
    public RendererModel mantle_r;

    public RendererModel bodyAnchor;
    public RendererModel body;
    public RendererModel plate;

    public RendererModel armLAnchor;
    public RendererModel armLpauldron;
    public RendererModel fitting_l;

    public RendererModel armRAnchor;
    public RendererModel armRpauldron;
    public RendererModel fitting_r;

    public ModelArmorMantle() {
        super(EquipmentSlotType.CHEST);

        float s = 0.01F;
        this.textureWidth = 64;
        this.textureHeight = 128;

        this.cowl = new RendererModel(this, 0, 33);
        this.cowl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cowl.addBox(-4.5F, -4.0F, -4.0F, 9, 5, 9, s);
        this.setRotateAngle(cowl, 0.2617993877991494F, 0.0F, 0.0F);
        this.mantle_l = new RendererModel(this, 0, 47);
        this.mantle_l.mirror = true;
        this.mantle_l.setRotationPoint(6.25F, 2.0F, 0.0F);
        this.mantle_l.addBox(-8.0F, -3.5F, 1.0F, 9, 21, 5, s);
        this.setRotateAngle(mantle_l, 0.08726646259971647F, 0.2617993877991494F, 0.0F);
        this.mantle_r = new RendererModel(this, 0, 47);
        this.mantle_r.setRotationPoint(-6.25F, 2.0F, 0.0F);
        this.mantle_r.addBox(-1.0F, -3.5F, 1.0F, 9, 21, 5, s);
        this.setRotateAngle(mantle_r, 0.08726646259971647F, -0.2617993877991494F, 0.0F);

        this.bodyAnchor = new RendererModel(this, 0, 41);
        this.bodyAnchor.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyAnchor.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2, s);
        this.body = new RendererModel(this, 0, 0);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.5F, -0.5F, -3.0F, 9, 6, 6, s);
        this.plate = new RendererModel(this, 0, 12);
        this.plate.setRotationPoint(0.0F, 1.0F, -3.0F);
        this.plate.addBox(-3.5F, -0.5F, -1.0F, 7, 7, 2, s);
        this.setRotateAngle(plate, 0.08726646259971647F, 0.0F, 0.0F);

        this.armLAnchor = new RendererModel(this, 0, 41);
        this.armLAnchor.mirror = true;
        this.armLAnchor.setRotationPoint(4.0F, 2.0F, 0.0F);
        this.armLAnchor.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2, s);
        this.armLpauldron = new RendererModel(this, 0, 21);
        this.armLpauldron.mirror = true;
        this.armLpauldron.setRotationPoint(0.0F, 0.0F, -0.0F);
        this.armLpauldron.addBox(0.55F, -3.0F, -3.0F, 5, 6, 6, s);
        this.fitting_l = new RendererModel(this, 18, 12);
        this.fitting_l.setRotationPoint(0.5F, -3.0F, 0.0F);
        this.fitting_l.addBox(0.0F, -1.0F, -1.0F, 4, 1, 2, s);
        this.setRotateAngle(fitting_l, 0.0F, 0.0F, 0.08726646259971647F);

        this.armRAnchor = new RendererModel(this, 0, 41);
        this.armRAnchor.mirror = true;
        this.armRAnchor.setRotationPoint(-4.0F, 2.0F, 0.0F);
        this.armRAnchor.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2, s);
        this.armRpauldron = new RendererModel(this, 0, 21);
        this.armRpauldron.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armRpauldron.addBox(-5.55F, -3.0F, -3.0F, 5, 6, 6, s);
        this.fitting_r = new RendererModel(this, 18, 12);
        this.fitting_r.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.fitting_r.addBox(-4.5F, -1.0F, -1.0F, 4, 1, 2, s);
        this.setRotateAngle(fitting_r, 0.0F, 0.0F, -0.08726646259971647F);

        this.fitting_r = new RendererModel(this, 18, 12);
        this.fitting_r.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.fitting_r.addBox(-4.5F, -1.0F, -1.0F, 4, 1, 2, s);
        this.setRotateAngle(fitting_r, 0.0F, 0.0F, -0.08726646259971647F);

        this.bodyReplacement = new RendererModel(this);
        this.bodyReplacement.addChild(this.bodyAnchor);
        this.bodyAnchor.addChild(this.body);
        this.body.addChild(this.plate);
        this.body.addChild(this.mantle_l);
        this.body.addChild(this.mantle_r);

        this.headReplacement = new RendererModel(this);
        this.headReplacement.addChild(this.cowl);

        this.lArm = new RendererModel(this);
        this.lArm.offsetX = -0.40F;
        this.lArm.addChild(this.armLAnchor);
        this.armLAnchor.addChild(this.armLpauldron);
        this.armLpauldron.addChild(this.fitting_l);

        this.rArm = new RendererModel(this);
        this.rArm.offsetX = 0.40F;
        this.rArm.addChild(this.armRAnchor);
        this.armRAnchor.addChild(this.armRpauldron);
        this.armRpauldron.addChild(this.fitting_r);
    }

    @Override
    public void render(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
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

        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }
}
