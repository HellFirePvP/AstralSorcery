/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.base.ScreenContainerAltar;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.container.ContainerAltarTrait;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
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
        super(screenContainer, inv, name, 256, 202);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_ALTAR_RADIANCE;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.enableBlend();

        SimpleAltarRecipe recipe = this.findRecipe(false);
        if (recipe != null) {
            ItemStack out = recipe.getOutputForRender(this.getContainer().getTileEntity());
            this.blitOffset = 10;
            GlStateManager.pushMatrix();
            GlStateManager.translated(190, 35, 0);
            GlStateManager.scaled(2.5, 2.5, 2.5);

            RenderingUtils.renderItemStack(Minecraft.getInstance().getItemRenderer(), out, 0, 0, null);

            GlStateManager.popMatrix();
            this.blitOffset = 0;
        }

        float pTicks = Minecraft.getInstance().getRenderPartialTicks();
        TexturesAS.TEX_STAR_1.bindTexture();
        GlStateManager.disableDepthTest();
        rand.setSeed(0x889582997FF29A92L);
        for (int i = 0; i < 16; i++) {

            int x = rand.nextInt(54);
            int y = rand.nextInt(54);

            GlStateManager.pushMatrix();
            float brightness = 0.3F + (RenderingConstellationUtils.stdFlicker(ClientScheduler.getClientTick(), pTicks, 10 + rand.nextInt(20))) * 0.6F;
            GlStateManager.color4f(brightness, brightness, brightness, brightness);
            drawRect(15 + x, 39 + y, 5, 5);
            GlStateManager.color4f(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }
        GlStateManager.enableDepthTest();

        TileAltar altar = this.getContainer().getTileEntity();
        IConstellation c = altar.getFocusedConstellation();
        if (c != null && altar.hasMultiblock() && ResearchHelper.getClientProgress().hasConstellationDiscovered(c)) {
            rand.setSeed(0x61FF25A5B7C24109L);

            GlStateManager.pushMatrix();
            GlStateManager.disableDepthTest();

            RenderingConstellationUtils.renderConstellationIntoGUI(c.getConstellationColor(), c,
                    16, 41, this.blitOffset,
                    58, 58,
                    2, () -> 0.2F + 0.8F * RenderingConstellationUtils.conCFlicker(Minecraft.getInstance().world.getGameTime(), pTicks, 5 + rand.nextInt(5)),
                    true, false);

            GlStateManager.enableDepthTest();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void renderGuiBackground(float partialTicks, int mouseX, int mouseY) {
        TileAltar altar = this.getContainer().getTileEntity();
        GlStateManager.pushMatrix();
        GlStateManager.disableAlphaTest();

        TexturesAS.TEX_BLACK.bindTexture();
        drawRect(guiLeft + 11, guiTop + 104, 232, 10);

        float percFilled;
        if (altar.hasMultiblock()) {
            percFilled = altar.getAmbientStarlightPercent();
        } else {
            GlStateManager.color4f(1.0F, 0F, 0F, 1.0F);
            percFilled = 1.0F;
        }

        if (percFilled > 0) {
            SpriteSheetResource spriteStarlight = SpritesAS.SPR_STARLIGHT_STORE;
            spriteStarlight.getResource().bindTexture();

            int tick = altar.getTicksExisted();
            Tuple<Double, Double> uvOffset = spriteStarlight.getUVOffset(tick);
            drawRect(guiLeft + 11, guiTop + 104, (int) (232 * percFilled), 10,
                    uvOffset.getA(), uvOffset.getB(),
                    spriteStarlight.getULength() * percFilled, spriteStarlight.getVLength());

            SimpleAltarRecipe aar = findRecipe(true);
            if (aar != null) {
                int req = aar.getStarlightRequirement();
                int has = altar.getStoredStarlight();
                if (has < req) {
                    int max = altar.getAltarType().getStarlightCapacity();
                    float percReq = (float) (req - has) / (float) max;
                    int from = (int) (232 * percFilled);
                    int to = (int) (232 * percReq);
                    GlStateManager.color4f(0.2F, 0.5F, 1.0F, 0.4F);

                    drawRect(guiLeft + 11 + from, guiTop + 103, to, 10,
                            uvOffset.getA() + spriteStarlight.getULength() * percFilled, uvOffset.getB(),
                            spriteStarlight.getULength() * percReq, spriteStarlight.getVLength());
                }
            }
        }

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
