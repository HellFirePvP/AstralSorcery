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
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageRecipe
 * Created by HellFirePvP
 * Date: 07.10.2016 / 12:24
 */
public class JournalPageRecipe implements IJournalPage {

    private final AbstractCacheableRecipe recipe;

    public JournalPageRecipe(AbstractCacheableRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(recipe.make());
    }

    public static class Render implements IGuiRenderablePage {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridDisc");

        private final IAccessibleRecipe recipe;

        private Map<Rectangle, ItemStack> thisFrameStackFrames = new HashMap<>();

        public Render(IAccessibleRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();
            texGrid.bind();
            drawRect(offsetX + 25, offsetY, 129, 202, zLevel);

            GL11.glColor4f(1F, 1F, 1F, 1F);
            TextureHelper.refreshTextureBindState();

            RenderHelper.enableGUIStandardItemLighting();
            ItemStack out = recipe.getRecipeOutput();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX + 78, offsetY + 25, zLevel + 60);
            GL11.glScaled(1.4, 1.4, 1.4);
            Rectangle r = drawItemStack(out, 0, 0, 0);
            r = new Rectangle((int) offsetX + 78, (int) offsetY + 25, (int) (r.getWidth() * 1.4), (int) (r.getHeight() * 1.4));
            this.thisFrameStackFrames.put(r, out);
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();

            double offX = offsetX + 55;
            double offY = offsetY + 103;
            for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {

                List<ItemStack> expected = recipe.getExpectedStack(srs);
                if(expected == null || expected.isEmpty()) expected = recipe.getExpectedStack(srs.rowMultipler, srs.columnMultiplier);
                if(expected == null || expected.isEmpty()) continue;

                int select = ((ClientScheduler.getClientTick() + srs.rowMultipler * 40 + srs.columnMultiplier * 40) / 20);
                select %= expected.size();
                ItemStack draw = expected.get(select);

                GL11.glPushMatrix();
                GL11.glTranslated(offX + (srs.columnMultiplier * 25), offY + (srs.rowMultipler * 25), zLevel + 60);
                GL11.glScaled(1.13, 1.13, 1.13);
                RenderHelper.enableGUIStandardItemLighting();
                r = drawItemStack(draw, 0, 0, 0);
                r = new Rectangle((int) offX + (srs.columnMultiplier * 25), (int) offY + (srs.rowMultipler * 25), (int) (r.getWidth() * 1.4), (int) (r.getHeight() * 1.4));
                this.thisFrameStackFrames.put(r, draw);
                GL11.glPopMatrix();
                TextureHelper.refreshTextureBindState();
            }

            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_BLEND);
            RenderHelper.disableStandardItemLighting();

            GL11.glPopAttrib();
        }

        public void addStackTooltip(float mouseX, float mouseY, List<String> tooltip) {
            for (Rectangle rect : thisFrameStackFrames.keySet()) {
                if(rect.contains(mouseX, mouseY)) {
                    ItemStack stack = thisFrameStackFrames.get(rect);
                    tooltip.addAll(stack.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips));
                }
            }
        }

        @Override
        public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            java.util.List<String> out = Lists.newLinkedList();
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
