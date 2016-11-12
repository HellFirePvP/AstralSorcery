package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
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
            drawItemStack(out, 0, 0, 0);
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();

            double offX = offsetX + 55;
            double offY = offsetY + 103;
            for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
                ItemStack expected = recipe.getExpectedStack(srs);
                if(expected == null) expected = recipe.getExpectedStack(srs.rowMultipler, srs.columnMultiplier);
                if(expected == null) continue;
                GL11.glPushMatrix();
                GL11.glTranslated(offX + (srs.columnMultiplier * 25), offY + (srs.rowMultipler * 25), zLevel + 60);
                GL11.glScaled(1.13, 1.13, 1.13);
                RenderHelper.enableGUIStandardItemLighting();
                drawItemStack(expected, 0, 0, 0);
                GL11.glPopMatrix();
                TextureHelper.refreshTextureBindState();
            }

            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_BLEND);
            RenderHelper.disableStandardItemLighting();

            GL11.glPopAttrib();
        }

    }

}
