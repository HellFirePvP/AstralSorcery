/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container.altar;

import com.mojang.blaze3d.pipeline.RenderTarget;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.ShaderProgramsAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.shader.DrawChainRenderType;
import hellfirepvp.astralsorcery.client.shader.WrappedBufferSource;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.container.ContainerAltar;
import hellfirepvp.astralsorcery.common.container.ContainerAltarIllumination;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenContainerAltarIllumination
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ScreenContainerAltarIllumination extends ScreenContainerAltar<ContainerAltarIllumination> {

    public ScreenContainerAltarIllumination(ContainerAltarIllumination menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 216, 215);
    }

    @Override
    public AbstractRenderTexture getBackgroundTexture() {
        return TexturesAS.SCREEN_CONTAINER_ALTAR_ILLUMINATION;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderAltarOutput(guiGraphics, 154, 55, partialTick);
    }
}
