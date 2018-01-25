/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.event.ClientGatewayHandler;
import hellfirepvp.astralsorcery.client.sky.RenderAstralSkybox;
import hellfirepvp.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import hellfirepvp.astralsorcery.common.data.world.data.GatewayCache;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: UIGateway
 * Created by HellFirePvP
 * Date: 17.04.2017 / 18:54
 */
public class UIGateway {

    private List<GatewayEntry> gatewayEntries = new LinkedList<>();
    private final Vector3 origin;
    private final double radius;

    private UIGateway(Vector3 pos, double radius) {
        this.origin = pos;
        this.radius = radius;
    }

    public Vector3 getPos() {
        return origin;
    }

    public double getRadius() {
        return radius;
    }

    public static UIGateway initialize(World world, Vector3 source, double sphereRadius) {
        UIGateway ui = new UIGateway(source, sphereRadius);
        CelestialGatewaySystem system = CelestialGatewaySystem.instance;
        int dimid = world.provider.getDimension();
        List<GatewayCache.GatewayNode> sameDimensionPositions = system.getGatewaysForWorld(world, Side.CLIENT);
        if(sameDimensionPositions != null) {
            gatherStars(ui, world.provider.getDimension(), sameDimensionPositions, true, sphereRadius);
        }

        for (Map.Entry<Integer, List<GatewayCache.GatewayNode>> entries : system.getGatewayCache(Side.CLIENT).entrySet()) {
            if(entries.getKey() == dimid) continue;
            List<GatewayCache.GatewayNode> otherPositions = entries.getValue();
            gatherStars(ui, entries.getKey(), otherPositions, false, sphereRadius);
        }

        return ui;
    }

    @Nullable
    public GatewayEntry findMatchingEntry(float yaw, float pitch) {
        float matchAccurancy = 4;
        for (GatewayEntry entry : gatewayEntries) {
            if(Math.abs(entry.pitch - pitch) < matchAccurancy &&
                    (Math.abs(entry.yaw - yaw) <= matchAccurancy || Math.abs(entry.yaw - yaw - 360F) <= matchAccurancy)) {
                return entry;
            }
        }
        return null;
    }

    private static void gatherStars(UIGateway gateway, int dimId, List<GatewayCache.GatewayNode> otherPositions, boolean sameWorld, double sphereRadius) {
        Vector3 gatePosition = gateway.getPos();
        for (GatewayCache.GatewayNode other : otherPositions) {
            Vector3 otherPos = new Vector3(other);
            if(sameWorld && otherPos.distance(gatePosition) < 16) continue;

            Vector3 direction = otherPos.subtract(gatePosition).normalize().multiply(sphereRadius);
            GatewayEntry potentialEntry = new GatewayEntry(other, dimId, direction);
            if(sameWorld) {
                boolean mayAdd = true;
                for (GatewayEntry entry : gateway.gatewayEntries) {
                    if(Math.abs(entry.pitch - potentialEntry.pitch) < 10 &&
                            (Math.abs(entry.yaw - potentialEntry.yaw) <= 10 || Math.abs(entry.yaw - potentialEntry.yaw - 360F) <= 10)) {
                        mayAdd = false;
                    }
                }
                if(mayAdd) {
                    gateway.gatewayEntries.add(potentialEntry);
                }
            } else {
                long seed = 0xA7401CE1466A1095L;
                seed |= ((long) other.getX()) << 48;
                seed |= ((long) other.getY()) << 24;
                seed |= ((long) other.getZ());
                Random rand = new Random(seed);
                direction = Vector3.positiveYRandom(rand).normalize().multiply(sphereRadius);
                potentialEntry = new GatewayEntry(other, dimId, direction);
                int tries = 30;
                boolean foundSpace = false;
                while (!foundSpace && tries > 0) {
                    boolean mayAdd = true;
                    for (GatewayEntry entry : gateway.gatewayEntries) {
                        if(Math.abs(entry.pitch - potentialEntry.pitch) < 25 &&
                                (Math.abs(entry.yaw - potentialEntry.yaw) <= 25 || Math.abs(entry.yaw - potentialEntry.yaw - 360F) <= 25)) {
                            mayAdd = false;
                        }
                    }
                    if(mayAdd) {
                        foundSpace = true;
                    } else {
                        direction = Vector3.positiveYRandom(rand).normalize().multiply(sphereRadius);
                        potentialEntry = new GatewayEntry(other, dimId, direction);
                    }
                    tries--;
                }
                if(foundSpace) {
                    gateway.gatewayEntries.add(potentialEntry);
                }
            }
        }
    }

    public void renderIntoWorld(float pticks) {
        if(Minecraft.getMinecraft().player == null) return;

        EntityPlayer player = Minecraft.getMinecraft().player;
        double dst = new Vector3(origin).distance(Vector3.atEntityCorner(player).addY(1.5));
        if(dst > 3) return;
        float alpha = 1F - ((float) (dst / 2D));
        alpha = MathHelper.clamp(alpha, 0F, 1F);
        Color c = new Color(0xF0BD00);
        float red = c.getRed() / 255F;
        float green = c.getGreen() / 255F;
        float blue = c.getBlue() / 255F;

        long seed = 0xA781B4F01C771923L;
        seed |= ((long) origin.getBlockX()) << 48;
        seed |= ((long) origin.getBlockY()) << 24;
        seed |= ((long) origin.getBlockZ());
        Random rand = new Random(seed);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableAlpha();
        GlStateManager.color(1F, 1F, 1F, 1F);
        RenderAstralSkybox.TEX_STAR_1.bind();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (int i = 0; i < 300; i++) {
            Vector3 dir = Vector3.random(rand).normalize().multiply(radius);
            float a = RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), pticks, rand.nextInt(7) + 6);
            a *= alpha;
            RenderingUtils.renderFacingFullQuadVB(vb,
                    origin.getX() + dir.getX(),
                    origin.getY() + dir.getY(),
                    origin.getZ() + dir.getZ(),
                    pticks, 0.07F, 0, 1F, 1F, 1F, a);
        }
        for (GatewayEntry entry : gatewayEntries) {
            float a = RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), pticks, rand.nextInt(7) + 6);
            a = 0.4F + (0.6F * a);
            a = (a * alpha);
            RenderingUtils.renderFacingFullQuadVB(vb,
                    origin.getX() + entry.relativePos.getX(),
                    origin.getY() + entry.relativePos.getY(),
                    origin.getZ() + entry.relativePos.getZ(),
                    pticks, 0.16F, 0, red, green, blue, a);
        }
        RenderingUtils.sortVertexData(vb);
        tes.draw();
        TextureHelper.refreshTextureBindState();
        GlStateManager.enableAlpha();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();

        UIGateway.GatewayEntry entry = findMatchingEntry(MathHelper.wrapDegrees(player.rotationYaw), MathHelper.wrapDegrees(player.rotationPitch));
        if(entry != null) {
            String display = entry.originalBlockPos.display;
            if(display != null && !display.isEmpty()) {
                GlStateManager.pushMatrix();
                FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
                Vector3 pos = entry.relativePos.clone().add(origin);
                RenderingUtils.removeStandartTranslationFromTESRMatrix(pticks);
                GlStateManager.translate(pos.getX(), pos.getY() + 0.25, pos.getZ());
                GlStateManager.scale(0.015, -0.015, 0.015);
                GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
                float plRot = RenderingUtils.interpolateRotation(MathHelper.wrapDegrees(player.prevRotationYaw), MathHelper.wrapDegrees(player.rotationYaw), pticks);
                GlStateManager.rotate(-plRot + 180, 0, 1, 0);
                GlStateManager.depthMask(false);
                GlStateManager.disableDepth();
                int length = fr.getStringWidth(display);
                fr.drawString(display, -(length / 2), 0, 0x88F0BD00);

                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.color(1F, 1F, 1F, 1F);
                GlStateManager.popMatrix();
            }
        }
    }

    public void renderGatewayTarget(float pTicks) {
        int focusTicks = ClientGatewayHandler.focusTicks;
        UIGateway.GatewayEntry focusingEntry = ClientGatewayHandler.focusingEntry;
        float perc = (Math.min(40F, focusTicks) / 40F) * 0.5F;
        if(focusTicks > 50) {
            perc = ((float) (focusTicks - 50)) / 25F;
            perc = MathHelper.clamp(perc, 0.5F, 1F);
        }
        ResourceLocation screenshot = ClientScreenshotCache.tryQueryTextureFor(focusingEntry.originalDimId, focusingEntry.originalBlockPos);
        if(screenshot != null) {
            GlStateManager.pushMatrix();
            Minecraft.getMinecraft().getTextureManager().bindTexture(screenshot);
            GlStateManager.enableBlend();
            Blending.DEFAULT.applyStateManager();
            GlStateManager.disableAlpha();
            Tessellator tes = Tessellator.getInstance();
            BufferBuilder vb = tes.getBuffer();
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

            Vector3 pos = focusingEntry.relativePos.clone().multiply(0.85).add(origin);
            RenderingUtils.renderFacingFullQuadVB(vb, pos.getX(), pos.getY(), pos.getZ(),
                    pTicks, 0.4F, 0,
                    1F, 1F, 1F, perc);
            tes.draw();
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
    }

    public static class GatewayEntry {

        //Used for transfer
        public final GatewayCache.GatewayNode originalBlockPos;
        public final int originalDimId;

        //Used for drawing
        public final Vector3 relativePos;

        //Used for matching
        private final float yaw, pitch;

        private GatewayEntry(GatewayCache.GatewayNode originalBlockPos, int originalDimId, Vector3 relativePos) {
            this.originalBlockPos = originalBlockPos;
            this.originalDimId = originalDimId;
            this.relativePos = relativePos.clone();
            if(this.relativePos.getY() < 0) {
                this.relativePos.setY(0);
            }

            Vector3 angles = relativePos.copyToPolar();
            this.yaw = (float) (180F - angles.getZ());
            this.pitch = Math.min(0F, (float) (-90F + angles.getY()));
        }

    }

}
