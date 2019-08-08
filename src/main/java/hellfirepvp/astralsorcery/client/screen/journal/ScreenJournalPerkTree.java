/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.screen.helper.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.helper.ScreenRenderBoundingBox;
import hellfirepvp.astralsorcery.client.screen.helper.SizeHandler;
import hellfirepvp.astralsorcery.client.screen.journal.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkTreeSizeHandler;
import hellfirepvp.astralsorcery.client.util.ScreenTextEntry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktPerkGemModification;
import hellfirepvp.astralsorcery.common.network.packet.client.PktRequestPerkSealAction;
import hellfirepvp.astralsorcery.common.network.packet.client.PktUnlockPerk;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.ProgressGatedPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
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

    //private GemSlotPerk socketMenu = null;
    private Rectangle rSocketMenu = null;
    private Map<Rectangle, Integer> slotsSocketMenu = Maps.newHashMap();
    private Rectangle rStatStar = null;

    private ItemStack mouseSealStack = ItemStack.EMPTY;
    private ItemStack foundSeals = ItemStack.EMPTY;

    public ScreenJournalPerkTree() {
        super(new TranslationTextComponent("gui.journal.perks"), 30);
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

        //TODO add sprites
        //searchContext = drawBuffer.addContext(textureSearchMark, BatchPerkContext.PRIORITY_OVERLAY);
        //sealContext = drawBuffer.addContext(SpriteLibrary.spritePerkSeal, BatchPerkContext.PRIORITY_FOREGROUND);

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


    private Point.Double shift2DOffset(double x, double y) {
        double scaledLeft = this.mousePosition.getScaledPosX() - sizeHandler.widthToBorder;
        double scaledTop =  this.mousePosition.getScaledPosY() - sizeHandler.heightToBorder;
        double xAdd = x - scaledLeft;
        double yAdd = y - scaledTop;
        double offsetX = guiOffsetX + xAdd;
        double offsetY = guiOffsetY + yAdd;
        return new Point.Double(offsetX, offsetY);
    }

    private void handleMouseMovement(int mouseX, int mouseY) {
        int guiMouseX = mouseX - guiLeft;
        int guiMouseY = mouseY - guiTop;

        if(this.mouseSealStack.isEmpty() &&
                Minecraft.getInstance().mouseHelper.isLeftDown() &&
                guiBox.isInBox(guiMouseX, guiMouseY)) {
            if(mouseDragging) {
                moveMouse(-(guiMouseX - mouseBufferX), -(guiMouseY - mouseBufferY));
            } else {
                mouseBufferX = guiMouseX;
                mouseBufferY = guiMouseY;
                mouseDragging = true;
            }
        } else {
            applyMovedMouseOffset();
            mouseDragging = false;
        }
    }

    private void moveMouse(int changeX, int changeY) {
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
            String catStr = perk.getCategory().getLocalizedName();
            if (catStr != null && catStr.toLowerCase().contains(matchText)) {
                this.searchMatches.add(perk);
            } else {
                for (String tooltip : perk.getLocalizedTooltip()) {
                    if (tooltip.toLowerCase().contains(matchText)) {
                        this.searchMatches.add(perk);
                        break;
                    }
                }
            }
        }
        if (I18n.format("perk.info.sealed").toLowerCase().contains(matchText)) {
            PlayerProgress prog = ResearchHelper.getClientProgress();
            for (AbstractPerk sealed : prog.getSealedPerks()) {
                if (!this.searchMatches.contains(sealed)) {
                    this.searchMatches.add(sealed);
                }
            }
        }
    }

    private void closeSocketMenu() {
        //this.socketMenu = null;
        this.rSocketMenu = null;
        this.slotsSocketMenu.clear();
    }

    /* TODO actually implement fully tomorrow.
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
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        this.unlockPrimed = null;

        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    protected boolean handleRightClickClose(double mouseX, double mouseY) {
        if (super.handleRightClickClose(mouseX, mouseY)) {
            return true;
        }

        if (rectSearchTextEntry.contains(mouseX - guiLeft, mouseY - guiTop)) {
            searchTextEntry.setText("");
            return true;
        }
        if (socketMenu != null &&
                rSocketMenu != null &&
                !rSocketMenu.contains(mouseX, mouseY)) {
            closeSocketMenu();
            return true;
        }

        for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
            if (rctPerk.getValue().contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                AbstractPerk perk = rctPerk.getKey();
                if (perk instanceof GemSlotPerk) {
                    return true;
                }
            }
        }
        return false;
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
                    if (r.contains(mouseX, mouseY) && !socketMenu.hasItem(mc.player, Dist.CLIENT)) {
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
                    this.mouseSealStack = new ItemStack(ItemsAS.perkSeal);
                }
                return true;
            }

            if (rStatStar.contains(mouseX, mouseY)) {
                this.expectReinit = true;
                mc.displayGuiScreen(new GuiJournalOverlayPerkStats(this));
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
                    mc.player.sendMessage(new TranslationTextComponent("misc.ctrlcopy.copied", perkKey));
                    break;
                }
                if (mouseButton == 1) {
                    if (prog.hasPerkEffect(perk) && perk instanceof GemSlotPerk) {
                        if (((GemSlotPerk) perk).hasItem(mc.player, Dist.CLIENT)) {
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

    */

    @Override
    public boolean charTyped(char charCode, int keyModifiers) {
        if (super.charTyped(charCode, keyModifiers)) {
            return true;
        }

        if (charCode != 256) { //InputMappings escape key
            this.searchTextEntry.textboxKeyTyped(charCode, keyModifiers);
            return true;
        }
        return false;
    }

    public void playUnlockAnimation(AbstractPerk perk) {
        this.unlockEffects.put(perk, ClientScheduler.getClientTick());
    }

    public void playSealBreakAnimation(AbstractPerk perk) {
        this.updateSearchHighlight();
        this.breakEffects.put(perk, ClientScheduler.getClientTick());
    }
}
