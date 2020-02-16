/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.model.builtin;

import net.minecraft.client.renderer.entity.model.RendererModel;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelAttunementAltar
 * Created by HellFirePvP
 * Date: 17.11.2019 / 07:54
 */
public class ModelAttunementAltar extends CustomModel {

    public RendererModel base;
    public RendererModel hovering;

    public ModelAttunementAltar() {
        this.textureWidth = 128;
        this.textureHeight = 32;

        this.base = new RendererModel(this, 0, 0);
        this.base.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.base.addBox(-10.0F, -14.0F, -10.0F, 20, 6, 20, 0.0F);

        this.hovering = new RendererModel(this, 0, 0);
        this.hovering.setRotationPoint(-2.0F, -16.0F, -2.0F); //was -14, -14
        this.hovering.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
    }

    public void renderBase() {
        this.base.render(1F);
    }

    public void renderHovering(float offX, float offZ, float perc) {
        float distance = 0.9453125F;
        this.hovering.setRotationPoint(-2F + (16F * offX * distance), -16F, -2F + (16F * offZ * distance));
        this.setRotateAngle(this.hovering, offZ * 0.39269908169872414F * perc, 0, offX * -0.39269908169872414F * perc);
        this.hovering.render(1F);
    }
}
