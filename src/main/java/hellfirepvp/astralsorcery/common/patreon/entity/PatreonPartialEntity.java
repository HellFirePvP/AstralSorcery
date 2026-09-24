/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon.entity;

import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.common.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonPartialEntity
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatreonPartialEntity {

    protected final RandomSource rand = RandomSource.create();

    private final UUID ownerUUID, effectUUID;

    protected Vector3 pos = new Vector3(), prevPos = new Vector3();
    protected Vector3 motion = new Vector3();
    protected boolean removed = false;

    protected ResourceKey<Level> lastTickedDimension = null;

    public PatreonPartialEntity(UUID effectUUID, UUID ownerUUID) {
        this.effectUUID = effectUUID;
        this.ownerUUID = ownerUUID;
    }

    public final UUID getEffectUUID() {
        return this.effectUUID;
    }

    public final UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public Vector3 getPosition() {
        return this.pos.copy();
    }

    public Vector3 getPreviousPosition() {
        return this.prevPos.copy();
    }

    public Vector3 getInterpolatedPosition(float partialTicks) {
        return RenderVectorUtil.interpolate(this.prevPos, this.pos, partialTicks);
    }

    public Vector3 getMotion() {
        return this.motion;
    }

    public ResourceKey<Level> getLastTickedDimension() {
        return this.lastTickedDimension;
    }

    public void setRemoved() {
        this.removed = true;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    @Nullable
    public PatreonEffect getEffect() {
        return PatreonEffectHelper.getEffect(this.getEffectUUID());
    }

    public void placeNear(Player player) {
        this.pos = new Vector3(player)
                .setY(player.getY())
                .addY(player.getBbHeight())
                .add(Vector3.random(this.rand).setY(0).normalize());
        this.prevPos = this.pos.copy();
        this.motion = new Vector3();
    }

    public boolean tick(Level level) {
        boolean changed = this.lastTickedDimension == null || !this.lastTickedDimension.equals(level.dimension());
        this.lastTickedDimension = level.dimension();

        if (this.updateMovement(level)) changed = true;
        if (this.move(level)) changed = true;

        return changed;
    }

    protected boolean updateMovement(Level level) {
        Vector3 prevMot = this.motion.copy();

        Player target = this.findOwner(level);
        if (target == null) {
            this.motion = new Vector3();
        } else {
            Vector3 moveTarget = new Vector3(target).addY(2.5);
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

    protected boolean move(Level level) {
        this.prevPos = this.pos.copy();

        Player owner = this.findOwner(level);
        if (owner != null && this.pos.distance(new Vector3(owner)) >= 25) {
            this.placeNear(owner);
            return true;
        }
        this.pos.add(this.motion);
        return !this.pos.equals(this.prevPos);
    }

    @Nullable
    public Player findOwner(Level level) {
        return level.getPlayerByUUID(this.ownerUUID);
    }

    public final UpdateInfo getUpdateInfo() {
        return new UpdateInfo(this.getLastTickedDimension(), this.getPosition(), this.getPreviousPosition());
    }

    public final void applyUpdateInfo(UpdateInfo info) {
        this.lastTickedDimension = info.lastTickedDimension();
        this.pos = info.position();
        this.prevPos = info.previousPosition();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PatreonPartialEntity that = (PatreonPartialEntity) o;
        return Objects.equals(this.effectUUID, that.effectUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.effectUUID);
    }

    public record Provider(Function<UUID, PatreonPartialEntity> serverProvider,
                           Function<UUID, PatreonPartialEntity> clientProvider) {

    }

    public record UpdateInfo(ResourceKey<Level> lastTickedDimension,
                             Vector3 position,
                             Vector3 previousPosition) {

        public static final StreamCodec<RegistryFriendlyByteBuf, UpdateInfo> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.DIMENSION),
                UpdateInfo::lastTickedDimension,
                Vector3.STREAM_CODEC,
                UpdateInfo::position,
                Vector3.STREAM_CODEC,
                UpdateInfo::previousPosition,
                UpdateInfo::new);
    }
}
