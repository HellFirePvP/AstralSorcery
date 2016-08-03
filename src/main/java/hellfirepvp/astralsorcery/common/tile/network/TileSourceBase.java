package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.util.link.ILinkableTile;
import hellfirepvp.astralsorcery.common.util.NBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileSourceBase
 * Created by HellFirePvP
 * Date: 03.08.2016 / 17:48
 */
public abstract class TileSourceBase extends TileNetworkSkybound implements IStarlightSource, ILinkableTile {

    private Constellation sourceType = null;
    private List<BlockPos> positions = new LinkedList<>();

    @Override
    public void update() {
        super.update();
    }

    @Override
    public Constellation getSourceType() {
        return sourceType;
    }

    public void setSourceType(Constellation sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        positions.clear();

        if(compound.hasKey("linked")) {
            NBTTagList list = compound.getTagList("linked", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                positions.add(NBTUtils.readBlockPosFromNBT(tag));
            }
        }

        if(compound.hasKey("constellation")) {
            String type = compound.getString("constellation");
            Constellation c = ConstellationRegistry.getConstellationByName(type);
            if(c != null) {
                setSourceType(c);
            } else {
                AstralSorcery.log.warn("Deserialized starlight source without constellation?");
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        NBTTagList list = new NBTTagList();
        for (BlockPos pos : positions) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(pos, tag);
            list.appendTag(tag);
        }
        compound.setTag("linked", list);
        if(getSourceType() != null) {
            compound.setString("constellation", getSourceType().getName());
        }
    }

    //TODO ugh. do.
    @Override
    public void onLinkCreate(EntityPlayer player, BlockPos other) {

    }

    @Override
    public boolean tryLink(EntityPlayer player, BlockPos other) {
        return false;
    }

    @Override
    public boolean tryUnlink(EntityPlayer player, BlockPos other) {
        return false;
    }

    @Override
    public List<BlockPos> getLinkedPositions() {
        return positions;
    }

    @Override
    public boolean canConduct(@Nonnull Constellation type) {
        return sourceType != null && type.equals(sourceType);
    }

}
