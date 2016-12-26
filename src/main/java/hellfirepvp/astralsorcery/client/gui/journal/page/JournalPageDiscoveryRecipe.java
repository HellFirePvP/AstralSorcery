package hellfirepvp.astralsorcery.client.gui.journal.page;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageDiscoveryRecipe
 * Created by HellFirePvP
 * Date: 06.10.2016 / 11:35
 */
public class JournalPageDiscoveryRecipe implements IJournalPage {

    private final DiscoveryRecipe recipeToRender;

    public JournalPageDiscoveryRecipe(DiscoveryRecipe recipeToRender) {
        this.recipeToRender = recipeToRender;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(recipeToRender);
    }

    public static class Render implements IGuiRenderablePage {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridDisc");

        private final DiscoveryRecipe recipe;
        protected BindableResource gridTexture;

        private Map<Rectangle, ItemStack> thisFrameStackFrames = new HashMap<>();

        public Render(DiscoveryRecipe recipe) {
            this.recipe = recipe;
            this.gridTexture = texGrid;
        }

        protected void renderStandartRecipeGrid(float offsetX, float offsetY, float zLevel, BindableResource grid) {
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();
            grid.bind();
            drawRect(offsetX + 25, offsetY, 129, 202, zLevel);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            TextureHelper.refreshTextureBindState();
        }

        protected void renderOutputOnGrid(float offsetX, float offsetY, float zLevel) {
            RenderHelper.enableGUIStandardItemLighting();
            ItemStack out = recipe.getOutputForRender();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX + 78, offsetY + 25, zLevel + 60);
            GL11.glScaled(1.4, 1.4, 1.4);
            Rectangle r = drawItemStack(out, 0, 0, 0);
            r = new Rectangle((int) offsetX + 78, (int) offsetY + 25, (int) (r.getWidth() * 1.4), (int) (r.getHeight() * 1.4));
            addRenderedStackRectangle(r, out);
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
            RenderHelper.disableStandardItemLighting();
        }

        protected void renderDefaultExpectedItems(float offsetX, float offsetY, float zLevel, IAccessibleRecipe recipe) {
            RenderHelper.enableGUIStandardItemLighting();
            double offX = offsetX + 55;
            double offY = offsetY + 103;
            for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {

                List<ItemStack> expected = recipe.getExpectedStack(srs);
                if(expected == null || expected.isEmpty()) expected = recipe.getExpectedStack(srs.rowMultipler, srs.columnMultiplier);
                if(expected == null || expected.isEmpty()) continue;
                int select = ((ClientScheduler.getClientTick() + srs.rowMultipler * 40 + srs.columnMultiplier * 40) / 20);
                select %= expected.size();
                ItemStack draw = expected.get(select);

                TextureHelper.refreshTextureBindState();
                GL11.glPushMatrix();
                GL11.glTranslated(offX + (srs.columnMultiplier * 25), offY + (srs.rowMultipler * 25), zLevel + 60);
                GL11.glScaled(1.1, 1.1, 1.1);
                Rectangle r = drawItemStack(draw, 0, 0, 0);
                r = new Rectangle((int) (offX + (srs.columnMultiplier * 25)), (int) (offY + (srs.rowMultipler * 25)),
                        (int) (r.getWidth() * 1.1), (int) (r.getHeight() * 1.1));
                addRenderedStackRectangle(r, draw);
                GL11.glPopMatrix();
            }
            RenderHelper.disableStandardItemLighting();
        }

        public void addTooltip(List<String> out) {
            if(recipe.getPassiveStarlightRequired() > 0) {
                String displReq = getDescriptionFromStarlightAmount(recipe.getPassiveStarlightRequired());
                displReq = I18n.format(displReq);
                String dsc = I18n.format("astralsorcery.journal.recipe.amt.desc", displReq);
                out.add(dsc);
            }
            if(recipe instanceof INighttimeRecipe) {
                out.add(I18n.format("astralsorcery.journal.recipe.nighttime"));
            }
        }

        public void addStackTooltip(float mouseX, float mouseY, List<String> tooltip) {
            for (Rectangle rect : thisFrameStackFrames.keySet()) {
                if(rect.contains(mouseX, mouseY)) {
                    ItemStack stack = thisFrameStackFrames.get(rect);
                    tooltip.addAll(stack.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips));
                }
            }
        }

        protected void addRenderedStackRectangle(Rectangle r, ItemStack rendered) {
            this.thisFrameStackFrames.put(r, rendered);
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);

            List<String> out = Lists.newLinkedList();
            addTooltip(out);
            if(!out.isEmpty()) {
                float widthHeightStar = 15F;
                Rectangle r = drawInfoStar(offsetX + 140, offsetY + 20, zLevel, widthHeightStar, pTicks);
                if(r.contains(mouseX, mouseY)) {
                    RenderingUtils.renderBlueTooltip((int) (offsetX), (int) (offsetY),
                            out, getStandardFontRenderer());
                }
            }

            renderStandartRecipeGrid(offsetX, offsetY, zLevel, gridTexture);

            renderOutputOnGrid(offsetX, offsetY, zLevel);

            renderDefaultExpectedItems(offsetX, offsetY, zLevel, recipe.getNativeRecipe());

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
        }

        @Override
        public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            List<String> out = Lists.newLinkedList();
            addStackTooltip(mouseX, mouseY, out);
            if(!out.isEmpty()) {
                RenderingUtils.renderBlueTooltip((int) (mouseX), (int) (mouseY),
                        out, getStandardFontRenderer());
            }
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
            thisFrameStackFrames.clear();
        }
    }

}
