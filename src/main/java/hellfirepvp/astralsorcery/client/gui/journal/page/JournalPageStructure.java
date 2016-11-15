package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.util.BlockArrayRenderHelper;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageStructure
 * Created by HellFirePvP
 * Date: 30.09.2016 / 12:59
 */
public class JournalPageStructure implements IJournalPage {

    private BlockArray structure;

    public JournalPageStructure(BlockArray struct) {
        this.structure = struct;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(new BlockArrayRenderHelper(structure));
    }

    public static class Render implements IGuiRenderablePage {

        private BlockArrayRenderHelper structRender;
        private long totalRenderFrame = 0;

        public Render(BlockArrayRenderHelper structRender) {
            //this.structRender = new BlockArrayRenderHelper(MultiBlockArrays.patternAltarConstellation);
            this.structRender = structRender;
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            totalRenderFrame++;

            Point.Double offset = renderOffset(offsetX + 8, offsetY);

            if (Mouse.isButtonDown(0) && totalRenderFrame > 30) {
                structRender.rotate(0.25*Mouse.getDY(), 0.25*Mouse.getDX(), 0);
            }

            structRender.render3DGUI(offset.x, offset.y, pTicks);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }

        private Point.Double renderOffset(float stdPageOffsetX, float stdPageOffsetY) {
            return new Point.Double(stdPageOffsetX + IJournalPage.DEFAULT_WIDTH / 2D, stdPageOffsetY + ((IJournalPage.DEFAULT_HEIGHT * 4D) / 6D));
        }

    }

}
