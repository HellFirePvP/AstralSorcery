package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.network.IStarlightSource;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
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
public class TileCollectorCrystal extends TileEntitySkybound implements IStarlightSource, ITickable {

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
        float distribution = CelestialHandler.getCurrentDistribution(getTransmittingType());
        float coll = usedCrystalProperties.getCollectionAmt(distribution);
        int newCharge = MathHelper.floor_float(coll);
        newCharge = Math.min(getMaxCharge(), charge + newCharge);
        if(Math.abs(charge - newCharge) > 0) {
            this.charge = newCharge;
            changed = true;
        }
        return changed;
    }

    public boolean canCharge() {
        return playerMade && doesSeeSky && !isFull() && getTransmittingType() != null;
    }

    public int getCharge() {
        return charge;
    }

    private boolean isFull() {
        return charge >= getMaxCharge();
    }

    public int getMaxCharge() {
        return playerMade ? PLAYER_DEF_BUFFER_SIZE : STRUCTURE_BUFFER_SIZE;
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

    public CrystalProperties getCrystalProperties() {
        return usedCrystalProperties;
    }

    public void onPlace(Constellation constellation, CrystalProperties properties, int charge, boolean player) {
        this.associatedType = constellation;
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
    public static void breakParticles(PktParticleEvent event) {}

    public static void breakDamage(World world, BlockPos pos) {}

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if(compound.hasKey("constellation")) {
            String type = compound.getString("constellation");
            Constellation c = ConstellationRegistry.getConstellationByName(type);
            if(c != null) {
                associatedType = c;
            } else {
                AstralSorcery.log.warn("Deserialized collector crystal without constellation?");
            }
        }

        this.playerMade = compound.getBoolean("player");
        this.charge = compound.getInteger("charge");
        this.usedCrystalProperties = CrystalProperties.readFromNBT(compound);
        System.out.println("read");
        System.out.println(getPos());
        System.out.println(playerMade);
        System.out.println(charge);
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
        System.out.println("write");
        System.out.println(getPos());
        System.out.println(playerMade);
        System.out.println(charge);
    }

}
