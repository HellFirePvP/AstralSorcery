package hellfirepvp.astralsorcery.client.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderSkybox
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:44
 */
public class RenderSkybox extends IRenderHandler {

    private static final RenderDefaultSkybox defaultSky = new RenderDefaultSkybox();
    private static final RenderAstralSkybox astralSky = new RenderAstralSkybox();

    private final IRenderHandler otherSkyRenderer;

    public RenderSkybox(IRenderHandler skyRenderer) {
        this.otherSkyRenderer = skyRenderer;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {

        if (!astralSky.isInitialized() && world.provider.getDimension() == 0) { //DimID == 0 should always be the case tho.
            astralSky.setInitialized(world.getWorldInfo().getSeed());
        }

        if (otherSkyRenderer != null) {
            //Expecting a world renderer that does not render the whole sky, only a part of it.
            //Its the overworld after all. The sky "should" not be changed Kappa
            otherSkyRenderer.render(partialTicks, world, mc);
        }

        /*PlayerProgress progr = ResearchManager.clientProgress;
        if(progr != null && progr.isInvolved()) {
            astralSky.render(partialTicks, world, mc);
        } else {
            if(otherSkyRenderer != null) {
                //Expecting a world renderer that does not render the whole sky, only a part of it.
                //Its the overworld after all. The sky "should" not be changed Kappa
                otherSkyRenderer.render(partialTicks, world, mc);
            } else {
                defaultSky.render(partialTicks, world, mc);
            }
        }*/

        astralSky.render(partialTicks, world, mc);
    }

    static {
        RenderDefaultSkybox.setupDefaultSkybox();
    }

}
