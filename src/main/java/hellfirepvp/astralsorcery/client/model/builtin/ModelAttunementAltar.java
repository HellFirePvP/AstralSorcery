/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.builtin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.lib.ModelLayersAS;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ModelAttunementAltar
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ModelAttunementAltar extends CustomModel {

    private final ModelPart base;
    private final ModelPart hovering;

    private ModelAttunementAltar(ModelPart root) {
        this.base = root.getChild("base");
        this.hovering = root.getChild("hovering");
    }

    public static ModelAttunementAltar bake(EntityModelSet modelSet) {
        return new ModelAttunementAltar(modelSet.bakeLayer(ModelLayersAS.ATTUNEMENT_ALTAR.layerLocation()));
    }

    public static LayerDefinition createLayer() {
        return LayerDefinition.create(ModelAttunementAltar.createMesh(), 128, 32);
    }

    public static MeshDefinition createMesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-10.0F, -14.0F, -10.0F, 20, 6, 20), PartPose.offset(0.0F, 16.0F, 0.0F));
        root.addOrReplaceChild("hovering", CubeListBuilder.create().texOffs(0, 0)
                .addBox(0.0F, 0.0F, 0.0F, 4, 4, 4), PartPose.offset(-2.0F, -16.0F, -2.0F));
        return mesh;
    }

    public void renderBase(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
        this.base.render(poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
    }

    public void renderHoveringToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float offX, float offZ, float perc) {
        float distance = 0.9453125F;
        this.hovering.setPos(-2F + (16F * offX * distance), -16F, -2F + (16F * offZ * distance));
        this.setAngles(this.hovering, offZ * 0.39269908169872414F * perc, 0, offX * -0.39269908169872414F * perc);
        this.hovering.render(poseStack, buffer, packedLight, packedOverlay, 0xFFFFFFFF);
    }
}
