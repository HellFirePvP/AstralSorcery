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
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.helper.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.helper.ScreenRenderBoundingBox;
import hellfirepvp.astralsorcery.client.screen.helper.SizeHandler;
import hellfirepvp.astralsorcery.client.screen.journal.overlay.ScreenJournalOverlayPerkStatistics;
import hellfirepvp.astralsorcery.client.screen.journal.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.screen.journal.perk.DynamicPerkRender;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkTreeSizeHandler;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGem;
import hellfirepvp.astralsorcery.common.item.useables.ItemPerkSeal;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.client.PktPerkGemModification;
import hellfirepvp.astralsorcery.common.network.play.client.PktRequestPerkSealAction;
import hellfirepvp.astralsorcery.common.network.play.client.PktUnlockPerk;
import hellfirepvp.astralsorcery.common.perk.*;
import hellfirepvp.astralsorcery.common.perk.node.GemSlotPerk;
import hellfirepvp.astralsorcery.common.perk.source.AttributeConverterProvider;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    private static BatchPerkContext drawBuffer;
    private static BatchPerkContext.TextureObjectGroup searchContext;
    private static BatchPerkContext.TextureObjectGroup sealContext;

    private SizeHandler sizeHandler;
    private ScreenRenderBoundingBox guiBox;

    private ScalingPoint mousePosition, previousMousePosition;

    private AbstractPerk unlockPrimed = null;

    private AbstractPerk sealBreakPrimed = null;
    private int tickSealBreak = 0;

    private int guiOffsetX, guiOffsetY;
    public boolean expectReinit = false;

    private Map<AbstractPerk, Rectangle.Float> thisFramePerks = Maps.newHashMap();
    private Map<AbstractPerk, Long> unlockEffects = Maps.newHashMap();
    private Map<AbstractPerk, Long> breakEffects = Maps.newHashMap();

    private ScreenTextEntry searchTextEntry = new ScreenTextEntry();
    private List<AbstractPerk> searchMatches = Lists.newArrayList();

    private GemSlotPerk socketMenu = null;
    private Rectangle.Float rSocketMenu = null;
    private Map<Rectangle.Float, Integer> slotsSocketMenu = Maps.newHashMap();
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

        this.sizeHandler = new PerkTreeSizeHandler();
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
                Point.Float shift = this.sizeHandler.evRelativePos(root.getOffset());
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
        this.thisFramePerks.clear();

        double guiFactor = Minecraft.getInstance().getMainWindow().getGuiScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(MathHelper.floor((guiLeft + 27) * guiFactor), MathHelper.floor((guiTop + 27) * guiFactor),
                MathHelper.floor((guiWidth - 54) * guiFactor), MathHelper.floor((guiHeight - 54) * guiFactor));
        this.changeZLevel(-50);
        this.drawBackground();
        this.changeZLevel(50);

        this.drawPerkTree(pTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        RenderSystem.depthMask(false);
        this.drawDefault(TexturesAS.TEX_GUI_BOOK_FRAME_FULL, mouseX, mouseY);
        RenderSystem.depthMask(true);

        drawSearchBox();
        drawMiscInfo(mouseX, mouseY, pTicks);
        drawSocketContextMenu();
        drawSealBox();

        this.changeZLevel(510);
        drawHoverTooltips(mouseX, mouseY);
        this.changeZLevel(-510);

        if (!this.mouseSealStack.isEmpty()) {
            RenderingUtils.renderItemStack(this.itemRenderer, this.mouseSealStack, mouseX - 8, mouseY - 8, null);
        }
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
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        TexturesAS.TEX_GUI_MENU_SLOT.bindTexture();
        RenderingGuiUtils.drawTexturedRect(guiLeft + rectSealBox.x - 1, guiTop + rectSealBox.y - 1, this.getGuiZLevel(), rectSealBox.width + 2, rectSealBox.height + 2, TexturesAS.TEX_GUI_MENU_SLOT);
        RenderSystem.disableBlend();

        if (!this.foundSeals.isEmpty()) {
            RenderingUtils.renderItemStack(this.itemRenderer, this.foundSeals, guiLeft + rectSealBox.x, guiTop + rectSealBox.y, null);
        }
    }

    private void drawHoverTooltips(int mouseX, int mouseY) {
        PlayerEntity player = Minecraft.getInstance().player;

        for (Rectangle.Float r : this.slotsSocketMenu.keySet()) {
            if (r.contains(mouseX, mouseY)) {
                Integer slot = this.slotsSocketMenu.get(r);
                ItemStack in = player.inventory.getStackInSlot(slot);
                if (!in.isEmpty()) {
                    FontRenderer fr = in.getItem().getFontRenderer(in);
                    if (fr == null) {
                        fr = Minecraft.getInstance().fontRenderer;
                    }
                    List<String> toolTip = this.getTooltipFromItem(in);
                    RenderingDrawUtils.renderBlueTooltipString(mouseX, mouseY, this.getGuiZLevel(), toolTip, fr, true);
                }
                return;
            }
        }

        if (rStatStar.contains(mouseX, mouseY)) {
            RenderingDrawUtils.renderBlueTooltipString(rStatStar.x + rStatStar.width / 2, rStatStar.y + rStatStar.height, this.getGuiZLevel(),
                    Lists.newArrayList(I18n.format("perk.reader.astralsorcery.infostar")), font, false);
            return;
        }

        if (!this.foundSeals.isEmpty() && rectSealBox.contains(mouseX - guiLeft, mouseY - guiTop)) {
            List<ITextComponent> toolTip = this.foundSeals.getTooltip(Minecraft.getInstance().player,
                    Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
            toolTip.add(new StringTextComponent(""));
            toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.sealed.usage").applyTextStyle(TextFormatting.GRAY));
            RenderingDrawUtils.renderBlueTooltipComponents(mouseX, mouseY, this.getGuiZLevel(), toolTip, font, false);
        } else {
            for (Map.Entry<AbstractPerk, Rectangle.Float> rctPerk : this.thisFramePerks.entrySet()) {
                if (rctPerk.getValue().contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                    List<ITextComponent> toolTip = new LinkedList<>();
                    AbstractPerk perk = rctPerk.getKey();
                    PlayerProgress prog = ResearchHelper.getClientProgress();

                    perk.getLocalizedTooltip().forEach(line -> {
                        Style style = line.getStyle();
                        if (style.getColor() == null) {
                            line.applyTextStyle(TextFormatting.GRAY).applyTextStyle(TextFormatting.ITALIC);
                        }
                        toolTip.add(line);
                    });

                    if (prog.isPerkSealed(perk)) {
                        toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.sealed").applyTextStyle(TextFormatting.RED));
                        toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.sealed.break").applyTextStyle(TextFormatting.RED));
                    } else if (prog.hasPerkUnlocked(perk)) {
                        toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.active").applyTextStyle(TextFormatting.GREEN));
                    } else if (perk.mayUnlockPerk(prog, player)) {
                        toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.available").applyTextStyle(TextFormatting.BLUE));
                    } else {
                        toolTip.add(new TranslationTextComponent("perk.info.astralsorcery.locked").applyTextStyle(TextFormatting.GRAY));
                    }

                    if (Minecraft.getInstance().gameSettings.advancedItemTooltips) {
                        if (I18n.hasKey(perk.getCategory().getUnlocalizedName())) {
                            toolTip.add(new StringTextComponent(String.format("[%s]", perk.getCategory().getLocalizedName()))
                                    .applyTextStyle(TextFormatting.GRAY).applyTextStyle(TextFormatting.ITALIC));
                        }
                    }
                    Collection<ITextComponent> modInfo = perk.getSource();
                    if (modInfo != null) {
                        for (ITextComponent cmp : modInfo) {
                            toolTip.add(cmp.applyTextStyle(TextFormatting.BLUE).applyTextStyle(TextFormatting.ITALIC));
                        }
                    }
                    if (Minecraft.getInstance().gameSettings.showDebugInfo) {
                        toolTip.add(new StringTextComponent(""));
                        toolTip.add(new StringTextComponent(perk.getRegistryName().toString()).applyTextStyle(TextFormatting.GRAY));
                        toolTip.add(new TranslationTextComponent("astralsorcery.misc.ctrlcopy").applyTextStyle(TextFormatting.GRAY));
                    }
                    RenderingDrawUtils.renderBlueTooltipComponents(mouseX, mouseY, this.getGuiZLevel(), toolTip, font, true);
                    break;
                }
            }
        }
    }

    private void drawSocketContextMenu() {
        this.rSocketMenu = null;
        this.slotsSocketMenu.clear();

        if (socketMenu != null) {
            AbstractPerk sMenuPerk = (AbstractPerk) socketMenu;
            Map<Integer, ItemStack> found = ItemUtils.findItemsIndexedInPlayerInventory(Minecraft.getInstance().player,
                    s -> !s.isEmpty() && s.getItem() instanceof ItemPerkGem && !DynamicModifierHelper.getStaticModifiers(s).isEmpty());
            if (found.isEmpty()) { // Close then.
                closeSocketMenu();
                return;
            }

            Point.Float offset = this.sizeHandler.scalePointToGui(this, this.mousePosition, sMenuPerk.getPoint().getOffset());
            float offsetX = MathHelper.floor(offset.x);
            float offsetY = MathHelper.floor(offset.y);

            float scale = this.sizeHandler.getScalingFactor();
            float scaledSlotSize = 18F * scale;

            int realWidth = Math.min(5, found.size());
            int realHeight = (found.size() / 5 + (found.size() % 5 == 0 ? 0 : 1));

            float width  = realWidth * scaledSlotSize;
            float height = realHeight * scaledSlotSize;
            this.rSocketMenu = new Rectangle.Float(offsetX + (12 * scale) - 4, offsetY - (12 * scale) - 4, width + 4, height + 4);

            if (!this.guiBox.isInBox(rSocketMenu.x - guiLeft, rSocketMenu.y - guiTop) ||
                    !this.guiBox.isInBox(rSocketMenu.x + rSocketMenu.width - guiLeft, rSocketMenu.y + rSocketMenu.height - guiTop)) {
                closeSocketMenu();
                return;
            }

            RenderSystem.pushMatrix();
            RenderSystem.translated(offsetX, offsetY, 0);
            RenderSystem.scaled(scale, scale, scale);
            RenderingDrawUtils.renderBlueTooltipBox(0, 0, realWidth * 18, realHeight * 18);
            RenderSystem.popMatrix();

            float inventoryOffsetX = offsetX + 12 * scale;
            float inventoryOffsetY = offsetY - 12 * scale;
            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            TexturesAS.TEX_GUI_MENU_SLOT_GEM_CONTEXT.bindTexture();
            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                for (int index = 0; index < found.size(); index++) {
                    float addedX = (index % 5) * scaledSlotSize;
                    float addedY = (index / 5) * scaledSlotSize;
                    RenderingGuiUtils.rect(buf, inventoryOffsetX + addedX, inventoryOffsetY + addedY, this.getGuiZLevel(), scaledSlotSize, scaledSlotSize).draw();
                }
            });
            RenderSystem.disableBlend();

            offsetX += 12 * scale;
            offsetY -= 12 * scale;

            int index = 0;
            for (Integer slotId : found.keySet()) {
                ItemStack stack = found.get(slotId);
                float addedX = (index % 5) * scaledSlotSize;
                float addedY = (index / 5) * scaledSlotSize;
                Rectangle.Float r = new Rectangle.Float(offsetX + addedX, offsetY + addedY, scaledSlotSize, scaledSlotSize);

                RenderSystem.pushMatrix();
                RenderSystem.translated(offsetX + addedX + 1, offsetY + addedY + 1, 0);
                RenderSystem.scaled(scale, scale, scale);
                RenderingUtils.renderItemStack(this.itemRenderer, stack, 0, 0, null);
                RenderSystem.popMatrix();

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
            RenderingDrawUtils.renderStringAtPos(guiLeft + 50, guiTop + 18, this.getGuiZLevel(), font,
                    I18n.format("perk.info.astralsorcery.points", availablePerks), 0xCCCCCC, false);
        }

        MatrixStack renderStack = new MatrixStack();
        renderStack.translate(guiLeft + 288, guiTop + 20, this.getGuiZLevel());
        rStatStar = RenderingDrawUtils.drawInfoStar(renderStack, IDrawRenderTypeBuffer.defaultBuffer(), 16, pTicks);
        rStatStar.translate(guiLeft + 288, guiTop + 20);
    }

    private void drawSearchBox() {
        TexturesAS.TEX_GUI_TEXT_FIELD.bindTexture();
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, guiLeft + 300, guiTop + 16, this.getGuiZLevel(), 88.5F, 15).draw();
        });
        RenderSystem.disableBlend();

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

        RenderSystem.pushMatrix();
        RenderSystem.translated(guiLeft + 304, guiTop + 20, this.getGuiZLevel());
        RenderingDrawUtils.renderStringAtCurrentPos(font, text, 0xCCCCCC);
        RenderSystem.popMatrix();
    }

    private void drawPerkTree(float partialTicks) {
        PlayerEntity player = Minecraft.getInstance().player;
        PlayerProgress progress = ResearchHelper.getClientProgress();

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        TexturesAS.TEX_GUI_LINE_CONNECTION.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
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

                Point.Float offsetOne = perkConnection.getA().getPoint().getOffset();
                Point.Float offsetTwo = perkConnection.getB().getPoint().getOffset();
                drawConnection(buf, status, offsetOne, offsetTwo, ClientScheduler.getClientTick() + (int) offsetOne.x + (int) offsetOne.y + (int) offsetTwo.x + (int) offsetTwo.y);
            }
        });
        RenderSystem.disableBlend();

        drawBuffer.beginDrawingPerks();

        List<Runnable> renderDynamic = Lists.newArrayList();
        for (PerkTreePoint perkPoint : PerkTree.PERK_TREE.getPerkPoints()) {
            Point.Float offset = perkPoint.getOffset();
            Rectangle.Float perkRect = drawPerk(drawBuffer, perkPoint,
                    partialTicks, ClientScheduler.getClientTick() + (int) offset.x + (int) offset.y,
                    progress.isPerkSealed(perkPoint.getPerk()),
                    renderDynamic);
            if (perkRect != null) {
                this.thisFramePerks.put(perkPoint.getPerk(), perkRect);
            }
        }

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        drawBuffer.draw();
        RenderSystem.disableBlend();

        renderDynamic.forEach(Runnable::run);

        this.unlockEffects.keySet().removeIf(perk -> !drawPerkUnlock(perk, this.unlockEffects.get(perk)));
        this.breakEffects.keySet().removeIf(perk -> !drawPerkSealBreak(perk, this.breakEffects.get(perk), partialTicks));
    }

    private boolean drawPerkSealBreak(AbstractPerk perk, long tick, float pTicks) {
        int count = (int) (ClientScheduler.getClientTick() - tick);
        SpriteSheetResource sealBreakSprite = SpritesAS.SPR_PERK_SEAL_BREAK;
        if (count >= sealBreakSprite.getFrameCount()) {
            return false;
        }
        Point.Float offset = this.sizeHandler.scalePointToGui(this, this.mousePosition, perk.getOffset());

        float sealFade = 1.0F - (((float) count) + pTicks) / ((float) sealBreakSprite.getFrameCount());
        float width = 22;
        Rectangle.Float rct;
        if ((rct = thisFramePerks.get(perk)) != null) {
            width = rct.width;
        }
        float sealWidth = width * 0.75F;

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        SpritesAS.SPR_PERK_SEAL.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            Point.Float pOffset = perk.getPoint().getOffset();
            drawSeal(buf, sealWidth, offset.x, offset.y, ClientScheduler.getClientTick() + (int) pOffset.x + (int) pOffset.y, sealFade * 0.75F);
        });

        float uLength = sealBreakSprite.getUWidth();
        float vLength = sealBreakSprite.getVWidth();
        Tuple<Float, Float> uv = sealBreakSprite.getUVOffset(count);

        sealBreakSprite.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, offset.x - sealWidth, offset.y - sealWidth, this.getGuiZLevel(), sealWidth * 2, sealWidth * 2)
                    .color(1F, 1F, 1F, 0.85F)
                    .tex(uv.getA(), uv.getB(), uLength, vLength)
                    .draw();
        });
        RenderSystem.disableBlend();
        return true;
    }

    private boolean drawPerkUnlock(AbstractPerk perk, long tick) {
        int count = (int) (ClientScheduler.getClientTick() - tick);
        SpriteSheetResource spritePerkUnlock = SpritesAS.SPR_PERK_UNLOCK;
        if (count >= spritePerkUnlock.getFrameCount()) {
            return false;
        }
        Point.Float offset = this.sizeHandler.scalePointToGui(this, this.mousePosition, perk.getOffset());

        float width = 22;
        Rectangle.Float rct;
        if ((rct = thisFramePerks.get(perk)) != null) {
            width = rct.width;
        }
        float unlockWidth = width * 2.5F;

        float uLength = spritePerkUnlock.getUWidth();
        float vLength = spritePerkUnlock.getVWidth();
        Tuple<Float, Float> uv = spritePerkUnlock.getUVOffset(count);

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        spritePerkUnlock.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, offset.x - unlockWidth, offset.y - unlockWidth, this.getGuiZLevel(), unlockWidth * 2, unlockWidth * 2)
                    .tex(uv.getA(), uv.getB(), uLength, vLength)
                    .draw();
        });
        RenderSystem.disableBlend();
        return true;
    }

    @Nullable
    private Rectangle.Float drawPerk(BatchPerkContext ctx, PerkTreePoint perkPoint,
                                      float pTicks, long effectTick, boolean renderSeal,
                                      Collection<Runnable> outRenderDynamic) {
        Point.Float offset = this.sizeHandler.scalePointToGui(this, this.mousePosition, perkPoint.getOffset());

        float scale = this.sizeHandler.getScalingFactor();
        AllocationStatus status = perkPoint.getPerk().getPerkStatus(Minecraft.getInstance().player, LogicalSide.CLIENT);
        Rectangle.Float drawSize = perkPoint.renderPerkAtBatch(ctx, status, effectTick, pTicks, offset.x, offset.y, this.getGuiZLevel(), scale);

        if (perkPoint instanceof DynamicPerkRender) {
            outRenderDynamic.add(() ->
                    ((DynamicPerkRender) perkPoint).renderAt(status, effectTick, pTicks, offset.x, offset.y, this.getGuiZLevel(), scale));
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

        float mapDrawSize = 28F;
        if (perkPoint.getPerk() instanceof AttributeConverterProvider) {
            for (PerkConverter converter : ((AttributeConverterProvider) perkPoint.getPerk()).getConverters(Minecraft.getInstance().player, LogicalSide.CLIENT, true)) {
                if (converter instanceof PerkConverter.Radius) {
                    float radius = ((PerkConverter.Radius) converter).getRadius();

                    drawSearchHalo(ctx, mapDrawSize * radius * scale, offset.x, offset.y);
                }
            }
        }

        return new Rectangle.Float(offset.x - (drawSize.width / 2), offset.y - (drawSize.height / 2), drawSize.width, drawSize.height);
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

        float uLength = tex.getULength();
        float vLength = tex.getVLength();
        Tuple<Float, Float> frameUV = tex.getUVOffset(spriteOffsetTick);
        Vector3 starVec = new Vector3(x - size, y - size, 0);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(size * u * 2).addY(size * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ())
                    .color(1F, 1F, 1F, alpha)
                    .tex(frameUV.getA() + uLength * u, frameUV.getB() + vLength * v)
                    .endVertex();
        }
    }

    private void drawSearchMarkHalo(BatchPerkContext ctx, Rectangle.Float draw, float x, float y) {
        drawSearchHalo(ctx, (draw.width + draw.height) / 2F, x, y);
    }

    private void drawSearchHalo(BatchPerkContext ctx, float size, float x, float y) {
        BufferContext batch = ctx.getContext(searchContext);
        SpriteSheetResource searchMark = SpritesAS.SPR_PERK_SEARCH;

        searchMark.bindTexture();
        Vector3 starVec = new Vector3(x - size, y - size, 0);
        float uLength = searchMark.getUWidth();
        float vLength = searchMark.getVWidth();
        Tuple<Float, Float> frameUV = searchMark.getUVOffset();

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(size * u * 2).addY(size * v * 2);
            batch.pos(pos.getX(), pos.getY(), pos.getZ())
                    .color(0.8F, 0.1F, 0.1F, 1F)
                    .tex(frameUV.getA() + uLength * u, frameUV.getB() + vLength * v)
                    .endVertex();
        }
    }

    private void drawConnection(BufferBuilder vb, AllocationStatus status, Point.Float offset, Point.Float target, long effectTick) {
        Point.Float offsetSrc = this.sizeHandler.scalePointToGui(this, this.mousePosition, offset);
        Point.Float offsetDst = this.sizeHandler.scalePointToGui(this, this.mousePosition, target);
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
            vb.pos(pos.getX(), pos.getY(), pos.getZ())
                    .color(rR, rG, rB, rA)
                    .tex(u, v)
                    .endVertex();
        }
    }

    @Override
    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {
        super.mouseDragTick(mouseX, mouseY, mouseDiffX, mouseDiffY, mouseOffsetX, mouseOffsetY);
        if (this.mouseSealStack.isEmpty()) {
            moveMouse((float) mouseDiffX, (float) mouseDiffY);
        }
    }

    @Override
    protected void mouseDragStop(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY) {
        super.mouseDragStop(mouseX, mouseY, mouseDiffX, mouseDiffY);
        if (this.mouseSealStack.isEmpty()) {
            applyMovedMouseOffset();
        }
    }

    private void moveMouse(float changeX, float changeY) {
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
        TexturesAS.TEX_GUI_BACKGROUND_PERKS.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, guiLeft - 10, guiTop - 10, this.getGuiZLevel(), guiWidth + 20, guiHeight + 20)
                    .color(0.65F ,0.65F, 0.65F, 1F)
                    .draw();
        });
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
            for (Map.Entry<AbstractPerk, Rectangle.Float> rctPerk : this.thisFramePerks.entrySet()) {
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

        for (Map.Entry<AbstractPerk, Rectangle.Float> rctPerk : this.thisFramePerks.entrySet()) {
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

        for (Map.Entry<AbstractPerk, Rectangle.Float> rctPerk : this.thisFramePerks.entrySet()) {
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
                for (Rectangle.Float r : slotsSocketMenu.keySet()) {
                    if (r.contains(mouseX, mouseY) && !socketMenu.hasItem(mc.player, LogicalSide.CLIENT)) {
                        int slotId = slotsSocketMenu.get(r);
                        ItemStack potentialStack = mc.player.inventory.getStackInSlot(slotId);
                        if (!potentialStack.isEmpty() &&
                                !DynamicModifierHelper.getStaticModifiers(potentialStack).isEmpty()) {
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
        for (Map.Entry<AbstractPerk, Rectangle.Float> rctPerk : this.thisFramePerks.entrySet()) {
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
