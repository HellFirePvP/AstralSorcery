package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.BlockArrayRenderHelper;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageStructure
 * Created by HellFirePvP
 * Date: 30.09.2016 / 12:59
 */
public class JournalPageStructure implements IJournalPage {

    private BlockArray structure;
    private String unlocName;

    public JournalPageStructure(BlockArray struct) {
        this(struct, null);
    }

    public JournalPageStructure(BlockArray struct, @Nullable String unlocName) {
        this.structure = struct;
        this.unlocName = unlocName;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(structure, unlocName);
    }

    public static class Render implements IGuiRenderablePage {

        private final BlockArrayRenderHelper structRender;
        private final BlockArray blocks;
        private final List<Tuple<ItemStack, String>> descriptionStacks = new LinkedList<>();
        private final String unlocName;
        private long totalRenderFrame = 0;

        public Render(BlockArray structure, @Nullable String unlocName) {
            this.structRender = new BlockArrayRenderHelper(structure);
            this.blocks = structure;
            this.unlocName = unlocName;
            List<ItemStack> stacksNeeded = structure.getAsDescriptiveStacks();
            for (ItemStack stack : stacksNeeded) {
                if(stack.getItem() instanceof UniversalBucket) {
                    FluidStack f = ((UniversalBucket) stack.getItem()).getFluid(stack);
                    descriptionStacks.add(new Tuple<>(stack, stack.stackSize + "x " + I18n.format(stack.getUnlocalizedName() + ".name", f.getLocalizedName())));
                } else {
                    descriptionStacks.add(new Tuple<>(stack, stack.stackSize + "x " + I18n.format(stack.getUnlocalizedName() + ".name")));
                }
            }
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            totalRenderFrame++;

            renderStructure(offsetX, offsetY, pTicks);

            float shift = renderSizeDescription(offsetX, offsetY + 5);

            if(unlocName != null) {
                renderHeadline(offsetX + shift, offsetY + 5, unlocName);
            }

            drawBlockInformations(offsetX, offsetY, zLevel, pTicks, mouseX, mouseY);

            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }

        private void drawBlockInformations(float offsetX, float offsetY, float offsetZ, float pTicks, float mouseX, float mouseY) {
            Rectangle rect = drawInfoStar(offsetX + 160, offsetY + 10, offsetZ, 15, pTicks);
            if(rect.contains(mouseX, mouseY)) {
                /*List<Tuple<ItemStack, String>> localized = new LinkedList<>();
                for (Tuple<ItemStack, String> entry : descriptionStacks) {
                    localized.add(new Tuple<>(entry.key, entry.key.stackSize + "x " + I18n.format(entry.value)));
                }*/
                RenderingUtils.renderBlueStackTooltip((int) offsetX + 160, (int) offsetY + 10, descriptionStacks,
                        getStandardFontRenderer(), getRenderItem());
            }
        }

        private void renderHeadline(float offsetX, float offsetY, String unlocName) {
            String head = I18n.format(unlocName);
            FontRenderer fr = getStandardFontRenderer();
            float scale = 1.3F;
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX, offsetY, 0);
            GL11.glScalef(scale, scale, scale);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            fr.drawString(head, 0, 0, 0x00DDDDDD, true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
            GlStateManager.color(1F, 1F, 1F, 1F);
        }

        private float renderSizeDescription(float offsetX, float offsetY) {
            Vec3i size = blocks.getSize();
            FontRenderer fr = getStandardFontRenderer();
            float scale = 1.3F;
            String desc = size.getX() + " - " + size.getY() + " - " + size.getZ();
            float length = fr.getStringWidth(desc) * scale;
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX, offsetY, 0);
            GL11.glScalef(scale, scale, scale);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            fr.drawString(desc, 0, 0, 0x00DDDDDD, true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
            GlStateManager.color(1F, 1F, 1F, 1F);
            return length + 8F;
        }

        private void renderStructure(float offsetX, float offsetY, float pTicks) {
            Point.Double offset = renderOffset(offsetX + 8, offsetY);

            if (Mouse.isButtonDown(0) && totalRenderFrame > 30) {
                structRender.rotate(0.25*Mouse.getDY(), 0.25*Mouse.getDX(), 0);
            }

            structRender.render3DGUI(offset.x, offset.y, pTicks);
        }

        private Point.Double renderOffset(float stdPageOffsetX, float stdPageOffsetY) {
            return new Point.Double(stdPageOffsetX + ((IJournalPage.DEFAULT_WIDTH * 2D) / 5D), stdPageOffsetY + ((IJournalPage.DEFAULT_HEIGHT * 4D) / 6D));
        }

    }

}
