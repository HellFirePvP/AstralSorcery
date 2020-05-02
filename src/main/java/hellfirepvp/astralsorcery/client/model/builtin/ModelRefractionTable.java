/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.builtin;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import net.minecraft.client.renderer.entity.model.RendererModel;

import java.util.function.Supplier;

/**
 * AS_starmapper - wiiv
 * Created using Tabula 7.0.0
 */
public class ModelRefractionTable extends CustomModel {

    private static final Supplier<AbstractRenderableTexture> TEX_REFRACTION_TABLE =
            AssetLibrary.loadReference(AssetLoader.TextureLocation.BLOCKS, "entity", "refraction_table");

    private RendererModel fitting_l;
    private RendererModel fitting_r;
    private RendererModel support_1;
    private RendererModel support_2;
    private RendererModel support_3;
    private RendererModel support_4;
    private RendererModel platform_l;
    private RendererModel platform_r;
    private RendererModel platform_f;
    private RendererModel platform_b;
    private RendererModel basin_l;
    private RendererModel basim_r;
    private RendererModel basin_f;
    private RendererModel basin_b;
    private RendererModel socket;
    private RendererModel base;
    private RendererModel leg_1;
    private RendererModel leg_2;
    private RendererModel leg_3;
    private RendererModel leg_4;

    private RendererModel parchment;
    private RendererModel black_mirror;

    private RendererModel treated_glass;

    public ModelRefractionTable() {
        this.textureWidth = 128;
        this.textureHeight = 128;

        this.fitting_l = new RendererModel(this, 0, 48);
        this.fitting_l.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.fitting_l.addBox(-14.0F, 0.0F, -12.0F, 4, 4, 24, 0.0F);
        this.fitting_r = new RendererModel(this, 56, 48);
        this.fitting_r.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.fitting_r.addBox(10.0F, 0.0F, -12.0F, 4, 4, 24, 0.0F);
        this.support_1 = new RendererModel(this, 24, 76);
        this.support_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support_1.addBox(-14.0F, 4.0F, -12.0F, 4, 6, 2, 0.0F);
        this.support_2 = new RendererModel(this, 24, 76);
        this.support_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support_2.addBox(10.0F, 4.0F, -12.0F, 4, 6, 2, 0.0F);
        this.support_3 = new RendererModel(this, 24, 76);
        this.support_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support_3.addBox(10.0F, 4.0F, 10.0F, 4, 6, 2, 0.0F);
        this.support_4 = new RendererModel(this, 24, 76);
        this.support_4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.support_4.addBox(-14.0F, 4.0F, 10.0F, 4, 6, 2, 0.0F);
        this.platform_l = new RendererModel(this, 0, 0);
        this.platform_l.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.platform_l.addBox(-14.0F, -6.0F, -12.0F, 4, 2, 24, 0.0F);
        this.platform_r = new RendererModel(this, 0, 0);
        this.platform_r.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.platform_r.addBox(10.0F, -6.0F, -12.0F, 4, 2, 24, 0.0F);
        this.platform_f = new RendererModel(this, 32, 0);
        this.platform_f.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.platform_f.addBox(-10.0F, -6.0F, -12.0F, 20, 2, 2, 0.0F);
        this.platform_b = new RendererModel(this, 32, 0);
        this.platform_b.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.platform_b.addBox(-10.0F, -6.0F, 10.0F, 20, 2, 2, 0.0F);
        this.basin_l = new RendererModel(this, 84, 76);
        this.basin_l.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.basin_l.addBox(-10.0F, -8.0F, -10.0F, 2, 6, 20, 0.0F);
        this.basim_r = new RendererModel(this, 84, 102);
        this.basim_r.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.basim_r.addBox(8.0F, -8.0F, -10.0F, 2, 6, 20, 0.0F);
        this.basin_f = new RendererModel(this, 36, 84);
        this.basin_f.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.basin_f.addBox(-8.0F, -8.0F, -10.0F, 16, 6, 2, 0.0F);
        this.basin_b = new RendererModel(this, 36, 76);
        this.basin_b.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.basin_b.addBox(-8.0F, -8.0F, 8.0F, 16, 6, 2, 0.0F);
        this.socket = new RendererModel(this, 0, 76);
        this.socket.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.socket.addBox(-3.0F, -4.0F, -3.0F, 6, 2, 6, 0.0F);
        this.base = new RendererModel(this, 0, 26);
        this.base.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.base.addBox(-10.0F, -2.0F, -10.0F, 20, 2, 20, 0.0F);
        this.leg_1 = new RendererModel(this, 0, 76);
        this.leg_1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_1.addBox(-10.0F, 0.0F, -10.0F, 6, 8, 6, 0.0F);
        this.leg_2 = new RendererModel(this, 0, 76);
        this.leg_2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_2.addBox(4.0F, 0.0F, -10.0F, 6, 8, 6, 0.0F);
        this.leg_3 = new RendererModel(this, 0, 76);
        this.leg_3.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_3.addBox(4.0F, 0.0F, 4.0F, 6, 8, 6, 0.0F);
        this.leg_4 = new RendererModel(this, 0, 76);
        this.leg_4.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leg_4.addBox(-10.0F, 0.0F, 4.0F, 6, 8, 6, 0.0F);

        this.parchment = new RendererModel(this, 66, 28);
        this.parchment.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.parchment.addBox(-7.0F, -8.5F, -7.0F, 14, 0, 14, 0.0F);
        this.black_mirror = new RendererModel(this, 64, 12);
        this.black_mirror.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.black_mirror.addBox(-8.0F, -8.0F, -8.0F, 16, 0, 16, 0.0F);

        this.treated_glass = new RendererModel(this, 0, 107);
        this.treated_glass.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.treated_glass.addBox(-10.0F, -15.0F, -10.0F, 20, 1, 20, 0.0F);
    }

    public void render(boolean hasParchment, int destroyStage) {
        this.bindDamaged(TEX_REFRACTION_TABLE.get(), destroyStage);

        this.fitting_l.render(1F);
        this.fitting_r.render(1F);
        this.support_1.render(1F);
        this.support_2.render(1F);
        this.support_3.render(1F);
        this.support_4.render(1F);
        this.platform_l.render(1F);
        this.platform_r.render(1F);
        this.platform_f.render(1F);
        this.platform_b.render(1F);
        this.basin_l.render(1F);
        this.basim_r.render(1F);
        this.basin_f.render(1F);
        this.basin_b.render(1F);
        this.socket.render(1F);
        this.base.render(1F);
        this.leg_1.render(1F);
        this.leg_2.render(1F);
        this.leg_3.render(1F);
        this.leg_4.render(1F);

        if (hasParchment) {
            this.parchment.render(1F);
            this.black_mirror.render(1F);
        }
    }

    public void renderGlass(int destroyStage) {
        this.bindDamaged(TEX_REFRACTION_TABLE.get(), destroyStage);

        this.treated_glass.render(1F);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(RendererModel RendererModel, float x, float y, float z) {
        RendererModel.rotateAngleX = x;
        RendererModel.rotateAngleY = y;
        RendererModel.rotateAngleZ = z;
    }
}