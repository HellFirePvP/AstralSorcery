/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.compound;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.math.MathHelper;

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

    private List<Face> sphereFaces = new LinkedList<>();
    private Vector3 offset;
    private Vector3 axis;

    public CompoundEffectSphere(Vector3 centralPoint, Vector3 southNorthAxis, double sphereRadius, int fractionsSplit, int fractionsCircle) {
        this.offset = centralPoint;
        this.axis = southNorthAxis.clone().normalize().multiply(sphereRadius);
        fractionsSplit = MathHelper.clamp(fractionsSplit, 2, Integer.MAX_VALUE);
        fractionsCircle = MathHelper.clamp(fractionsCircle, 3, Integer.MAX_VALUE);
        buildFaces(fractionsSplit, fractionsCircle);
    }

    private void buildFaces(int fractionsSplit, int fractionsCircle) {
        Vector3 centerPerp = axis.clone().perpendicular();
        double degSplit =       180D / ((double) fractionsSplit);
        double degCircleSplit = 360D / ((double) fractionsCircle);
        Vector3 prev = offset.clone().add(axis);
        for (int i = 0; i <= fractionsSplit; i++) {
            Vector3 splitVec = axis.clone().rotate(Math.toRadians(degSplit * i), centerPerp);
            //TODO finish

            prev = splitVec;
        }
    }

    @Override
    public void render(VertexBuffer vb, float pTicks) {

    }

    @Override
    public ObjectGroup getGroup() {
        return ObjectGroup.GASEOUS_SPHERE;
    }

    @Override
    public void tick() {

    }

}
