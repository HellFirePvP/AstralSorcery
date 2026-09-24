/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.key;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.*;
import hellfirepvp.astralsorcery.common.perk.tree.point.MajorPerkTreePoint;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerPerkData;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocation;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationType;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import java.nio.charset.StandardCharsets;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkTreeConnector
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkTreeConnector extends AttributeModifierPerk<KeyPerkTreeConnector.Data> {

    public static final MapCodec<KeyPerkTreeConnector> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkTreeConnector::new));
    public static final PerkType<KeyPerkTreeConnector> TYPE =
            PerkType.of(KeyPerkTreeConnector.CODEC, PerkDataTypesAS.KEY_TREE_CONNECTOR_DATA, KeyPerkTreeConnector::new);

    private KeyPerkTreeConnector(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.EPIPHANY, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkTreeConnector(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, Player player) {
        if (!super.mayUnlockPerk(progress, player)) {
            return false;
        }

        LogicalSide side = this.getSide(player);
        PlayerPerkData perkData = progress.getPerkData();
        if (perkData.getPerks().keySet().stream()
                .filter(perkData::hasPerkAllocationGrantingConnections)
                .anyMatch(otherPerk -> otherPerk instanceof KeyPerkTreeConnector)) {
            return true;
        }

        boolean hasAllAdjacent = true;
        for (AbstractPerk<?> perk : this.getConnectedPerks(progress, side, true)) {
            if (!perkData.hasPerkAllocationGrantingConnections(perk)) {
                hasAllAdjacent = false;
                break;
            }
        }
        return hasAllAdjacent;
    }

    @Override
    public Optional<PerkAllocation> providesAllocation(PlayerProgress progress, Player player, AbstractPerk<?> otherPerk) {
        Optional<PerkAllocation> existingAlloc = super.providesAllocation(progress, player, otherPerk);
        if (existingAlloc.isPresent()) return existingAlloc;

        if (!(otherPerk instanceof KeyPerkTreeConnector)) return Optional.empty();
        PlayerPerkData perkData = progress.getPerkData();
        if (perkData.hasPerkAllocationGrantingConnections(this)) {
            return Optional.of(PerkAllocation.unlock());
        }
        return Optional.empty();
    }

    @Override
    public void onUnlockPerkServer(@Nullable Player player, PerkAllocationType allocation, PlayerProgress progress, Data data) {
        super.onUnlockPerkServer(player, allocation, progress, data);

        if (allocation == PerkAllocationType.UNLOCKED && player instanceof ServerPlayer sPlayer) {
            for (AbstractPerk<?> otherPerk : this.getConnectedPerks(progress, LogicalSide.SERVER, true)) {
                ResourceLocation perkToken = AstralSorcery.key("connector_tk_" + otherPerk.getKey().getPath());
                UUID tokenId = this.seededId(perkToken);
                ResearchHelper.applyPerk(sPlayer, otherPerk, PerkAllocation.grantedConnections(tokenId));
            }
        }
    }

    @Override
    public void onRemovePerkServer(ServerPlayer player, PerkAllocationType allocation, PlayerProgress progress, Data data) {
        super.onRemovePerkServer(player, allocation, progress, data);

        if (allocation == PerkAllocationType.UNLOCKED || allocation == PerkAllocationType.UNLOCKED_NON_CONNECT) {
            for (AbstractPerk<?> otherPerk : this.getConnectedPerks(progress, LogicalSide.SERVER, true)) {
                ResourceLocation perkToken = AstralSorcery.key("connector_tk_" + otherPerk.getKey().getPath());
                UUID tokenId = this.seededId(perkToken);
                ResearchHelper.removePerk(player, otherPerk, PerkAllocation.grantedConnections(tokenId));
            }
        }
    }
    
    private UUID seededId(ResourceLocation token) {
        UUID baseV3 = UUID.nameUUIDFromBytes(token.toString().getBytes(StandardCharsets.UTF_8));
        long seed = baseV3.getMostSignificantBits() ^ Long.rotateLeft(baseV3.getLeastSignificantBits(), 31);
        RandomSource rand = RandomSource.create(seed);
        return new UUID(rand.nextLong(), rand.nextLong());
    }

    @Override
    protected PerkTreePoint<?> initPerkTreePoint() {
        return new MajorPerkTreePoint<>(this.getOffset(), this);
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    @Override
    public Collection<AbstractPerk<?>> getConnectedPerks(PlayerProgress progress, LogicalSide side, boolean direct) {
        Set<AbstractPerk<?>> connected = new HashSet<>(super.getConnectedPerks(progress, side, direct));
        if (direct) return connected;

        // If not direct, all other perk tree connectors are also "connected"
        PerkTree.getInstance().getPerkPoints(side).stream()
                .map(PerkTreePoint::getPerk)
                .filter(perk -> perk instanceof KeyPerkTreeConnector)
                .filter(perk -> perk != this)
                .forEach(connected::add);

        // If not direct and this is not a normally-allocated connector, all delegates' connected nodes count as connected to the connector
        PerkTree.getInstance().getConnectedPerks(side, this).stream()
                .filter(perk -> perk instanceof KeyPerkTreeConnectorDelegate)
                .map(perk -> (KeyPerkTreeConnectorDelegate) perk)
                .filter(delegate -> delegate.isDelegateAllocated(side, progress.getPerkData()))
                .forEach(delegate -> {
                    connected.remove(delegate);
                    PerkTree.getInstance().getConnectedPerks(side, delegate).stream()
                            .filter(perk -> !(perk instanceof KeyPerkTreeConnectorDelegate))
                            .filter(perk -> !(perk instanceof KeyPerkTreeConnector))
                            .forEach(connected::add);
                });
        return connected;
    }

    public static class Data extends AbstractPerk.Data {

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(inst -> perkTokenFields(inst).apply(inst, Data::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> SYNC_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::isSealed,
                ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
                Data::getPerkTokens,
                Data::new
        );

        private static <T extends Data> Products.P2<RecordCodecBuilder.Mu<T>, Boolean, List<ResourceLocation>> perkTokenFields(RecordCodecBuilder.Instance<T> instance) {
            return dataFields(instance).and(
                    ResourceLocation.CODEC.listOf().fieldOf("perkTokens").forGetter(Data::getPerkTokens)
            );
        }

        protected List<ResourceLocation> perkTokens = new ArrayList<>();

        protected Data(boolean sealed, List<ResourceLocation> perkTokens) {
            super(sealed);
            this.perkTokens.addAll(perkTokens);
        }

        public <T extends AbstractPerk.Data> T copy() {
            return MiscUtil.cast(new Data(this.isSealed(), List.copyOf(this.getPerkTokens())));
        }

        public static Data create() {
            return new Data(false, List.of());
        }

        @Override
        public PerkDataType<?> getType() {
            return PerkDataTypesAS.KEY_TREE_CONNECTOR_DATA.get();
        }

        public List<ResourceLocation> getPerkTokens() {
            return Collections.unmodifiableList(this.perkTokens);
        }

        private void addToken(ResourceLocation perk) {
            this.perkTokens.add(perk);
        }

        private void clearTokens() {
            this.perkTokens.clear();
        }
    }
}
