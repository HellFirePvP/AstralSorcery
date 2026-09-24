/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.PerkLevelManager;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkDataType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.RootPerk;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocation;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationType;
import hellfirepvp.astralsorcery.common.research.perk.PerkRemovalResult;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.codec.HashComparedCodec;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayerPerkData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PlayerPerkData {

    public static final Codec<PlayerPerkData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SetCodec.of(ResourceLocation.CODEC).fieldOf("pointTokens").forGetter(PlayerPerkData::getPointTokens),
            Codec.unboundedMap(ResourceLocation.CODEC, AppliedPerkData.CODEC).fieldOf("perks").forGetter(PlayerPerkData::getSerializablePerks),
            Codec.DOUBLE.fieldOf("perkExp").forGetter(PlayerPerkData::getPerkExp)
    ).apply(inst, PlayerPerkData::resolveServerPerkData));
    public static final Codec<PlayerPerkData> PERKTREE_HASH_CODEC =
            HashComparedCodec.wrap(CODEC,
                    () -> PerkTree.getInstance().getVersion(LogicalSide.SERVER).orElse(0L),
                    () -> {
                        AstralSorcery.LOG.warn("Clearing player perk tree as player version was outdated.");
                        return PlayerPerkData.blankData();
                    });

    private final Set<ResourceLocation> pointTokens = new HashSet<>();
    private final Map<AbstractPerk<?>, AppliedPerkData<?>> perks = new HashMap<>();
    private double perkExp;

    PlayerPerkData(Set<ResourceLocation> pointTokens, Map<AbstractPerk<?>, AppliedPerkData<?>> perks, double perkExp) {
        this.pointTokens.addAll(pointTokens);
        this.perks.putAll(perks);
        this.perkExp = perkExp;
    }

    static PlayerPerkData resolveServerPerkData(Set<ResourceLocation> pointTokens, Map<ResourceLocation, AppliedPerkData<?>> perkReferences, double perkExp) {
        Map<AbstractPerk<?>, AppliedPerkData<?>> resolvedPerks = new HashMap<>();
        perkReferences.forEach((ref, data) -> {
            PerkTree.getInstance().getPerk(LogicalSide.SERVER, ref).ifPresent(perk -> {
                resolvedPerks.put(perk, data);
            });
        });
        return new PlayerPerkData(pointTokens, resolvedPerks, perkExp);
    }

    public static PlayerPerkData blankData() {
        return new PlayerPerkData(Collections.emptySet(), Collections.emptyMap(), 0);
    }

    // Roundabout way to avoid dealing with partial-success deserialization in the codec
    // before the hash-based comparison makes it fail later
    // Unknown perks will cause a reset, but triggering that separately after "just" reading the hash comparison
    // value isn't a thing, so perks are saved/read as just their keys
    private Map<ResourceLocation, AppliedPerkData<?>> getSerializablePerks() {
        return MiscUtil.cast(MapStream.of(this.getPerks())
                .mapKey(AbstractPerk::getKey)
                .toMap());
    }

    // *************************** PERK BATCH ***************************

    public Collection<AbstractPerk<?>> getSealedPerks() {
        return MapStream.of(this.perks)
                .filterValue(data -> data.getPerkData().isSealed())
                .keyStream()
                .toList();
    }

    public Collection<AbstractPerk<?>> getEffectGrantingPerks() {
        return MapStream.of(this.perks)
                .filterValue(data -> !data.getPerkData().isSealed())
                .keyStream()
                .toList();
    }

    public Collection<AbstractPerk<?>> getAllocatedPerks(PerkAllocationType type) {
        return MapStream.of(this.perks)
                .filterValue(data -> data.isAllocated(type))
                .keyStream()
                .toList();
    }

    // *************************** PERKS ***************************

    public boolean hasPerkEffect(Predicate<AbstractPerk<?>> perkMatch) {
        return this.hasPerkAllocation(perkMatch) && !this.isPerkSealed(perkMatch);
    }

    public boolean hasPerkEffect(AbstractPerk<?> perk) {
        return this.hasPerkAllocation(perk) && !this.isPerkSealed(perk);
    }

    public boolean hasPerkAllocation(Predicate<AbstractPerk<?>> perkMatch) {
        return this.findAppliedPerk(perkMatch).isPresent();
    }

    public boolean hasPerkAllocation(AbstractPerk<?> perk) {
        return this.findAppliedPerk(perk).isPresent();
    }

    public boolean hasPerkAllocationGrantingConnections(AbstractPerk<?> perk) {
        return this.findAppliedPerk(perk)
                .map(AppliedPerkData::grantsConnectivity)
                .orElse(false);
    }

    public boolean hasPerkAllocation(AbstractPerk<?> perk, PerkAllocationType type) {
        return this.findAppliedPerk(perk)
                .map(appliedPerk -> appliedPerk.isAllocated(type))
                .orElse(false);
    }

    protected boolean canSealPerk(AbstractPerk<?> perk) {
        return !this.isPerkSealed(perk) && this.hasPerkAllocation(perk);
    }

    public boolean isPerkSealed(AbstractPerk<?> perk) {
        return this.findAppliedPerk(perk)
                .map(AppliedPerkData::getPerkData)
                .map(AbstractPerk.Data::isSealed)
                .orElse(false);
    }

    public boolean isPerkSealed(Predicate<AbstractPerk<?>> perkMatch) {
        return this.findAppliedPerk(perkMatch)
                .map(AppliedPerkData::getPerkData)
                .map(AbstractPerk.Data::isSealed)
                .orElse(false);
    }

    protected boolean sealPerk(AbstractPerk<?> perk) {
        if (!this.canSealPerk(perk)) {
            return false;
        }
        return this.modifyPerkData(perk, data -> data.setSealed(true));
    }

    protected boolean breakSeal(AbstractPerk<?> perk) {
        if (this.canSealPerk(perk)) {
            return false;
        }
        return this.modifyPerkData(perk, data -> data.setSealed(false));
    }

    public boolean applyPerkAllocation(AbstractPerk<?> perk, PerkAllocation allocation, boolean simulate) {
        if (simulate && !this.perks.containsKey(perk)) {
            return true;
        }
        AppliedPerkData<?> appliedPerk = this.perks.computeIfAbsent(perk, p -> AppliedPerkData.create(p.getType().dataType()));
        return appliedPerk.addAllocation(allocation, simulate);
    }

    public PerkRemovalResult removePerkAllocation(AbstractPerk<?> perk, PerkAllocation allocation, boolean simulate) {
        AppliedPerkData<?> appliedPerk = this.perks.get(perk);
        if (appliedPerk == null) {
            return PerkRemovalResult.FAILURE;
        }
        if (appliedPerk.isAllocated(allocation.getType())) {
            PerkRemovalResult result = appliedPerk.removeAllocation(allocation, simulate);
            if (result.isFailure()) {
                return result;
            }

            if (!simulate && result == PerkRemovalResult.REMOVE_PERK) {
                this.perks.remove(perk);
            }
            return result;
        }
        return PerkRemovalResult.FAILURE;
    }

    public Collection<AbstractPerk<?>> getDependentPerks(PlayerProgress progress, AbstractPerk<?> perk, RootPerk<?> rootPerk, LogicalSide side) {
        Set<AbstractPerk<?>> allocated = new HashSet<>(this.getAllocatedPerks(PerkAllocationType.UNLOCKED));
        allocated.addAll(this.getAllocatedPerks(PerkAllocationType.UNLOCKED_NON_CONNECT));

        if (!allocated.contains(perk) || !allocated.contains(rootPerk)) {
            return Collections.emptySet();
        }

        Set<AbstractPerk<?>> rootReachable = new HashSet<>();
        rootReachable.add(rootPerk);

        Deque<AbstractPerk<?>> queue = new LinkedList<>();
        queue.add(rootPerk);
        while (!queue.isEmpty()) {
            AbstractPerk<?> step = queue.removeFirst();
            for (AbstractPerk<?> neighbour : step.getConnectedPerks(progress, side, false)) {
                if (neighbour == perk) continue;
                if (allocated.contains(neighbour) && rootReachable.add(neighbour)) {
                    queue.add(neighbour);
                }
            }
        }

        Set<AbstractPerk<?>> dependents = new HashSet<>(allocated);
        dependents.removeAll(rootReachable);
        dependents.remove(perk);
        dependents.addAll(perk.getAlwaysDependentPerks(progress, side));
        return Collections.unmodifiableSet(dependents);
    }

    public Collection<PerkAllocationType> getAllocationTypes(AbstractPerk<?> perk) {
        return this.findAppliedPerk(perk)
                .map(AppliedPerkData::getApplicationTypes)
                .orElse(Collections.emptySet());
    }

    public Map<AbstractPerk<?>, AppliedPerkData<?>> getPerks() {
        return Collections.unmodifiableMap(this.perks);
    }

    public <D extends AbstractPerk.Data> Optional<D> getPerkData(AbstractPerk<D> perk) {
        return this.getData(perk).map(AppliedPerkData::getPerkData).map(AbstractPerk.Data::copy);
    }

    private <D extends AbstractPerk.Data> boolean modifyPerkData(AbstractPerk<D> perk, Consumer<D> dataFn) {
        return this.findAppliedPerk(perk)
                .map(appliedPerk -> {
                    dataFn.accept(appliedPerk.getPerkData());
                    return true;
                })
                .orElse(false);
    }

    public <D extends AbstractPerk.Data> boolean updateAllocatedPerkData(AbstractPerk<D> perk, D data) {
        if (!this.hasPerkAllocation(perk)) return false;
        return this.findAppliedPerk(perk)
                .map(perkData -> {
                    perkData.setPerkData(data);
                    return true;
                }).orElse(false);
    }

    private <D extends AbstractPerk.Data> Optional<AppliedPerkData<D>> getData(AbstractPerk<D> perk) {
        return Optional.ofNullable(MiscUtil.cast(this.perks.get(perk)));
    }

    private <D extends AbstractPerk.Data> Optional<AppliedPerkData<D>> findAppliedPerk(AbstractPerk<D> perk) {
        return MiscUtil.cast(Optional.ofNullable(this.perks.get(perk)));
    }

    private <D extends AbstractPerk.Data> Optional<AppliedPerkData<D>> findAppliedPerk(Predicate<AbstractPerk<?>> perkFilter) {
        return MapStream.of(this.perks)
                .filterKey(perkFilter)
                .valueStream()
                .findFirst()
                .map(MiscUtil::cast);
    }

    // *************************** POINTS ***************************

    public Set<ResourceLocation> getPointTokens() {
        return Collections.unmodifiableSet(this.pointTokens);
    }

    protected boolean grantFreeAllocationPoint(ResourceLocation freePointToken) {
        if (this.pointTokens.contains(freePointToken)) {
            return false;
        }
        this.pointTokens.add(freePointToken);
        return true;
    }

    protected boolean removeAllocationPoint(ResourceLocation token) {
        return this.pointTokens.remove(token);
    }

    public int getAvailablePerkPoints(Player player, LogicalSide side) {
        int allocatedPerks = (int) this.perks.values().stream()
                .filter(perk -> perk.isAllocated(PerkAllocationType.UNLOCKED) || perk.isAllocated(PerkAllocationType.UNLOCKED_NON_CONNECT))
                .count() - 1;
        int allocationLevels = PerkLevelManager.getInstance().getLevel(getPerkExp(), player, side);
        return (allocationLevels + this.pointTokens.size()) - allocatedPerks;
    }

    public boolean hasFreeAllocationPoint(Player player, LogicalSide side) {
        return getAvailablePerkPoints(player, side) > 0;
    }

    // *************************** EXP ***************************

    public double getPerkExp() {
        return this.perkExp;
    }

    public int getPerkLevel(@Nullable Player player, LogicalSide side) {
        return PerkLevelManager.getInstance().getLevel(getPerkExp(), player, side);
    }

    public float getPercentToNextLevel(@Nullable Player player, LogicalSide side) {
        return PerkLevelManager.getInstance().getNextLevelPercent(getPerkExp(), player, side);
    }

    protected void modifyExp(@Nullable ServerPlayer player, double exp) {
        PerkLevelManager mgr = PerkLevelManager.getInstance();
        int currLevel = mgr.getLevel(getPerkExp(), player, LogicalSide.SERVER);
        if (exp >= 0 && currLevel >= mgr.getMaxLevel(LogicalSide.SERVER, player)) {
            return;
        }
        long expThisLevel = mgr.getExpForLevel(currLevel, player, LogicalSide.SERVER);
        long expNextLevel = mgr.getExpForLevel(currLevel + 1, player, LogicalSide.SERVER);
        long cap = Mth.lfloor(((float) (expNextLevel - expThisLevel)) * 0.08F);
        if (exp > cap) {
            exp = cap;
        }

        this.perkExp = Math.max(this.perkExp + exp, 0);
    }

    protected void setExp(double exp) {
        this.perkExp = Math.max(exp, 0);
    }

    public static class AppliedPerkData<D extends AbstractPerk.Data> {

        public static final Codec<AppliedPerkData<?>> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                PerkDataType.DATA_CODEC.fieldOf("perkData").forGetter(AppliedPerkData::getPerkData),
                SetCodec.of(CodecUtil.enumCodec(PerkAllocationType.class))
                        .fieldOf("applicationTypes").forGetter(AppliedPerkData::getApplicationTypes),
                Codec.unboundedMap(StringRepresentable.fromEnum(PerkAllocationType::values), Codec.list(CodecUtil.uuidCodec()))
                        .fieldOf("usedAllocationTokens").forGetter(d -> d.usedAllocationTokens)
        ).apply(inst, AppliedPerkData::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, AppliedPerkData<?>> STREAM_CODEC = StreamCodec.composite(
                PerkDataType.DATA_SYNC_CODEC,
                AppliedPerkData::getPerkData,
                CodecUtil.enumStreamCodec(PerkAllocationType.class).apply(SetCodec.streamOp()),
                AppliedPerkData::getApplicationTypes,
                ByteBufCodecs.map(size -> new HashMap<>(),
                        CodecUtil.enumStreamCodec(PerkAllocationType.class),
                        CodecUtil.uuidStreamCodec().apply(ByteBufCodecs.list())),
                AppliedPerkData::getUsedAllocationTokens,
                AppliedPerkData::new
        );

        private D perkData;
        private final Set<PerkAllocationType> applicationTypes = new HashSet<>();
        private final Map<PerkAllocationType, List<UUID>> usedAllocationTokens = new HashMap<>();

        private AppliedPerkData(D perkData, Set<PerkAllocationType> applicationTypes, Map<PerkAllocationType, List<UUID>> usedAllocationTokens) {
            this.perkData = perkData;
            this.applicationTypes.addAll(applicationTypes);
            usedAllocationTokens.forEach((type, ids) -> {
                this.usedAllocationTokens.put(type, new ArrayList<>(ids));
            });
        }

        private static <D extends AbstractPerk.Data> AppliedPerkData<D> create(PerkDataType<D> dataType) {
            return new AppliedPerkData<>(dataType.emptyDataFn().get(), Collections.emptySet(), Collections.emptyMap());
        }

        public AppliedPerkData<D> copy() {
            return new AppliedPerkData<>(this.getPerkData().copy(), this.getApplicationTypes(), this.getUsedAllocationTokens());
        }

        public D getPerkData() {
            return this.perkData;
        }

        private void setPerkData(D data) {
            this.perkData = data;
        }

        public Set<PerkAllocationType> getApplicationTypes() {
            return Collections.unmodifiableSet(this.applicationTypes);
        }

        private Map<PerkAllocationType, List<UUID>> getUsedAllocationTokens() {
            return Collections.unmodifiableMap(this.usedAllocationTokens);
        }

        private int getTotalAllocationCount() {
            int sum = 0;
            for (PerkAllocationType type : PerkAllocationType.values()) {
                sum += getAllocationCount(type);
            }
            return sum;
        }

        private int getAllocationCount(PerkAllocationType type) {
            return this.usedAllocationTokens.getOrDefault(type, Collections.emptyList()).size();
        }

        public boolean isAllocated(PerkAllocationType type) {
            return this.applicationTypes.contains(type);
        }

        public boolean grantsConnectivity() {
            return this.applicationTypes.stream().anyMatch(PerkAllocationType::hasConnectivity);
        }

        private PerkRemovalResult removeAllocation(PerkAllocation alloc, boolean simulate) {
            List<UUID> allocations = this.usedAllocationTokens.getOrDefault(alloc.getType(), new ArrayList<>());
            if (!allocations.contains(alloc.getLockUUID())) {
                return PerkRemovalResult.FAILURE;
            }
            if (!simulate && !allocations.remove(alloc.getLockUUID())) {
                return PerkRemovalResult.FAILURE;
            }

            if (simulate) {
                if (allocations.size() == 1) {
                    if (this.applicationTypes.size() > 1) {
                        return PerkRemovalResult.REMOVE_ALLOCATION_TYPE;
                    } else {
                        return PerkRemovalResult.REMOVE_PERK;
                    }
                }
                return PerkRemovalResult.REMOVE_ALLOCATION;
            }

            if (allocations.isEmpty()) {
                this.applicationTypes.remove(alloc.getType());
                if (this.applicationTypes.isEmpty()) {
                    return PerkRemovalResult.REMOVE_PERK;
                }
                return PerkRemovalResult.REMOVE_ALLOCATION_TYPE;
            }
            return PerkRemovalResult.REMOVE_ALLOCATION;
        }

        private boolean addAllocation(PerkAllocation alloc, boolean simulate) {
            if (!simulate) {
                this.applicationTypes.add(alloc.getType());
            }

            List<UUID> allocations = this.usedAllocationTokens.computeIfAbsent(alloc.getType(), t -> new ArrayList<>());
            if (allocations.contains(alloc.getLockUUID())) {
                return false;
            }
            if (!simulate) {
                return allocations.add(alloc.getLockUUID());
            }
            return true;
        }
    }
}
