/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.builtin;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import net.minecraft.client.renderer.entity.model.RendererModel;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelTelescope
 * Created by HellFirePvP
 * Date: 15.01.2020 / 17:10
 */
public class ModelTelescope extends CustomModel {

    private static final Supplier<AbstractRenderableTexture> MODEL_TEXTURE =
            AssetLibrary.loadReference(AssetLoader.TextureLocation.BLOCKS, "entity", "telescope");

    public RendererModel mountpiece;
    public RendererModel opticalTube;
    public RendererModel leg;
    public RendererModel mountpiece_1;
    public RendererModel aperture;
    public RendererModel extension;
    public RendererModel detail;
    public RendererModel aperture_1;

    public ModelTelescope() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.leg = new RendererModel(this, 56, 0);
        this.leg.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.leg.addBox(-1.0F, -10.0F, -1.0F, 2, 36, 2, 0.0F);
        this.mountpiece_1 = new RendererModel(this, 32, 0);
        this.mountpiece_1.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.mountpiece_1.addBox(-2.0F, 20.0F, -1.0F, 4, 6, 4, 0.0F);
        this.aperture_1 = new RendererModel(this, 28, 28);
        this.aperture_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.aperture_1.addBox(-1.0F, -3.0F, -6.0F, 6, 6, 2, 0.0F);
        this.aperture = new RendererModel(this, 0, 28);
        this.aperture.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.aperture.addBox(-1.0F, -3.0F, -16.0F, 6, 6, 8, 0.0F);
        this.extension = new RendererModel(this, 0, 12);
        this.extension.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.extension.addBox(-2.0F, -6.0F, 6.0F, 2, 6, 2, 0.0F);
        this.detail = new RendererModel(this, 0, 8);
        this.detail.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.detail.addBox(1.0F, -1.0F, 10.0F, 2, 2, 2, 0.0F);
        this.opticalTube = new RendererModel(this, 0, 0);
        this.opticalTube.setRotationPoint(1.0F, -3.0F, 0.0F);
        this.opticalTube.addBox(0.0F, -2.0F, -14.0F, 4, 4, 24, 0.0F);
        this.setRotateAngle(opticalTube, -0.7853981633974483F, 0.0F, 0.0F);
        this.mountpiece = new RendererModel(this, 0, 0);
        this.mountpiece.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.mountpiece.addBox(-2.0F, 4.0F, -2.0F, 4, 4, 4, 0.0F);

        this.opticalTube.addChild(this.extension);
        this.opticalTube.addChild(this.aperture_1);
        this.opticalTube.addChild(this.aperture);
        this.opticalTube.addChild(this.detail);
        this.mountpiece.addChild(this.leg);
        this.mountpiece.addChild(this.mountpiece_1);
    }

    public void render(int destroyStage) {
        this.bindDamaged(MODEL_TEXTURE.get(), destroyStage);

        this.mountpiece.render(1F);
        this.opticalTube.render(1F);
    }
}
