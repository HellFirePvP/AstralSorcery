/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
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

    public ConstellationRecipe recipe;

    public JournalPageConstellationRecipe(ConstellationRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(recipe, TileAltar.AltarLevel.CONSTELLATION_CRAFT);
    }

    public static class Render extends JournalPageAttunementRecipe.Render {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridcst");

        private final ConstellationRecipe recipe;

        public Render(ConstellationRecipe recipe, TileAltar.AltarLevel altarLevel) {
            super(recipe, altarLevel);
            this.recipe = recipe;
            this.gridTexture = texGrid;
        }

        protected void renderAdditionalSlots(float offsetX, float offsetY, float zLevel, ConstellationRecipe recipe) {
            RenderHelper.enableGUIStandardItemLighting();
            renderAltarSlot(offsetX +  55, offsetY +  78, zLevel, recipe.getCstItems(ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT));
            renderAltarSlot(offsetX + 105, offsetY +  78, zLevel, recipe.getCstItems(ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT));

            renderAltarSlot(offsetX +  30, offsetY + 103, zLevel, recipe.getCstItems(ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT));
            renderAltarSlot(offsetX + 131, offsetY + 103, zLevel, recipe.getCstItems(ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT));

            renderAltarSlot(offsetX +  30, offsetY + 153, zLevel, recipe.getCstItems(ConstellationRecipe.ConstellationAtlarSlot.DOWN_LEFT_LEFT));
            renderAltarSlot(offsetX + 131, offsetY + 153, zLevel, recipe.getCstItems(ConstellationRecipe.ConstellationAtlarSlot.DOWN_RIGHT_RIGHT));

            renderAltarSlot(offsetX +  55, offsetY + 178, zLevel, recipe.getCstItems(ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_LEFT));
            renderAltarSlot(offsetX + 105, offsetY + 178, zLevel, recipe.getCstItems(ConstellationRecipe.ConstellationAtlarSlot.DOWN_DOWN_RIGHT));
            RenderHelper.disableStandardItemLighting();
            TextureHelper.refreshTextureBindState();
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
