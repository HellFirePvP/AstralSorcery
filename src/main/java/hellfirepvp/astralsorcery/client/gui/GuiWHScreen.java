/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;

import java.io.IOException;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiWHScreen
 * Created by HellFirePvP
 * Date: 11.08.2016 / 20:06
 */
public abstract class GuiWHScreen extends GuiScreen {

    protected final int guiHeight;
    protected final int guiWidth;
    protected int guiLeft, guiTop;

    protected GuiWHScreen(int guiHeight, int guiWidth) {
        this.guiHeight = guiHeight;
        this.guiWidth = guiWidth;
    }

    @Override
    public void initGui() {
        super.initGui();

        initComponents();
    }

    public int getGuiHeight() {
        return guiHeight;
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }

    public int getGuiWidth() {
        return guiWidth;
    }

    private void initComponents() {
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
    }

    protected void drawWHRect(BindableResource resource) {
        resource.bind();
        drawRect(guiLeft, guiTop, guiWidth, guiHeight);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton == 1 && !handleRightClickClose(mouseX, mouseY)) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    /**
     * @return false if rightclick is not handled any other way and allow for close. true to deny rightclick close and handle otherwise;
     */
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        return false;
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(0, 0).endVertex();
        tes.draw();
    }

}
