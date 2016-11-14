package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;

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
        return new RecipePage(recipeToRender);
    }

    public static class RecipePage implements IGuiRenderablePage {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridDisc");

        private final DiscoveryRecipe recipe;

        public RecipePage(DiscoveryRecipe recipe) {
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
            ItemStack out = recipe.getOutputForRender();
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

        protected void renderStarlightRequirementString(float offsetX, float offsetY, float zLevel, int sLightRequirement) {
            RenderHelper.enableGUIStandardItemLighting();
            if(sLightRequirement > 0) {
                GL11.glPushMatrix();
                GL11.glTranslated(0, 0, 200);
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                Blending.DEFAULT.apply();
                String displReq = getDescriptionFromStarlightAmount(sLightRequirement);
                displReq = I18n.translateToLocal(displReq);
                String dsc = I18n.translateToLocal("astralsorcery.journal.recipe.amt.desc");
                dsc = String.format(dsc, displReq);

                TextureHelper.refreshTextureBindState();
                getStandardFontRenderer().drawString(dsc, offsetX + 5F, offsetY + 210F, 0xDDDDDDDD, false);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                Blending.DEFAULT.apply();
                GL11.glPopMatrix();
            }
            RenderHelper.disableStandardItemLighting();
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);

            renderStandartRecipeGrid(offsetX, offsetY, zLevel, texGrid);

            renderOutputOnGrid(offsetX, offsetY, zLevel);

            renderDefaultExpectedItems(offsetX, offsetY, zLevel, recipe.getNativeRecipe());

            renderStarlightRequirementString(offsetX, offsetY, zLevel, recipe.getPassiveStarlightRequired());

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
        }

    }

}
