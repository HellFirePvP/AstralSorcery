/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.mappings.ClientPerkTextureMapping;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkMap;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktUnlockPerk;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalPerk
 * Created by HellFirePvP
 * Date: 23.11.2016 / 14:50
 */
public class GuiJournalPerkMap extends GuiScreenJournal {

    private static final BindableResource textureResBack = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiresbg");

    private static final float mouseHoverMerge = 0.03F;
    private float mouseHoverPerc = 0F;

    private static final double widthHeight = 70;
    private Map<ConstellationPerks, Long> unlockPlayMap = new HashMap<>();

    private ConstellationPerkMap mapToDisplay = null;
    private IMajorConstellation attunedConstellation = null;

    private Map<Rectangle, ConstellationPerks> thisFramePerks = new HashMap<>();

    public GuiJournalPerkMap() {
        super(2);

        IMajorConstellation attuned = ResearchManager.clientProgress.getAttunedConstellation();
        if (attuned != null) {
            ConstellationPerkMap map = attuned.getPerkMap();
            if (map != null) {
                this.mapToDisplay = map;
                this.attunedConstellation = attuned;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.thisFramePerks.clear();

        Rectangle rectHover = new Rectangle(guiLeft + 10, guiTop + 5, guiWidth - 20, guiHeight - 10);
        if(rectHover.contains(mouseX, mouseY)) {
            mouseHoverPerc = Math.min(1F, mouseHoverPerc + mouseHoverMerge);
        } else {
            mouseHoverPerc = Math.max(0F, mouseHoverPerc - mouseHoverMerge);
        }

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        TextureHelper.refreshTextureBindState();
        drawDefault(textureResShell);
        drawBaseBackground(zLevel - 50);

        if(mapToDisplay != null) {
            drawPerkMap(mapToDisplay, new Point(mouseX, mouseY));
        }

        if(attunedConstellation != null) {
            drawOverlayTexture(attunedConstellation);
        }

        drawUnlockEffects();

        TextureHelper.refreshTextureBindState();

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawUnlockEffects() {
        double whStar = 80;
        double whBetweenStars = widthHeight / 7D;
        double offsetX = guiLeft + ((guiWidth ) / 2D) - widthHeight;
        double offsetY = guiTop  + ((guiHeight) / 2D) - widthHeight;
        Vector3 offset = new Vector3(offsetX, offsetY, zLevel);

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        Iterator<ConstellationPerks> iterator = unlockPlayMap.keySet().iterator();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        while (iterator.hasNext()) {
            ConstellationPerks perk = iterator.next();
            ConstellationPerkMap.Position position = mapToDisplay.getPosition(perk);
            if (position == null) continue;
            Long startTick = unlockPlayMap.get(perk);
            int count = (int) (ClientScheduler.getClientTick() - startTick);
            SpriteSheetResource sprite = SpriteLibrary.spritePerkActivate;
            if (count >= sprite.getFrameCount()) {
                iterator.remove();
                continue;
            }

            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            sprite.getResource().bind();
            int starX = position.x;
            int starY = position.y;

            GL11.glColor4f(1F, 1F, 1F, 0.2F + 0.8F * mouseHoverPerc);

            Vector3 starVec = offset.clone().addX(starX * whBetweenStars - whStar).addY(starY * whBetweenStars - whStar);

            double uLength = sprite.getULength();
            double vLength = sprite.getVLength();
            Tuple<Double, Double> frameUV = sprite.getUVOffset(count);

            for (int i = 0; i < 4; i++) {
                int u = ((i + 1) & 2) >> 1;
                int v = ((i + 2) & 2) >> 1;

                Vector3 pos = starVec.clone().addX(whStar * u * 2).addY(whStar * v * 2);
                vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(frameUV.key + uLength * u, frameUV.value + vLength * v).endVertex();
            }
            tes.draw();
        }
        Blending.DEFAULT.apply();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        TextureHelper.refreshTextureBindState();
    }

    private void drawPerkMap(ConstellationPerkMap mapToDisplay, Point mouse) {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        double whStar = 12;
        double whBetweenStars = widthHeight / 7D;

        double offsetX = guiLeft + ((guiWidth ) / 2D) - widthHeight;
        double offsetY = guiTop  + ((guiHeight) / 2D) - widthHeight;

        drawConnections(mapToDisplay, offsetX, offsetY, whBetweenStars, 3D);

        Map<Rectangle, ConstellationPerks> rects = drawStars(mapToDisplay.getPerks(), offsetX, offsetY, whStar, whBetweenStars);
        this.thisFramePerks.putAll(rects);
        for (Rectangle r : rects.keySet()) {
            if(r.contains(mouse)) {
                List<String> toolTip = new LinkedList<>();
                ConstellationPerk perk = rects.get(r).getSingleInstance();
                perk.addLocalizedDescription(toolTip);
                toolTip.add("");
                PlayerProgress prog = ResearchManager.clientProgress;
                String unlockStr;
                if(prog.hasPerkUnlocked(perk)) {
                    int unlock = prog.getAppliedPerks().get(perk);
                    if(unlock > 0) {
                        toolTip.add(I18n.format("perk.info.unlocked.level", unlock));
                    } else {
                        toolTip.add(I18n.format("perk.info.unlocked.free"));
                    }
                    if (prog.isPerkActive(perk)) {
                        unlockStr = "perk.info.active";
                    } else {
                        unlockStr = "perk.info.inactive";
                    }
                } else if(mayUnlockClient(prog, perk)) {
                    toolTip.add(I18n.format("perk.info.unlock.level", prog.getNextFreeLevel()));
                    unlockStr = "perk.info.available";
                } else {
                    unlockStr = "perk.info.locked";
                }
                toolTip.add(I18n.format(unlockStr));
                RenderingUtils.renderBlueTooltip(mouse.x, mouse.y, toolTip, Minecraft.getMinecraft().fontRenderer);
                GlStateManager.color(1F, 1F, 1F, 1F);
                GL11.glColor4f(1F, 1F, 1F, 1F);
            }
        }
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    private boolean mayUnlockClient(PlayerProgress prog, ConstellationPerk perk) {
        if(!prog.hasFreeAlignmentLevel()) return false;

        ConstellationPerks enumPerk = ConstellationPerks.getById(perk.getId());
        Map<ConstellationPerk, Integer> appliedPerks = prog.getAppliedPerks();
        if(!appliedPerks.containsKey(perk)) {
            boolean canUnlock = prog.hasFreeAlignmentLevel();
            for (ConstellationPerkMap.Dependency d : mapToDisplay.getPerkDependencies()) {
                if(d.to.equals(enumPerk)) {
                    if(!appliedPerks.containsKey(d.from.getSingleInstance())) {
                        canUnlock = false;
                    }
                }
            }
            return canUnlock;
        }
        return false;
    }

    public void playUnlockAnimation(ConstellationPerks perk) {
        ConstellationPerkMap.Position pos = mapToDisplay.getPosition(perk);
        if(pos != null) {
            unlockPlayMap.put(perk, ClientScheduler.getClientTick());
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(I18n.format("perk.info.unlock", I18n.format(perk.getSingleInstance().getUnlocalizedName()))));
        }
    }

    private void drawConnections(ConstellationPerkMap mapToDisplay, double offsetX, double offsetY, double whBetweenStars, double linebreadth) {
        PlayerProgress prog = ResearchManager.clientProgress;
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        Vector3 offset = new Vector3(offsetX, offsetY, zLevel);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        for (ConstellationPerkMap.Dependency dep : mapToDisplay.getPerkDependencies()) {
            BindableResource tex;
            Color overlay = null;
            if(prog.hasPerkUnlocked(dep.to)) {
                tex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks");
                overlay = new Color(0x00EEEE00);
            } else {
                tex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks");
                overlay = new Color(0xBBBBFF);
            }

            ConstellationPerkMap.Position from = mapToDisplay.getPosition(dep.from);
            ConstellationPerkMap.Position to = mapToDisplay.getPosition(dep.to);
            if(from != null && to != null) {

                long count = ClientScheduler.getClientTick() + from.x + from.y + to.x + to.y;
                double part = (Math.sin(Math.toRadians(((count) * 8) % 360D)) + 1D) / 4D;

                float br = 0.5F + 0.2F * (2F - ((float) part));
                float rR = br;
                float rG = br;
                float rB = br;
                float rA = br;
                if(overlay != null) {
                    rR *= (overlay.getRed()   / 255F);
                    rG *= (overlay.getGreen() / 255F);
                    rB *= (overlay.getBlue()  / 255F);
                    rA *= (overlay.getAlpha() / 255F);
                }
                float aPart = 0.15F * rA;
                GL11.glColor4f(rR, rG, rB, aPart + (rA * 0.85F * mouseHoverPerc));

                tex.bind();
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                Vector3 fromStar = new Vector3(offset.getX() + from.x * whBetweenStars, offset.getY() + from.y * whBetweenStars, offset.getZ());
                Vector3 toStar = new Vector3(offset.getX() + to.x * whBetweenStars, offset.getY() + to.y * whBetweenStars, offset.getZ());

                Vector3 dir = toStar.clone().subtract(fromStar);
                Vector3 degLot = dir.clone().crossProduct(new Vector3(0, 0, 1)).normalize().multiply(linebreadth);//.multiply(j == 0 ? 1 : -1);

                Vector3 vec00 = fromStar.clone().add(degLot);
                Vector3 vecV = degLot.clone().multiply(-2);

                for (int i = 0; i < 4; i++) {
                    int u = ((i + 1) & 2) >> 1;
                    int v = ((i + 2) & 2) >> 1;

                    Vector3 pos = vec00.clone().add(dir.clone().multiply(u)).add(vecV.clone().multiply(v));
                    vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(u, v).endVertex();
                }
                tes.draw();
            }
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private Map<Rectangle, ConstellationPerks> drawStars(Map<ConstellationPerks, ConstellationPerkMap.Position> perks, double offsetX, double offsetY, double whStar, double whBetweenStars) {
        Map<Rectangle, ConstellationPerks> drawn = new HashMap<>(perks.size());
        PlayerProgress prog = ResearchManager.clientProgress;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        Vector3 offset = new Vector3(offsetX, offsetY, zLevel);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        for (Map.Entry<ConstellationPerks, ConstellationPerkMap.Position> star : perks.entrySet()) {
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            SpriteSheetResource sprite;
            if(prog.hasPerkUnlocked(star.getKey())) {
                sprite = SpriteLibrary.spritePerkActive;
            } else if(mayUnlockClient(prog, star.getKey().getSingleInstance())) {
                sprite = SpriteLibrary.spritePerkActivateable;
            } else {
                sprite = SpriteLibrary.spritePerkInactive;
            }
            sprite.getResource().bind();
            int starX = star.getValue().x;
            int starY = star.getValue().y;

            long count = ClientScheduler.getClientTick() + starX + starY;
            //float part = (MathHelper.sin((float) Math.toRadians(((count) * 8) % 360F)) / 2F + 1F);

            //float br = 0.6F + 0.3F * (2F - part);
            float br = 1F;
            float rR = br;
            float rG = br;
            float rB = br;
            float rA = br;
            /*if(overlay != null) {
                rR *= (overlay.getRed()   / 255F);
                rG *= (overlay.getGreen() / 255F);
                rB *= (overlay.getBlue()  / 255F);
                rA *= (overlay.getAlpha() / 255F);
            }*/
            float aPart = 0.2F * rA;
            GL11.glColor4f(rR, rG, rB, aPart + (rA * 0.8F * mouseHoverPerc));

            Vector3 starVec = offset.clone().addX(starX * whBetweenStars - whStar).addY(starY * whBetweenStars - whStar);
            Point upperLeft = new Point(starVec.getBlockX(), starVec.getBlockY());

            double uLength = sprite.getULength();
            double vLength = sprite.getVLength();
            Tuple<Double, Double> frameUV = sprite.getUVOffset(count);

            for (int i = 0; i < 4; i++) {
                int u = ((i + 1) & 2) >> 1;
                int v = ((i + 2) & 2) >> 1;

                Vector3 pos = starVec.clone().addX(whStar * u * 2).addY(whStar * v * 2);
                vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(frameUV.key + uLength * u, frameUV.value + vLength * v).endVertex();
            }

            drawn.put(new Rectangle(upperLeft.x, upperLeft.y, (int) (whStar * 2), (int) (whStar * 2)), star.getKey());
            tes.draw();
        }

        GL11.glColor4f(1F, 1F, 1F, 1F);
        return drawn;
    }

    private void drawOverlayTexture(IMajorConstellation attunedConstellation) {
        BindableResource overlayTex = ClientPerkTextureMapping.getOverlayTexture(attunedConstellation);
        if(overlayTex == null) return;
        GL11.glColor4f(255F / 255F, 222F / 255F, 0F, 0.05F + 0.35F * (1F - mouseHoverPerc));
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        double overlayWH = 90;
        double cX = guiLeft + guiWidth / 2D - overlayWH;
        double cY = guiTop + guiHeight / 2D - overlayWH;

        overlayTex.bind();

        BufferBuilder vb = Tessellator.getInstance().getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(cX,                 cY + overlayWH * 2, zLevel).tex(0, 1).endVertex();
        vb.pos(cX + overlayWH * 2, cY + overlayWH * 2, zLevel).tex(1, 1).endVertex();
        vb.pos(cX + overlayWH * 2, cY,                 zLevel).tex(1, 0).endVertex();
        vb.pos(cX,                 cY,                 zLevel).tex(0, 0).endVertex();
        Tessellator.getInstance().draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private void drawBaseBackground(float zLevel) {
        float br = 0.5F;
        GL11.glColor4f(br, br, br, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        textureResBack.bind();

        BufferBuilder vb = Tessellator.getInstance().getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(guiLeft + 10,            guiTop - 10 + guiHeight, zLevel).tex(0, 1).endVertex();
        vb.pos(guiLeft - 10 + guiWidth, guiTop - 10 + guiHeight, zLevel).tex(1, 1).endVertex();
        vb.pos(guiLeft - 10 + guiWidth, guiTop + 10,             zLevel).tex(1, 0).endVertex();
        vb.pos(guiLeft + 10,            guiTop + 10,             zLevel).tex(0, 0).endVertex();
        Tessellator.getInstance().draw();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            GuiJournalProgression.resetJournal();
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.getJournalInstance());
            return;
        }
        if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellationCluster.getConstellationScreen());
            return;
        }
        for (Rectangle r : thisFramePerks.keySet()) {
            if(r.contains(mouseX, mouseY)) {
                ConstellationPerks perk = thisFramePerks.get(r);
                PlayerProgress prog = ResearchManager.clientProgress;
                if(!prog.hasPerkUnlocked(perk) && mayUnlockClient(prog, perk.getSingleInstance())) {
                    PktUnlockPerk pkt = new PktUnlockPerk(perk, attunedConstellation);
                    PacketChannel.CHANNEL.sendToServer(pkt);
                }
                return;
            }
        }
    }

}
