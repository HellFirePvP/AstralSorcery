package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:18
 */
public class TileAltar extends TileReceiverBase {

    private AltarLevel level = AltarLevel.DISCOVERY;
    private int experience = 0;

    public TileAltar() {}

    public TileAltar(AltarLevel level) {
        this.level = level;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.level = AltarLevel.values()[compound.getInteger("level")];
        this.experience = compound.getInteger("exp");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("level", level.ordinal());
        compound.setInteger("exp", experience);
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockAltar.general.name";
    }

    @Override
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new SimpleTransmissionReceiver(at);
    }

    public static enum AltarLevel {

        DISCOVERY(100),
        ATTENUATION(1000),
        CONSTELLATION_CRAFT(4000),
        TRAIT_CRAFT(12000),
        ENDGAME(-1);

        private final int totalExpNeeded;

        private AltarLevel(int levelExp) {
            this.totalExpNeeded = levelExp;
        }

        public int getTotalExpNeededForLevel() {
            return totalExpNeeded;
        }

        public boolean canLevel() {
            return totalExpNeeded > 0;
        }

    }

}
