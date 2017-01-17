/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

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
        return new Render(recipe, TileAltar.AltarLevel.CONSTELLATION_CRAFT);
    }

    public static class Render extends JournalPageAttunementRecipe.Render {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridCst");

        private final ConstellationRecipe recipe;

        public Render(ConstellationRecipe recipe, TileAltar.AltarLevel altarLevel) {
            super(recipe, altarLevel);
            this.recipe = recipe;
            this.gridTexture = texGrid;
        }

        protected void renderAdditionalSlots(float offsetX, float offsetY, float zLevel, ConstellationRecipe recipe) {
            RenderHelper.enableGUIStandardItemLighting();
            renderAdditionalSlot(offsetX +  55, offsetY +  78, zLevel, recipe.getCstItems(ConstellationRecipe.AltarAdditionalSlot.UP_UP_LEFT));
            renderAdditionalSlot(offsetX + 105, offsetY +  78, zLevel, recipe.getCstItems(ConstellationRecipe.AltarAdditionalSlot.UP_UP_RIGHT));

            renderAdditionalSlot(offsetX +  30, offsetY + 103, zLevel, recipe.getCstItems(ConstellationRecipe.AltarAdditionalSlot.UP_LEFT_LEFT));
            renderAdditionalSlot(offsetX + 131, offsetY + 103, zLevel, recipe.getCstItems(ConstellationRecipe.AltarAdditionalSlot.UP_RIGHT_RIGHT));

            renderAdditionalSlot(offsetX +  30, offsetY + 153, zLevel, recipe.getCstItems(ConstellationRecipe.AltarAdditionalSlot.DOWN_LEFT_LEFT));
            renderAdditionalSlot(offsetX + 131, offsetY + 153, zLevel, recipe.getCstItems(ConstellationRecipe.AltarAdditionalSlot.DOWN_RIGHT_RIGHT));

            renderAdditionalSlot(offsetX +  55, offsetY + 178, zLevel, recipe.getCstItems(ConstellationRecipe.AltarAdditionalSlot.DOWN_DOWN_LEFT));
            renderAdditionalSlot(offsetX + 105, offsetY + 178, zLevel, recipe.getCstItems(ConstellationRecipe.AltarAdditionalSlot.DOWN_DOWN_RIGHT));
            RenderHelper.disableStandardItemLighting();
            TextureHelper.refreshTextureBindState();
        }

        private void renderAdditionalSlot(float offsetX, float offsetY, float zLevel, List<ItemStack> stacks) {
            if(stacks == null || stacks.isEmpty()) return;

            long select = ((ClientScheduler.getClientTick() + ((int) offsetX) * 40 + ((int) offsetY) * 40) / 20);
            select %= stacks.size();
            ItemStack draw = stacks.get((int) select);

            TextureHelper.refreshTextureBindState();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX, offsetY, zLevel + 60);
            GL11.glScaled(1.1, 1.1, 1.1);
            Rectangle r = drawItemStack(draw, 0, 0, 0);
            r = new Rectangle((int) offsetX, (int) offsetY, (int) (r.getWidth() * 1.1), (int) (r.getHeight() * 1.1));
            addRenderedStackRectangle(r, draw);
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
