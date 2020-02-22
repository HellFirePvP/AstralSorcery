/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.helper.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.helper.ScreenRenderBoundingBox;
import hellfirepvp.astralsorcery.client.screen.helper.SizeHandler;
import hellfirepvp.astralsorcery.client.screen.journal.overlay.ScreenJournalOverlayPerkStatistics;
import hellfirepvp.astralsorcery.client.screen.journal.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.screen.journal.perk.DynamicPerkRender;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkTreeSizeHandler;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.ScreenTextEntry;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.useables.ItemPerkSeal;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGem;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.client.PktPerkGemModification;
import hellfirepvp.astralsorcery.common.network.play.client.PktRequestPerkSealAction;
import hellfirepvp.astralsorcery.common.network.play.client.PktUnlockPerk;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.AllocationStatus;
import hellfirepvp.astralsorcery.common.perk.AttributeConverterPerk;
import hellfirepvp.astralsorcery.common.perk.ProgressGatedPerk;
import hellfirepvp.astralsorcery.common.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.perk.node.GemSlotPerk;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalPerkTree
 * Created by HellFirePvP
 * Date: 08.08.2019 / 18:29
 */
public class ScreenJournalPerkTree extends ScreenJournal {

    private static Rectangle rectSealBox = new Rectangle(29, 16, 16, 16);
    private static Rectangle rectSearchTextEntry = new Rectangle(300, 16, 88, 15);

    //private static final BufferBatch drawBufferConnections = BufferBatch.make();
    private static BatchPerkContext drawBuffer;
    private static BatchPerkContext.TextureObjectGroup searchContext;
    private static BatchPerkContext.TextureObjectGroup sealContext;

    private SizeHandler sizeHandler;
    private ScreenRenderBoundingBox guiBox;

    private ScalingPoint mousePosition, previousMousePosition;
    private int mouseBufferX, mouseBufferY;
    private boolean mouseDragging = false;

    private AbstractPerk unlockPrimed = null;

    private AbstractPerk sealBreakPrimed = null;
    private int tickSealBreak = 0;

    private int guiOffsetX, guiOffsetY;
    public boolean expectReinit = false;

    private Map<AbstractPerk, Rectangle.Double> thisFramePerks = Maps.newHashMap();
    private Map<AbstractPerk, Long> unlockEffects = Maps.newHashMap();
    private Map<AbstractPerk, Long> breakEffects = Maps.newHashMap();

    private ScreenTextEntry searchTextEntry = new ScreenTextEntry();
    private List<AbstractPerk> searchMatches = Lists.newArrayList();

    private GemSlotPerk socketMenu = null;
    private Rectangle rSocketMenu = null;
    private Map<Rectangle, Integer> slotsSocketMenu = Maps.newHashMap();
    private Rectangle rStatStar = null;

    private ItemStack mouseSealStack = ItemStack.EMPTY;
    private ItemStack foundSeals = ItemStack.EMPTY;

    public ScreenJournalPerkTree() {
        super(new TranslationTextComponent("screen.astralsorcery.tome.perks"), 30);
        this.closeWithInventoryKey = false;
        this.searchTextEntry.setChangeCallback(this::updateSearchHighlight);

        buildTree();
    }

    private void buildTree() {
        this.guiBox = new ScreenRenderBoundingBox(10, 10, guiWidth - 10, guiHeight - 10);

        this.sizeHandler = new PerkTreeSizeHandler(this.guiHeight - 40, this.guiWidth - 20);
        this.sizeHandler.setScaleSpeed(0.04F);
        this.sizeHandler.setMaxScale(1F);
        this.sizeHandler.setMinScale(0.1F);
        this.sizeHandler.updateSize();

        this.mousePosition = ScalingPoint.createPoint(0, 0, this.sizeHandler.getScalingFactor(), false);
    }

    public static void initializeDrawBuffer() {
        drawBuffer = new BatchPerkContext();

        searchContext = drawBuffer.addContext(SpritesAS.SPR_PERK_SEARCH, BatchPerkContext.PRIORITY_OVERLAY);
        sealContext = drawBuffer.addContext(SpritesAS.SPR_PERK_SEAL, BatchPerkContext.PRIORITY_FOREGROUND);

        List<PerkRenderGroup> groups = Lists.newArrayList();
        for (PerkTreePoint<?> p : PerkTree.PERK_TREE.getPerkPoints()) {
            p.addGroups(groups);
        }
        for (PerkRenderGroup group : groups) {
            group.batchRegister(drawBuffer);
        }
    }

    @Override
    protected void init() {
        super.init();

        if (this.expectReinit) {
            this.expectReinit = false;
            return;
        }

        this.guiOffsetX = guiLeft + 10;
        this.guiOffsetY = guiTop + 10;

        boolean shifted = false;
        PlayerProgress progress = ResearchHelper.getClientProgress();

        IMajorConstellation attunement = progress.getAttunedConstellation();
        if (attunement != null) {
            AbstractPerk root = PerkTree.PERK_TREE.getRootPerk(attunement);
            if (root != null) {
                Point.Double shift = this.sizeHandler.evRelativePos(root.getOffset());
                this.moveMouse(MathHelper.floor(shift.x), MathHelper.floor(shift.y));
                shifted = true;
            }
        }

        if (!shifted) {
            this.moveMouse(MathHelper.floor(this.sizeHandler.getTotalWidth() / 2),
                    MathHelper.floor(this.sizeHandler.getTotalHeight() / 2));
        }

        this.applyMovedMouseOffset();
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        GlStateManager.enableBlend();
        this.thisFramePerks.clear();

        GlStateManager.pushMatrix();

        this.blitOffset -= 50;
        this.drawBackground();
        this.blitOffset += 50;

        double guiFactor = Minecraft.getInstance().mainWindow.getGuiScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(MathHelper.floor((guiLeft + 27) * guiFactor), MathHelper.floor((guiTop + 27) * guiFactor),
                MathHelper.floor((guiWidth - 54) * guiFactor), MathHelper.floor((guiHeight - 54) * guiFactor));
        drawPerkTree(pTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.drawDefault(TexturesAS.TEX_GUI_BOOK_FRAME_FULL, mouseX, mouseY);

        drawSearchBox();
        drawMiscInfo(mouseX, mouseY, pTicks);
        drawSocketContextMenu();
        drawSealBox();
        drawHoverTooltips(mouseX, mouseY);

        if (!this.mouseSealStack.isEmpty()) {
            GlStateManager.disableDepthTest();
            RenderingUtils.renderItemStack(this.itemRenderer, this.mouseSealStack, mouseX - 8, mouseY - 8, null);
            GlStateManager.enableDepthTest();
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void tick() {
        super.tick();

        if (Minecraft.getInstance().player != null) {
            int count = ItemPerkSeal.getPlayerSealCount(Minecraft.getInstance().player);
            if (count > 0) {
                this.foundSeals = new ItemStack(ItemsAS.PERK_SEAL, count);
            } else {
                this.foundSeals = ItemStack.EMPTY;
            }
        } else {
            this.foundSeals = ItemStack.EMPTY;
        }

        this.tickSealBreak--;
        if (this.tickSealBreak <= 0) {
            this.tickSealBreak = 0;
            this.sealBreakPrimed = null;
        }
    }

    private void drawSealBox() {
        GlStateManager.disableDepthTest();
        GlStateManager.disableAlphaTest();
        TexturesAS.TEX_GUI_MENU_SLOT.bindTexture();
        RenderingGuiUtils.drawTexturedRect(guiLeft + rectSealBox.x - 1, guiTop + rectSealBox.y - 1, this.blitOffset, rectSealBox.width + 2, rectSealBox.height + 2, TexturesAS.TEX_GUI_MENU_SLOT);
        GlStateManager.enableAlphaTest();

        if (!this.foundSeals.isEmpty()) {
            RenderingUtils.renderItemStack(this.itemRenderer, this.foundSeals, guiLeft + rectSealBox.x, guiTop + rectSealBox.y, null);
        }
        GlStateManager.enableDepthTest();
    }

    private void drawHoverTooltips(int mouseX, int mouseY) {
        PlayerEntity player = Minecraft.getInstance().player;

        for (Rectangle r : this.slotsSocketMenu.keySet()) {
            if (r.contains(mouseX, mouseY)) {
                Integer slot = this.slotsSocketMenu.get(r);
                ItemStack in = player.inventory.getStackInSlot(slot);
                if (!in.isEmpty()) {
                    renderTooltip(in, mouseX, mouseY);
                }
                return;
            }
        }

        GlStateManager.disableDepthTest();

        Style gray  = new Style().setColor(TextFormatting.GRAY);
        Style red   = new Style().setColor(TextFormatting.RED);
        Style blue  = new Style().setColor(TextFormatting.BLUE);
        Style green = new Style().setColor(TextFormatting.GREEN);
        Style miscInfo  = new Style().setColor(TextFormatting.BLUE).setItalic(true);
        Style tTipInfo  = new Style().setColor(TextFormatting.GRAY).setItalic(true);

        if (!this.foundSeals.isEmpty() && rectSealBox.contains(mouseX - guiLeft, mouseY - guiTop)) {
            List<ITextComponent> toolTip = this.foundSeals.getTooltip(Minecraft.getInstance().player,
                    Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
            toolTip.add(new StringTextComponent(""));
            toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.sealed.usage").setStyle(gray));
            RenderingDrawUtils.renderBlueTooltipComponents(mouseX, mouseY, toolTip, font, false);
        } else {
            for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
                if (rctPerk.getValue().contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                    List<ITextComponent> toolTip = new LinkedList<>();
                    AbstractPerk perk = rctPerk.getKey();
                    PlayerProgress prog = ResearchHelper.getClientProgress();

                    perk.getLocalizedTooltip().forEach(line -> {
                        Style style = line.getStyle();
                        if (style.getColor() == null) {
                            line.setStyle(tTipInfo);
                        }
                        toolTip.add(line);
                    });

                    if (prog.isPerkSealed(perk)) {
                        toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.sealed").setStyle(red));
                        toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.sealed.break").setStyle(red));
                    } else if (prog.hasPerkUnlocked(perk)) {
                        toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.active").setStyle(green));
                    } else if (perk.mayUnlockPerk(prog, player)) {
                        toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.available").setStyle(blue));
                    } else {
                        toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.locked").setStyle(gray));
                    }

                    if (Minecraft.getInstance().gameSettings.advancedItemTooltips) {
                        if (I18n.hasKey(perk.getCategory().getUnlocalizedName())) {
                            toolTip.add(new StringTextComponent(String.format("[%s]", perk.getCategory().getLocalizedName())).setStyle(tTipInfo));
                        }
                    }
                    Collection<ITextComponent> modInfo = perk.getSource();
                    if (modInfo != null) {
                        for (ITextComponent cmp : modInfo) {
                            toolTip.add(cmp.setStyle(miscInfo));
                        }
                    }
                    if (Minecraft.getInstance().gameSettings.showDebugInfo) {
                        toolTip.add(new StringTextComponent(""));
                        toolTip.add(new StringTextComponent(perk.getRegistryName().toString()).setStyle(gray));
                        toolTip.add(new TranslationTextComponent("astralsorcery.misc.ctrlcopy").setStyle(gray));
                    }
                    RenderingDrawUtils.renderBlueTooltipComponents(mouseX, mouseY, toolTip, font, true);
                    break;
                }
            }
        }

        GlStateManager.enableDepthTest();
    }

    private void drawSocketContextMenu() {
        this.rSocketMenu = null;
        this.slotsSocketMenu.clear();

        if (socketMenu != null) {
            AbstractPerk sMenuPerk = (AbstractPerk) socketMenu;
            Map<Integer, ItemStack> found = ItemUtils.findItemsIndexedInPlayerInventory(Minecraft.getInstance().player,
                    s -> !s.isEmpty() && s.getItem() instanceof ItemPerkGem && !ItemPerkGem.getModifiers(s).isEmpty());
            if (found.isEmpty()) { // Close then.
                closeSocketMenu();
                return;
            }

            Point offset = sMenuPerk.getPoint().getOffset();
            double x = this.sizeHandler.evRelativePosX(offset.x);
            double y = this.sizeHandler.evRelativePosY(offset.y);
            Point.Double scaledOffset = shift2DOffset(x, y);
            int offsetX = MathHelper.floor(scaledOffset.x);
            int offsetY = MathHelper.floor(scaledOffset.y);

            double scale = this.sizeHandler.getScalingFactor();

            int scaledSlotSize = (int) Math.round(18 * scale);

            int realWidth = Math.min(5, found.size());
            int realHeight = (found.size() / 5 + (found.size() % 5 == 0 ? 0 : 1));

            int width  = realWidth * scaledSlotSize;
            int height = realHeight * scaledSlotSize;
            rSocketMenu = new Rectangle((int) (offsetX + (12 * scale) - 4), (int) (offsetY - (12 * scale) - 4), width + 4, height + 4);

            if (!this.guiBox.isInBox(rSocketMenu.x - guiLeft, rSocketMenu.y - guiTop) ||
                    !this.guiBox.isInBox(rSocketMenu.x + rSocketMenu.width - guiLeft, rSocketMenu.y + rSocketMenu.height - guiTop)) {
                closeSocketMenu();
                return;
            }

            GlStateManager.pushMatrix();
            GlStateManager.translated(offsetX, offsetY, 0);
            GlStateManager.scaled(scale, scale, scale);
            RenderingDrawUtils.renderBlueTooltipBox(0, 0, realWidth * 18, realHeight * 18);
            GlStateManager.popMatrix();

            offsetX += 12 * scale;
            offsetY -= 12 * scale;

            TexturesAS.TEX_GUI_MENU_SLOT_GEM_CONTEXT.bindTexture();

            GlStateManager.disableDepthTest();
            for (int index = 0; index < found.size(); index++) {
                int addedX = (index % 5) * scaledSlotSize;
                int addedY = (index / 5) * scaledSlotSize;
                RenderingGuiUtils.drawTexturedRect(offsetX + addedX, offsetY + addedY, this.blitOffset, scaledSlotSize, scaledSlotSize,
                        0, 0, 1, 1);
            }

            GlStateManager.enableDepthTest();

            int index = 0;
            for (Integer slotId : found.keySet()) {
                ItemStack stack = found.get(slotId);
                int addedX = (index % 5) * scaledSlotSize;
                int addedY = (index / 5) * scaledSlotSize;
                Rectangle r = new Rectangle(offsetX + addedX, offsetY + addedY, scaledSlotSize, scaledSlotSize);

                GlStateManager.pushMatrix();
                GlStateManager.translated(offsetX + addedX + 1, offsetY + addedY + 1, 0);
                GlStateManager.scaled(scale, scale, scale);
                RenderingUtils.renderItemStack(this.itemRenderer, stack, 0, 0, null);
                GlStateManager.popMatrix();

                slotsSocketMenu.put(r, slotId);
                index++;
            }
        }
    }

    private void drawMiscInfo(int mouseX, int mouseY, float pTicks) {
        PlayerProgress prog = ResearchHelper.getClientProgress();
        PlayerEntity player = Minecraft.getInstance().player;

        int availablePerks;
        if (prog.getAttunedConstellation() != null && (availablePerks = prog.getAvailablePerkPoints(player)) > 0) {
            GlStateManager.disableDepthTest();
            GlStateManager.pushMatrix();
            GlStateManager.translated(guiLeft + 50, guiTop + 18, 0);

            RenderingDrawUtils.renderStringAtCurrentPos(font, I18n.format("perk.info.astralsorcery.points", availablePerks), 0xCCCCCC);

            GlStateManager.popMatrix();
            GlStateManager.enableDepthTest();
        }

        rStatStar = RenderingDrawUtils.drawInfoStar(guiLeft + 288, guiTop + 20, this.blitOffset + 100, 16, pTicks);
        if (rStatStar.contains(mouseX, mouseY)) {
            GlStateManager.disableDepthTest();
            GlStateManager.pushMatrix();
            GlStateManager.translated(rStatStar.x + rStatStar.width / 2F, rStatStar.y + rStatStar.height, 0);

            RenderingDrawUtils.renderBlueTooltipString(0, 0, Lists.newArrayList(I18n.format("perk.reader.astralsorcery.infostar")), font, false);

            GlStateManager.popMatrix();
            GlStateManager.enableDepthTest();
        }
    }

    private void drawSearchBox() {
        GlStateManager.color4f(1F, 1F, 1F, 1F);

        GlStateManager.disableAlphaTest();
        GlStateManager.disableDepthTest();
        GlStateManager.pushMatrix();
        GlStateManager.translated(guiLeft + 300, guiTop + 16, 0);
        TexturesAS.TEX_GUI_TEXT_FIELD.bindTexture();
        RenderingGuiUtils.drawTexturedRectAtCurrentPos(88.5, 15, this.blitOffset);

        String text = this.searchTextEntry.getText();

        int length = font.getStringWidth(text);
        boolean addDots = length > 75;
        while (length > 75) {
            text = text.substring(1);
            length = font.getStringWidth("..." + text);
        }
        if (addDots) {
            text = "..." + text;
        }

        if ((ClientScheduler.getClientTick() % 20) > 10) {
            text += "_";
        }

        GlStateManager.translated(4, 4, 0);
        RenderingDrawUtils.renderStringAtCurrentPos(font, text, 0xCCCCCC);

        GlStateManager.popMatrix();
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
    }

    private void drawPerkTree(float partialTicks) {
        PlayerEntity player = Minecraft.getInstance().player;

        GlStateManager.disableAlphaTest();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder connBuffer = tes.getBuffer();
        connBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        TexturesAS.TEX_GUI_LINE_CONNECTION.bindTexture();
        PlayerProgress progress = ResearchHelper.getClientProgress();

        for (Tuple<AbstractPerk, AbstractPerk> perkConnection : PerkTree.PERK_TREE.getConnections()) {
            AllocationStatus status;

            int alloc = 0;
            if (progress.hasPerkUnlocked(perkConnection.getA())) {
                alloc++;
            }
            if (progress.hasPerkUnlocked(perkConnection.getB())) {
                alloc++;
            }
            if (alloc == 2) {
                status = AllocationStatus.ALLOCATED;
            } else if (alloc == 1 && progress.hasFreeAllocationPoint(player)) {
                status = AllocationStatus.UNLOCKABLE;
            } else {
                status = AllocationStatus.UNALLOCATED;
            }

            Point offsetOne = perkConnection.getA().getPoint().getOffset();
            Point offsetTwo = perkConnection.getB().getPoint().getOffset();
            Point.Double shiftOne = this.sizeHandler.evRelativePos(offsetOne);
            Point.Double shiftTwo = this.sizeHandler.evRelativePos(offsetTwo);
            drawConnection(connBuffer, status, shiftOne, shiftTwo, ClientScheduler.getClientTick() + offsetOne.x + offsetOne.y + offsetTwo.x + offsetTwo.y);
        }
        tes.draw();

        drawBuffer.beginDrawingPerks();

        List<Runnable> renderDynamic = Lists.newArrayList();
        for (PerkTreePoint perkPoint : PerkTree.PERK_TREE.getPerkPoints()) {
            Point offset = perkPoint.getOffset();
            double x = this.sizeHandler.evRelativePosX(offset.x);
            double y = this.sizeHandler.evRelativePosY(offset.y);
            Rectangle.Double perkRect = drawPerk(drawBuffer, perkPoint,
                    x, y,
                    partialTicks, ClientScheduler.getClientTick() + offset.x + offset.y,
                    progress.isPerkSealed(perkPoint.getPerk()),
                    renderDynamic);
            if (perkRect != null) {
                this.thisFramePerks.put(perkPoint.getPerk(), perkRect);
            }
        }
        drawBuffer.draw();

        renderDynamic.forEach(Runnable::run);
        GlStateManager.enableAlphaTest();

        this.unlockEffects.keySet().removeIf(perk -> !drawPerkUnlock(perk, this.unlockEffects.get(perk)));
        this.breakEffects.keySet().removeIf(perk -> !drawPerkSealBreak(perk, this.breakEffects.get(perk), partialTicks));
    }

    private boolean drawPerkSealBreak(AbstractPerk perk, long tick, float pTicks) {
        int count = (int) (ClientScheduler.getClientTick() - tick);
        SpriteSheetResource sealBreakSprite = SpritesAS.SPR_PERK_SEAL_BREAK;
        if (count >= sealBreakSprite.getFrameCount()) {
            return false;
        }
        Point.Double oPos = this.sizeHandler.evRelativePos(perk.getOffset());
        Point.Double offset = shift2DOffset(oPos.x, oPos.y);

        float sealFade = 1.0F - (((float) count) + pTicks) / ((float) sealBreakSprite.getFrameCount());
        double width = 22;
        Rectangle.Double rct;
        if ((rct = thisFramePerks.get(perk)) != null) {
            width = rct.width;
        }
        width *= 0.75;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        SpritesAS.SPR_PERK_SEAL.bindTexture();

        GlStateManager.pushMatrix();
        GlStateManager.translated(offset.x, offset.y, 0);

        Point pOffset = perk.getPoint().getOffset();
        drawSeal(vb, width, 0, 0,
                ClientScheduler.getClientTick() + pOffset.x + pOffset.y, sealFade * 0.75F);

        GlStateManager.color4f(1F, 1F, 1F, 0.85F);

        tes.draw();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        sealBreakSprite.bindTexture();

        Vector3 starVec = new Vector3(-width, -width, 0);
        double uLength = sealBreakSprite.getUWidth();
        double vLength = sealBreakSprite.getVWidth();
        Tuple<Double, Double> off = sealBreakSprite.getUVOffset(count);
        Point.Double frameUV = new Point.Double(off.getA(), off.getB());

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(width * u * 2).addY(width * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(frameUV.x + uLength * u, frameUV.y + vLength * v).endVertex();
        }

        GlStateManager.disableAlphaTest();
        tes.draw();
        GlStateManager.enableAlphaTest();

        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
        return true;
    }

    private boolean drawPerkUnlock(AbstractPerk perk, long tick) {
        int count = (int) (ClientScheduler.getClientTick() - tick);
        SpriteSheetResource spritePerkUnlock = SpritesAS.SPR_PERK_UNLOCK;
        if (count >= spritePerkUnlock.getFrameCount()) {
            return false;
        }
        Point.Double oPos = this.sizeHandler.evRelativePos(perk.getOffset());
        Point.Double offset = shift2DOffset(oPos.x, oPos.y);

        double width = 22;
        Rectangle.Double rct;
        if ((rct = thisFramePerks.get(perk)) != null) {
            width = rct.width;
        }
        width *= 2.5;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        spritePerkUnlock.bindTexture();

        GlStateManager.pushMatrix();
        GlStateManager.translated(offset.x, offset.y, 0);

        Vector3 starVec = new Vector3(-width, -width, 0);
        double uLength = spritePerkUnlock.getUWidth();
        double vLength = spritePerkUnlock.getVWidth();
        Tuple<Double, Double> off = spritePerkUnlock.getUVOffset(count);
        Point.Double frameUV = new Point.Double(off.getA(), off.getB());

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(width * u * 2).addY(width * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(frameUV.x + uLength * u, frameUV.y + vLength * v).endVertex();
        }

        GlStateManager.disableAlphaTest();
        tes.draw();
        GlStateManager.enableAlphaTest();
        GlStateManager.popMatrix();
        return true;
    }

    @Nullable
    private Rectangle.Double drawPerk(BatchPerkContext ctx, PerkTreePoint perkPoint,
                                      double lowX, double lowY, float pTicks, long effectTick, boolean renderSeal,
                                      Collection<Runnable> outRenderDynamic) {
        Point.Double offset = shift2DOffset(lowX, lowY);

        double scale = this.sizeHandler.getScalingFactor();
        AllocationStatus status = perkPoint.getPerk().getPerkStatus(Minecraft.getInstance().player, LogicalSide.CLIENT);

        Rectangle.Double drawSize = perkPoint.renderPerkAtBatch(ctx, status, effectTick, pTicks, offset.x, offset.y, scale);


        if (perkPoint instanceof DynamicPerkRender) {
            outRenderDynamic.add(() ->
                    ((DynamicPerkRender) perkPoint).renderAt(status, effectTick, pTicks, offset.x, offset.y, scale));
        }

        if (drawSize == null) {
            return null;
        }

        if (renderSeal) {
            this.drawSeal(ctx, drawSize.width * 0.75, offset.x, offset.y, effectTick);
        }

        if (this.searchMatches.contains(perkPoint.getPerk())) {
            drawSearchMarkHalo(ctx, drawSize, offset.x, offset.y);
        }

        double mapDrawSize = 28;
        if (perkPoint.getPerk() instanceof AttributeConverterPerk) {
            for (PerkConverter converter : ((AttributeConverterPerk) perkPoint.getPerk()).provideConverters(Minecraft.getInstance().player, LogicalSide.CLIENT)) {
                if (converter instanceof PerkConverter.Radius) {
                    double radius = ((PerkConverter.Radius) converter).getRadius();

                    drawSearchHalo(ctx, mapDrawSize * radius * scale, offset.x, offset.y);
                }
            }
        }

        return new Rectangle.Double(offset.x - (drawSize.width / 2), offset.y - (drawSize.height / 2),
                drawSize.width, drawSize.height);
    }

    private void drawSeal(BatchPerkContext ctx, double size, double x, double y, long spriteOffsetTick) {
        BufferContext batch = ctx.getContext(sealContext);
        drawSeal(batch, size, x, y, spriteOffsetTick, 1F);
    }

    private void drawSeal(BufferBuilder vb, double size, double x, double y, long spriteOffsetTick, float alpha) {
        SpriteSheetResource tex = SpritesAS.SPR_PERK_SEAL;
        if (tex == null) {
            return;
        }

        double uLength = tex.getULength();
        double vLength = tex.getVLength();
        Tuple<Double, Double> frameUV = tex.getUVOffset(spriteOffsetTick);
        Vector3 starVec = new Vector3(x - size, y - size, 0);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(size * u * 2).addY(size * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ())
                    .tex(frameUV.getA() + uLength * u, frameUV.getB() + vLength * v)
                    .color(1F, 1F, 1F, alpha)
                    .endVertex();
        }
    }

    private void drawSearchMarkHalo(BatchPerkContext ctx, Rectangle.Double draw, double x, double y) {
        drawSearchHalo(ctx, draw.width, x, y);
    }

    private void drawSearchHalo(BatchPerkContext ctx, double size, double x, double y) {
        BufferContext batch = ctx.getContext(searchContext);
        SpriteSheetResource searchMark = SpritesAS.SPR_PERK_SEARCH;

        searchMark.bindTexture();
        Vector3 starVec = new Vector3(x - size, y - size, 0);
        double uLength = searchMark.getUWidth();
        double vLength = searchMark.getVWidth();
        Point.Double frameUV = searchMark.getUVOffset();

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(size * u * 2).addY(size * v * 2);
            batch.pos(pos.getX(), pos.getY(), pos.getZ())
                    .tex(frameUV.x + uLength * u, frameUV.y + vLength * v)
                    .color(0.8F, 0.1F, 0.1F, 1F)
                    .endVertex();
        }
    }

    private void drawConnection(BufferBuilder vb, AllocationStatus status, Point.Double offset, Point.Double target, long effectTick) {
        Point.Double offsetSrc = shift2DOffset(offset.x, offset.y);
        Point.Double offsetDst = shift2DOffset(target.x, target.y);
        Color overlay = Color.WHITE;
        switch (status) {
            case UNALLOCATED:
                overlay = ColorsAS.PERK_CONNECTION_UNALLOCATED;
                break;
            case ALLOCATED:
                overlay = ColorsAS.PERK_CONNECTION_ALLOCATED;
                break;
            case UNLOCKABLE:
                overlay = ColorsAS.PERK_CONNECTION_UNLOCKABLE;
                break;
            default:
                break;
        }

        double effectPart = (Math.sin(Math.toRadians(((effectTick) * 8) % 360D)) + 1D) / 4D;
        float br = 0.1F + 0.4F * (2F - ((float) effectPart));
        float rR = (overlay.getRed()   / 255F) * br;
        float rG = (overlay.getGreen() / 255F) * br;
        float rB = (overlay.getBlue()  / 255F) * br;
        float rA = (overlay.getAlpha() / 255F) * br;

        Vector3 fromStar = new Vector3(offsetSrc.x, offsetSrc.y, 0);
        Vector3 toStar   = new Vector3(offsetDst.x, offsetDst.y, 0);

        double width = 4.0D * this.sizeHandler.getScalingFactor();

        Vector3 dir = toStar.clone().subtract(fromStar);
        Vector3 degLot = dir.clone().crossProduct(new Vector3(0, 0, 1)).normalize().multiply(width);//.multiply(j == 0 ? 1 : -1);

        Vector3 vec00 = fromStar.clone().add(degLot);
        Vector3 vecV = degLot.clone().multiply(-2);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = vec00.clone().add(dir.clone().multiply(u)).add(vecV.clone().multiply(v));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(u, v).color(rR, rG, rB, rA).endVertex();
        }
    }

    private Point.Double shift2DOffset(double x, double y) {
        double scaledLeft = this.mousePosition.getScaledPosX() - sizeHandler.widthToBorder;
        double scaledTop =  this.mousePosition.getScaledPosY() - sizeHandler.heightToBorder;
        double xAdd = x - scaledLeft;
        double yAdd = y - scaledTop;
        double offsetX = guiOffsetX + xAdd;
        double offsetY = guiOffsetY + yAdd;
        return new Point.Double(offsetX, offsetY);
    }

    @Override
    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {
        super.mouseDragTick(mouseX, mouseY, mouseDiffX, mouseDiffY, mouseOffsetX, mouseOffsetY);
        if (this.mouseSealStack.isEmpty()) {
            moveMouse(mouseDiffX, mouseDiffY);
        }
    }

    @Override
    protected void mouseDragStop(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY) {
        super.mouseDragStop(mouseX, mouseY, mouseDiffX, mouseDiffY);
        if (this.mouseSealStack.isEmpty()) {
            applyMovedMouseOffset();
        }
    }

    private void moveMouse(double changeX, double changeY) {
        if (this.previousMousePosition != null) {
            mousePosition.updateScaledPos(
                    sizeHandler.clampX(previousMousePosition.getScaledPosX() + changeX),
                    sizeHandler.clampY(previousMousePosition.getScaledPosY() + changeY),
                    sizeHandler.getScalingFactor());
        } else {
            mousePosition.updateScaledPos(
                    sizeHandler.clampX(changeX),
                    sizeHandler.clampY(changeY),
                    sizeHandler.getScalingFactor());
        }
    }

    private void applyMovedMouseOffset() {
        this.previousMousePosition = ScalingPoint.createPoint(
                this.mousePosition.getScaledPosX(),
                this.mousePosition.getScaledPosY(),
                this.sizeHandler.getScalingFactor(),
                true);
    }

    private void drawBackground() {
        float br = 0.8F;

        GlStateManager.color4f(br, br, br, 1F);
        GlStateManager.disableBlend();
        TexturesAS.TEX_GUI_BACKGROUND_PERKS.bindTexture();
        BufferBuilder vb = Tessellator.getInstance().getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(guiLeft + 10,            guiTop - 10 + guiHeight, this.blitOffset).tex(0, 1).endVertex();
        vb.pos(guiLeft - 10 + guiWidth, guiTop - 10 + guiHeight, this.blitOffset).tex(1, 1).endVertex();
        vb.pos(guiLeft - 10 + guiWidth, guiTop + 10,             this.blitOffset).tex(1, 0).endVertex();
        vb.pos(guiLeft + 10,            guiTop + 10,             this.blitOffset).tex(0, 0).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.enableBlend();
        GlStateManager.color4f(1F, 1F, 1F, 1F);
    }

    private void updateSearchHighlight() {
        this.searchMatches.clear();

        String matchText = this.searchTextEntry.getText().toLowerCase();
        if (matchText.length() < 3) return;
        for (PerkTreePoint point : PerkTree.PERK_TREE.getPerkPoints()) {
            AbstractPerk perk = point.getPerk();
            if (perk instanceof ProgressGatedPerk &&
                    !((ProgressGatedPerk) perk).canSeeClient()) {
                continue;
            }
            if (I18n.hasKey(perk.getCategory().getUnlocalizedName()) && perk.getCategory().getLocalizedName().toLowerCase().contains(matchText)) {
                this.searchMatches.add(perk);
            } else {
                for (ITextComponent tooltip : perk.getLocalizedTooltip()) {
                    if (tooltip.getFormattedText().toLowerCase().contains(matchText)) {
                        this.searchMatches.add(perk);
                        break;
                    }
                }
            }
        }
        if (I18n.format("perk.info.astralsorcery.sealed").toLowerCase().contains(matchText)) {
            PlayerProgress prog = ResearchHelper.getClientProgress();
            for (AbstractPerk sealed : prog.getSealedPerks()) {
                if (!this.searchMatches.contains(sealed)) {
                    this.searchMatches.add(sealed);
                }
            }
        }
    }

    private void closeSocketMenu() {
        this.socketMenu = null;
        this.rSocketMenu = null;
        this.slotsSocketMenu.clear();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        if (super.mouseReleased(mouseX, mouseY, state)) {
            return true;
        }

        PlayerEntity player = Minecraft.getInstance().player;

        if (!this.mouseSealStack.isEmpty()) {
            this.mouseSealStack = ItemStack.EMPTY;
            if (Minecraft.getInstance().player == null) {
                return false;
            }

            PlayerProgress prog = ResearchHelper.getClientProgress();
            for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
                if (rctPerk.getValue().contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                    if (prog.hasPerkUnlocked(rctPerk.getKey()) &&
                            !prog.isPerkSealed(rctPerk.getKey()) &&
                            ItemPerkSeal.useSeal(player, true)) {
                        PktRequestPerkSealAction pkt = new PktRequestPerkSealAction(rctPerk.getKey(), true);
                        PacketChannel.CHANNEL.sendToServer(pkt);
                        return true;
                    }
                }
            }
        }

        if (this.unlockPrimed == null) {
            return false;
        }

        for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
            if (this.unlockPrimed.equals(rctPerk.getKey()) && rctPerk.getValue().contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                if (rctPerk.getKey().mayUnlockPerk(ResearchHelper.getClientProgress(), player)) {
                    PktUnlockPerk pkt = new PktUnlockPerk(false, rctPerk.getKey());
                    PacketChannel.CHANNEL.sendToServer(pkt);
                    break;
                }
            }
        }

        this.unlockPrimed = null;
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (scroll < 0) {
            this.sizeHandler.handleZoomOut();
            this.rescaleMouse();
            return true;
        }
        if (scroll > 0)  {
            this.sizeHandler.handleZoomIn();
            this.rescaleMouse();
            return true;
        }
        return false;
    }

    private void rescaleMouse() {
        this.mousePosition.rescale(this.sizeHandler.getScalingFactor());
        if (this.previousMousePosition != null) {
            this.previousMousePosition.rescale(this.sizeHandler.getScalingFactor());
        }
        this.moveMouse(0, 0);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        this.unlockPrimed = null;

        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        if (rectSearchTextEntry.contains(mouseX - guiLeft, mouseY - guiTop)) {
            searchTextEntry.setText("");
            return false;
        }
        if (socketMenu != null &&
                rSocketMenu != null &&
                !rSocketMenu.contains(mouseX, mouseY)) {
            closeSocketMenu();
            return false;
        }

        for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
            if (rctPerk.getValue().contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                AbstractPerk perk = rctPerk.getKey();
                if (perk instanceof GemSlotPerk) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        Minecraft mc = Minecraft.getInstance();

        if (socketMenu != null &&
                (mouseButton == 0 || mouseButton == 1) &&
                rSocketMenu != null &&
                !rSocketMenu.contains(mouseX, mouseY)) {
            closeSocketMenu();
        }

        if (mouseButton == 0) {
            if (socketMenu != null) {
                for (Rectangle r : slotsSocketMenu.keySet()) {
                    if (r.contains(mouseX, mouseY) && !socketMenu.hasItem(mc.player, LogicalSide.CLIENT)) {
                        int slotId = slotsSocketMenu.get(r);
                        ItemStack potentialStack = mc.player.inventory.getStackInSlot(slotId);
                        if (!potentialStack.isEmpty() &&
                                !ItemPerkGem.getModifiers(potentialStack).isEmpty()) {
                            PktPerkGemModification pkt = PktPerkGemModification.insertItem((AbstractPerk) socketMenu, slotId);
                            PacketChannel.CHANNEL.sendToServer(pkt);
                            closeSocketMenu();
                            SoundHelper.playSoundClient(SoundEvents.BLOCK_GLASS_PLACE, .35F, 9f);
                        }
                        return true;
                    }
                }
            }

            if (handleBookmarkClick(mouseX, mouseY)) {
                return true;
            }

            if (rectSealBox.contains(mouseX - guiLeft, mouseY - guiTop)) {
                if (!this.foundSeals.isEmpty()) {
                    this.mouseSealStack = new ItemStack(ItemsAS.PERK_SEAL);
                }
                return true;
            }

            if (rStatStar.contains(mouseX, mouseY)) {
                this.expectReinit = true;
                mc.displayGuiScreen(new ScreenJournalOverlayPerkStatistics(this));
                return true;
            }
        }

        PlayerProgress prog = ResearchHelper.getClientProgress();
        for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
            if (rctPerk.getValue().contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                AbstractPerk perk = rctPerk.getKey();
                if (mouseButton == 0 && mc.gameSettings.showDebugInfo && hasControlDown()) {
                    String perkKey = perk.getRegistryName().toString();
                    Minecraft.getInstance().keyboardListener.setClipboardString(perkKey);
                    mc.player.sendMessage(new TranslationTextComponent("astralsorcery.misc.ctrlcopy.copied", perkKey));
                    break;
                }
                if (mouseButton == 1) {
                    if (prog.hasPerkEffect(perk) && perk instanceof GemSlotPerk) {
                        if (((GemSlotPerk) perk).hasItem(mc.player, LogicalSide.CLIENT)) {
                            PktPerkGemModification pkt = PktPerkGemModification.dropItem(perk);
                            PacketChannel.CHANNEL.sendToServer(pkt);
                            AstralSorcery.getProxy().scheduleClientside(() -> {
                                if (mc.currentScreen == this) { //Only if user hasn't closed
                                    updateSearchHighlight();
                                }
                            }, 10);
                            SoundHelper.playSoundClient(SoundEvents.BLOCK_GLASS_PLACE, .35F, 9f);
                        } else {
                            this.socketMenu = (GemSlotPerk) perk;
                        }
                        return true;
                    }
                } else if (mouseButton == 0) {
                    if (perk.handleMouseClick(this, mouseX, mouseY)) {
                        return true;
                    }

                    if (!prog.hasPerkUnlocked(perk) && perk.mayUnlockPerk(prog, mc.player)) {
                        this.unlockPrimed = perk;
                    } else if (this.sealBreakPrimed != null && this.tickSealBreak > 0) {
                        PktRequestPerkSealAction pkt = new PktRequestPerkSealAction(perk, false);
                        PacketChannel.CHANNEL.sendToServer(pkt);
                    } else if (prog.isPerkSealed(perk)) {
                        this.sealBreakPrimed = perk;
                        this.tickSealBreak = 4;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (this.searchTextEntry.keyTyped(key)) {
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char charCode, int keyModifiers) {
        if (this.searchTextEntry.charTyped(charCode)) {
            return true;
        }
        return super.charTyped(charCode, keyModifiers);
    }

    public void playUnlockAnimation(AbstractPerk perk) {
        this.unlockEffects.put(perk, ClientScheduler.getClientTick());
        SoundHelper.playSoundClient(SoundsAS.PERK_UNLOCK, 0.5F, 1F);
    }

    public void playSealBreakAnimation(AbstractPerk perk) {
        this.updateSearchHighlight();
        this.breakEffects.put(perk, ClientScheduler.getClientTick());
        SoundHelper.playSoundClient(SoundsAS.PERK_UNSEAL, 0.5F, 1F);
    }

    public void playSealApplyAnimation(AbstractPerk perk) {
        this.updateSearchHighlight();
        SoundHelper.playSoundClient(SoundsAS.PERK_SEAL, 0.5F, 1F);
    }
}
