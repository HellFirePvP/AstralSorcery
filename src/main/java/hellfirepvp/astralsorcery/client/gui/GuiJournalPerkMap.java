package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.mappings.ClientPerkTextureMapping;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkMap;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkMapRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalPerk
 * Created by HellFirePvP
 * Date: 23.11.2016 / 14:50
 */
public class GuiJournalPerkMap extends GuiScreenJournal {

    private static final BindableResource textureResBack = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiResBG");

    private static final double widthHeight = 70;

    private ConstellationPerkMap mapToDisplay = null;
    private IMajorConstellation attunedConstellation = null;

    public GuiJournalPerkMap() {
        super(2);
        IMajorConstellation attuned = ResearchManager.clientProgress.getAttunedConstellation();
        if(attuned != null) {
            ConstellationPerkMap map = ConstellationPerkMapRegistry.getPerkMap(attuned);
            if(map != null) {
                this.mapToDisplay = map;
                this.attunedConstellation = attuned;
            }
        }
        this.attunedConstellation = Constellations.evorsio;

        this.mapToDisplay = new ConstellationPerkMap();
        //Goes from 0,0 -> 14,14 max.
        this.mapToDisplay.addPerk(ConstellationPerks.OFF_DMG_INCREASE,  ConstellationPerkMap.PerkOrder.DEFAULT, 14, 14);
        this.mapToDisplay.addPerk(ConstellationPerks.OFF_DMG_DISTANCE,  ConstellationPerkMap.PerkOrder.DEFAULT, 12,  7, ConstellationPerks.OFF_DMG_INCREASE);
        this.mapToDisplay.addPerk(ConstellationPerks.OFF_DMG_KNOCKBACK, ConstellationPerkMap.PerkOrder.DEFAULT, 10,  2, ConstellationPerks.OFF_DMG_DISTANCE);
        this.mapToDisplay.addPerk(ConstellationPerks.OFF_DMG_AFTERKILL, ConstellationPerkMap.PerkOrder.DEFAULT,  0,  0, ConstellationPerks.OFF_DMG_INCREASE);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        TextureHelper.refreshTextureBindState();
        drawDefault(textureResShell);
        drawBaseBackground(zLevel - 50);

        if(attunedConstellation != null) {
            drawOverlayTexture(attunedConstellation);
        }

        if(mapToDisplay != null) {
            drawPerkMap(mapToDisplay, new Point(mouseX, mouseY));
        }

        TextureHelper.refreshTextureBindState();

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawPerkMap(ConstellationPerkMap mapToDisplay, Point mouse) {
        double whStar = 6;
        double whBetweenStars = widthHeight / 7D;

        double offsetX = guiLeft + ((guiWidth ) / 2D) - widthHeight;
        double offsetY = guiTop  + ((guiHeight) / 2D) - widthHeight;

        drawConnections(mapToDisplay, offsetX, offsetY, whBetweenStars, 4D);

        Map<Rectangle, ConstellationPerks> rects = drawStars(mapToDisplay.getPerks(), offsetX, offsetY, whStar, whBetweenStars);
        for (Rectangle r : rects.keySet()) {
            if(r.contains(mouse)) {
                List<String> toolTip = new LinkedList<>();
                ConstellationPerk perk = rects.get(r).getSingleInstance();
                perk.addLocalizedDescription(toolTip);
                RenderingUtils.renderBlueTooltip(mouse.x, mouse.y, toolTip, Minecraft.getMinecraft().fontRendererObj);
                GlStateManager.color(1F, 1F, 1F, 1F);
                GL11.glColor4f(1F, 1F, 1F, 1F);
            }
        }
    }

    private void drawConnections(ConstellationPerkMap mapToDisplay, double offsetX, double offsetY, double whBetweenStars, double linebreadth) {
        PlayerProgress prog = ResearchManager.clientProgress;
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();

        Vector3 offset = new Vector3(offsetX, offsetY, zLevel);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        for (ConstellationPerkMap.Dependency dep : mapToDisplay.getPerkDependencies()) {
            BindableResource tex;
            Color overlay = null;
            if(prog.hasPerkUnlocked(dep.to)) {
                tex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "connection"); //TODO wiiv, here.
                overlay = new Color(0x00EEEE00);
            } else {
                tex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "connection");
                overlay = new Color(0xBBBBFF);
            }

            ConstellationPerkMap.Position from = mapToDisplay.getPosition(dep.from);
            ConstellationPerkMap.Position to = mapToDisplay.getPosition(dep.to);
            if(from != null && to != null) {

                int count = ClientScheduler.getClientTick() + from.x + from.y + to.x + to.y;
                double part = (Math.sin(Math.toRadians(((count) * 8) % 360D)) + 1D) / 4D;

                float br = 0.2F + 0.4F * (2F - ((float) part));
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
                GL11.glColor4f(rR, rG, rB, rA);

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
        VertexBuffer vb = tes.getBuffer();
        Vector3 offset = new Vector3(offsetX, offsetY, zLevel);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        for (Map.Entry<ConstellationPerks, ConstellationPerkMap.Position> star : perks.entrySet()) {
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            BindableResource tex;
            Color overlay = null;
            if(prog.hasPerkUnlocked(star.getKey())) {
                tex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1"); //TODO wiiv, here.
                overlay = new Color(0x00EEEE00);
            } else {
                tex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1");
                overlay = new Color(0xBBBBFF);
            }
            tex.bind();
            int starX = star.getValue().x;
            int starY = star.getValue().y;

            int count = ClientScheduler.getClientTick() + starX + starY;
            float part = (MathHelper.sin((float) Math.toRadians(((count) * 8) % 360F)) / 2F + 1F);

            float br = 0.6F + 0.3F * (2F - part);
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
            GL11.glColor4f(rR, rG, rB, rA);

            Vector3 starVec = offset.clone().addX(starX * whBetweenStars - whStar).addY(starY * whBetweenStars - whStar);
            Point upperLeft = new Point(starVec.getBlockX(), starVec.getBlockY());

            for (int i = 0; i < 4; i++) {
                int u = ((i + 1) & 2) >> 1;
                int v = ((i + 2) & 2) >> 1;

                Vector3 pos = starVec.clone().addX(whStar * u * 2).addY(whStar * v * 2);
                vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(u, v).endVertex();
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

        double cX = guiLeft + guiWidth / 2D - widthHeight;
        double cY = guiTop + guiHeight / 2D - widthHeight;

        overlayTex.bind();

        VertexBuffer vb = Tessellator.getInstance().getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(cX,                   cY + widthHeight * 2, zLevel).tex(0, 1).endVertex();
        vb.pos(cX + widthHeight * 2, cY + widthHeight * 2, zLevel).tex(1, 1).endVertex();
        vb.pos(cX + widthHeight * 2, cY,                   zLevel).tex(1, 0).endVertex();
        vb.pos(cX,                   cY,                   zLevel).tex(0, 0).endVertex();
        Tessellator.getInstance().draw();

        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private void drawBaseBackground(float zLevel) {
        float br = 0.5F;
        GL11.glColor4f(br, br, br, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        textureResBack.bind();

        VertexBuffer vb = Tessellator.getInstance().getBuffer();
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
        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            GuiJournalProgression.resetJournal();
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.getJournalInstance());
            return;
        }
        if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellations.getConstellationScreen());
        }
    }

}
