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
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelLensColored
 * Created by wiiv
 * Created using Tabula 4.1.1
 * Date: 21.09.2019 / 15:18
 */
public class ModelLensColored extends CustomModel {

    private static final Supplier<AbstractRenderableTexture> TEX_LENS_COLOR =
            AssetLibrary.loadReference(AssetLoader.TextureLocation.BLOCKS, "entity", "lens_color");

    public RendererModel glass;
    public RendererModel detail1;
    public RendererModel detail1_1;
    public RendererModel fitting2;
    public RendererModel fitting1;

    public ModelLensColored() {
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.glass = new RendererModel(this, 0, 0);
        this.glass.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.glass.addBox(-5.0F, -5.0F, -1.51F, 10, 10, 1, 0.0F);
        this.fitting1 = new RendererModel(this, 22, 0);
        this.fitting1.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.fitting1.addBox(-5.0F, -7.0F, -1.5F, 2, 1, 2, 0.0F);
        this.detail1_1 = new RendererModel(this, 22, 3);
        this.detail1_1.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.detail1_1.addBox(3.0F, -6.0F, -1.5F, 2, 1, 1, 0.0F);
        this.fitting2 = new RendererModel(this, 22, 0);
        this.fitting2.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.fitting2.addBox(3.0F, -7.0F, -1.5F, 2, 1, 2, 0.0F);
        this.detail1 = new RendererModel(this, 22, 3);
        this.detail1.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.detail1.addBox(-5.0F, -6.0F, -1.5F, 2, 1, 1, 0.0F);
    }

    public void renderGlass(float scale, int destroyStage) {
        this.bindDamaged(TEX_LENS_COLOR.get(), destroyStage);

        this.glass.render(scale);
    }

    public void renderFrame(float scale, int destroyStage) {
        this.bindDamaged(TEX_LENS_COLOR.get(), destroyStage);

        this.fitting1.render(scale);
        this.detail1_1.render(scale);
        this.fitting2.render(scale);
        this.detail1.render(scale);
    }

}
