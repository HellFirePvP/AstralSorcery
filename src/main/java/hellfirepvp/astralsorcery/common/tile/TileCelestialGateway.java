package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.GatewayCache;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialGateway
 * Created by HellFirePvP
 * Date: 16.04.2017 / 20:43
 */
public class TileCelestialGateway extends TileEntityTick {
    private boolean hasMultiblock = false;
    private boolean doesSeeSky = false;

    private Object clientSphere = null;

    @Override
    public void update() {
        super.update();

        if(world.isRemote) {
            playEffects();
        } else {
            if((ticksExisted & 15) == 0) {
                updateSkyState(world.canSeeSky(getPos()));
            }

            if((ticksExisted & 15) == 0) {
                updateMultiblockState(MultiBlockArrays.patternCelestialGateway.matches(world, pos));
            }
        }
    }

    private void updateMultiblockState(boolean matches) {
        boolean update = hasMultiblock != matches;
        this.hasMultiblock = matches;
        if(update) {
            markForUpdate();
        }
    }

    private void updateSkyState(boolean seeSky) {
        boolean update = doesSeeSky != seeSky;
        this.doesSeeSky = seeSky;
        if(update) {
            markForUpdate();
        }
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {

    }

    @Override
    protected void onFirstTick() {
        if(world.isRemote) return;

        GatewayCache cache = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.GATEWAY_DATA);
        cache.offerPosition(world, pos);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.hasMultiblock = compound.getBoolean("mbState");
        this.doesSeeSky = compound.getBoolean("skyState");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("mbState", this.hasMultiblock);
        compound.setBoolean("skyState", this.doesSeeSky);
    }

}
