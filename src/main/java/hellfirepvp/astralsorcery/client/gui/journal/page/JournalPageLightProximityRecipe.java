package hellfirepvp.astralsorcery.client.gui.journal.page;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageLightProximityRecipe
 * Created by HellFirePvP
 * Date: 23.10.2016 / 20:02
 */
public class JournalPageLightProximityRecipe implements IJournalPage {

    private final ShapedRecipe shapedLightProxRecipe;

    public JournalPageLightProximityRecipe(ShapedRecipe shapedLightProxRecipe) {
        this.shapedLightProxRecipe = shapedLightProxRecipe;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(shapedLightProxRecipe);
    }

    public static class Render implements IGuiRenderablePage {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridDisc");

        private final ShapedRecipe recipe;

        public Render(ShapedRecipe recipe) {
            this.recipe = recipe;
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
            ItemStack out = recipe.getOutput();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX + 78, offsetY + 25, zLevel + 60);
            GL11.glScaled(1.4, 1.4, 1.4);
            drawItemStack(out, 0, 0, 0);
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
            RenderHelper.disableStandardItemLighting();
        }

        protected void renderDefaultExpectedItems(float offsetX, float offsetY, float zLevel, IAccessibleRecipe recipe) {
            RenderHelper.enableGUIStandardItemLighting();
            double offX = offsetX + 55;
            double offY = offsetY + 103;
            for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
                ItemStack expected = recipe.getExpectedStack(srs);
                if(expected == null) expected = recipe.getExpectedStack(srs.rowMultipler, srs.columnMultiplier);
                if(expected == null) continue;
                TextureHelper.refreshTextureBindState();
                GL11.glPushMatrix();
                GL11.glTranslated(offX + (srs.columnMultiplier * 25), offY + (srs.rowMultipler * 25), zLevel + 60);
                GL11.glScaled(1.13, 1.13, 1.13);
                drawItemStack(expected, 0, 0, 0);
                GL11.glPopMatrix();
            }
            RenderHelper.disableStandardItemLighting();
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPushMatrix();

            float widthHeightStar = 15F;
            Rectangle r = drawInfoStar(offsetX + 140, offsetY + 20, zLevel, widthHeightStar, pTicks);
            if(r.contains(mouseX, mouseY)) {
                RenderingUtils.renderTooltip((int) (offsetX), (int) (offsetY),
                        Lists.newArrayList(I18n.translateToLocal("astralsorcery.journal.recipe.starlight")),
                        new Color(0x000033), new Color(0x000044), Minecraft.getMinecraft().fontRendererObj);
            }

            renderStandartRecipeGrid(offsetX, offsetY, zLevel, texGrid);

            renderOutputOnGrid(offsetX, offsetY, zLevel);

            renderDefaultExpectedItems(offsetX, offsetY, zLevel, recipe.make());

            TextureHelper.refreshTextureBindState();
            TextureHelper.setActiveTextureToAtlasSprite();
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }

    }

}
