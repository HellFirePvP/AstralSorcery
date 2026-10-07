/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen;

import hellfirepvp.astralsorcery.common.network.play.PktSetStructureMarker;
import hellfirepvp.astralsorcery.common.tile.TileStructureMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenStructureMarker
 * Created by HellFirePvP
 * Date: 07.10.2026 / 10:49
 */
@OnlyIn(Dist.CLIENT)
public class ScreenStructureMarker extends Screen {

    private final BlockPos markerPos;
    private final ResourceLocation initialMarkerId;

    private EditBox identifierBox;

    public ScreenStructureMarker(BlockPos markerPos, ResourceLocation initialMarkerId) {
        super(Component.translatable("screen.astralsorcery.element.structure_marker"));
        this.markerPos = markerPos;
        this.initialMarkerId = initialMarkerId;
    }

    public static void open(TileStructureMarker marker) {
        Minecraft.getInstance().setScreen(new ScreenStructureMarker(marker.getBlockPos(), marker.getTileData().getMarkerId()));
    }

    @Override
    protected void init() {
        super.init();

        this.identifierBox = new EditBox(this.font,
                (this.width - 220) / 2, (this.height - 20) / 2, 220, 20, this.getTitle());
        this.identifierBox.setMaxLength(128);
        this.identifierBox.setFilter(str -> ResourceLocation.tryParse(str) != null);
        this.identifierBox.setResponder(value ->
                this.identifierBox.setTextColor(ResourceLocation.tryParse(value) != null ? 0xE0E0E0 : 0xFF5555));
        this.identifierBox.setValue(this.initialMarkerId.toString());

        this.addRenderableWidget(this.identifierBox);
        this.setInitialFocus(this.identifierBox);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, this.identifierBox.getY() - 14, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        Optional.ofNullable(ResourceLocation.tryParse(this.identifierBox.getValue()))
                .filter(markerId -> !markerId.equals(this.initialMarkerId))
                .ifPresent(markerId -> PacketDistributor.sendToServer(PktSetStructureMarker.setMarker(this.markerPos, markerId)));

        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
