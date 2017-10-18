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

    public ModelRenderer armLAnchor;
    public ModelRenderer armLpauldron;
    public ModelRenderer armLpauldron_1;
    public ModelRenderer armLpauldron_2;

    public ModelRenderer armRAnchor;
    public ModelRenderer armRpauldron;
    public ModelRenderer armRpauldron_1;
    public ModelRenderer armRpauldron_2;

    public ModelRenderer bodyAnchor;
    public ModelRenderer bodyTop;
    public ModelRenderer bodyBottom;
    public ModelRenderer bodyBottom_1;
    public ModelRenderer belt;

    public ASCape() {
        super(EntityEquipmentSlot.CHEST);
        this.textureWidth = 64;
        this.textureHeight = 128;

        this.bodyTop = new ModelRenderer(this, 0, 0);
        this.bodyTop.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyTop.addBox(-4.5F, 0.0F, -3.0F, 9, 6, 6, 0.0F);
        this.bodyBottom = new ModelRenderer(this, 0, 41);
        this.bodyBottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyBottom.addBox(-4.5F, -4.0F, -3.0F, 9, 5, 10, 0.0F);
        this.setRotateAngle(bodyBottom, 0.2617993877991494F, 0.0F, 0.0F);
        this.armLAnchor = new ModelRenderer(this, 0, 41);
        this.armLAnchor.mirror = true;
        this.armLAnchor.setRotationPoint(4.0F, 2.0F, 0.0F);
        this.armLAnchor.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.armRpauldron_1 = new ModelRenderer(this, 0, 56);
        this.armRpauldron_1.setRotationPoint(-6.25F, 2.0F, 0.0F);
        this.armRpauldron_1.addBox(-1.0F, -3.0F, 1.0F, 9, 21, 6, 0.0F);
        this.setRotateAngle(armRpauldron_1, 0.2617993877991494F, -0.2617993877991494F, 0.0F);
        this.armRpauldron = new ModelRenderer(this, 0, 29);
        this.armRpauldron.setRotationPoint(-6.5F, 2.0F, 0.0F);
        this.armRpauldron.addBox(-2.95F, -3.0F, -3.0F, 5, 6, 6, 0.0F);
        this.armLpauldron_1 = new ModelRenderer(this, 0, 56);
        this.armLpauldron_1.mirror = true;
        this.armLpauldron_1.setRotationPoint(6.25F, 2.0F, 0.0F);
        this.armLpauldron_1.addBox(-8.0F, -3.0F, 1.0F, 9, 21, 6, 0.0F);
        this.setRotateAngle(armLpauldron_1, 0.2617993877991494F, 0.2617993877991494F, 0.0F);
        this.belt = new ModelRenderer(this, 0, 20);
        this.belt.setRotationPoint(0.0F, 1.0F, -3.0F);
        this.belt.addBox(-3.5F, 0.0F, -1.0F, 7, 7, 2, 0.0F);
        this.setRotateAngle(belt, 0.08726646259971647F, 0.0F, 0.0F);
        this.bodyBottom_1 = new ModelRenderer(this, 0, 12);
        this.bodyBottom_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyBottom_1.addBox(-3.5F, 6.0F, -2.5F, 7, 3, 5, 0.0F);
        this.bodyAnchor = new ModelRenderer(this, 0, 41);
        this.bodyAnchor.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyAnchor.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2, 0.0F);
        this.armRAnchor = new ModelRenderer(this, 0, 41);
        this.armRAnchor.mirror = true;
        this.armRAnchor.setRotationPoint(-4.0F, 2.0F, 0.0F);
        this.armRAnchor.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.armLpauldron = new ModelRenderer(this, 0, 29);
        this.armLpauldron.mirror = true;
        this.armLpauldron.setRotationPoint(6.5F, 2.0F, -0.0F);
        this.armLpauldron.addBox(-1.95F, -3.0F, -3.0F, 5, 6, 6, 0.0F);
        this.armRpauldron_2 = new ModelRenderer(this, 18, 20);
        this.armRpauldron_2.setRotationPoint(-5.5F, -1.0F, 0.0F);
        this.armRpauldron_2.addBox(-3.0F, -1.0F, -1.0F, 4, 1, 3, 0.0F);
        this.setRotateAngle(armRpauldron_2, 0.0F, 0.0F, -0.08726646259971647F);
        this.armLpauldron_2 = new ModelRenderer(this, 18, 20);
        this.armLpauldron_2.setRotationPoint(-1.0F, -3.0F, 0.0F);
        this.armLpauldron_2.addBox(-1.0F, -1.0F, -1.0F, 4, 1, 3, 0.0F);
        this.setRotateAngle(armLpauldron_2, 0.0F, 0.0F, 0.08726646259971647F);

        this.bodyAnchor.addChild(this.bodyTop);
        this.bodyAnchor.addChild(this.bodyBottom);
        this.bodyTop.addChild(this.bodyBottom_1);

        makeRenderCascade();
    }

    //Build hierarchy so a single rendercall cascades down
    private void makeRenderCascade() {
        this.bodyReplacement = new ModelRenderer(this);
        this.bodyReplacement.addChild(belt);
        this.bodyReplacement.addChild(this.bodyAnchor);
        this.bodyReplacement.addChild(this.armRpauldron_1);
        this.bodyReplacement.addChild(this.armLpauldron_1);

        this.headReplacement = new ModelRenderer(this);

        this.lArm = new ModelRenderer(this);
        this.lArm.offsetX = -0.35F;
        this.lArm.addChild(this.armLAnchor);
        this.lArm.addChild(this.armLpauldron);
        this.armLpauldron.addChild(this.armLpauldron_2);

        this.rArm = new ModelRenderer(this);
        this.rArm.offsetX = 0.35F;
        this.rArm.addChild(this.armRAnchor);
        this.rArm.addChild(this.armRpauldron);
        this.rArm.addChild(this.armRpauldron_2);

        this.bodyAnchor.addChild(this.bodyTop);
        this.bodyAnchor.addChild(this.bodyBottom);
        this.bodyTop.addChild(this.bodyBottom_1);
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
