/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.base.ScreenContainerAltar;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.container.ContainerAltarTrait;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerAltarRadiance
 * Created by HellFirePvP
 * Date: 15.08.2019 / 17:33
 */
public class ScreenContainerAltarRadiance extends ScreenContainerAltar<ContainerAltarTrait> {

    private static final Random rand = new Random();

    public ScreenContainerAltarRadiance(ContainerAltarTrait screenContainer, PlayerInventory inv, ITextComponent name) {
        super(screenContainer, inv, name, 255, 202);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_ALTAR_RADIANCE;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        SimpleAltarRecipe recipe = this.findRecipe(false);
        if (recipe != null) {
            ItemStack out = recipe.getOutputForRender(this.getContainer().getTileEntity().getInventory());
            RenderSystem.pushMatrix();
            RenderSystem.translated(190, 35, 0);
            RenderSystem.scaled(2.5, 2.5, 2.5);

            RenderingUtils.renderItemStack(Minecraft.getInstance().getItemRenderer(), out, 0, 0, null);

            RenderSystem.popMatrix();
        }

        float pTicks = Minecraft.getInstance().getRenderPartialTicks();
        TexturesAS.TEX_STAR_1.bindTexture();
        RenderSystem.disableDepthTest();
        rand.setSeed(0x889582997FF29A92L);
        for (int i = 0; i < 16; i++) {

            int x = rand.nextInt(54);
            int y = rand.nextInt(54);

            float brightness = 0.3F + (RenderingConstellationUtils.stdFlicker(ClientScheduler.getClientTick(), pTicks, 10 + rand.nextInt(20))) * 0.6F;

            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, 15 + x, 39 + y, this.getBlitOffset(), 5, 5)
                        .color(brightness, brightness, brightness, brightness)
                        .draw();
            });
        }

        TileAltar altar = this.getContainer().getTileEntity();
        IConstellation c = altar.getFocusedConstellation();
        if (c != null && altar.hasMultiblock() && ResearchHelper.getClientProgress().hasConstellationDiscovered(c)) {
            rand.setSeed(0x61FF25A5B7C24109L);

            RenderingConstellationUtils.renderConstellationIntoGUI(c.getConstellationColor(), c,
                    16, 41, this.getBlitOffset(),
                    58, 58,
                    2, () -> 0.2F + 0.8F * RenderingConstellationUtils.conCFlicker(Minecraft.getInstance().world.getDayTime(), pTicks, 5 + rand.nextInt(5)),
                    true, false);
        }
        RenderSystem.enableDepthTest();
    }

    @Override
    public void renderGuiBackground(float partialTicks, int mouseX, int mouseY) {
        this.renderStarlightBar(11, 104, 232, 10);
    }
}
