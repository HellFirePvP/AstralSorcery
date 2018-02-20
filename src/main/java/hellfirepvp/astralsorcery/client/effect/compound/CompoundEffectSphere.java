/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.compound;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompoundEffectSphere
 * Created by HellFirePvP
 * Date: 16.02.2017 / 16:31
 */
public class CompoundEffectSphere extends CompoundObjectEffect {

    private double alphaFadeMaxDist = -1;
    private boolean removeIfInvisible = false;

    private List<SolidColorTriangleFace> sphereFaces = new LinkedList<>();
    private Vector3 offset;
    private Vector3 axis;

    public CompoundEffectSphere(Vector3 centralPoint, Vector3 southNorthAxis, double sphereRadius, int fractionsSplit, int fractionsCircle) {
        this.offset = centralPoint;
        this.axis = southNorthAxis.clone().normalize().multiply(sphereRadius);
        fractionsSplit = MathHelper.clamp(fractionsSplit, 2, Integer.MAX_VALUE);
        fractionsCircle = MathHelper.clamp(fractionsCircle, 3, Integer.MAX_VALUE);
        buildFaces(fractionsSplit, fractionsCircle);
    }

    public CompoundEffectSphere setAlphaFadeDistance(double fadeDistance) {
        this.alphaFadeMaxDist = fadeDistance;
        return this;
    }

    public CompoundEffectSphere setRemoveIfInvisible(boolean removeIfInvisible) {
        this.removeIfInvisible = removeIfInvisible;
        return this;
    }

    private void buildFaces(int fractionsSplit, int fractionsCircle) {
        Vector3 centerPerp = axis.clone().perpendicular();
        double degSplit =       180D / ((double) fractionsSplit);
        double degCircleSplit = 360D / ((double) fractionsCircle);
        double degCircleOffsetShifted = degCircleSplit / 2D;
        boolean shift = false;

        Vector3[] prevArray = new Vector3[fractionsCircle];
        Vector3 prev = axis.clone();
        Arrays.fill(prevArray, prev.clone());
        for (int i = 1; i <= fractionsSplit; i++) {
            Vector3 splitVec = axis.clone().rotate(Math.toRadians(degSplit * i), centerPerp);

            Vector3[] circlePositions = new Vector3[fractionsCircle];
            for (int j = 0; j < fractionsCircle; j++) {
                double deg = shift ? degCircleOffsetShifted : 0;
                deg += degCircleSplit * j;
                circlePositions[j] = splitVec.clone().rotate(Math.toRadians(deg), axis);
            }

            for (int k = 0; k < fractionsCircle; k++) {
                int prevIndex = shift ? k : k - 1;
                if(prevIndex < 0) {
                    prevIndex = fractionsCircle - 1;
                }
                int nextIndex = shift ? k + 1 : k;
                if(nextIndex >= fractionsCircle) {
                    nextIndex = 0;
                }
                sphereFaces.add(new SolidColorTriangleFace(prevArray[prevIndex], prevArray[nextIndex], circlePositions[k]));
                int nextCircle = k + 1;
                if(nextCircle >= fractionsCircle) {
                    nextCircle = 0;
                }
                sphereFaces.add(new SolidColorTriangleFace(circlePositions[k], prevArray[nextIndex], circlePositions[nextCircle]));
            }

            prevArray = circlePositions;
            shift = !shift;
        }
    }

    public Vector3 getPosition() {
        return offset;
    }

    @Override
    public void render(BufferBuilder vb, float pTicks) {
        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);
        GL11.glTranslated(offset.getX(), offset.getY(), offset.getZ());
        float alpha = 1F;
        if(alphaFadeMaxDist != -1 && Minecraft.getMinecraft().player != null) {
            Vector3 plVec = Vector3.atEntityCenter(Minecraft.getMinecraft().player);
            double dst = plVec.distance(getPosition()) - 1.2;

            alpha *= 1D - (dst / alphaFadeMaxDist);
            if(removeIfInvisible && alpha <= 0) {
                requestRemoval();
            }
            alpha = MathHelper.clamp(alpha, 0, 1);
        }
        for (SolidColorTriangleFace face : this.sphereFaces) {
            vb.pos(face.v1.getX(), face.v1.getY(), face.v1.getZ()).color(0, 0, 0, alpha).endVertex();
            vb.pos(face.v2.getX(), face.v2.getY(), face.v2.getZ()).color(0, 0, 0, alpha).endVertex();
            vb.pos(face.v3.getX(), face.v3.getY(), face.v3.getZ()).color(0, 0, 0, alpha).endVertex();
        }
    }

    @Override
    public ObjectGroup getGroup() {
        return ObjectGroup.SOLID_COLOR_SPHERE;
    }

    @Override
    public void tick() {
        if(alphaFadeMaxDist == -1 || !removeIfInvisible) {
            super.tick();
        }
    }

    public static class SolidColorTriangleFace {

        private Vector3 v1, v2, v3;

        public SolidColorTriangleFace(Vector3 v1, Vector3 v2, Vector3 v3) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
        }

    }

}
