/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.base.ScreenContainerAltar;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.container.ContainerAltarDiscovery;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerAltarDiscovery
 * Created by HellFirePvP
 * Date: 15.08.2019 / 17:06
 */
public class ScreenContainerAltarDiscovery extends ScreenContainerAltar<ContainerAltarDiscovery> {

    public ScreenContainerAltarDiscovery(ContainerAltarDiscovery screenContainer, PlayerInventory inv, ITextComponent name) {
        super(screenContainer, inv, name, 176, 166);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_ALTAR_DISCOVERY;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack renderStack, int mouseX, int mouseY) {
        SimpleAltarRecipe recipe = this.findRecipe(false);
        if (recipe != null) {
            ItemStack out = recipe.getOutputForRender(this.getContainer().getTileEntity().getInventory());
            renderStack.push();
            renderStack.translate(130, 20, 0);
            renderStack.scale(1.7F, 1.7F, 1F);

            RenderingUtils.renderItemStackGUI(renderStack, out, null);

            renderStack.pop();
        }
    }

    @Override
    public void renderGuiBackground(MatrixStack renderStack, float partialTicks, int mouseX, int mouseY) {
        this.renderStarlightBar(renderStack, 6, 69, 165, 10);
    }
}
