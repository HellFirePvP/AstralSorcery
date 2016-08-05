package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionSourceNode;
import hellfirepvp.astralsorcery.common.tile.base.TileSourceBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
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
public class TileCollectorCrystal extends TileSourceBase {

    private CrystalProperties usedCrystalProperties;
    private boolean playerMade;
    private Constellation associatedType;

    public boolean isPlayerMade() {
        return playerMade;
    }

    public CrystalProperties getCrystalProperties() {
        return usedCrystalProperties;
    }

    public Constellation getConstellation() {
        return associatedType;
    }

    public void onPlace(Constellation constellation, CrystalProperties properties, boolean player) {
        this.associatedType = constellation;
        this.playerMade = player;
        //this.charge = charge;
        this.usedCrystalProperties = properties;
        markDirty();
    }

    //TODO do. eventually. at some point. maybe.
    @SideOnly(Side.CLIENT)
    public static void breakParticles(PktParticleEvent event) {}

    public static void breakDamage(World world, BlockPos pos) {}

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.playerMade = compound.getBoolean("player");
        this.associatedType = Constellation.readFromNBT(compound);
        this.usedCrystalProperties = CrystalProperties.readFromNBT(compound);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("player", playerMade);
        associatedType.writeToNBT(compound);
        usedCrystalProperties.writeToNBT(compound);
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockCollectorCrystal.name";
    }

    @Override
    public IIndependentStarlightSource provideNewSourceNode() {
        return new IndependentCrystalSource(usedCrystalProperties, associatedType, doesSeeSky);
    }

    @Override
    public ITransmissionSource provideSourceNode(BlockPos at) {
        return new SimpleTransmissionSourceNode(at);
    }

}
