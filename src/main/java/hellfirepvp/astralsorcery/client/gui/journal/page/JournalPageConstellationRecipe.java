package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttenuationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageConstellationRecipe
 * Created by HellFirePvP
 * Date: 13.11.2016 / 22:13
 */
public class JournalPageConstellationRecipe implements IJournalPage {

    private final ConstellationRecipe recipe;

    public JournalPageConstellationRecipe(ConstellationRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(recipe);
    }

    public static class Render extends JournalPageAttenuationRecipe.Render {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridCst");

        private final ConstellationRecipe recipe;

        public Render(ConstellationRecipe recipe) {
            super(recipe);
            this.recipe = recipe;
            this.gridTexture = texGrid;
        }

        protected void renderAdditionalSlots(float offsetX, float offsetY, float zLevel, ConstellationRecipe recipe) {
            RenderHelper.enableGUIStandardItemLighting();
            renderAdditionalSlot(offsetX +  55, offsetY +  78, zLevel, recipe.getCstItem(ConstellationRecipe.AltarAdditionalSlot.UP_UP_LEFT));
            renderAdditionalSlot(offsetX + 105, offsetY +  78, zLevel, recipe.getCstItem(ConstellationRecipe.AltarAdditionalSlot.UP_UP_RIGHT));

            renderAdditionalSlot(offsetX +  30, offsetY + 103, zLevel, recipe.getCstItem(ConstellationRecipe.AltarAdditionalSlot.UP_LEFT_LEFT));
            renderAdditionalSlot(offsetX + 131, offsetY + 103, zLevel, recipe.getCstItem(ConstellationRecipe.AltarAdditionalSlot.UP_RIGHT_RIGHT));

            renderAdditionalSlot(offsetX +  30, offsetY + 153, zLevel, recipe.getCstItem(ConstellationRecipe.AltarAdditionalSlot.DOWN_LEFT_LEFT));
            renderAdditionalSlot(offsetX + 131, offsetY + 153, zLevel, recipe.getCstItem(ConstellationRecipe.AltarAdditionalSlot.DOWN_RIGHT_RIGHT));

            renderAdditionalSlot(offsetX +  55, offsetY + 178, zLevel, recipe.getCstItem(ConstellationRecipe.AltarAdditionalSlot.DOWN_DOWN_LEFT));
            renderAdditionalSlot(offsetX + 105, offsetY + 178, zLevel, recipe.getCstItem(ConstellationRecipe.AltarAdditionalSlot.DOWN_DOWN_RIGHT));
            RenderHelper.disableStandardItemLighting();
            TextureHelper.refreshTextureBindState();
        }

        private void renderAdditionalSlot(float offsetX, float offsetY, float zLevel, ItemStack stack) {
            if(stack == null) return;
            TextureHelper.refreshTextureBindState();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX, offsetY, zLevel + 60);
            GL11.glScaled(1.1, 1.1, 1.1);
            Rectangle r = drawItemStack(stack, 0, 0, 0);
            r = new Rectangle((int) offsetX, (int) offsetY, (int) (r.getWidth() * 1.1), (int) (r.getHeight() * 1.1));
            addRenderedStackRectangle(r, stack);
            GL11.glPopMatrix();
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            super.render(offsetX, offsetY, pTicks, zLevel, mouseX, mouseY);

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            renderAdditionalSlots(offsetX, offsetY, zLevel, recipe);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
        }
    }

}
