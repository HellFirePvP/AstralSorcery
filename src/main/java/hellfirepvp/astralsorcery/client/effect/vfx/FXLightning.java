/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXLightning
 * Created by HellFirePvP
 * Date: 17.07.2019 / 22:27
 */
public class FXLightning extends EntityVisualFX {

    private static final float optimalLightningLength = 7F;

    private static final float growSpeed = 0.09F;
    private static final float fadeTime = 0.03F;
    private static final float defaultMinJitterDst = 0.2F;
    private static final float defaultMaxJitterDst = 0.7F;
    private static final float defaultForkChance = 1F;
    private static final float defaultMinForkAngleDeg = 15F;
    private static final float defaultMaxForkAngleDeg = 35F;

    private LightningVertex root = null;

    private float buildSpeed = 0.2F;
    private float buildWaitTime = 0.01F;
    private float bufRenderDepth = -1F;

    public FXLightning(Vector3 pos) {
        super(pos);
    }

    public FXLightning setBuildSpeed(float buildSpeed) {
        this.buildSpeed = buildSpeed;
        return this;
    }

    public FXLightning setBuildWaitTime(float buildWaitTime) {
        this.buildWaitTime = buildWaitTime;
        return this;
    }

    public FXLightning makeDefault(Vector3 to) {
        double dstLength = to.clone().subtract(this.getPosition()).length();
        float perc = 1F;
        if (dstLength > optimalLightningLength) {
            perc = MathHelper.sqrt(dstLength / optimalLightningLength);
        } else if (dstLength < optimalLightningLength) {
            perc = (float) Math.pow(dstLength / optimalLightningLength, 2);
        }

        this.make(rand.nextLong(), this.getPosition(), to, defaultMinJitterDst * perc, defaultMaxJitterDst * perc, defaultForkChance, defaultMinForkAngleDeg, defaultMaxForkAngleDeg);
        this.setBuildSpeed(Math.max(0.01F, growSpeed * perc));
        this.setBuildWaitTime(Math.max(0.0067F, fadeTime * perc));
        return this;
    }

    private void make(long seed, Vector3 source, Vector3 destination, float minJitterDistance, float maxJitterDistance, float forkChance, float minForkAngle, float maxForkAngle) {
        Vector3 directionVector = destination.clone().subtract(source);
        Random lightningSeed = new Random(seed);

        List<LightningVertex> rootVertices = Lists.newLinkedList();
        this.root = new LightningVertex(source);
        this.root.next.add(new LightningVertex(destination));
        rootVertices.add(this.root);

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
                    if (lightningSeed.nextFloat() < forkChance) {
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
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, MatrixStack renderStack, IVertexBuilder vb, float pTicks) {
        if (root == null) {
            return;
        }

        Color c = this.getColor(pTicks);
        bufRenderDepth = Math.min(1F, (age + pTicks) / (buildSpeed * 20F));
        renderRec(this.root, vb, renderStack, c.getRed(), c.getGreen(), c.getBlue());
    }

    private void renderRec(LightningVertex root, IVertexBuilder vb, MatrixStack renderStack, float r, float g, float b) {
        int allDepth = root.followingDepth;
        boolean mayRenderNext = 1F - (((float) root.followingDepth) / ((float) allDepth)) <= bufRenderDepth;
        for (LightningVertex next : root.next) {
            drawLine(root.offset, next.offset, vb, renderStack, r, g, b);
            if (mayRenderNext) {
                renderRec(next, vb, renderStack, r, g, b);
            }
        }
    }

    private void drawLine(Vector3 from, Vector3 to, IVertexBuilder vb, MatrixStack renderStack, float r, float g, float b) {
        renderCurrentTextureAroundAxis(from, to, Math.toRadians(0F),  0.035F, vb, renderStack, r, g, b);
        renderCurrentTextureAroundAxis(from, to, Math.toRadians(90F), 0.035F, vb, renderStack, r, g, b);
    }

    private void renderCurrentTextureAroundAxis(Vector3 from, Vector3 to, double angle, double size, IVertexBuilder buf, MatrixStack renderStack, float r, float g, float b) {
        Vector3 aim = to.clone().subtract(from).normalize();
        Vector3 aimPerp = aim.clone().perpendicular().normalize();
        Vector3 perp = aimPerp.clone().rotate(angle, aim).normalize();
        Vector3 perpFrom = perp.clone().multiply(size);
        Vector3 perpTo = perp.multiply(size);

        Matrix4f matr = renderStack.getLast().getMatrix();
        Vector3 vec = from.clone().add(perpFrom.clone().multiply(-1));
        vec.drawPos(matr, buf).color(r, g, b, 1F).tex(1, 1).endVertex();
        vec = from.clone().add(perpFrom);
        vec.drawPos(matr, buf).color(r, g, b, 1F).tex(1, 0).endVertex();
        vec = to.clone().add(perpTo);
        vec.drawPos(matr, buf).color(r, g, b, 1F).tex(0, 0).endVertex();
        vec = to.clone().add(perpTo.clone().multiply(-1));
        vec.drawPos(matr, buf).color(r, g, b, 1F).tex(0, 1).endVertex();
    }

    @Override
    public boolean canRemove() {
        return Math.max((buildSpeed + buildWaitTime) * 20F, 1) < age;
    }

    private static class LightningVertex {

        private Vector3 offset;
        private List<LightningVertex> next = new LinkedList<>();
        private int followingDepth = -1;

        private LightningVertex(Vector3 offset) {
            this.offset = offset;
        }

        public void calcDepthRec() {
            if (next.isEmpty()) {
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
