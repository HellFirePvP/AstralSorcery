/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.gui.base.GuiTileBase;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiMapDrawing
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:29
 */
public class GuiMapDrawing extends GuiTileBase<TileMapDrawingTable> {

    public static final BindableResource texMapDrawing = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guidrawing");
    public static final BindableResource texMapDrawingEmpty = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guidrawing_empty");

    private static final LinkedList<Point> offsetPoints = new LinkedList<>();
    private static int whConstellations = 40;
    private static double lwidthConstellations = 0.5;

    public GuiMapDrawing(TileMapDrawingTable table) {
        super(table, 256, 512);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!getOwningTileEntity().hasParchment()) {
            drawWHRect(texMapDrawingEmpty);
        } else {
            drawWHRect(texMapDrawing);
        }

        PlayerProgress client = ResearchManager.clientProgress;

        //WorldSkyHandler wh = ConstellationSkyHandler.getInstance().getWorldHandler(Minecraft.getMinecraft().world);
        //if (wh != null) {
        //    LinkedList<IConstellation> sorted = wh.getSortedActiveConstellations();
        //    for (int i = 0, j = 0; j < Math.min(offsetPoints.size(), sorted.size()) && i < sorted.size(); i++) {
        //        IConstellation c = sorted.get(i);
        //        if (wh.isActive(c) && client.getKnownConstellations().contains(c.getUnlocalizedName())) {
        //            drawConstellation(c, offsetPoints.get(j));
        //            j++;
        //        }
        //    }
        //}

        TextureHelper.refreshTextureBindState();
    }

    private void drawConstellation(IConstellation c, Point p) {
        RenderConstellation.renderConstellationIntoGUI(!getOwningTileEntity().hasParchment() ? Color.BLACK.brighter() : c.getRenderColor(), c,
                guiLeft + p.x, guiTop + p.y, zLevel,
                whConstellations, whConstellations, lwidthConstellations, new RenderConstellation.BrightnessFunction() {
                    @Override
                    public float getBrightness() {
                        return 1F;
                    }
                },
                true, false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    static {
        offsetPoints.add(new Point(90, 10));
        offsetPoints.add(new Point(380, 10));
        offsetPoints.add(new Point(60, 50));
        offsetPoints.add(new Point(430, 50));
        offsetPoints.add(new Point(45, 90));
        offsetPoints.add(new Point(445, 90));
        offsetPoints.add(new Point(45, 130));
        offsetPoints.add(new Point(445, 130));
        offsetPoints.add(new Point(60, 170));
        offsetPoints.add(new Point(430, 170));
        offsetPoints.add(new Point(90, 210));
        offsetPoints.add(new Point(380, 210));
    }

}

