package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimplePrismTransmissionNode;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:25
 */
public class TileCollectorCrystal extends TileSourceBase implements IStarlightSource {

    public static final int STRUCTURE_BUFFER_SIZE = 1000;
    public static final int PLAYER_DEF_BUFFER_SIZE = 200;

    private CrystalProperties usedCrystalProperties;
    private boolean playerMade;
    private Constellation associatedType;
    private int charge;

    @Override
    public void update() {
        super.update();

        boolean changed = false;

        if(canCharge()) {
            changed = chargeStarlight(changed);
        }
        if(isPlayerMade()) {
            changed = naturalDischarge(changed);
        }

        if(changed) {
            markDirty();
        }
    }

    private boolean naturalDischarge(boolean changed) {
        if(charge >= 0) {
            float discharge = usedCrystalProperties.getDischargePerc();
            this.charge = MathHelper.floor_float(this.charge * discharge);
            changed = true;
        }
        return changed;
    }

    private boolean chargeStarlight(boolean changed) {
        float distribution = CelestialHandler.getCurrentDistribution(getSourceType());
        float coll = usedCrystalProperties.getCollectionAmt(distribution);
        int newCharge = MathHelper.floor_float(coll);
        newCharge = Math.min(getMaxCharge(), charge + newCharge);
        if(Math.abs(charge - newCharge) > 0) {
            this.charge = newCharge;
            changed = true;
        }
        return changed;
    }

    @Override
    public int tryDrain(Constellation type, int amount) {
        if(!canConduct(type)) return 0;
        if(charge >= amount) {
            this.charge -= amount;
            return amount;
        } else {
            int diff = this.charge;
            this.charge = 0;
            return diff;
        }
    }

    public boolean canCharge() {
        return playerMade && doesSeeSky && !isFull() && getSourceType() != null;
    }

    private boolean isFull() {
        return charge >= getMaxCharge();
    }

    public int getMaxCharge() {
        return playerMade ? PLAYER_DEF_BUFFER_SIZE : STRUCTURE_BUFFER_SIZE;
    }

    public boolean isPlayerMade() {
        return playerMade;
    }

    public CrystalProperties getCrystalProperties() {
        return usedCrystalProperties;
    }

    public void onPlace(Constellation constellation, CrystalProperties properties, int charge, boolean player) {
        setSourceType(constellation);
        this.playerMade = player;
        this.charge = charge;
        this.usedCrystalProperties = properties;
        markDirty();
    }

    @SideOnly(Side.CLIENT)
    public float getRenderPercFilled() {
        if(playerMade) return 1.0F; //Well...
        return 0.1F + (((float) charge) / ((float) STRUCTURE_BUFFER_SIZE)) * 0.9F;
    }

    //TODO do. eventually. at some point. maybe.
    @SideOnly(Side.CLIENT)
    public static void breakParticles(PktParticleEvent event) {
        System.out.println("info particle event at " + event.getPos());
    }

    public static void breakDamage(World world, BlockPos pos) {
        System.out.println("info particle event at " + pos);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.playerMade = compound.getBoolean("player");
        this.charge = compound.getInteger("charge");
        this.usedCrystalProperties = CrystalProperties.readFromNBT(compound);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("player", playerMade);
        compound.setInteger("charge", charge);
        usedCrystalProperties.writeToNBT(compound);
    }

    @Nullable
    @Override
    public String getUnlocalizedDisplayName() {
        return "tile.BlockCollectorCrystal.name";
    }

    @Override
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new SimplePrismTransmissionNode(at);
    }

}
