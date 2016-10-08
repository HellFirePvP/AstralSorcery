package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.CrystalTransmissionNode;
import hellfirepvp.astralsorcery.common.tile.base.TileTransmissionBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCrystalLens
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:11
 */
public class TileCrystalLens extends TileTransmissionBase {

    private CrystalProperties properties;

    public CrystalProperties getCrystalProperties() {
        return properties;
    }

    public void onPlace(CrystalProperties properties) {
        this.properties = properties;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.properties = CrystalProperties.readFromNBT(compound);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        this.properties.writeToNBT(compound);
    }

    @Override
    public void onLinkCreate(EntityPlayer player, BlockPos other) {
        super.onLinkCreate(player, other);

    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockLens.name";
    }

    @Override
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new CrystalTransmissionNode(at, getCrystalProperties());
    }
}
