package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.network.IStarlightSource;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:25
 */
public class TileCollectorCrystal extends TileEntitySynchronized implements IStarlightSource, ITickable {

    public static final int STRUCTURE_BUFFER_SIZE = 10000;
    public static final int PLAYER_DEF_BUFFER_SIZE = 300;

    private CrystalProperties usedCrystalProperties;
    private boolean playerMade;
    private Constellation associatedType;
    private int charge;

    public TileCollectorCrystal() {}

    @Override
    public void update() {

    }

    @Nullable
    @Override
    public Constellation getTransmittingType() {
        return associatedType;
    }

    @Override
    public int drain(Constellation type, int tryAmount) {
        return 0;
    }

    public boolean isPlayerMade() {
        return playerMade;
    }

    public void forceCharge(int charge) {
        this.charge = charge;
    }

    public void onPlace(Constellation constellation, CrystalProperties properties, boolean player) {
        this.associatedType = constellation;
        this.playerMade = player;
        this.usedCrystalProperties = properties;
        markDirty();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if(compound.hasKey("constellation")) {
            String type = compound.getString("constellation");
            Constellation c = ConstellationRegistry.getConstellationByName(type);
            if(c != null) {
                associatedType = c;
            } else {
                AstralSorcery.log.warn("deserialized collector crystal without constellation?");
            }
        }

        this.playerMade = compound.getBoolean("player");
        this.charge = compound.getInteger("charge");
        this.usedCrystalProperties = CrystalProperties.readFromNBT(compound);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if(associatedType != null) {
            compound.setString("constellation", associatedType.getName());
        }
        compound.setBoolean("player", playerMade);
        compound.setInteger("charge", charge);
        usedCrystalProperties.writeToNBT(compound);
    }

}
