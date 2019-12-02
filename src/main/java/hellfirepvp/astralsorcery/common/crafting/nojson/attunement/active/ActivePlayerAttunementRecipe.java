package hellfirepvp.astralsorcery.common.crafting.nojson.attunement.active;

import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.client.util.camera.path.CameraPathBuilder;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.AttunementRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActivePlayerAttunementRecipe
 * Created by HellFirePvP
 * Date: 02.12.2019 / 19:43
 */
public class ActivePlayerAttunementRecipe extends AttunementRecipe.Active {

    //Client camera flight cache
    public Object cameraHack;

    public ActivePlayerAttunementRecipe(AttunementRecipe recipe) {
        super(recipe);
    }

    @Override
    public void stopCrafting(TileAttunementAltar altar) {

    }

    @Override
    public void doTick(LogicalSide side, TileAttunementAltar altar) {
        if (side.isServer()) {

        } else {
            doClientTick(altar);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doClientTick(TileAttunementAltar altar) {
        if (cameraHack == null) {
            Vector3 offset = new Vector3(altar).add(0, 6, 0);
            CameraPathBuilder builder = CameraPathBuilder.builder(offset.clone().add(4, 0, 4), new Vector3(altar).add(0.5, 0.5, 0.5));
            builder.addCircularPoints(offset, CameraPathBuilder.DynamicRadiusGetter.dyanmicIncrease( 5,  0.025), 200, 2);
            builder.addCircularPoints(offset, CameraPathBuilder.DynamicRadiusGetter.dyanmicIncrease(10, -0.01) , 200, 2);

            this.cameraHack = builder.finishAndStart();
        }
    }

    @Override
    public boolean isFinished(TileAttunementAltar altar) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void stopEffects(TileAttunementAltar altar) {
        if (this.cameraHack != null) {
            ClientCameraManager.INSTANCE.removeAllAndCleanup();
        }
    }
}
