/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.screen.base.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.element.FakeSlotElement;
import hellfirepvp.astralsorcery.client.screen.element.SearchInputElement;
import hellfirepvp.astralsorcery.client.screen.tome.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.screen.tome.perk.PerkTreeSizeHandler;
import hellfirepvp.astralsorcery.client.screen.tome.perk.render.PerkRenderType;
import hellfirepvp.astralsorcery.client.screen.tome.perk.render.PerkRenderer;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.client.util.tooltip.TooltipUtil;
import hellfirepvp.astralsorcery.common.constellation.property.AttunePlayerProperty;
import hellfirepvp.astralsorcery.common.item.PerkNullifierItem;
import hellfirepvp.astralsorcery.common.item.PerkSealItem;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.network.play.PktRequestPerkSealAction;
import hellfirepvp.astralsorcery.common.network.play.PktRequestRemovePerk;
import hellfirepvp.astralsorcery.common.network.play.PktRequestSocketPerkItem;
import hellfirepvp.astralsorcery.common.network.play.PktRequestUnlockPerk;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.perk.tree.ProgressPerk;
import hellfirepvp.astralsorcery.common.perk.tree.perk.socket.GemSocketPerk;
import hellfirepvp.astralsorcery.common.research.PlayerPerkData;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationStatus;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationType;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import hellfirepvp.astralsorcery.common.util.data.FloatRectangle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tick.TimeoutList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomePerkTreeScreen
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomePerkTreeScreen extends TomeScreen {

    public static final BookmarkProvider BOOKMARK = new BookmarkProvider("perks",
            30, () -> Minecraft.getInstance().setScreen(new TomePerkTreeScreen()), () -> ResearchManager.getClientProgress().isAttuned());

    private static Long lastTreeBufferVersion = null;
    private static BatchPerkContext treeBuffers = null;

    private final PerkTreeSizeHandler sizeHandler;

    private final ScalingPoint mousePoint;
    private ScalingPoint previousMousePoint;

    private FakeSlotElement sealSlot = null, nullifierSlot = null;
    private SearchInputElement searchInput = null;
    private final List<AbstractPerk<?>> searchMatches = Lists.newArrayList();

    //Tick/per-frame data
    private final Map<AbstractPerk<?>, FloatRectangle> thisFramePerks = new HashMap<>();
    private final TimeoutList<AbstractPerk<?>> unlockEffects = new TimeoutList<>();
    private final TimeoutList<AbstractPerk<?>> breakEffects = new TimeoutList<>();

    private AbstractPerk<?> unlockPrimed = null;
    private AbstractPerk<?> sealBreakPrimed = null;
    private int sealBreakDClickTimeout = 0;
    private int sealInventoryCount = 0, nullifierInventoryCount = 0;
    private boolean draggingSeal = false, draggingNullifier = false;

    private GemSocketPerk socketMenuPerk = null;
    private FloatRectangle thisFrameSocketMenu = null;
    private final Map<FloatRectangle, Integer> socketMenuSlots = new HashMap<>();

    private TomePerkTreeScreen() {
        super(BOOKMARK.getBookmarkIndex());

        this.sizeHandler = new PerkTreeSizeHandler();
        this.sizeHandler.setScaleSpeed(0.1F);
        this.sizeHandler.setMaxScale(1F);
        this.sizeHandler.setMinScale(0.2F);
        this.sizeHandler.updateSize();

        this.mousePoint = ScalingPoint.createPoint(0, 0, this.sizeHandler.getScalingFactor(), false);
        this.previousMousePoint = ScalingPoint.copy(this.mousePoint);
    }

    public static void releaseBuffers() {
        if (treeBuffers != null) {
            treeBuffers.free();
            treeBuffers = null;
            lastTreeBufferVersion = null;
        }
    }

    public static void ensureBuffers() {
        PerkTree.getInstance().getVersion(LogicalSide.CLIENT).ifPresent(version -> {
            if (lastTreeBufferVersion == null || !lastTreeBufferVersion.equals(version)) {
                releaseBuffers();

                BatchPerkContext.Builder builder = BatchPerkContext.builder();

                Set<PerkRenderType> neededTypes = new HashSet<>();
                neededTypes.add(PerkRenderType.Types.PERK_SEARCH);
                neededTypes.add(PerkRenderType.Types.PERK_SEAL);
                neededTypes.add(PerkRenderType.Types.PERK_NULLIFIER);
                PerkTree.getInstance().getPerkPoints(LogicalSide.CLIENT).forEach(point -> {
                    neededTypes.addAll(point.getFixedRenderTypes().get());
                });
                neededTypes.forEach(builder::addRenderType);

                treeBuffers = builder.build();
                lastTreeBufferVersion = version;
            }
        });
    }

    @Override
    protected void init() {
        super.init();

        this.clearWidgets();
        this.initBookmarks();

        this.sealSlot = new FakeSlotElement(this.screenLeft + 29, this.screenTop + 16, TexturesAS.SCREEN_ELEMENT_MENU_SLOT, () -> {
            return ItemsAS.PERK_SEAL.toStack(this.sealInventoryCount);
        });
        this.sealSlot.setOnClick(button -> {
            if (button == 0 && this.sealInventoryCount > 0) this.draggingSeal = true;
        });
        this.addRenderableWidget(this.sealSlot);
        this.nullifierSlot = new FakeSlotElement(this.screenLeft + 47, this.screenTop + 16, TexturesAS.SCREEN_ELEMENT_MENU_SLOT, () -> {
            return ItemsAS.PERK_NULLIFIER.toStack(this.nullifierInventoryCount);
        });
        this.nullifierSlot.setOnClick(button -> {
            if (button == 0 && this.nullifierInventoryCount > 0) this.draggingNullifier = true;
        });
        this.addRenderableWidget(this.nullifierSlot);
        this.searchInput = this.addRenderableWidget(new SearchInputElement(this.screenLeft + 300, this.screenTop + 16, this::onSearchInput));

        boolean shiftView = AttunePlayerProperty.getRootPerk(ResearchManager.getClientProgress().getAttunedConstellation(), LogicalSide.CLIENT)
                .map(root -> {
                    FloatPoint shiftedPos = this.sizeHandler.evRelativePos(root.getOffset());
                    this.moveMouse(Mth.floor(shiftedPos.x()), Mth.floor(shiftedPos.y()));
                    return true;
                }).orElse(false);

        if (!shiftView) {
            this.moveMouse(Mth.floor(this.sizeHandler.getScaledWidth() / 2), Mth.floor(this.sizeHandler.getScaledHeight() / 2));
        }

        this.applyMovedMouseOffset();
    }

    private void onSearchInput() {
        this.searchMatches.clear();

        String searchText = this.searchInput.getText().toLowerCase(Locale.ROOT);
        if (searchText.length() < SearchInputElement.getMinSearchLength()) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        PlayerProgress progress = ResearchManager.getClientProgress();

        for (PerkTreePoint<?> point : PerkTree.getInstance().getPerkPoints(LogicalSide.CLIENT)) {
            AbstractPerk<?> perk = point.getPerk();
            if (perk instanceof ProgressPerk<?> progressPerk &&
                    !progressPerk.canSee(progress)) {
                continue;
            }

            if (perk.getCategory().getDisplayName().getString().toLowerCase(Locale.ROOT).contains(searchText)) {
                this.searchMatches.add(perk);
            } else {
                for (MutableComponent cmp : perk.getTooltip(progress, player, LogicalSide.CLIENT)) {
                    if (cmp.getString().toLowerCase(Locale.ROOT).contains(searchText)) {
                        this.searchMatches.add(perk);
                        break;
                    }
                }
            }
        }

        MutableComponent sealedText = Component.translatable("perk.info.astralsorcery.sealed");
        if (sealedText.getString().toLowerCase(Locale.ROOT).contains(searchText)) {
            for (AbstractPerk<?> sealed : progress.getPerkData().getSealedPerks()) {
                if (!this.searchMatches.contains(sealed)) {
                    this.searchMatches.add(sealed);
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        Player player = Minecraft.getInstance().player;
        this.sealInventoryCount = 0;
        this.nullifierInventoryCount = 0;
        if (player != null) {
            this.sealInventoryCount = PerkSealItem.getPerkSealCount(player);
            this.nullifierInventoryCount = PerkNullifierItem.getPerkNullifierCount(player);
        }
        if (!this.isDragging()) this.releaseDragState();

        this.sealBreakDClickTimeout--;
        if (this.sealBreakDClickTimeout <= 0) {
            this.sealBreakDClickTimeout = 0;
            this.sealBreakPrimed = null;
        }

        this.unlockEffects.tick();
        this.breakEffects.tick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ensureBuffers();
        this.thisFramePerks.clear();
        this.renderTransparentBackground(guiGraphics);

        ScreenRectangle rect = this.getScreenRectangle();
        guiGraphics.enableScissor(rect.left() + 20, rect.top() + 20, rect.right() - 20, rect.bottom() - 20);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderingDrawUtil.drawTexturedRectColor(guiGraphics.pose(), TexturesAS.SCREEN_TOME_BACKGROUND_PERKS, ColorWrapper.opaque(0xAAAAAA), rect);
        RenderSystem.disableBlend();
        this.drawPerkTree(guiGraphics, partialTick);

        guiGraphics.disableScissor();

        RenderingDrawUtil.drawTexturedRect(guiGraphics.pose(), TexturesAS.SCREEN_TOME_FRAME_CUTOUT, this.getScreenRectangle());
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.drawInfoText(guiGraphics);
        this.drawGemSocketMenu(guiGraphics);
        this.drawHoverText(guiGraphics, mouseX, mouseY);

        if (this.draggingSeal && this.sealInventoryCount <= 0) this.draggingSeal = false;
        if (this.draggingSeal) {
            guiGraphics.pose().pushPose();
            guiGraphics.renderItem(ItemsAS.PERK_SEAL.toStack(), mouseX - 8, mouseY - 8);
            guiGraphics.pose().popPose();
        }
        if (this.draggingNullifier && this.nullifierInventoryCount <= 0) this.draggingNullifier = false;
        if (this.draggingNullifier) {
            guiGraphics.pose().pushPose();
            guiGraphics.renderItem(ItemsAS.PERK_NULLIFIER.toStack(), mouseX - 8, mouseY - 8);
            guiGraphics.pose().popPose();
        }
    }

    private void drawInfoText(GuiGraphics graphics) {
        Player player = Minecraft.getInstance().player;
        PlayerProgress prog = ResearchManager.getClientProgress();

        int availablePerkPoints;
        if (prog.isAttuned() && (availablePerkPoints = prog.getPerkData().getAvailablePerkPoints(player, LogicalSide.CLIENT)) > 0) {
            Component pointsTxt = Component.translatable("perk.info.astralsorcery.points", availablePerkPoints);
            graphics.drawString(Minecraft.getInstance().font, pointsTxt, this.screenLeft + 68, this.screenTop + 17, 0xCCCCCC);
        }
    }

    private void drawGemSocketMenu(GuiGraphics graphics) {
        this.thisFrameSocketMenu = null;
        this.socketMenuSlots.clear();
        if (this.socketMenuPerk == null) return;
        Player player = Minecraft.getInstance().player;
        PlayerProgress progress = ResearchManager.getClientProgress();

        Map<Integer, ItemStack> socketableItems = ItemUtil.findItemsInInventory(player, stack -> {
            return this.socketMenuPerk.canSocketItem(stack, player, progress, LogicalSide.CLIENT);
        });
        if (socketableItems.isEmpty()) {
            this.closeSocketMenu();
            return;
        }

        FloatPoint socketMenuOffset = this.sizeHandler.scalePointToGui(this, this.mousePoint, this.socketMenuPerk.getOffset());
        float offsetX = socketMenuOffset.x() + 8;
        float offsetY = socketMenuOffset.y() + 4;

        float scale = this.sizeHandler.getScalingFactor();
        float slotSize = 18F * scale;

        int slotMenuWidth = Math.min(5, socketableItems.size());
        int slotMenuHeight = ((socketableItems.size() / 5) + ((socketableItems.size() % 5) == 0 ? 0 : 1));

        this.thisFrameSocketMenu = new FloatRectangle(offsetX, offsetY, slotMenuWidth * slotSize + 8, slotMenuHeight * slotSize + 8);

        int rawMenuX = Mth.floor(this.thisFrameSocketMenu.x() - this.screenLeft);
        int rawMenuY = Mth.floor(this.thisFrameSocketMenu.y() - this.screenTop);
        if (!this.getScreenRectangle().containsPoint(rawMenuX, rawMenuY) ||
                !this.getScreenRectangle().containsPoint(rawMenuX + Mth.floor(this.thisFrameSocketMenu.width()), rawMenuY + Mth.floor(this.thisFrameSocketMenu.height()))) {
            this.closeSocketMenu();
            return;
        }

        graphics.pose().pushPose();
        graphics.pose().translate(offsetX, offsetY, 1);
        graphics.pose().scale(scale, scale, 1F);
        TooltipRenderUtil.renderTooltipBackground(graphics, 0, 0, slotMenuWidth * 18, slotMenuHeight * 18, 0,
                        0xEE000011, 0xEE000011, 0xEE000047, 0xEE000047);
        graphics.pose().popPose();

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 100);

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        TexturesAS.SCREEN_ELEMENT_MENU_SLOT_GEM_PERK.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            for (int index = 0; index < socketableItems.size(); index++) {
                float addedX = (index % 5) * slotSize;
                float addedY = (index / 5) * slotSize;
                RenderQuadUtil.rect(buf, graphics.pose(), offsetX + addedX, offsetY + addedY, slotSize, slotSize)
                        .draw();
            }
        });
        RenderSystem.disableBlend();

        int menuSlotIndex = 0;
        for (Integer invSlot : socketableItems.keySet()) {
            ItemStack invStack = socketableItems.get(invSlot);
            float addedX = (menuSlotIndex % 5) * slotSize;
            float addedY = (menuSlotIndex / 5) * slotSize;
            FloatRectangle slotRect = new FloatRectangle(offsetX + addedX, offsetY + addedY, slotSize, slotSize);
            Font fr = IClientItemExtensions.of(invStack).getFont(invStack, IClientItemExtensions.FontContext.ITEM_COUNT);
            if (fr == null) fr = Minecraft.getInstance().font;

            graphics.pose().pushPose();
            graphics.pose().translate(offsetX + addedX + 1, offsetY + addedY + 1, 0);
            graphics.pose().scale(scale, scale, 1F);
            graphics.renderItem(invStack, 0, 0);
            graphics.renderItemDecorations(fr, invStack, 0, 0);
            graphics.pose().popPose();

            this.socketMenuSlots.put(slotRect, invSlot);
            menuSlotIndex++;
        }

        graphics.pose().popPose();
    }

    private void drawHoverText(GuiGraphics graphics, int mouseX, int mouseY) {
        Player player = Minecraft.getInstance().player;

        if (!this.socketMenuSlots.isEmpty()) {
            for (FloatRectangle rct : this.socketMenuSlots.keySet()) {
                if (rct.contains(mouseX, mouseY)) {
                    ItemStack inSlot = player.getInventory().getItem(this.socketMenuSlots.get(rct));
                    if (!inSlot.isEmpty()) {
                        TooltipUtil.blueColor(() -> {
                            List<Component> toolTip = Screen.getTooltipFromItem(Minecraft.getInstance(), inSlot);
                            Font fr = IClientItemExtensions.of(inSlot).getFont(inSlot, IClientItemExtensions.FontContext.TOOLTIP);
                            if (fr == null) fr = Minecraft.getInstance().font;
                            graphics.renderComponentTooltip(fr, toolTip, mouseX, mouseY);
                        });
                    }
                    return;
                }
            }
        }

        if (this.sealInventoryCount > 0 && this.sealSlot.getRectangle().containsPoint(mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.addAll(Screen.getTooltipFromItem(Minecraft.getInstance(), ItemsAS.PERK_SEAL.toStack(1)));
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("perk.info.astralsorcery.sealed.usage"));
            tooltip.add(Component.translatable("perk.info.astralsorcery.sealed.usage.ctrl"));

            TooltipUtil.blueColor(() -> {
                graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
            });
            return;
        }

        if (this.nullifierInventoryCount > 0 && this.nullifierSlot.getRectangle().containsPoint(mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.addAll(Screen.getTooltipFromItem(Minecraft.getInstance(), ItemsAS.PERK_NULLIFIER.toStack(1)));
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("perk.info.astralsorcery.nullifier.usage"));
            tooltip.add(Component.translatable("perk.info.astralsorcery.nullifier.usage.ctrl"));

            TooltipUtil.blueColor(() -> {
                graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
            });
            return;
        }

        PlayerProgress progress = ResearchManager.getClientProgress();
        PlayerPerkData perkData = progress.getPerkData();
        for (AbstractPerk<?> perk : this.getMouseOverPerks(mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();

            perk.getTooltip(progress, player, LogicalSide.CLIENT).forEach(cmp -> {
                if (cmp.getStyle().getColor() == null) {
                    cmp.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
                }
                tooltip.add(cmp);
            });

            boolean removeable = this.draggingNullifier &&
                    (perkData.hasPerkAllocation(perk, PerkAllocationType.UNLOCKED) || perkData.hasPerkAllocation(perk, PerkAllocationType.UNLOCKED_NON_CONNECT)) &&
                    !perkData.isPerkSealed(perk) &&
                    perk.mayRemovePerk(progress, player);

            if (perkData.isPerkSealed(perk)) {
                tooltip.add(Component.translatable("perk.info.astralsorcery.sealed").withStyle(ChatFormatting.RED));
                tooltip.add(Component.translatable("perk.info.astralsorcery.sealed.break").withStyle(ChatFormatting.RED));
            } else if (removeable) {
                tooltip.add(Component.translatable("perk.info.astralsorcery.removable").withStyle(ChatFormatting.DARK_RED));
            } else if (perkData.hasPerkEffect(perk)) {
                tooltip.add(Component.translatable("perk.info.astralsorcery.active").withStyle(ChatFormatting.GREEN));
            } else if (perk.hasPlayerPerkAllowingUnlock(progress, player) && perk.mayUnlockPerk(progress, player)) {
                tooltip.add(Component.translatable("perk.info.astralsorcery.available").withStyle(ChatFormatting.BLUE));
            } else {
                tooltip.add(Component.translatable("perk.info.astralsorcery.inactive").withStyle(ChatFormatting.GRAY));
            }

            if (Minecraft.getInstance().options.advancedItemTooltips && !PerkCategory.DEFAULT.equals(perk.getCategory())) {
                tooltip.add(perk.getCategory().getDisplayName().withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }

            perk.getPerkSource().forEach(modDescriptor -> {
                tooltip.add(modDescriptor.withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC));
            });

            if (Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
                tooltip.add(Component.empty());
                tooltip.add(Component.literal(perk.getKey().toString()).withStyle(ChatFormatting.GRAY));
            }

            TooltipUtil.blueColor(() -> {
                graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
            });

            //Only render topmost/first
            return;
        }
    }

    private void drawPerkTree(GuiGraphics graphics, float pTicks) {
        Player player = Minecraft.getInstance().player;
        PlayerProgress progress = ResearchManager.getClientProgress();
        PlayerPerkData perkData = progress.getPerkData();

        List<DrawablePerkConnection> connections = this.collectPerkConnections();

        if (!connections.isEmpty()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            TexturesAS.SCREEN_ELEMENT_LINE_CONNECTION.bindTexture();
            RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                connections.forEach(conn -> {
                    AbstractPerk<?> perkFrom = conn.from();
                    AbstractPerk<?> perkTo = conn.to();

                    long tickOffset = ClientProxy.getClientTick() +
                            Mth.floor(perkFrom.getX()) + Mth.floor(perkFrom.getY()) +
                            Mth.floor(perkTo.getX()) + Mth.floor(perkTo.getY());
                    this.drawPerkConnection(buf, graphics, conn.status(), perkFrom.getOffset(), perkTo.getOffset(), tickOffset);
                });
            });
            RenderSystem.disableBlend();
        }

        List<PerkTreePoint<?>> immediatePerks = new ArrayList<>();
        for (PerkTreePoint<?> point : PerkTree.getInstance().getPerkPoints(LogicalSide.CLIENT)) {
            AbstractPerk<?> perk = point.getPerk();
            if (perk instanceof ProgressPerk<?> progressPerk && !progressPerk.canSee(progress)) {
                continue;
            }

            FloatRectangle drawnPerkRect = this.drawPerk(treeBuffers, graphics, point, perk.getPerkStatus(player, LogicalSide.CLIENT),
                    pTicks, perkData.isPerkSealed(perk));
            if (drawnPerkRect != null) {
                this.thisFramePerks.put(perk, drawnPerkRect);
            }
            if (point.getRenderer().get().needsImmediateRender(point)) {
                immediatePerks.add(point);
            }
        }
        treeBuffers.draw();

        graphics.pose().pushPose();
        immediatePerks.forEach(point -> {
            AbstractPerk<?> perk = point.getPerk();
            PerkRenderer renderer = point.getRenderer().get();
            PerkAllocationStatus status = perk.getPerkStatus(player, LogicalSide.CLIENT);
            FloatPoint offset = this.sizeHandler.scalePointToGui(this, this.mousePoint, point.getOffset());
            renderer.renderImmediate(graphics, point, status,
                    pTicks, offset.x(), offset.y(), this.sizeHandler.getScalingFactor());
        });
        graphics.pose().popPose();

        this.unlockEffects.forEach(perk -> this.drawPerkUnlockEffect(perk, graphics.pose(), this.unlockEffects.getTimeout(perk)));
        this.breakEffects.forEach(perk -> this.drawPerkSealBreakEffect(perk, graphics.pose(), this.breakEffects.getTimeout(perk), pTicks));
    }

    private void drawPerkUnlockEffect(AbstractPerk<?> perk, PoseStack pose, long tick) {
        SpriteSheet effect = SpritesAS.SPRITE_PERK_UNLOCK;
        int frame = (int) (effect.getFrameCount() - tick);
        if (frame < 0 || frame >= effect.getFrameCount()) {
            return;
        }
        FloatPoint offset = this.sizeHandler.scalePointToGui(this, this.mousePoint, perk.getOffset());
        UVFrame uv = effect.getUV(frame);

        float size = PerkTreePoint.PERK_RENDER_SIZE * 2F;
        FloatRectangle rct;
        if ((rct = this.thisFramePerks.get(perk)) != null) {
            size = rct.width();
        }
        float effectSize = size * 2.5F;

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        effect.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            RenderQuadUtil.rect(buf, pose, offset.x() - effectSize, offset.y() - effectSize, effectSize * 2, effectSize * 2)
                    .color(ColorWrapper.WHITE.copyWithAlpha(0xCC).getColor())
                    .tex(uv)
                    .draw();
        });
        RenderSystem.disableBlend();
    }

    private void drawPerkSealBreakEffect(AbstractPerk<?> perk, PoseStack pose, long tick, float pTicks) {
        PerkTreePoint<?> point = perk.getPerkTreePoint();
        PerkRenderer renderer = point.getRenderer().get();
        SpriteSheet effect = SpritesAS.SPRITE_PERK_SEAL_BREAK;
        int frame = (int) (effect.getFrameCount() - tick);
        if (frame < 0 || frame >= effect.getFrameCount()) {
            return;
        }
        FloatPoint offset = this.sizeHandler.scalePointToGui(this, this.mousePoint, perk.getOffset());
        UVFrame uv = effect.getUV(frame);
        float sealFade = 1.0F - (((float) frame) + pTicks) / ((float) effect.getFrameCount());

        float size = PerkTreePoint.PERK_RENDER_SIZE * 2F;
        FloatRectangle rct;
        if ((rct = this.thisFramePerks.get(perk)) != null) {
            size = rct.width();
        }
        float effectSize = renderer.getSealRenderSize(point, size);

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        SpritesAS.SPRITE_PERK_SEAL.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            this.drawPerkSeal(buf, pose, effectSize, offset, renderer.getEffectTick(point), sealFade);
        });

        effect.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            RenderQuadUtil.rect(buf, pose, offset.x() - effectSize, offset.y() - effectSize, effectSize * 2, effectSize * 2)
                    .tex(uv)
                    .draw();
        });
        RenderSystem.disableBlend();
    }

    @Nullable
    private FloatRectangle drawPerk(BatchPerkContext ctx, GuiGraphics graphics, PerkTreePoint<?> point,
                                    PerkAllocationStatus status, float pTicks, boolean renderSeal) {
        PerkRenderer renderer = point.getRenderer().get();
        FloatPoint offset = this.sizeHandler.scalePointToGui(this, this.mousePoint, point.getOffset());
        float scale = this.sizeHandler.getScalingFactor();
        long effectTick = renderer.getEffectTick(point);

        FloatRectangle drawnRct = renderer.renderPerk(ctx, graphics, point, status, pTicks, offset.x(), offset.y(), scale);
        if (drawnRct == null) {
            return null;
        }
        float drawnEffectSize = (drawnRct.width() + drawnRct.height()) / 2F;
        if (renderSeal) {
            this.drawPerkSeal(ctx.getBuffer(PerkRenderType.Types.PERK_SEAL), graphics.pose(),
                    renderer.getSealRenderSize(point, drawnEffectSize), offset, effectTick, 1F);
        }

        if (this.searchMatches.contains(point.getPerk())) {
            this.drawSearchHighlight(ctx.getBuffer(PerkRenderType.Types.PERK_SEARCH), graphics.pose(), drawnEffectSize, offset, effectTick);
        }

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            PlayerProgress progress = ResearchManager.getClientProgress();
            PlayerPerkData perkData = progress.getPerkData();
            AbstractPerk<?> perk = point.getPerk();
            if (this.draggingNullifier &&
                    (perkData.hasPerkAllocation(perk, PerkAllocationType.UNLOCKED) ||
                            perkData.hasPerkAllocation(perk, PerkAllocationType.UNLOCKED_NON_CONNECT)) &&
                    !perkData.isPerkSealed(perk) &&
                    perk.mayRemovePerk(progress, player)) {
                this.drawPerkRemovalHighlight(ctx.getBuffer(PerkRenderType.Types.PERK_NULLIFIER), graphics.pose(), drawnEffectSize * 1.3F, offset, effectTick);
            }
        }

        //TODO radius converter effect

        return new FloatRectangle(offset.x() - (drawnRct.width() / 2F), offset.y() - (drawnRct.height() / 2F), drawnRct.width(), drawnRct.height());
    }

    private void drawPerkSeal(VertexConsumer buf, PoseStack pose, float effectSize, FloatPoint offset, long effectTick, float alpha) {
        UVFrame uv = SpritesAS.SPRITE_PERK_SEAL.getUV(effectTick);
        Vector3 vec = new Vector3(offset.x() - effectSize, offset.y() - effectSize, 0);

        Matrix4f matr = pose.last().pose();
        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = vec.copy().addX(effectSize * u * 2).addY(effectSize * v * 2);
            pos.drawPos(matr, buf)
                    .setColor(1F, 1F, 1F, alpha)
                    .setUv(uv.u() + uv.uWidth() * u, uv.v() + uv.vHeight() * v);
        }
    }

    private void drawSearchHighlight(VertexConsumer buf, PoseStack pose, float effectSize, FloatPoint offset, long effectTick) {
        UVFrame uv = SpritesAS.SPRITE_PERK_SEARCH.getUV(effectTick);
        Vector3 vec = new Vector3(offset.x() - effectSize, offset.y() - effectSize, 0);

        Matrix4f matr = pose.last().pose();
        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = vec.copy().addX(effectSize * u * 2).addY(effectSize * v * 2);
            pos.drawPos(matr, buf)
                    .setColor(0.8F, 0.1F, 0.1F, 1F)
                    .setUv(uv.u() + uv.uWidth() * u, uv.v() + uv.vHeight() * v);
        }
    }

    private void drawPerkRemovalHighlight(VertexConsumer buf, PoseStack pose, float effectSize, FloatPoint offset, long effectTick) {
        UVFrame uv = SpritesAS.SPRITE_PERK_SEAL.getUV(effectTick);
        Vector3 vec = new Vector3(offset.x() - effectSize, offset.y() - effectSize, 0);

        Matrix4f matr = pose.last().pose();
        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = vec.copy().addX(effectSize * u * 2).addY(effectSize * v * 2);
            pos.drawPos(matr, buf)
                    .setColor(0.4F, 0.1F, 0.1F, 1F)
                    .setUv(uv.u() + uv.uWidth() * u, uv.v() + uv.vHeight() * v);
        }
    }

    private void drawPerkConnection(VertexConsumer buf, GuiGraphics graphics, PerkAllocationStatus status,
                                    FloatPoint from, FloatPoint to, long effectTick) {
        FloatPoint offsetFrom = this.sizeHandler.scalePointToGui(this, this.mousePoint, from);
        FloatPoint offsetTo = this.sizeHandler.scalePointToGui(this, this.mousePoint, to);
        ColorWrapper overlay = status.getPerkTreeConnectionColor();

        double effectPart = (Math.sin(Math.toRadians(((effectTick) * 8) % 360D)) + 1D) / 4D;
        float br = 0.1F + 0.4F * (2F - ((float) effectPart));
        float rR = (overlay.getRed()   / 255F) * br;
        float rG = (overlay.getGreen() / 255F) * br;
        float rB = (overlay.getBlue()  / 255F) * br;
        float rA = (overlay.getAlpha() / 255F) * br;

        Vector3 fromStar = new Vector3(offsetFrom.x(), offsetFrom.y(), 0);
        Vector3 toStar   = new Vector3(offsetTo.x(), offsetTo.y(), 0);

        double width = 4.0D * this.sizeHandler.getScalingFactor();

        Vector3 dir = toStar.copy().subtract(fromStar);
        Vector3 degLot = dir.copy().crossProduct(new Vector3(0, 0, 1)).normalize().multiply(width);

        Vector3 vec00 = fromStar.copy().add(degLot);
        Vector3 vecV = degLot.copy().multiply(-2);

        Matrix4f offset = graphics.pose().last().pose();
        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = vec00.copy().add(dir.copy().multiply(u)).add(vecV.copy().multiply(v));
            pos.drawPos(offset, buf).setColor(rR, rG, rB, rA).setUv(u, v);
        }
    }

    private List<DrawablePerkConnection> collectPerkConnections() {
        Player player = Minecraft.getInstance().player;
        PlayerProgress progress = ResearchManager.getClientProgress();
        PlayerPerkData perkData = progress.getPerkData();
        List<DrawablePerkConnection> connections = new ArrayList<>();
        PerkTree.getInstance().getConnections().forEach(tpl -> {
            AbstractPerk<?> perkFrom = tpl.getA();
            AbstractPerk<?> perkTo = tpl.getB();
            if ((perkFrom instanceof ProgressPerk<?> progressFromPerk && !progressFromPerk.canSee(progress)) ||
                    (perkTo instanceof ProgressPerk<?> progressToPerk && !progressToPerk.canSee(progress))) {
                return;
            }

            int allocations = 0;
            if (perkData.hasPerkAllocationGrantingConnections(perkFrom)) allocations++;
            if (perkData.hasPerkAllocationGrantingConnections(perkTo)) allocations++;

            PerkAllocationStatus status;
            if (allocations == 2) {
                status = PerkAllocationStatus.ALLOCATED;
            } else if (allocations == 1 && perkData.hasFreeAllocationPoint(player, LogicalSide.CLIENT)) {
                status = PerkAllocationStatus.UNLOCKABLE;
            } else {
                status = PerkAllocationStatus.UNALLOCATED;
            }
            connections.add(new DrawablePerkConnection(status, perkFrom, perkTo));
        });
        return connections;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {
        super.mouseDragTick(mouseX, mouseY, mouseDiffX, mouseDiffY, mouseOffsetX, mouseOffsetY);
        if (!this.draggingSeal && !this.draggingNullifier) {
            this.moveMouse((float) mouseDiffX, (float) mouseDiffY);
        }
    }

    @Override
    protected void mouseDragStop(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY) {
        super.mouseDragStop(mouseX, mouseY, mouseDiffX, mouseDiffY);
        if (!this.draggingSeal && !this.draggingNullifier) {
            this.applyMovedMouseOffset();
        }
    }

    private void moveMouse(float changeX, float changeY) {
        this.mousePoint.updateScaledPos(
                this.sizeHandler.clampX(this.previousMousePoint.getScaledPosX() + changeX),
                this.sizeHandler.clampY(this.previousMousePoint.getScaledPosY() + changeY),
                this.sizeHandler.getScalingFactor());
    }

    private void applyMovedMouseOffset() {
        this.previousMousePoint = ScalingPoint.createPoint(
                this.mousePoint.getScaledPosX(),
                this.mousePoint.getScaledPosY(),
                this.sizeHandler.getScalingFactor(),
                true);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Player player = Minecraft.getInstance().player;

        PlayerProgress progress = ResearchManager.getClientProgress();
        PlayerPerkData perkData = progress.getPerkData();
        if (this.draggingSeal) {
            this.releaseDragState();
            if (button == 0) this.stopDragging(mouseX, mouseY);

            for (AbstractPerk<?> perk : this.getMouseOverPerks(mouseX, mouseY)) {
                if (perkData.hasPerkEffect(perk) &&
                        !perkData.isPerkSealed(perk) &&
                        PerkSealItem.consumePerkSeal(player, true)) {
                    PacketDistributor.sendToServer(PktRequestPerkSealAction.sealPerk(perk.getKey()));
                    return true;
                }
            }
            return false;
        }
        if (this.draggingNullifier) {
            this.releaseDragState();
            if (button == 0) this.stopDragging(mouseX, mouseY);

            for (AbstractPerk<?> perk : this.getMouseOverPerks(mouseX, mouseY)) {
                if ((perkData.hasPerkAllocation(perk, PerkAllocationType.UNLOCKED) || perkData.hasPerkAllocation(perk, PerkAllocationType.UNLOCKED_NON_CONNECT)) &&
                        !perkData.isPerkSealed(perk) &&
                        perk.mayRemovePerk(progress, player)) {
                    PacketDistributor.sendToServer(PktRequestRemovePerk.remove(perk.getKey()));
                    return true;
                }
            }
            return false;
        }

        if (super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }

        if (this.unlockPrimed != null) {
            for (AbstractPerk<?> perk : this.getMouseOverPerks(mouseX, mouseY)) {
                if (perk.equals(this.unlockPrimed)) {
                    if (!perkData.hasPerkAllocation(perk) &&
                            perk.mayUnlockPerk(progress, player) &&
                            perk.hasPlayerPerkAllowingUnlock(progress, player)) {
                        PacketDistributor.sendToServer(PktRequestUnlockPerk.unlock(perk.getKey()));
                        this.unlockPrimed = null;
                        return true;
                    }
                }
            }
            this.unlockPrimed = null;
            return false;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.releaseDragState()) {
            return true;
        }

        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (this.socketMenuPerk != null && this.thisFrameSocketMenu != null && !this.thisFrameSocketMenu.contains(mouseX, mouseY)) {
            this.closeSocketMenu();
        }

        Player player = Minecraft.getInstance().player;
        PlayerProgress progress = ResearchManager.getClientProgress();
        PlayerPerkData perkData = progress.getPerkData();

        if (button == 0) {
            if (this.socketMenuPerk != null && !this.socketMenuPerk.hasGemStack(progress)) {
                for (FloatRectangle rect : this.socketMenuSlots.keySet()) {
                    if (rect.contains(mouseX, mouseY)) {
                        int slotId = this.socketMenuSlots.get(rect);
                        if (this.tryInsertGem(slotId, this.socketMenuPerk)) {
                            return true;
                        }
                    }
                }
            }
        }

        for (AbstractPerk<?> perk : this.getMouseOverPerks(mouseX, mouseY)) {
            if (button == 1) {
                if (perk instanceof GemSocketPerk gemSocketPerk && perkData.hasPerkEffect(perk)) {
                    if (gemSocketPerk.hasGemStack(progress)) {
                        PacketDistributor.sendToServer(PktRequestSocketPerkItem.dropItem(gemSocketPerk));
                        ClientProxy.scheduleEffectTask(10, () -> {
                            if (Minecraft.getInstance().screen == this) {
                                this.onSearchInput();
                            }
                        });
                        PlayableSoundInstance.of(SoundEvents.GLASS_HIT, SoundSource.MASTER)
                                .forUI()
                                .volume(0.35F)
                                .pitch(2F)
                                .play();
                    } else {
                        this.socketMenuPerk = gemSocketPerk;
                    }
                    return true;
                }
            } else if (button == 0) {
                if (!perkData.hasPerkAllocation(perk) &&
                        perk.mayUnlockPerk(progress, player) &&
                        perk.hasPlayerPerkAllowingUnlock(progress, player)) {
                    this.unlockPrimed = perk;
                } else if (this.sealBreakPrimed != null && this.sealBreakDClickTimeout > 0) {
                    PacketDistributor.sendToServer(PktRequestPerkSealAction.unsealPerk(this.sealBreakPrimed.getKey()));
                } else if (perkData.isPerkSealed(perk)) {
                    this.sealBreakPrimed = perk;
                    this.sealBreakDClickTimeout = 4;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY < 0) {
            this.sizeHandler.handleZoomOut();
            this.rescaleMouse();
            return true;
        }
        if (scrollY > 0)  {
            this.sizeHandler.handleZoomIn();
            this.rescaleMouse();
            return true;
        }
        return false;
    }

    private boolean releaseDragState() {
        if (this.draggingSeal || this.draggingNullifier) {
            if (Screen.hasControlDown()) {
                return true;
            }
            this.draggingSeal = false;
            this.draggingNullifier = false;
        }
        return false;
    }

    private void rescaleMouse() {
        this.mousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.previousMousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.moveMouse(0, 0);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.unlockPrimed = null;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        int iMouseX = Mth.floor(mouseX);
        int iMouseY = Mth.floor(mouseY);

        if (this.searchInput.getRectangle().containsPoint(iMouseX, iMouseY)) {
            this.searchInput.setText("");
            return false;
        }

        if (this.socketMenuPerk != null && this.thisFrameSocketMenu != null) {
            this.closeSocketMenu();
            return false;
        }

        for (AbstractPerk<?> perk : this.getMouseOverPerks(mouseX, mouseY)) {
            if (perk instanceof GemSocketPerk) {
                return false;
            }
        }
        return true;
    }

    private boolean tryInsertGem(int slotId, GemSocketPerk perk) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        ItemStack stack = player.getInventory().getItem(slotId);
        if (!perk.canSocketItem(stack, player, ResearchManager.getClientProgress(), LogicalSide.CLIENT)) return false;

        this.closeSocketMenu();
        PacketDistributor.sendToServer(PktRequestSocketPerkItem.insertItem(perk, slotId));
        PlayableSoundInstance.of(SoundEvents.GLASS_HIT, SoundSource.MASTER)
                .forUI()
                .volume(0.35F)
                .pitch(2F)
                .play();
        return true;
    }

    private void closeSocketMenu() {
        this.socketMenuPerk = null;
        this.thisFrameSocketMenu = null;
        this.socketMenuSlots.clear();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchInput.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.searchInput.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    private List<AbstractPerk<?>> getMouseOverPerks(double mouseX, double mouseY) {
        if (!this.getScreenRectangle().containsPoint(Mth.floor(mouseX), Mth.floor(mouseY))) return List.of();

        List<AbstractPerk<?>> perks = new ArrayList<>();
        for (Map.Entry<AbstractPerk<?>, FloatRectangle> rctPerk : this.thisFramePerks.entrySet()) {
            if (rctPerk.getValue().contains(mouseX, mouseY)) {
                perks.add(rctPerk.getKey());
            }
        }
        return perks;
    }

    public void playUnlockAnimation(AbstractPerk<?> perk) {
        this.unlockEffects.setOrAddTimeout(SpritesAS.SPRITE_PERK_UNLOCK.getFrameCount(), perk);
        PlayableSoundInstance.of(SoundsAS.SCREEN_PERK_UNLOCK)
                .forUI()
                .volume(0.3F)
                .play();
    }

    public void playSealBreakAnimation(AbstractPerk<?> perk) {
        this.onSearchInput();
        this.breakEffects.setOrAddTimeout(SpritesAS.SPRITE_PERK_SEAL_BREAK.getFrameCount(), perk);
        PlayableSoundInstance.of(SoundsAS.SCREEN_PERK_UNSEAL)
                .forUI()
                .volume(0.35F)
                .play();
    }

    public void playSealApplyAnimation(AbstractPerk<?> perk) {
        this.onSearchInput();
        PlayableSoundInstance.of(SoundsAS.SCREEN_PERK_SEAL)
                .forUI()
                .volume(0.15F)
                .play();
    }

    private record DrawablePerkConnection(PerkAllocationStatus status, AbstractPerk<?> from, AbstractPerk<?> to) {
    }
}
