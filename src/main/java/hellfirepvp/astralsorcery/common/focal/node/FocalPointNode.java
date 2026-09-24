/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.focal.node;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileStarlightFocusCrystal;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.ColumnPos;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class FocalPointNode {

    public static final Codec<FocalPointNode> CODEC = RegistriesAS.REGISTRY_FOCAL_NODE_TYPES.byNameCodec()
            .dispatch(FocalPointNode::getType, FocalPointNode.Type::codec);

    public static final StreamCodec<RegistryFriendlyByteBuf, FocalPointNode> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_FOCAL_NODE_TYPES)
            .dispatch(FocalPointNode::getType, FocalPointNode.Type::syncCodec);

    private final ColumnPos pos;
    private final BaseConstellation constellation;
    private long ticksExisted;
    private BlockPos focalPosition;

    public FocalPointNode(ColumnPos pos, BaseConstellation constellation, long ticksExisted, Optional<BlockPos> focalPosition) {
        this.pos = pos;
        this.constellation = constellation;
        this.ticksExisted = ticksExisted;
        this.focalPosition = focalPosition.orElse(null);
    }

    public void onLoad(ServerLevel sLevel) {}

    public void onUnload(ServerLevel sLevel) {}

    protected static <T extends FocalPointNode> Products.P4<RecordCodecBuilder.Mu<T>, ColumnPos, BaseConstellation, Long, Optional<BlockPos>> commonFields(RecordCodecBuilder.Instance<T> inst) {
        return inst.group(
                ColumnPos.CODEC.fieldOf("pos").forGetter(FocalPointNode::getPos),
                RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().fieldOf("constellation").forGetter(FocalPointNode::getConstellation),
                CodecUtil.defaulted(Codec.LONG, "ticks_existed", () -> 0L, FocalPointNode::getTicksExisted),
                BlockPos.CODEC.optionalFieldOf("focal_position").forGetter(FocalPointNode::getFocalPosition));
    }

    public ColumnPos getPos() {
        return this.pos;
    }

    public BaseConstellation getConstellation() {
        return this.constellation;
    }

    public long getTicksExisted() {
        return this.ticksExisted;
    }

    public void updateFocalPosition(Level level) {
        BlockPos validFocalPosition = MiscUtil.iterateTopDown(level, this.getPos().toBlockPos(0), pos -> {
            return MiscUtil.getTileAt(level, pos, TileStarlightFocusCrystal.class, true).isPresent();
        }).orElse(null);
        this.setFocalPosition(validFocalPosition);
    }

    protected void setFocalPosition(@Nullable BlockPos focalPosition) {
        this.focalPosition = focalPosition;
    }

    public Optional<BlockPos> getFocalPosition() {
        return Optional.ofNullable(this.focalPosition);
    }

    public void markDirty(Level level) {
        DataAS.DOMAIN_AS.getData(level, DataAS.KEY_FOCAL_POINT_DATA).markDirty(this.getPos().toBlockPos(0));
    }

    public void markForSync(ServerLevel level) {
        SyncDataManager.getInstance().getData(SyncDataTypesAS.FOCAL_POINT).markForUpdate(level, this);
    }

    public void tick(ServerLevel sLevel) {
        this.ticksExisted++;
    }

    @OnlyIn(Dist.CLIENT)
    public void tickEffects(ClientLevel level) {
        this.ticksExisted++;
    }

    public abstract Type<?> getType();

    public record Type<T extends FocalPointNode>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> syncCodec) {}
}
