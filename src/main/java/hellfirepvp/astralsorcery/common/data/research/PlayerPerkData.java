/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncKnowledge;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkLevelManager;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerPerkData
 * Created by HellFirePvP
 * Date: 28.11.2020 / 15:05
 */
public class PlayerPerkData {

    private Set<ResourceLocation> freePointTokens = new HashSet<>();
    private Map<AbstractPerk, AppliedPerk> perks = new HashMap<>();
    private double perkExp = 0;

    public Collection<AbstractPerk> getSealedPerks() {
        return this.perks.values().stream()
                .filter(AppliedPerk::isSealed)
                .map(AppliedPerk::getPerk)
                .collect(Collectors.toList());
    }

    public Collection<AbstractPerk> getEffectGrantingPerks() {
        return this.perks.values().stream()
                .filter(appliedPerk -> !appliedPerk.isSealed())
                .map(AppliedPerk::getPerk)
                .collect(Collectors.toList());
    }

    public Collection<AbstractPerk> getAllocatedPerks(PerkAllocationType type) {
        return this.perks.values().stream()
                .filter(appliedPerk -> appliedPerk.isAllocated(type))
                .map(AppliedPerk::getPerk)
                .collect(Collectors.toList());
    }

    public Collection<PerkAllocationType> getAllocationTypes(AbstractPerk perk) {
        return this.findAppliedPerk(perk)
                .map(AppliedPerk::getApplicationTypes)
                .orElse(Collections.emptySet());
    }

    public boolean hasPerkEffect(Predicate<AbstractPerk> perkMatch) {
        return hasPerkAllocation(perkMatch) && !isPerkSealed(perkMatch);
    }

    public boolean hasPerkEffect(AbstractPerk perk) {
        return hasPerkAllocation(perk) && !isPerkSealed(perk);
    }

    public boolean hasPerkAllocation(Predicate<AbstractPerk> perkMatch) {
        return this.findAppliedPerk(perkMatch).isPresent();
    }

    public boolean hasPerkAllocation(AbstractPerk perk) {
        return this.findAppliedPerk(perk).isPresent();
    }

    public boolean hasPerkAllocation(AbstractPerk perk, PerkAllocationType type) {
        return this.findAppliedPerk(perk)
                .map(appliedPerk -> appliedPerk.isAllocated(type))
                .orElse(false);
    }

    protected boolean canSealPerk(AbstractPerk perk) {
        return !isPerkSealed(perk) && hasPerkAllocation(perk);
    }

    public boolean isPerkSealed(AbstractPerk perk) {
        return this.findAppliedPerk(perk)
                .map(AppliedPerk::isSealed)
                .orElse(false);
    }

    public boolean isPerkSealed(Predicate<AbstractPerk> perkMatch) {
        return this.findAppliedPerk(perkMatch)
                .map(AppliedPerk::isSealed)
                .orElse(false);
    }

    protected boolean sealPerk(AbstractPerk perk) {
        if (!canSealPerk(perk)) {
            return false;
        }
        return this.findAppliedPerk(perk)
                .map(appliedPerk -> appliedPerk.setSealed(true))
                .orElse(false);
    }

    protected boolean breakSeal(AbstractPerk perk) {
        return this.findAppliedPerk(perk)
                .filter(AppliedPerk::isSealed)
                .map(appliedPerk -> appliedPerk.setSealed(false))
                .orElse(false);
    }

    public boolean updatePerkData(AbstractPerk perk, CompoundNBT data) {
        AppliedPerk appliedPerk = this.perks.get(perk);
        if (appliedPerk == null) {
            return false;
        }
        appliedPerk.perkData = data.copy();
        return true;
    }

    public boolean applyPerkAllocation(AbstractPerk perk, PlayerPerkAllocation allocation, boolean simulate) {
        if (simulate && !this.perks.containsKey(perk)) {
            return true;
        }
        AppliedPerk appliedPerk = this.perks.computeIfAbsent(perk, AppliedPerk::new);
        return appliedPerk.addAllocation(allocation, simulate);
    }

    public PerkRemovalResult removePerkAllocation(AbstractPerk perk, PlayerPerkAllocation allocation, boolean simulate) {
        AppliedPerk appliedPerk = this.perks.get(perk);
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

    @Nullable
    public CompoundNBT getData(AbstractPerk perk) {
        return this.findAppliedPerk(perk)
                .map(AppliedPerk::getPerkData)
                .orElse(null);
    }

    @Nullable
    public CompoundNBT getMetaData(AbstractPerk perk) {
        return this.findAppliedPerk(perk)
                .map(AppliedPerk::getApplicationData)
                .orElse(null);
    }

    private Optional<AppliedPerk> findAppliedPerk(AbstractPerk perk) {
        return Optional.ofNullable(this.perks.get(perk));
    }

    private Optional<AppliedPerk> findAppliedPerk(Predicate<AbstractPerk> perkFilter) {
        return MapStream.of(this.perks)
                .filterKey(perkFilter)
                .valueStream()
                .findFirst();
    }

    protected boolean grantFreeAllocationPoint(ResourceLocation freePointToken) {
        if (this.freePointTokens.contains(freePointToken)) {
            return false;
        }
        this.freePointTokens.add(freePointToken);
        return true;
    }

    protected boolean tryRevokeAllocationPoint(ResourceLocation token) {
        return this.freePointTokens.remove(token);
    }

    public Collection<ResourceLocation> getFreePointTokens() {
        return Collections.unmodifiableCollection(this.freePointTokens);
    }

    public int getAvailablePerkPoints(PlayerEntity player, LogicalSide side) {
        int allocatedPerks = (int) this.perks.values().stream().filter(perk -> perk.isAllocated(PerkAllocationType.UNLOCKED)).count() - 1;
        int allocationLevels = PerkLevelManager.getLevel(getPerkExp(), player, side);
        return (allocationLevels + this.freePointTokens.size()) - allocatedPerks;
    }

    public boolean hasFreeAllocationPoint(PlayerEntity player, LogicalSide side) {
        return getAvailablePerkPoints(player, side) > 0;
    }

    public double getPerkExp() {
        return perkExp;
    }

    public int getPerkLevel(PlayerEntity player, LogicalSide side) {
        return PerkLevelManager.getLevel(getPerkExp(), player, side);
    }

    public float getPercentToNextLevel(PlayerEntity player, LogicalSide side) {
        return PerkLevelManager.getNextLevelPercent(getPerkExp(), player, side);
    }

    protected void modifyExp(double exp, PlayerEntity player) {
        int currLevel = PerkLevelManager.getLevel(getPerkExp(), player, LogicalSide.SERVER);
        if (exp >= 0 && currLevel >= PerkLevelManager.getLevelCap(LogicalSide.SERVER, player)) {
            return;
        }
        long expThisLevel = PerkLevelManager.getExpForLevel(currLevel, player, LogicalSide.SERVER);
        long expNextLevel = PerkLevelManager.getExpForLevel(currLevel + 1, player, LogicalSide.SERVER);
        long cap = MathHelper.lfloor(((float) (expNextLevel - expThisLevel)) * 0.08F);
        if (exp > cap) {
            exp = cap;
        }

        this.perkExp = Math.max(this.perkExp + exp, 0);
    }

    protected void setExp(double exp) {
        this.perkExp = Math.max(exp, 0);
    }

    void load(PlayerProgress progress, CompoundNBT tag) {
        this.perks.clear();
        this.freePointTokens.clear();
        this.perkExp = 0;

        //TODO remove with 1.17
        if (isLegacyData(tag)) {
            loadLegacyData(progress, tag);
            return;
        }

        this.perkExp = tag.getDouble("perkExp");

        long perkTreeVersion = tag.getLong("perkTreeVersion");
        if (PerkTree.PERK_TREE.getVersion(LogicalSide.SERVER).map(v -> !v.equals(perkTreeVersion)).orElse(true)) { //If your perk tree is different, clear it.
            AstralSorcery.log.info("Clearing perk-tree because the player's skill-tree version was outdated!");
            if (progress.getAttunedConstellation() != null) {
                AbstractPerk root = PerkTree.PERK_TREE.getRootPerk(LogicalSide.SERVER, progress.getAttunedConstellation());
                if (root != null) {
                    AppliedPerk newPerk = new AppliedPerk(root);
                    newPerk.addAllocation(PlayerPerkAllocation.unlock(), false);
                    root.onUnlockPerkServer(null, PerkAllocationType.UNLOCKED, progress, newPerk.getPerkData());
                    this.perks.put(root, newPerk);
                }
            }
            return;
        }

        //TODO Remove .replace("-", "_") in 1.17
        this.freePointTokens.addAll(NBTHelper.readList(tag, "tokens", Constants.NBT.TAG_STRING,
                nbt -> new ResourceLocation(nbt.getString().replace("-", "_"))));

        ListNBT list = tag.getList("perks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT nbt = list.getCompound(i);
            AppliedPerk.deserialize(nbt).ifPresent(perk -> this.perks.put(perk.getPerk(), perk));
        }
    }

    void save(CompoundNBT tag) {
        PerkTree.PERK_TREE.getVersion(LogicalSide.SERVER)
                .ifPresent(version -> tag.putLong("perkTreeVersion", version));
        tag.putDouble("perkExp", this.perkExp);

        ListNBT tokens = new ListNBT();
        for (ResourceLocation key : this.freePointTokens) {
            tokens.add(StringNBT.valueOf(key.toString()));
        }
        tag.put("tokens", tokens);

        ListNBT perks = new ListNBT();
        for (AppliedPerk perk : this.perks.values()) {
            perks.add(perk.serialize());
        }
        tag.put("perks", perks);
    }

    public void write(PacketBuffer buf) {
        buf.writeDouble(this.perkExp);
        ByteBufUtils.writeCollection(buf, this.freePointTokens, ByteBufUtils::writeResourceLocation);
        ByteBufUtils.writeCollection(buf, this.perks.values(), (buffer, perk) -> {
            ByteBufUtils.writeResourceLocation(buffer, perk.getPerk().getRegistryName());
            perk.write(buffer);
        });
    }

    public static PlayerPerkData read(PacketBuffer buf, LogicalSide side) {
        PlayerPerkData data = new PlayerPerkData();
        data.perkExp = buf.readDouble();
        data.freePointTokens = ByteBufUtils.readSet(buf, ByteBufUtils::readResourceLocation);
        Set<AppliedPerk> appliedPerks = ByteBufUtils.readSet(buf, buffer -> {
            ResourceLocation key = ByteBufUtils.readResourceLocation(buffer);
            return PerkTree.PERK_TREE.getPerk(side, key)
                    .map(AppliedPerk::new)
                    .map(perk -> {
                        perk.read(buffer);
                        return perk;
                    })
                    .orElseThrow(() -> new IllegalArgumentException("Unknown perk: " + key));
        });
        appliedPerks.forEach(appliedPerk -> data.perks.put(appliedPerk.getPerk(), appliedPerk));
        return data;
    }

    @OnlyIn(Dist.CLIENT)
    void receive(PktSyncKnowledge message) {
        PlayerPerkData copyFrom = message.perkData;

        this.perkExp = copyFrom.perkExp;
        this.freePointTokens = copyFrom.freePointTokens;
        this.perks = copyFrom.perks;
    }

    private boolean isLegacyData(CompoundNBT tag) {
        return tag.contains("sealedPerks");
    }

    private void loadLegacyData(PlayerProgress progress, CompoundNBT compound) {
        long perkTreeLevel = compound.getLong("perkTreeVersion");
        if (PerkTree.PERK_TREE.getVersion(LogicalSide.SERVER).map(v -> !v.equals(perkTreeLevel)).orElse(true)) { //If your perk tree is different, clear it.
            AstralSorcery.log.info("Clearing perk-tree because the player's skill-tree version was outdated!");
            if (progress.getAttunedConstellation() != null) {
                AbstractPerk root = PerkTree.PERK_TREE.getRootPerk(LogicalSide.SERVER, progress.getAttunedConstellation());
                if (root != null) {
                    AppliedPerk newPerk = new AppliedPerk(root);
                    newPerk.addAllocation(PlayerPerkAllocation.unlock(), false);
                    root.onUnlockPerkServer(null, PerkAllocationType.UNLOCKED, progress, newPerk.getPerkData());
                    this.perks.put(root, newPerk);
                }
            }
        } else {
            if (compound.contains("perks")) {
                ListNBT list = compound.getList("perks", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < list.size(); i++) {
                    CompoundNBT tag = list.getCompound(i);
                    String perkRegName = tag.getString("perkName");
                    CompoundNBT data = tag.getCompound("perkData");
                    PerkTree.PERK_TREE.getPerk(LogicalSide.SERVER, new ResourceLocation(perkRegName)).ifPresent(perk -> {
                        AppliedPerk appliedPerk = new AppliedPerk(perk);
                        appliedPerk.addAllocation(PlayerPerkAllocation.unlock(), false);
                        appliedPerk.perkData = data;
                        this.perks.put(perk, appliedPerk);
                    });
                }
            }
            if (compound.contains("sealedPerks")) {
                ListNBT list = compound.getList("sealedPerks", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < list.size(); i++) {
                    CompoundNBT tag = list.getCompound(i);
                    String perkRegName = tag.getString("perkName");
                    PerkTree.PERK_TREE.getPerk(LogicalSide.SERVER, new ResourceLocation(perkRegName)).ifPresent(perk -> {
                        AppliedPerk newPerk = this.perks.get(perk);
                        if (newPerk != null) {
                            newPerk.setSealed(true);
                        }
                    });
                }
            }

            if (compound.contains("pointTokens")) {
                ListNBT list = compound.getList("pointTokens", Constants.NBT.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    String[] resource = legacySplitKey(list.getString(i).toLowerCase(Locale.ROOT));
                    resource[1] = resource[1].replace("-", "_").replace(":", "_");
                    this.freePointTokens.add(AstralSorcery.key(resource[1]));
                }
            }
        }

        if (compound.contains("perkExp")) {
            this.perkExp = compound.getDouble("perkExp");
        }
    }

    private static String[] legacySplitKey(String resource) {
        String[] keyParts = new String[]{ "minecraft", resource };
        int i = resource.indexOf(":");
        if (i >= 0) {
            keyParts[1] = resource.substring(i + 1);
        }
        return keyParts;
    }

    public static class AppliedPerk {

        private static final String SEALED_KEY = "sealed";
        private static final String APPLICATION_KEYS = "application";

        private final AbstractPerk perk;
        private CompoundNBT perkData = new CompoundNBT();
        private CompoundNBT applicationData = new CompoundNBT();
        private Set<PerkAllocationType> applicationTypes = new HashSet<>();

        public AppliedPerk(AbstractPerk perk) {
            this.perk = perk;
        }

        public boolean isSealed() {
            return this.applicationData.contains(SEALED_KEY);
        }

        public boolean setSealed(boolean sealed) {
            if (sealed) {
                this.applicationData.putBoolean(SEALED_KEY, true);
            } else {
                this.applicationData.remove(SEALED_KEY);
            }
            return true;
        }

        public AbstractPerk getPerk() {
            return this.perk;
        }

        public CompoundNBT getPerkData() {
            return this.perkData;
        }

        public CompoundNBT getApplicationData() {
            return this.applicationData;
        }

        private int getTotalAllocationCount() {
            int sum = 0;
            for (PerkAllocationType type : PerkAllocationType.values()) {
                sum += getAllocationCount(type);
            }
            return sum;
        }

        private int getAllocationCount(PerkAllocationType type) {
            CompoundNBT metaData = this.getApplicationData();
            if (!metaData.contains(APPLICATION_KEYS, Constants.NBT.TAG_COMPOUND)) {
                return 0;
            }
            CompoundNBT applicationMeta = metaData.getCompound(APPLICATION_KEYS);
            ListNBT allocations = applicationMeta.getList(type.getSaveKey(), Constants.NBT.TAG_COMPOUND);
            return allocations.size();
        }

        public boolean isAllocated(PerkAllocationType type) {
            return this.applicationTypes.contains(type);
        }

        private PerkRemovalResult removeAllocation(PlayerPerkAllocation type, boolean simulate) {
            CompoundNBT metaData = this.getApplicationData();
            if (!metaData.contains(APPLICATION_KEYS, Constants.NBT.TAG_COMPOUND)) {
                return PerkRemovalResult.FAILURE;
            }
            CompoundNBT applicationMeta = metaData.getCompound(APPLICATION_KEYS);
            ListNBT allocations = applicationMeta.getList(type.getType().getSaveKey(), Constants.NBT.TAG_COMPOUND);
            if (allocations.isEmpty()) {
                return PerkRemovalResult.FAILURE;
            }

            boolean removedMatch = false;
            UUID removeUUID = type.getLockUUID();
            for (int i = 0; i < allocations.size(); i++) {
                CompoundNBT tag = allocations.getCompound(i);
                UUID lockUUID = tag.getUniqueId("uuid");
                if (lockUUID.equals(removeUUID)) {
                    if (!simulate) {
                        allocations.remove(i);
                    }
                    removedMatch = true;
                    break;
                }
            }
            if (!removedMatch) {
                return PerkRemovalResult.FAILURE;
            }

            if (simulate && allocations.size() <= 1) {
                if (this.applicationTypes.size() > 1) {
                    return PerkRemovalResult.REMOVE_ALLOCATION_TYPE;
                } else {
                    return PerkRemovalResult.REMOVE_PERK;
                }
            }
            if (allocations.isEmpty()) {
                this.applicationTypes.remove(type.getType());
                if (this.applicationTypes.isEmpty()) {
                    return PerkRemovalResult.REMOVE_PERK;
                }
                return PerkRemovalResult.REMOVE_ALLOCATION_TYPE;
            }
            return PerkRemovalResult.REMOVE_ALLOCATION;
        }

        public boolean addAllocation(PlayerPerkAllocation type, boolean simulate) {
            if (!simulate) {
                this.applicationTypes.add(type.getType());
            }

            CompoundNBT metaData = this.getApplicationData();
            if (!metaData.contains(APPLICATION_KEYS, Constants.NBT.TAG_COMPOUND)) {
                if (simulate) {
                    return true;
                }
                metaData.put(APPLICATION_KEYS, new CompoundNBT());
            }
            CompoundNBT applicationMeta = metaData.getCompound(APPLICATION_KEYS);

            String key = type.getType().getSaveKey();
            if (!applicationMeta.contains(key, Constants.NBT.TAG_LIST)) {
                if (simulate) {
                    return true;
                }
                applicationMeta.put(key, new ListNBT());
            }
            ListNBT allocations = applicationMeta.getList(key, Constants.NBT.TAG_COMPOUND);

            UUID newUUID = type.getLockUUID();
            CompoundNBT newKeyTag = new CompoundNBT();
            newKeyTag.putUniqueId("uuid", newUUID);

            if (allocations.isEmpty()) {
                if (!simulate) {
                    allocations.add(newKeyTag);
                }
                return true;
            }
            for (int i = 0; i < allocations.size(); i++) {
                CompoundNBT tag = allocations.getCompound(i);
                UUID lockUUID = tag.getUniqueId("uuid");
                if (lockUUID.equals(newUUID)) {
                    return false;
                }
            }
            if (simulate) {
                return true;
            }
            return allocations.add(newKeyTag);
        }

        public Set<PerkAllocationType> getApplicationTypes() {
            return this.applicationTypes;
        }

        private CompoundNBT serialize() {
            CompoundNBT out = new CompoundNBT();
            out.putString("perk", this.perk.getRegistryName().toString());
            out.put("perkData", this.perkData);
            out.put("applicationData", this.applicationData);
            int[] types = this.applicationTypes.stream()
                    .mapToInt(Enum::ordinal)
                    .toArray();
            out.putIntArray("applicationTypes", types);
            return out;
        }

        private static Optional<AppliedPerk> deserialize(CompoundNBT tag) {
            ResourceLocation key = new ResourceLocation(tag.getString("perk"));
            return PerkTree.PERK_TREE.getPerk(LogicalSide.SERVER, key)
                    .map(AppliedPerk::new)
                    .map(appliedPerk -> {
                        appliedPerk.perkData = tag.getCompound("perkData");
                        appliedPerk.applicationData = tag.getCompound("applicationData");
                        int[] types = tag.getIntArray("applicationTypes");
                        appliedPerk.applicationTypes = IntStream.of(types)
                                .mapToObj(type -> PerkAllocationType.values()[type])
                                .collect(Collectors.toSet());
                        return appliedPerk;
                    });
        }

        private void write(PacketBuffer buf) {
            ByteBufUtils.writeNBTTag(buf, this.perkData);
            ByteBufUtils.writeNBTTag(buf, this.applicationData);
            ByteBufUtils.writeCollection(buf, this.applicationTypes, ByteBufUtils::writeEnumValue);
        }

        private void read(PacketBuffer buf) {
            this.perkData = ByteBufUtils.readNBTTag(buf);
            this.applicationData = ByteBufUtils.readNBTTag(buf);
            this.applicationTypes = ByteBufUtils.readSet(buf, buffer -> ByteBufUtils.readEnumValue(buffer, PerkAllocationType.class));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AppliedPerk that = (AppliedPerk) o;
            return Objects.equals(perk.getRegistryName(), that.perk.getRegistryName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(perk.getRegistryName());
        }
    }
}
