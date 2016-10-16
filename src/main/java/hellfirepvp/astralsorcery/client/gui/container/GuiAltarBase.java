package hellfirepvp.astralsorcery.client.gui.container;

import hellfirepvp.astralsorcery.common.container.ContainerAltarAttenuation;
import hellfirepvp.astralsorcery.common.container.ContainerAltarBase;
import hellfirepvp.astralsorcery.common.container.ContainerAltarDiscovery;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiAltarBase
 * Created by HellFirePvP
 * Date: 16.10.2016 / 19:28
 */
public abstract class GuiAltarBase extends GuiContainer {

    public final ContainerAltarBase containerAltarBase;

    public GuiAltarBase(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(buildContainer(playerInv, tileAltar));
        this.containerAltarBase = (ContainerAltarBase) super.inventorySlots;
    }

    private static ContainerAltarBase buildContainer(InventoryPlayer playerInv, TileAltar tileAltar) {
        switch (tileAltar.getAltarLevel()) {
            case DISCOVERY:
                return new ContainerAltarDiscovery(playerInv, tileAltar);
            case ATTENUATION:
                return new ContainerAltarAttenuation(playerInv, tileAltar);
            case CONSTELLATION_CRAFT:
                break;
            case TRAIT_CRAFT:
                break;
            case ENDGAME:
                break;
        }
        return new ContainerAltarDiscovery(playerInv, tileAltar);
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height, double u, double v, double uLength, double vLength) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(u,           v + vLength).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(u + uLength, v + vLength).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(u + uLength, v          ).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(u,           v          ).endVertex();
        tes.draw();
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
