package hellfirepvp.astralsorcery.client.effect.compound;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.VertexBuffer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompoundGatewayShield
 * Created by HellFirePvP
 * Date: 22.04.2017 / 15:57
 */
public class CompoundGatewayShield extends CompoundEffectSphere {

    public CompoundGatewayShield(Vector3 centralPoint, Vector3 southNorthAxis, double sphereRadius, int fractionsSplit, int fractionsCircle) {
        super(centralPoint, southNorthAxis, sphereRadius, fractionsSplit, fractionsCircle);
    }

    @Override
    public void render(VertexBuffer vb, float pTicks) {
        if(EffectHandler.getInstance().renderGateway) {
            super.render(vb, pTicks);
        }
    }
}
