/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.perk.tick.PerkCooldownHelper;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocation;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationStatus;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationType;
import hellfirepvp.astralsorcery.common.research.PlayerPerkData;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import hellfirepvp.astralsorcery.common.util.event.CachedEventBus;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.IModInfo;

import javax.annotation.Nullable;
import java.util.*;

import static hellfirepvp.astralsorcery.common.util.SidedHelper.getSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AbstractPerk
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class AbstractPerk<T extends AbstractPerk.Data> implements ModifierSource {

    public static final Codec<AbstractPerk<?>> DATAPACK_CODEC = RegistriesAS.REGISTRY_PERK_TYPES.byNameCodec()
            .dispatch(AbstractPerk::getType, PerkType::perkCodec);
    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractPerk<?>> TO_CLIENT_PERKTREE_CODEC = StreamCodec.of(
            (buf, perk) -> buf.writeResourceLocation(perk.getKey()),
            buf -> PerkTree.getInstance().getPerk(LogicalSide.CLIENT, buf.readResourceLocation()).orElseThrow()
    );
    protected static <T extends AbstractPerk<?>> Products.P5<RecordCodecBuilder.Mu<T>, ResourceLocation, String, Float, Float, PerkCategory> perkFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                ResourceLocation.CODEC.fieldOf("registry_name").forGetter(AbstractPerk::getKey),
                Codec.STRING.fieldOf("name").forGetter(AbstractPerk::getNameKey),
                Codec.FLOAT.fieldOf("x").forGetter(AbstractPerk::getX),
                Codec.FLOAT.fieldOf("y").forGetter(AbstractPerk::getY),
                PerkCategory.CODEC.fieldOf("category").forGetter(AbstractPerk::getCategory)
        );
    }

    protected final RandomSource rand = RandomSource.create();

    private final ResourceLocation key;
    private final float x;
    private final float y;
    private PerkCategory category;
    private String nameKey;

    private final CachedEventBus busWrapper;
    private PerkTreePoint<?> perkTreePoint = null;

    private List<MutableComponent> tooltipCache = null;
    private boolean cacheTooltip = true;

    protected AbstractPerk(ResourceLocation key, String nameKey, float x, float y, PerkCategory category) {
        this.key = key;
        this.nameKey = nameKey;
        this.x = x;
        this.y = y;
        this.category = category;

        this.busWrapper = CachedEventBus.of(NeoForge.EVENT_BUS);
    }

    public <P extends AbstractPerk<?>> P setNameKey(String nameKey) {
        AstralSorcery.assertDataGeneration();
        this.nameKey = nameKey;
        return MiscUtil.cast(this);
    }

    public <P extends AbstractPerk<?>> P setCategory(PerkCategory category) {
        AstralSorcery.assertDataGeneration();
        this.category = category;
        return MiscUtil.cast(this);
    }

    protected static String defaultNameKey(ResourceLocation key) {
        return Util.makeDescriptionId("perk", key);
    }

    public static MutableComponent getInfoText(String key) {
        return Component.translatable(Util.makeDescriptionId("perk.info", AstralSorcery.key(key)));
    }

    public void clearCaches(LogicalSide side) {}

    public final void clearTooltipCache() {
        this.tooltipCache = null;
    }

    protected final void disableTooltipCaching() {
        this.cacheTooltip = false;
        this.tooltipCache = null;
    }

    public final Collection<MutableComponent> getTooltip(PlayerProgress progress, @Nullable Player player, LogicalSide side) {
        if (this.cacheTooltip && this.tooltipCache != null) {
            return this.tooltipCache;
        }

        this.tooltipCache = new ArrayList<>();
        this.tooltipCache.add(this.getName(progress, player));
        if (this.addTooltip(this.tooltipCache, progress, player, side)) {
            this.tooltipCache.add(Component.empty());
        }
        List<MutableComponent> description = this.getDescription(progress, player);
        if (!description.isEmpty()) {
            this.tooltipCache.addAll(this.getDescription(progress, player));
            this.tooltipCache.add(Component.empty());
        }

        return this.tooltipCache;
    }

    protected boolean addTooltip(Collection<MutableComponent> tooltip, PlayerProgress progress, @Nullable Player player, LogicalSide side) {
        return false;
    }

    public void invalidate(LogicalSide side) {
        this.busWrapper.unregisterAll();
        PerkCooldownHelper.removePerkCooldowns(side, this);
    }

    public void validate(LogicalSide side) {
        this.attachEventListeners(SidedEventBus.of(this.busWrapper, side));
    }

    protected void attachEventListeners(SidedEventBus sidedEventBus) {}

    protected PerkTreePoint<?> initPerkTreePoint() {
        return new PerkTreePoint<>(this.getOffset(), this);
    }

    public ResourceLocation getKey() {
        return this.key;
    }

    public final String getNameKey() {
        return this.nameKey;
    }

    public final float getX() {
        return this.x;
    }

    public final float getY() {
        return this.y;
    }

    public final FloatPoint getOffset() {
        return new FloatPoint(this.x, this.y);
    }

    public final PerkCategory getCategory() {
        return this.category;
    }

    public final PerkTreePoint<?> getPerkTreePoint() {
        if (this.perkTreePoint == null) {
            this.perkTreePoint = this.initPerkTreePoint();
        }
        return this.perkTreePoint;
    }

    public MutableComponent getName(PlayerProgress progress, Player player) {
        return Component.translatable(String.format("%s.name", this.getNameKey()))
                .withColor(this.getCategory().getColor().getColor());
    }

    public List<String> getDescriptionIds() {
        return NameUtil.resolveLocalizedLines(String.format("%s.description", this.getNameKey()));
    }

    public List<MutableComponent> getDescription(PlayerProgress progress, @Nullable Player player) {
        return this.getDescriptionIds().stream()
                .map(Component::translatable)
                .toList();
    }

    @Override
    public boolean canApplySource(Player player, LogicalSide dist) {
        return !this.getPerkData(player, dist).map(Data::isSealed).orElse(false);
    }

    @Override
    public final void onApply(Player player, LogicalSide dist) {
        this.applyPerkLogic(player, dist);
    }

    @Override
    public final void onRemove(Player player, LogicalSide dist) {
        this.removePerkLogic(player, dist);
    }

    protected void applyPerkLogic(Player player, LogicalSide dist) {}

    protected void removePerkLogic(Player player, LogicalSide dist) {}

    /**
     * Called ONCE when the perk is unlocked
     * You may use the CompoundNBT to save data to remove it again later
     */
    public void onUnlockPerkServer(@Nullable Player player, PerkAllocationType allocation, PlayerProgress progress, T data) {}

    /**
     * Clean up and remove the perk from that single player.
     * Data in the dataStorage is filled with the data set in onUnlockPerkServer
     * Called after the perk is already removed from the player, but still in the player's perkData
     */
    public void onRemovePerkServer(ServerPlayer player, PerkAllocationType allocation, PlayerProgress progress, T data) {}

    public PerkAllocationStatus getPerkStatus(@Nullable Player player, LogicalSide side) {
        if (player == null) {
            return PerkAllocationStatus.UNALLOCATED;
        }
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (!progress.isValid()) {
            return PerkAllocationStatus.UNALLOCATED;
        }
        PlayerPerkData perkData = progress.getPerkData();
        if (perkData.hasPerkAllocationGrantingConnections(this)) {
            return PerkAllocationStatus.ALLOCATED;
        }
        if (perkData.hasPerkAllocation(this)) {
            return PerkAllocationStatus.GRANTED;
        }
        if (!this.mayUnlockPerk(progress, player)) {
            return PerkAllocationStatus.UNALLOCATED;
        }
        if (this.hasPlayerPerkAllowingUnlock(progress, player)) {
            return PerkAllocationStatus.UNLOCKABLE;
        }
        return PerkAllocationStatus.UNALLOCATED;
    }

    // explicitly only care about this single perk, not tree structure
    public boolean mayUnlockPerk(PlayerProgress progress, Player player) {
        PlayerPerkData perkData = progress.getPerkData();
        return perkData.hasFreeAllocationPoint(player, getSide(player));
    }

    public boolean mayRemovePerk(PlayerProgress progress, Player player) {
        if (this.getAlwaysDependentPerks(progress, getSide(player)).stream()
                .anyMatch(otherPerk -> progress.getPerkData().hasPerkAllocationGrantingConnections(otherPerk))) {
            return false;
        }
        return (progress.getPerkData().hasPerkAllocation(this, PerkAllocationType.UNLOCKED) ||
                progress.getPerkData().hasPerkAllocation(this, PerkAllocationType.UNLOCKED_NON_CONNECT)) &&
                progress.getDependentPerks(this, getSide(player)).isEmpty();
    }

    public boolean hasPlayerPerkAllowingUnlock(PlayerProgress progress, Player player) {
        PlayerPerkData perkData = progress.getPerkData();
        return perkData.getPerks().keySet().stream()
                .anyMatch(otherPerk -> otherPerk.providesAllocation(progress, player, this).isPresent());
    }

    public Optional<PerkAllocation> providesAllocation(PlayerProgress progress, Player player, AbstractPerk<?> otherPerk) {
        if (this == otherPerk) return Optional.empty();

        PlayerPerkData perkData = progress.getPerkData();
        if (!perkData.hasPerkAllocationGrantingConnections(this)) {
            return Optional.empty(); // Only if the player has spent a point unlocking from pathing may this provide further allocations
        }
        if (this.getConnectedPerks(progress, getSide(player), false).contains(otherPerk)) {
            return Optional.of(PerkAllocation.unlock());
        }
        return Optional.empty();
    }

    public List<MutableComponent> getPerkSource() {
        return ModList.get()
                .getModContainerById(this.getKey().getNamespace())
                .map(ModContainer::getModInfo)
                .map(IModInfo::getDisplayName)
                .map(Component::literal)
                .map(List::of)
                .orElse(Collections.emptyList());
    }

    @Override
    public final ModifierSourceProvider<?> getSourceProvider() {
        return PerksAS.Sources.PERKS.get();
    }

    public abstract PerkType<?> getType();

    public Optional<T> getPerkData(Player player, LogicalSide side) {
        return ResearchManager.getProgress(player, side).getPerkData().getPerkData(this);
    }

    protected LogicalSide getSide(Entity entity) {
        return SidedHelper.getSide(entity);
    }

    public Collection<AbstractPerk<?>> getConnectedPerks(PlayerProgress progress, LogicalSide side, boolean direct) {
        return PerkTree.getInstance().getConnectedPerks(side, this);
    }

    public Collection<AbstractPerk<?>> getAlwaysDependentPerks(PlayerProgress progress, LogicalSide side) {
        return Collections.emptyList();
    }

    public static class Data {

        protected static <T extends Data> Products.P1<RecordCodecBuilder.Mu<T>, Boolean> dataFields(RecordCodecBuilder.Instance<T> instance) {
            return instance.group(
                    CodecUtil.defaulted(Codec.BOOL, "sealed", () -> false, Data::isSealed)
            );
        }
        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(inst -> dataFields(inst).apply(inst, Data::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> SYNC_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::isSealed,
                Data::new
        );

        private boolean sealed;

        protected Data(boolean sealed) {
            this.sealed = sealed;
        }

        public static Data create() {
            return new Data(false);
        }

        public PerkDataType<?> getType() {
            return PerkDataTypesAS.DEFAULT_DATA.get();
        }

        public boolean isSealed() {
            return this.sealed;
        }

        public void setSealed(boolean sealed) {
            this.sealed = sealed;
        }

        public <T extends Data> T copy() {
            return MiscUtil.cast(new Data(this.sealed));
        }
    }
}
