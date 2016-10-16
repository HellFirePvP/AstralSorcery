package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.SpecialTextureLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

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
        return new GuiRender(recipe.make());
    }

    public static class GuiRender implements IGuiRenderablePage {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridDisc");

        private final IAccessibleRecipe recipe;

        public GuiRender(IAccessibleRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();
            texGrid.bind();
            drawRect(offsetX + 15, offsetY, IJournalPage.DEFAULT_WIDTH - 30, IJournalPage.DEFAULT_HEIGHT - 20, zLevel);
            GL11.glDisable(GL11.GL_BLEND);

            GL11.glColor4f(1F, 1F, 1F, 1F);
            SpecialTextureLibrary.refreshTextureBindState();

            RenderHelper.enableGUIStandardItemLighting();
            ItemStack out = recipe.getRecipeOutput();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX + 77, offsetY + 26, zLevel + 60);
            GL11.glScaled(1.4, 1.4, 1.4);
            drawItemStack(out, 0, 0, 0);
            GL11.glPopMatrix();
            SpecialTextureLibrary.refreshTextureBindState();

            double offX = offsetX + 35;
            double offY = offsetY + 85;
            for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
                ItemStack expected = recipe.getExpectedStack(srs);
                if(expected == null) expected = recipe.getExpectedStack(srs.rowMultipler, srs.columnMultiplier);
                if(expected == null) continue;
                SpecialTextureLibrary.refreshTextureBindState();
                GL11.glPushMatrix();
                GL11.glTranslated(offX + (srs.columnMultiplier * 40), offY + (srs.rowMultipler * 40), zLevel + 60);
                GL11.glScaled(1.4, 1.4, 1.4);
                drawItemStack(expected, 0, 0, 0);
                GL11.glPopMatrix();
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }

    }

}
