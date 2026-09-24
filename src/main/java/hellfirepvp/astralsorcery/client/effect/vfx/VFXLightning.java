/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VFXLightning
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VFXLightning extends EntityVisualFX {

    private static final float referenceLength = 7F;

    private static final float defaultBuildTime = 2F;
    private static final float defaultBuiltWaitTime = 2F;
    private static final float defaultMinJitterDst = 0.2F;
    private static final float defaultMaxJitterDst = 0.7F;
    private static final float defaultForkChance = 1F;
    private static final float defaultMinForkAngleDeg = 15F;
    private static final float defaultMaxForkAngleDeg = 35F;

    private Segment root = null;

    private int buildTime = 3;
    private int buildFinishWaitTime = 2;

    public VFXLightning(Vector3 pos) {
        super(pos);
    }

    public VFXLightning setBuildTime(int buildTime) {
        this.buildTime = buildTime;
        return this;
    }

    public VFXLightning setBuildFinishWaitTime(int buildFinishWaitTime) {
        this.buildFinishWaitTime = buildFinishWaitTime;
        return this;
    }

    public VFXLightning makeDefault(Vector3 target) {
        double dstLength = target.copy().subtract(this.getPos()).length();
        float perc = 1F;
        if (dstLength > referenceLength) {
            perc = Mth.sqrt((float) (dstLength / referenceLength));
        } else if (dstLength < referenceLength) {
            perc = (float) Math.pow(dstLength / referenceLength, 2);
        }
        this.setBuildTime(Math.max(2, Math.round(defaultBuildTime * perc)));
        this.setBuildFinishWaitTime(Math.max(1, Math.round(defaultBuiltWaitTime * perc)));

        return this.make(target, rand.nextLong(),
                defaultMinJitterDst * perc, defaultMaxJitterDst * perc,
                defaultForkChance,
                defaultMinForkAngleDeg, defaultMaxForkAngleDeg);
    }

    public VFXLightning make(Vector3 target, long seed, float minJitterDistance, float maxJitterDistance, float forkChance, float minForkAngle, float maxForkAngle) {
        Vector3 dir = target.copy().subtract(this.getPos());
        RandomSource rand = RandomSource.create(seed);

        List<Segment> segments = new ArrayList<>();
        this.root = new Segment(this.getPos());
        this.root.next.add(new Segment(target));
        segments.add(this.root);

        double length = dir.length();
        int splits = Math.min(Mth.floor(Math.round(Math.sqrt(length))), 200);
        for (int i = 0; i < splits; i++) {
            List<Segment> newSegments = new ArrayList<>();
            for (Segment segment : segments) {
                List<Segment> newSplitSegments = new ArrayList<>();
                for (Segment nextSegment : new ArrayList<>(segment.next)) {
                    float splitDistance = 0.4F + rand.nextFloat() * 0.2F;
                    Vector3 direction = nextSegment.pos.copy().subtract(segment.pos);
                    Vector3 split = direction.copy().multiply(splitDistance).add(segment.pos);
                    float jitterDistance = (minJitterDistance + (maxJitterDistance - minJitterDistance) * rand.nextFloat()) * ((float) (splits - i) / ((float) splits));
                    Vector3 axPerp = direction.copy().perpendicular().rotate(rand.nextFloat() * 2 * Math.PI, direction).normalize().multiply(jitterDistance);
                    split.add(axPerp);

                    Segment newSplitSegment = new Segment(split);
                    newSplitSegment.next.add(nextSegment);
                    newSplitSegments.add(newSplitSegment);

                    if (rand.nextFloat() < forkChance) {
                        Vector3 dirFork = split.copy().subtract(segment.pos);
                        float forkAngle = minForkAngle + (maxForkAngle - minForkAngle) * rand.nextFloat();
                        forkAngle = (float) Math.toRadians(forkAngle);
                        Vector3 perpAxis = dirFork.copy().perpendicular().rotate(rand.nextFloat() * 2 * Math.PI, dirFork);
                        Vector3 dirPos = dirFork.copy().rotate(forkAngle, perpAxis).normalize().multiply(dirFork.length() * 3D / 4D).add(split);

                        Segment forkVertex = new Segment(dirPos);
                        newSplitSegment.next.add(forkVertex);
                    }

                    newSegments.add(newSplitSegment);
                }

                segment.next = newSplitSegments;
                newSegments.add(segment);
            }

            segments = newSegments;
        }
        this.root.calcDepth();
        return this;
    }

    @Override
    public void render(EffectTemplate<?> ctx, Camera renderInfo, VertexConsumer vb, float pTicks) {
        if (this.root == null) return;

        ColorWrapper color = this.getColor(pTicks).copyWithAlpha(this.getAlphaI(pTicks));
        float renderDepth = Math.min(1F, (this.getAge() + pTicks) / this.buildTime);
        this.renderSegment(this.root, vb, renderInfo.getPosition(), renderDepth, color);
    }

    private void renderSegment(Segment segment, VertexConsumer vb, Vec3 cameraPos, float depth, ColorWrapper color) {
        int totalDepth = this.root.followingDepth;
        boolean mayRenderNext = 1F - (((float) segment.followingDepth) / ((float) totalDepth)) <= depth;
        segment.next.forEach(next -> {
            this.drawSegmentLine(segment.pos.copy().subtract(cameraPos), next.pos.copy().subtract(cameraPos), vb, color);
            if (mayRenderNext) {
                this.renderSegment(next, vb, cameraPos, depth, color);
            }
        });
    }

    private void drawSegmentLine(Vector3 from, Vector3 to, VertexConsumer vb, ColorWrapper color) {
        this.drawSegmentLinePart(from, to, vb, Math.toRadians(0F), 0.05F, color);
        this.drawSegmentLinePart(from, to, vb, Math.toRadians(90F), 0.05F, color);
    }

    private void drawSegmentLinePart(Vector3 from, Vector3 to, VertexConsumer vb, double angle, float size, ColorWrapper color) {
        Vector3 dir = to.copy().subtract(from);
        from.subtract(dir.multiply(0.006F));
        to.add(dir.multiply(0.006F));
        Vector3 aim = dir.normalize();
        Vector3 aimPerp = aim.copy().perpendicular().normalize();
        Vector3 perp = aimPerp.copy().rotate(angle, aim).normalize();
        Vector3 perpFrom = perp.copy().multiply(size);
        Vector3 perpTo = perp.multiply(size);

        Vector3 vec = from.copy().add(perpFrom.copy().multiply(-1));
        vec.drawPos(vb).setColor(color.getColor()).setUv(1, 1);
        vec = from.copy().add(perpFrom);
        vec.drawPos(vb).setColor(color.getColor()).setUv(1, 0);
        vec = to.copy().add(perpTo);
        vec.drawPos(vb).setColor(color.getColor()).setUv(0, 0);
        vec = to.copy().add(perpTo.copy().multiply(-1));
        vec.drawPos(vb).setColor(color.getColor()).setUv(0, 1);
    }

    @Override
    public boolean canRemove() {
        return this.buildTime + this.buildFinishWaitTime < this.getAge();
    }

    private static class Segment {

        private final Vector3 pos;
        private List<Segment> next = new ArrayList<>();
        private int followingDepth = -1;

        private Segment(Vector3 pos) {
            this.pos = pos;
        }

        public void calcDepth() {
            if (next.isEmpty()) {
                this.followingDepth = 0;
            } else {
                for (Segment vertex : next) {
                    vertex.calcDepth();
                }
                this.followingDepth = this.next.stream()
                        .mapToInt(s -> s.followingDepth)
                        .max()
                        .orElse(0) + 1;
            }
        }
    }
}
