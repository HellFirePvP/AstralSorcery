package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.util.BlendingHelper;
import hellfirepvp.astralsorcery.client.util.SpecialTextureLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.registry.RegistryRecipes;
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
        return new GuiPage(recipeToRender);
    }

    public static class GuiPage implements IGuiRenderablePage {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridDisc");

        private final DiscoveryRecipe recipe;

        public GuiPage(DiscoveryRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            BlendingHelper.DEFAULT.apply();
            texGrid.bind();
            drawRect(offsetX + 15, offsetY, IJournalPage.DEFAULT_WIDTH - 30, IJournalPage.DEFAULT_HEIGHT - 20, zLevel);

            GL11.glColor4f(1F, 1F, 1F, 1F);
            SpecialTextureLibrary.refreshTextureBindState();

            RenderHelper.enableGUIStandardItemLighting();
            ItemStack out = recipe.getOutputForRender();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX + 77, offsetY + 26, zLevel + 60);
            GL11.glScaled(1.4, 1.4, 1.4);
            drawItemStack(out, 0, 0, 0);
            GL11.glPopMatrix();
            SpecialTextureLibrary.refreshTextureBindState();

            double offX = offsetX + 35;
            double offY = offsetY + 85;
            IAccessibleRecipe rNative = recipe.getNativeRecipe();
            for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
                ItemStack expected = rNative.getExpectedStack(srs);
                if(expected == null) expected = rNative.getExpectedStack(srs.rowMultipler, srs.columnMultiplier);
                if(expected == null) continue;
                SpecialTextureLibrary.refreshTextureBindState();
                GL11.glPushMatrix();
                GL11.glTranslated(offX + (srs.columnMultiplier * 40), offY + (srs.rowMultipler * 40), zLevel + 60);
                GL11.glScaled(1.4, 1.4, 1.4);
                drawItemStack(expected, 0, 0, 0);
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();

            if(recipe.getPassiveStarlightRequired() > 0) {
                GL11.glPushMatrix();
                GL11.glTranslated(0, 0, 200);
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                BlendingHelper.DEFAULT.apply();
                String displReq = getDescriptionFromStarlightAmount(recipe.getPassiveStarlightRequired());
                displReq = I18n.translateToLocal(displReq);
                String dsc = I18n.translateToLocal("astralsorcery.journal.recipe.amt.desc");
                dsc = String.format(dsc, displReq);

                SpecialTextureLibrary.refreshTextureBindState();
                getStandardFontRenderer().drawString(dsc, offsetX + 5F, offsetY + 210F, Color.LIGHT_GRAY.getRGB(), false);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                BlendingHelper.DEFAULT.apply();
                GL11.glPopMatrix();
            }
            GL11.glDisable(GL11.GL_BLEND);
            RenderHelper.disableStandardItemLighting();

            GL11.glPopAttrib();
        }

    }

}
