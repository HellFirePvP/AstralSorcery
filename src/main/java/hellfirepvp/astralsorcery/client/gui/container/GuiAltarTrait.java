/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.container;

import hellfirepvp.astralsorcery.client.sky.RenderAstralSkybox;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiAltarTrait
 * Created by HellFirePvP
 * Date: 06.03.2017 / 14:22
 */
public class GuiAltarTrait extends GuiAltarBase {

    private static final Random rand = new Random();

    private static final BindableResource texAltarTrait = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guialtar4");
    private static final BindableResource texBlack = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "black");

    public GuiAltarTrait(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(playerInv, tileAltar);
    }

    @Override
    public void initGui() {
        this.xSize = 256;
        this.ySize = 202;
        super.initGui();
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        AbstractAltarRecipe rec = findCraftableRecipe();
        if(rec != null) {
            ItemStack out = rec.getOutputForRender();
            zLevel = 10F;
            itemRender.zLevel = 10F;

            RenderHelper.enableGUIStandardItemLighting();

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            GL11.glTranslated(190, 35, 0);
            GL11.glScaled(2.5, 2.5, 2.5);

            itemRender.renderItemAndEffectIntoGUI(mc.player, out, 0, 0);
            itemRender.renderItemOverlayIntoGUI(fontRenderer, out, 0, 0, null);

            GL11.glPopMatrix();
            GL11.glPopAttrib();

            zLevel = 0F;
            itemRender.zLevel = 0F;

            TextureHelper.refreshTextureBindState();
        }

        float pTicks = Minecraft.getMinecraft().getRenderPartialTicks();


        RenderAstralSkybox.TEX_STAR_1.bind();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        rand.setSeed(0x889582997FF29A92L);
        for (int i = 0; i < 16; i++) {

            int x = rand.nextInt(54);
            int y = rand.nextInt(54);

            GL11.glPushMatrix();
            float brightness = 0.3F + (RenderConstellation.stdFlicker(Minecraft.getMinecraft().world.getWorldTime(), pTicks, 10 + rand.nextInt(20))) * 0.6F;
            GL11.glColor4f(brightness, brightness, brightness, brightness);
            drawRect(15 + x, 39 + y, 5, 5);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glPopMatrix();
        }

        GL11.glPopAttrib();
        TextureHelper.refreshTextureBindState();

        IConstellation c = containerAltarBase.tileAltar.getFocusedConstellation();
        if(c != null && containerAltarBase.tileAltar.getMultiblockState() && ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) {
            rand.setSeed(0x61FF25A5B7C24109L);

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();

            GL11.glColor4f(1F, 1F, 1F, 1F);

            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();

            RenderConstellation.renderConstellationIntoGUI(c, 16, 41, zLevel, 58, 58, 2, new RenderConstellation.BrightnessFunction() {
                @Override
                public float getBrightness() {
                    return RenderConstellation.conCFlicker(Minecraft.getMinecraft().world.getTotalWorldTime(), pTicks, 5 + rand.nextInt(5));
                }
            }, true, false);

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_ALPHA_TEST);

            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }

        TextureHelper.setActiveTextureToAtlasSprite();
        TextureHelper.refreshTextureBindState();
    }

    @Override
    public void renderGuiBackground(float partialTicks, int mouseX, int mouseY) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float percFilled;
        if(containerAltarBase.tileAltar.getMultiblockState()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            percFilled = containerAltarBase.tileAltar.getAmbientStarlightPercent();
        } else {
            GL11.glColor4f(1.0F, 0F, 0F, 1.0F);
            percFilled = 1.0F;
        }

        texBlack.bind();
        drawRect(guiLeft + 11, guiTop + 104, 232, 10);

        if(percFilled > 0) {
            SpriteSheetResource spriteStarlight = SpriteLibrary.spriteStarlight;
            spriteStarlight.getResource().bind();
            int t = containerAltarBase.tileAltar.getTicksExisted();
            Tuple<Double, Double> uvOffset = spriteStarlight.getUVOffset(t);
            drawRect(guiLeft + 11, guiTop + 104, (int) (232 * percFilled), 10,
                    uvOffset.key, uvOffset.value,
                    spriteStarlight.getULength() * percFilled, spriteStarlight.getVLength());

            AbstractAltarRecipe aar = findCraftableRecipe(true);
            if(aar != null) {
                int req = aar.getPassiveStarlightRequired();
                int has = containerAltarBase.tileAltar.getStarlightStored();
                if(has < req) {
                    int max = containerAltarBase.tileAltar.getMaxStarlightStorage();
                    float percReq = (float) (req - has) / (float) max;
                    int from = (int) (232 * percFilled);
                    int to = (int) (232 * percReq);
                    GL11.glColor4f(0.2F, 0.5F, 1.0F, 0.4F);

                    drawRect(guiLeft + 11 + from, guiTop + 104, to, 10,
                            uvOffset.key + spriteStarlight.getULength() * percFilled, uvOffset.value,
                            spriteStarlight.getULength() * percReq, spriteStarlight.getVLength());
                }
            }

            ActiveCraftingTask task = containerAltarBase.tileAltar.getActiveCraftingTask();
            if(task != null && task.getState() != ActiveCraftingTask.CraftingState.ACTIVE) {
                int req = task.getRecipeToCraft().getPassiveStarlightRequired();
                int has = containerAltarBase.tileAltar.getStarlightStored();
                if(has < req) {
                    int max = containerAltarBase.tileAltar.getMaxStarlightStorage();
                    float percReq = (float) (req - has) / (float) max;
                    int from = (int) (232 * percFilled);
                    int to = (int) (232 * percReq);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.4F);
                    Color c = new Color(0xFFCC00);
                    GL11.glColor4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);

                    drawRect(guiLeft + 11 + from, guiTop + 104, to, 10,
                            uvOffset.key + spriteStarlight.getULength() * percFilled, uvOffset.value,
                            spriteStarlight.getULength() * percReq, spriteStarlight.getVLength());
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        texAltarTrait.bind();
        drawRect(guiLeft, guiTop, xSize, ySize);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

}
