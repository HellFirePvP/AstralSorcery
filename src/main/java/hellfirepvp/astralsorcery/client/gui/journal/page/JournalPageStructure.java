/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.util.BlockArrayRenderHelper;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    private Vector3 shift;

    public JournalPageStructure(BlockArray struct) {
        this(struct, null);
    }

    public JournalPageStructure(BlockArray struct, @Nullable String unlocName) {
        this(struct, unlocName, new Vector3());
    }

    public JournalPageStructure(BlockArray struct, @Nullable String unlocName, @Nonnull Vector3 shift) {
        this.structure = struct;
        this.unlocName = unlocName;
        this.shift = shift;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(structure, unlocName, shift.clone());
    }

    public static class Render implements IGuiRenderablePage {

        private static BindableResource texSlices = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiscructpreviewicons");

        private final BlockArrayRenderHelper structRender;
        private final BlockArray blocks;
        private final Vector3 shift;
        private final List<Tuple<ItemStack, String>> descriptionStacks = new LinkedList<>();
        private final String unlocName;
        private long totalRenderFrame = 0;

        private Optional<Integer> drawSlice = Optional.empty();
        private Rectangle switchView = null, sliceUp = null, sliceDown = null;

        public Render(BlockArray structure, @Nullable String unlocName, @Nonnull Vector3 shift) {
            this.structRender = new BlockArrayRenderHelper(structure);
            this.blocks = structure;
            this.unlocName = unlocName;
            this.shift = shift;
            List<ItemStack> stacksNeeded = structure.getAsDescriptiveStacks();
            for (ItemStack stack : stacksNeeded) {
                if(stack.getItem() instanceof UniversalBucket) {
                    FluidStack f = ((UniversalBucket) stack.getItem()).getFluid(stack);
                    descriptionStacks.add(new Tuple<>(stack, stack.getCount() + "x " + I18n.format(stack.getUnlocalizedName() + ".name", f.getLocalizedName())));
                } else {
                    descriptionStacks.add(new Tuple<>(stack, stack.getCount() + "x " + I18n.format(stack.getUnlocalizedName() + ".name")));
                }
            }
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GlStateManager.pushMatrix();
            totalRenderFrame++;

            renderStructure(offsetX, offsetY, pTicks);

            float shift = renderSizeDescription(offsetX, offsetY + 5);

            if(unlocName != null) {
                renderHeadline(offsetX + shift, offsetY + 5, unlocName);
            }
            renderSliceButtons(offsetX, offsetY + 10, mouseX, mouseY);

            GlStateManager.popMatrix();
            GL11.glPopAttrib();
        }

        private void renderSliceButtons(float offsetX, float offsetY, float mouseX, float mouseY) {
            texSlices.bindTexture();
            GlStateManager.disableDepth();

            switchView = null;
            sliceUp = null;
            sliceDown = null;

            switchView = new Rectangle(MathHelper.floor(offsetX) + 152, MathHelper.floor(offsetY) + 10, 16, 16);
            double u = drawSlice.isPresent() ? 0.5 : 0;
            drawRectPart(switchView.x, switchView.y, switchView.width, switchView.height, 0, u, 0, 0.5, 1D / 3D);

            if (drawSlice.isPresent()) {
                int yLevel = drawSlice.get();

                if (structRender.hasSlice(yLevel + 1)) {
                    sliceUp = new Rectangle(MathHelper.floor(offsetX) + 148, MathHelper.floor(offsetY) + 28, 11, 16);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(sliceUp.x + (sliceUp.width / 2D), sliceUp.y + (sliceUp.height / 2D), 0);
                    double v = 2D / 3D;
                    if (sliceUp.contains(mouseX, mouseY)) {
                        v = 1D / 3D;
                        GlStateManager.scale(1.1, 1.1, 1.1);
                    }
                    GlStateManager.translate(-(sliceUp.width / 2), -(sliceUp.height / 2), 0);
                    drawRectPart(0, 0, sliceUp.width, sliceUp.height, 0,
                            0, v, 11D / 32D, 1D / 3D);
                    GlStateManager.popMatrix();
                }
                if (structRender.hasSlice(yLevel - 1)) {
                    sliceDown = new Rectangle(MathHelper.floor(offsetX) + 160, MathHelper.floor(offsetY) + 28, 11, 16);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(sliceDown.x + (sliceDown.width / 2D), sliceDown.y + (sliceDown.height / 2D), 0);
                    double v = 2D / 3D;
                    if (sliceDown.contains(mouseX, mouseY)) {
                        v = 1D / 3D;
                        GlStateManager.scale(1.1, 1.1, 1.1);
                    }
                    GlStateManager.translate(-(sliceDown.width / 2), -(sliceDown.height / 2), 0);
                    drawRectPart(0, 0, sliceDown.width, sliceDown.height, 0,
                            12D / 32D, v, 11D / 32D, 1D / 3D);
                    GlStateManager.popMatrix();
                }
            }

            GlStateManager.enableDepth();
            TextureHelper.refreshTextureBindState();
        }

        @Override
        public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            Rectangle rect = drawInfoStar(offsetX + 160, offsetY + 10, zLevel, 15, pTicks);
            if(rect.contains(mouseX, mouseY)) {
                /*List<Tuple<ItemStack, String>> localized = new LinkedList<>();
                for (Tuple<ItemStack, String> entry : descriptionStacks) {
                    localized.add(new Tuple<>(entry.key, entry.key.getCount() + "x " + I18n.format(entry.value)));
                }*/
                RenderingUtils.renderBlueStackTooltip((int) offsetX + 160, (int) offsetY + 10, descriptionStacks,
                        getStandardFontRenderer(), getRenderItem());
            }
        }

        private void renderHeadline(float offsetX, float offsetY, String unlocName) {
            String head = I18n.format(unlocName);
            FontRenderer fr = getStandardFontRenderer();
            float scale = 1.3F;
            GlStateManager.pushMatrix();
            GlStateManager.translate(offsetX, offsetY, 0);
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.disableDepth();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            fr.drawString(head, 0, 0, 0x00DDDDDD, true);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
            GlStateManager.color(1F, 1F, 1F, 1F);
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }

        private float renderSizeDescription(float offsetX, float offsetY) {
            Vec3i size = blocks.getSize();
            FontRenderer fr = getStandardFontRenderer();
            float scale = 1.3F;
            String desc = size.getX() + " - " + size.getY() + " - " + size.getZ();
            float length = fr.getStringWidth(desc) * scale;
            GlStateManager.pushMatrix();
            GlStateManager.translate(offsetX, offsetY, 0);
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.disableDepth();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            fr.drawString(desc, 0, 0, 0x00DDDDDD, true);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
            GlStateManager.color(1F, 1F, 1F, 1F);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            return length + 8F;
        }

        private void renderStructure(float offsetX, float offsetY, float pTicks) {
            Point.Double offset = renderOffset(offsetX + 8, offsetY);

            if (Mouse.isButtonDown(0) && totalRenderFrame > 30) {
                structRender.rotate(0.25 * Mouse.getDY(), 0.25 * Mouse.getDX(), 0);
            }

            structRender.render3DSliceGUI(offset.x + shift.getX(), offset.y + shift.getY(), pTicks, drawSlice);
        }

        private Point.Double renderOffset(float stdPageOffsetX, float stdPageOffsetY) {
            return new Point.Double(stdPageOffsetX + ((IJournalPage.DEFAULT_WIDTH * 2D) / 5D), stdPageOffsetY + ((IJournalPage.DEFAULT_HEIGHT * 4D) / 6D));
        }

        @Override
        public boolean propagateMouseClick(int mouseX, int mouseZ) {
            if (switchView != null && switchView.contains(mouseX, mouseZ)) {
                if (drawSlice.isPresent()) {
                    drawSlice = Optional.empty();
                } else {
                    drawSlice = Optional.of(structRender.getDefaultSlice());
                }
                SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
                return true;
            }
            if (sliceUp != null && drawSlice.isPresent() && sliceUp.contains(mouseX, mouseZ)) {
                drawSlice = Optional.of(drawSlice.get() + 1);
                SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
                return true;
            }
            if (sliceDown != null && drawSlice.isPresent() && sliceDown.contains(mouseX, mouseZ)) {
                drawSlice = Optional.of(drawSlice.get() - 1);
                SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
                return true;
            }
            return false;
        }
    }

}
