/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.light;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectLightning
 * Created by HellFirePvP
 * Date: 05.12.2016 / 21:14
 */
public class EffectLightning extends EntityComplexFX {
    //Implementation of lightning generation according to NVIDIA's lightning paper

    private static final BindableResource connection = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "lightningpart");
    private static Random rand = new Random();

    private static final float optimalLightningLength = 7F;

    private static final float growSpeed = 0.09F;
    private static final float fadeTime = 0.03F;
    private static final float defaultMinJitterDst = 0.2F;
    private static final float defaultMaxJitterDst = 0.7F;
    private static final float defaultForkChance = 1F;
    private static final float defaultMinForkAngleDeg = 15F;
    private static final float defaultMaxForkAngleDeg = 35F;

    private final LightningVertex root;

    private float buildSpeed = 0.2F;
    private float buildWaitTime = 0.01F;
    private float bufRenderDepth = -1F;
    private float ovR = 166F / 255F, ovG = 166F / 255F, ovB = 205F / 255F;

    private EffectLightning(LightningVertex rootVertex) {
        this.root = rootVertex;
    }

    public EffectLightning setBuildSpeed(float buildSpeed) {
        this.buildSpeed = buildSpeed;
        return this;
    }

    public EffectLightning setBuildWaitTime(float buildWaitTime) {
        this.buildWaitTime = buildWaitTime;
        return this;
    }

    public EffectLightning setOverlayColor(Color color) {
        this.ovR = color.getRed()   / 255F;
        this.ovG = color.getGreen() / 255F;
        this.ovB = color.getBlue()  / 255F;
        return this;
    }

    public EffectLightning finalizeAndRegister() {
        this.root.calcDepthRec();
        EffectHandler.getInstance().registerFX(this);
        return this;
    }

    public static EffectLightning buildAndRegisterLightning(Vector3 source, Vector3 destination) {
        double dstLength = destination.clone().subtract(source).length();
        float perc = 1F;
        if(dstLength > optimalLightningLength) {
            perc = MathHelper.sqrt(dstLength / optimalLightningLength);
        } else if(dstLength < optimalLightningLength) {
            perc = (float) Math.pow(dstLength / optimalLightningLength, 2);
        }

        EffectLightning lightning = buildLightning(rand.nextLong(), source, destination, defaultMinJitterDst * perc, defaultMaxJitterDst * perc, defaultForkChance, defaultMinForkAngleDeg, defaultMaxForkAngleDeg);
        lightning.setBuildSpeed(Math.max(0.01F, growSpeed * perc));
        lightning.setBuildWaitTime(Math.max(0.0067F, fadeTime * perc));
        lightning.finalizeAndRegister();
        return lightning;
    }

    public static EffectLightning buildLightning(long seed, Vector3 source, Vector3 destination, float minJitterDistance, float maxJitterDistance, float forkChance, float minForkAngle, float maxForkAngle) {
        Vector3 directionVector = destination.clone().subtract(source);
        Random lightningSeed = new Random(seed);

        List<LightningVertex> rootVertices = Lists.newLinkedList();
        LightningVertex root = new LightningVertex(source);
        root.next.add(new LightningVertex(destination));
        rootVertices.add(root);

        double l = directionVector.length();
        int iterations = MathHelper.floor(Math.round(Math.sqrt(l)));
        for (int i = 0; i < iterations; i++) {
            LinkedList<LightningVertex> newRootVertices = new LinkedList<>();
            for (LightningVertex sourceVertex : rootVertices) {
                LinkedList<LightningVertex> newNext = new LinkedList<>();
                for (LightningVertex nextVertex : Lists.newArrayList(sourceVertex.next)) {
                    Vector3 direction = nextVertex.offset.clone().subtract(sourceVertex.offset);
                    Vector3 split = direction.clone().multiply(0.5F).add(sourceVertex.offset);
                    float jitDst = (minJitterDistance + (maxJitterDistance - minJitterDistance) * lightningSeed.nextFloat()) * ((float) (iterations - i) / ((float) iterations));
                    Vector3 axPerp = direction.clone().perpendicular().rotate(lightningSeed.nextFloat() * 2 * Math.PI, direction).normalize().multiply(jitDst);
                    split.add(axPerp);
                    LightningVertex newVertex = new LightningVertex(split);
                    newVertex.next.add(nextVertex);
                    newNext.add(newVertex);
                    if(lightningSeed.nextFloat() < forkChance) {
                        Vector3 dirFork = split.clone().subtract(sourceVertex.offset);
                        float forkAngle = minForkAngle + (maxForkAngle - minForkAngle) * lightningSeed.nextFloat();
                        forkAngle = (float) Math.toRadians(forkAngle);
                        Vector3 perpAxis = dirFork.clone().perpendicular().rotate(lightningSeed.nextFloat() * 2 * Math.PI, dirFork);
                        Vector3 dirPos = dirFork.clone().rotate(forkAngle, perpAxis).normalize().multiply(dirFork.length() * 3D / 4D).add(split);
                        LightningVertex forkVertex = new LightningVertex(dirPos);
                        newVertex.next.add(forkVertex);
                    }
                    newRootVertices.add(newVertex);
                }
                sourceVertex.next = newNext;
                newRootVertices.add(sourceVertex);
            }
            rootVertices = newRootVertices;
        }
        return new EffectLightning(root);
    }

    public static void renderFast(float pTicks, List<EffectLightning> toBeRendered) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        Blending.ADDITIVE_ALPHA.apply();
        connection.bind();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        for (EffectLightning fl : new ArrayList<>(toBeRendered)) {
            fl.renderF(pTicks, buf);
        }

        buf.sortVertexData(
                (float) TileEntityRendererDispatcher.staticPlayerX,
                (float) TileEntityRendererDispatcher.staticPlayerY,
                (float) TileEntityRendererDispatcher.staticPlayerZ);
        tes.draw();

        TextureHelper.refreshTextureBindState();
        Blending.DEFAULT.apply();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void renderF(float partialTicks, BufferBuilder vb) {
        bufRenderDepth = Math.min(1F, (age + partialTicks) / (buildSpeed * 20F));
        renderRec(this.root, vb);
    }

    private void renderRec(LightningVertex root, BufferBuilder vb) {
        int allDepth = this.root.followingDepth;
        boolean mayRenderNext = 1F - (((float) root.followingDepth) / ((float) allDepth)) <= bufRenderDepth;
        for (LightningVertex next : root.next) {
            drawLine(root.offset, next.offset, vb);
            if(mayRenderNext) renderRec(next, vb);
        }
    }

    private void drawLine(Vector3 from, Vector3 to, BufferBuilder vb) {
        renderCurrentTextureAroundAxis(from, to, Math.toRadians(0F),  0.02F, vb);
        renderCurrentTextureAroundAxis(from, to, Math.toRadians(90F), 0.02F, vb);
    }

    private void renderCurrentTextureAroundAxis(Vector3 from, Vector3 to, double angle, double size, BufferBuilder buf) {
        Vector3 aim = to.clone().subtract(from).normalize();
        Vector3 aimPerp = aim.clone().perpendicular().normalize();
        Vector3 perp = aimPerp.clone().rotate(angle, aim).normalize();
        Vector3 perpFrom = perp.clone().multiply(size);
        Vector3 perpTo = perp.multiply(size);

        Vector3 vec = from.clone().add(perpFrom.clone().multiply(-1));
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(1, 1).color(ovR, ovG, ovB, 1F).endVertex();
        vec = from.clone().add(perpFrom);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(1, 0).color(ovR, ovG, ovB, 1F).endVertex();
        vec = to.clone().add(perpTo);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(0, 0).color(ovR, ovG, ovB, 1F).endVertex();
        vec = to.clone().add(perpTo.clone().multiply(-1));
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(0, 1).color(ovR, ovG, ovB, 1F).endVertex();
    }

    @Override
    public boolean canRemove() {
        return Math.max((buildSpeed + buildWaitTime) * 20F, 1) < age;
    }

    @Override
    public void render(float pTicks) {}

    @Override
    public void tick() {
        super.tick();
    }

    private static class LightningVertex {

        private Vector3 offset;
        private List<LightningVertex> next = new LinkedList<>();
        private int followingDepth = -1;

        private LightningVertex(Vector3 offset) {
            this.offset = offset;
        }

        public void calcDepthRec() {
            if(next.isEmpty()) {
                this.followingDepth = 0;
            } else {
                for (LightningVertex vertex : next) {
                    vertex.calcDepthRec();
                }
                this.followingDepth = MiscUtils.getMaxEntry(this.next, (v) -> v.followingDepth) + 1;
            }
        }
    }

}
