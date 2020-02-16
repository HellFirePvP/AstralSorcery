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
 * Class: ModelLens
 * Created by wiiv
 * Created using Tabula 4.1.1
 * Date: 21.09.2019 / 15:16
 */
public class ModelLens extends CustomModel {

    private static final Supplier<AbstractRenderableTexture> TEX_LENS =
            AssetLibrary.loadReference(AssetLoader.TextureLocation.BLOCKS, "entity", "lens_frame");

    public RendererModel base;
    public RendererModel frame1;
    public RendererModel lens;
    public RendererModel frame2;

    public ModelLens() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.base = new RendererModel(this, 0, 13);
        this.base.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.base.addBox(-6.0F, 4.0F, -6.0F, 12, 2, 12, 0.0F);
        this.frame1 = new RendererModel(this, 0, 13);
        this.frame1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.frame1.addBox(-8.0F, -4.0F, -1.0F, 2, 10, 2, 0.0F);
        this.frame2 = new RendererModel(this, 0, 13);
        this.frame2.mirror = true;
        this.frame2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.frame2.addBox(6.0F, -4.0F, -1.0F, 2, 10, 2, 0.0F);
        this.lens = new RendererModel(this, 0, 0);
        this.lens.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.lens.addBox(-6.0F, -6.0F, -0.5F, 12, 12, 1, 0.0F);
    }

    public void render(float scale, int destroyStage) {
        this.bindDamaged(TEX_LENS.get(), destroyStage);

        this.base.render(scale);
        this.frame1.render(scale);
        this.frame2.render(scale);
        this.lens.render(scale);

        this.lens.rotateAngleX = 0;
    }

}
