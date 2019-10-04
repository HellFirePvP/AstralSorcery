package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeTile;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialCrystals
 * Created by HellFirePvP
 * Date: 30.09.2019 / 18:17
 */
public class TileCelestialCrystals extends TileEntityTick implements CrystalAttributeTile {

    private CrystalAttributes attributes = null;

    public TileCelestialCrystals() {
        super(TileEntityTypesAS.CELESTIAL_CRYSTAL_CLUSTER);
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        if (this.attributes != null) {
            this.attributes.store(compound);
        } else {
            CrystalAttributes.storeNull(compound);
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.attributes = CrystalAttributes.getCrystalAttributes(compound);
    }

    @Nullable
    @Override
    public CrystalAttributes getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(@Nullable CrystalAttributes attributes) {
        this.attributes = attributes;
    }
}
