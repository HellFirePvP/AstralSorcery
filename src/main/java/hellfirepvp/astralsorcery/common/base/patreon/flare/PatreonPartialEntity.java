/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.flare;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonPartialEntity
 * Created by HellFirePvP
 * Date: 14.07.2018 / 18:22
 */
public abstract class PatreonPartialEntity {

    protected static final Random rand = new Random();
    private static int counter = 0;

    private final UUID ownerUUID;
    private int id;

    protected Vector3 pos = new Vector3(), prevPos = new Vector3();
    protected Vector3 motion = new Vector3();
    protected boolean removed = false, updatePos = false;

    private Integer lastTickedDim = null;

    public PatreonPartialEntity(UUID ownerUUID) {
        this.id = counter;
        counter++;
        this.ownerUUID = ownerUUID;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public int getId() {
        return id;
    }

    public void setPositionNear(EntityPlayer player) {
        this.pos = Vector3.atEntityCenter(player).setY(player.posY).addY(player.height).add(Vector3.random().setY(0).normalize());
        this.prevPos = this.pos.clone();
        this.motion = new Vector3();
        this.updatePos = true;
    }

    @Nullable
    public Integer getLastTickedDim() {
        return lastTickedDim;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean update(World world) {
        boolean changed = lastTickedDim == null || lastTickedDim != world.provider.getDimension();
        lastTickedDim = world.provider.getDimension();

        if (trySetMoveTarget(world)) {
            changed = true;
        }

        if (tryMoveEntity(world)) {
            changed = true;
        }

        if (world.isRemote) {
            spawnEffects();
        }

        return changed;
    }

    @SideOnly(Side.CLIENT)
    protected void spawnEffects() {}

    private boolean trySetMoveTarget(World world) {
        Vector3 prevMot = this.motion.clone();

        EntityPlayer target = findOwner(world);
        if (target == null) {
            this.motion = new Vector3();
        } else {
            Vector3 moveTarget = Vector3.atEntityCenter(target).addY(1.5);
            if (moveTarget.distanceSquared(this.pos) <= 3D) {
                this.motion.multiply(0.95F);
            } else {
                double diffX = (moveTarget.getX() - pos.getX()) / 8;
                double diffY = (moveTarget.getY() - pos.getY()) / 8;
                double diffZ = (moveTarget.getZ() - pos.getZ()) / 8;
                double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
                this.motion = new Vector3(diffX * dist, diffY * dist, diffZ * dist);
            }
        }
        return !this.motion.equals(prevMot);
    }

    private boolean tryMoveEntity(World world) {
        this.prevPos = this.pos.clone();

        EntityPlayer owner = findOwner(world);
        if (owner != null && this.pos.distance(Vector3.atEntityCenter(owner)) >= 16) {
            setPositionNear(owner);
            return true;
        }
        this.pos.add(this.motion);
        return !this.pos.equals(this.prevPos);
    }

    public Vector3 getPos() {
        return pos.clone();
    }

    public Vector3 getPrevPos() {
        return prevPos.clone();
    }

    @Nullable
    public EntityPlayer findOwner(World world) {
        return world.getPlayerEntityByUUID(this.ownerUUID);
    }

    @SideOnly(Side.CLIENT)
    public void tickInRenderDistance() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof PatreonPartialEntity)) return false;
        PatreonPartialEntity that = (PatreonPartialEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void readFromNBT(NBTTagCompound cmp) {
        this.id = cmp.getInteger("flareID");
        this.lastTickedDim = cmp.getInteger("lastTickedDim");
        if (cmp.hasKey("pos") && cmp.hasKey("prevPos")) {
            this.pos = NBTHelper.readVector3(cmp.getCompoundTag("pos"));
            this.prevPos = NBTHelper.readVector3(cmp.getCompoundTag("prevPos"));
        }
    }

    public void writeToNBT(NBTTagCompound cmp) {
        cmp.setInteger("flareID", this.id);
        cmp.setInteger("lastTickedDim", this.lastTickedDim == null ? 0 : this.lastTickedDim);
        if (updatePos) {
            cmp.setTag("pos", NBTHelper.writeVector3(this.pos));
            cmp.setTag("prevPos", NBTHelper.writeVector3(this.prevPos));
            updatePos = false;
        }
    }

}
