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
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ModelLens
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ModelLens extends CustomModel {

    private final ModelPart base;
    private final ModelPart frame1;
    private final ModelPart lens;
    private final ModelPart frame2;

    private ModelLens(ModelPart root) {
        this.base = root.getChild("base");
        this.frame1 = root.getChild("frame1");
        this.lens = root.getChild("lens");
        this.frame2 = root.getChild("frame2");
    }

    public static ModelLens bake(EntityModelSet modelSet) {
        return new ModelLens(modelSet.bakeLayer(ModelLayersAS.LENS.layerLocation()));
    }

    public static LayerDefinition createLayer() {
        return LayerDefinition.create(ModelLens.createMesh(), 64, 32);
    }

    public static MeshDefinition createMesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 13)
                .addBox(-6.0F, 4.0F, -6.0F, 12, 2, 12), PartPose.offset(0.0F, 16.0F, 0.0F));
        root.addOrReplaceChild("frame1", CubeListBuilder.create().texOffs(0, 13)
                .addBox(-8.0F, -4.0F, -1.0F, 2, 10, 2), PartPose.offset(0.0F, 16.0F, 0.0F));
        root.addOrReplaceChild("frame2", CubeListBuilder.create().texOffs(0, 13).mirror()
                .addBox(6.0F, -4.0F, -1.0F, 2, 10, 2), PartPose.offset(0.0F, 16.0F, 0.0F));
        root.addOrReplaceChild("lens", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-6.0F, -6.0F, -0.5F, 12, 12, 1), PartPose.offset(0.0F, 14.0F, 0.0F));
        return mesh;
    }

    public void rotateLens(float pitchDegrees) {
        this.lens.xRot = (float) Math.toRadians(pitchDegrees);
    }

    public void renderModel(PoseStack poseStack, MultiBufferSource src, int packedLight, int packedOverlay) {
        VertexConsumer buf = src.getBuffer(RenderTypesAS.MODEL_LENS_SOLID);
        this.base.render(poseStack, buf, packedLight, packedOverlay);
        this.frame1.render(poseStack, buf, packedLight, packedOverlay);
        this.frame2.render(poseStack, buf, packedLight, packedOverlay);
        this.lens.render(poseStack, buf, packedLight, packedOverlay);

        buf = src.getBuffer(RenderTypesAS.MODEL_LENS_GLASS);
        this.lens.render(poseStack, buf, packedLight, packedOverlay);
        this.lens.xRot = 0;
    }
}
