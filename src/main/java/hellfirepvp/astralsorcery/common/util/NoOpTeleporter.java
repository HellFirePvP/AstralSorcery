package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NoOpTeleporter
 * Created by HellFirePvP
 * Date: 22.04.2017 / 15:50
 */
public class NoOpTeleporter extends Teleporter {

    public NoOpTeleporter(WorldServer worldIn) {
        super(worldIn);
    }

    @Override
    public void placeInPortal(Entity entityIn, float rotationYaw) {}

    @Override
    public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
        return true;
    }

    @Override
    public boolean makePortal(Entity entityIn) {
        return true;
    }

    @Override
    public void removeStalePortalLocations(long worldTime) {}

}
