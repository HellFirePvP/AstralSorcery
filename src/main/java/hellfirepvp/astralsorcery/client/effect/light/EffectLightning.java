package hellfirepvp.astralsorcery.client.effect.light;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayDeque;
import java.util.Deque;
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

    private static Random rand = new Random();

    private static final float defaultMinJitter = 0;
    private static final float defaultMaxJitter = 0;
    private static final float defaultForkChance = 0;
    private static final float defaultMinForkAngle = 0;
    private static final float defaultMaxForkAngle = 0;

    private final Deque<List<LightningVertex>> vertices;

    private EffectLightning(Deque<List<LightningVertex>> sortedLightningVertices) {
        this.vertices = sortedLightningVertices;
        setMaxAge(this.vertices.size() + (this.vertices.size() / 2));
    }

    @Deprecated
    public static EffectLightning buildLightning(Vector3 source, Vector3 destination) {
        return buildLightning(source, destination, defaultMinJitter, defaultMaxJitter, defaultForkChance, defaultMinForkAngle, defaultMaxForkAngle);
    }

    //TODO implement at some point

    @Deprecated
    public static EffectLightning buildLightning(Vector3 source, Vector3 destination, float minJitterAngle, float maxJitterAngle, float forkChance, float minForkAngle, float maxForkAngle) {
        Vector3 directionVector = destination.clone().subtract(source);
        List<LightningVertex> vertices = new LinkedList<>();
        vertices.add(LightningVertex.toTarget(source, destination));
        int iterations = MathHelper.ceil(Math.sqrt(directionVector.clone().length()));
        for (int i = 0; i < iterations; i++) {
            for (LightningVertex vertex : vertices) {
                //Vector3 splitDir =
            }
            //Vector3 rootDir = root.direction.divide(2);
        }
        return new EffectLightning(new ArrayDeque<>());
    }

    public static void renderFast(float pTicks, List<EffectLightning> toBeRendered) {

    }

    @Override
    public void render(float pTicks) {}

    @Override
    public void tick() {
        super.tick();
    }

    private static class LightningVertex {

        private Vector3 offset;
        private Vector3 direction;
        private List<LightningVertex> next = null;

        private LightningVertex(Vector3 offset, Vector3 direction) {
            this.offset = offset;
            this.direction = direction;
        }

        private static LightningVertex toTarget(Vector3 source, Vector3 target) {
            return new LightningVertex(source, target.clone().subtract(source));
        }

    }

}
