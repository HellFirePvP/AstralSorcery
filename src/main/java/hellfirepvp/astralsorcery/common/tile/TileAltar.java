package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBase;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
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

    private void receiveStarlight(Constellation type, double amount) {
        
    }

    public AltarLevel getAltarLevel() {
        return level;
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
        return new TransmissionReceiverAltar(at);
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

    public static class TransmissionReceiverAltar extends SimpleTransmissionReceiver {

        public TransmissionReceiverAltar(@Nonnull BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, Constellation type, double amount) {
            if(isChunkLoaded) {
                TileAltar ta = MiscUtils.getTileAt(world, getPos(), TileAltar.class);
                if(ta != null) {
                    ta.receiveStarlight(type, amount);
                }
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new AltarReceiverProvider();
        }

    }

    public static class AltarReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverAltar provideEmptyNode() {
            return new TransmissionReceiverAltar(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverAltar";
        }

    }

}
