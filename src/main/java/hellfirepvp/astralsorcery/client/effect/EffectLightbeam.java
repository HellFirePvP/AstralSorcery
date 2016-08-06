package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.util.AssetLoader;
import hellfirepvp.astralsorcery.client.util.BindableResource;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectLightbeam
 * Created by HellFirePvP
 * Date: 06.08.2016 / 15:05
 */
public class EffectLightbeam implements IComplexEffect {

    private static final BindableResource beamTex = AssetLoader.loadTexture(AssetLoader.TextureLocation.MISC, "lightbeam");
    private final Vector3 from, to;

    public EffectLightbeam(Vector3 from, Vector3 to, double size) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean canRemove() {
        return false;
    }

    @Override
    public RenderTarget getRenderTarget() {
        return RenderTarget.RENDERLOOP;
    }

    @Override
    public void render(float pTicks) {
        Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
        if(rView == null) return;
        if(rView.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > Config.maxEffectRenderDistanceSq) return;



    }

    @Override
    public void tick() {}

}
