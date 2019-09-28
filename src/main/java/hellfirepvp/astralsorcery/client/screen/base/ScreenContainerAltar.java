/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.common.container.ContainerAltarBase;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipeContext;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerAltar
 * Created by HellFirePvP
 * Date: 15.08.2019 / 17:08
 */
public abstract class ScreenContainerAltar<T extends ContainerAltarBase> extends ScreenCustomContainer<T> {

    public ScreenContainerAltar(T screenContainer, PlayerInventory inv, ITextComponent name, int width, int height) {
        super(screenContainer, inv, name, width, height);
    }

    @Nullable
    public SimpleAltarRecipe findRecipe() {
        TileAltar ta = getContainer().getTileEntity();
        return RecipeTypesAS.TYPE_ALTAR.findRecipe(new SimpleAltarRecipeContext(ta));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.renderGuiBackground(partialTicks, mouseX, mouseY);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    public abstract void renderGuiBackground(float partialTicks, int mouseX, int mouseY);
}
