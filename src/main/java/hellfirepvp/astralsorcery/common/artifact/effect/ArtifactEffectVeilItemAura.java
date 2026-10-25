/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.artifact.ArtifactEffect;
import hellfirepvp.astralsorcery.common.artifact.ContinuousArtifactEffect;
import hellfirepvp.astralsorcery.common.component.VeiledItemStackComponent;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.event.helper.VeiledItemStackHelper;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.types.ArtifactEffectTypesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectVeilItemAura
 * Created by HellFirePvP
 * Date: 25.10.2026 / 22:42
 */
public class ArtifactEffectVeilItemAura extends ArtifactEffect implements ContinuousArtifactEffect {

    public static final MapCodec<ArtifactEffectVeilItemAura> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            Codec.INT.fieldOf("range").forGetter(ArtifactEffectVeilItemAura::getRange),
            Codec.INT.fieldOf("interval").forGetter(ArtifactEffectVeilItemAura::getInterval),
            Codec.FLOAT.fieldOf("chance").forGetter(ArtifactEffectVeilItemAura::getChance),
            Codec.INT.fieldOf("duration").forGetter(ArtifactEffectVeilItemAura::getDuration),
            Codec.FLOAT.fieldOf("inventory_chance").forGetter(ArtifactEffectVeilItemAura::getInventoryChance)
    ).apply(inst, ArtifactEffectVeilItemAura::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactEffectVeilItemAura> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ArtifactEffect::getId,
            ByteBufCodecs.INT, ArtifactEffectVeilItemAura::getRange,
            ByteBufCodecs.INT, ArtifactEffectVeilItemAura::getInterval,
            ByteBufCodecs.FLOAT, ArtifactEffectVeilItemAura::getChance,
            ByteBufCodecs.INT, ArtifactEffectVeilItemAura::getDuration,
            ByteBufCodecs.FLOAT, ArtifactEffectVeilItemAura::getInventoryChance,
            ArtifactEffectVeilItemAura::new);

    private final int range;
    private final int interval;
    private final float chance;
    private final int duration;
    private final float inventoryChance;

    protected ArtifactEffectVeilItemAura(ResourceLocation id, int range, int interval, float chance, int duration, float inventoryChance) {
        super(id);
        this.range = range;
        this.interval = Math.max(1, interval);
        this.chance = chance;
        this.duration = duration;
        this.inventoryChance = inventoryChance;
    }

    public int getRange() {
        return this.range;
    }

    public int getInterval() {
        return this.interval;
    }

    public float getChance() {
        return this.chance;
    }

    public int getDuration() {
        return this.duration;
    }

    public float getInventoryChance() {
        return this.inventoryChance;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.VEIL_ITEM_AURA;
    }

    @Override
    public void tickEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        if (artifact.tickCount % this.getInterval() != 0) return;

        this.getPlayersInRange(sLevel, artifact).forEach(player -> {
            if (rand.nextFloat() < this.getChance()) {
                this.veilHeldItem(rand, sLevel, player);
            }
        });
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        this.getPlayersInRange(sLevel, artifact).forEach(player -> this.veilHeldItem(rand, sLevel, player));
    }

    private List<Player> getPlayersInRange(ServerLevel sLevel, ItemEntityArtifact artifact) {
        AABB bounds = artifact.getBoundingBox().inflate(this.getRange());
        return sLevel.getEntities(EntityTypeTest.forClass(Player.class), bounds, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(Entity::isAlive));
    }

    private void veilHeldItem(RandomSource rand, ServerLevel sLevel, Player player) {
        Inventory inventory = player.getInventory();
        ItemStack held = inventory.getSelected();
        if (held.isEmpty()) return;

        ItemStack appearance = null;
        if (rand.nextFloat() < this.getInventoryChance()) {
            List<ItemStack> inventoryAppearances = new ArrayList<>();
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                if (slot == inventory.selected) continue;

                ItemStack displayed = VeiledItemStackHelper.getDisplayedStack(inventory.getItem(slot));
                if (!displayed.isEmpty() && !ItemStack.isSameItem(displayed, held)) {
                    inventoryAppearances.add(displayed);
                }
            }
            appearance = MiscUtil.getRandomEntry(inventoryAppearances, rand).orElse(null);
        }
        if (appearance == null) {
            do {
                appearance = BuiltInRegistries.ITEM.getRandom(rand)
                        .map(holder -> holder.value().getDefaultInstance())
                        .orElse(ItemStack.EMPTY);
            } while (appearance.isEmpty() || ItemStack.isSameItem(appearance, held));
        }

        held.set(DataComponentsAS.VEILED, VeiledItemStackComponent.timeout(appearance, sLevel, this.getDuration(), false));
    }

    public static class Builder {

        private int range = 5;
        private int interval = 10 * 20;
        private float chance = 0.15F;
        private int duration = 30 * 20;
        private float inventoryChance = 0.5F;

        public Builder setRange(int range) {
            this.range = range;
            return this;
        }

        public Builder setInterval(int interval) {
            this.interval = interval;
            return this;
        }

        public Builder setChance(float chance) {
            this.chance = chance;
            return this;
        }

        public Builder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder setInventoryChance(float inventoryChance) {
            this.inventoryChance = inventoryChance;
            return this;
        }

        public Provider build() {
            return id -> new ArtifactEffectVeilItemAura(id, this.range, this.interval, this.chance, this.duration, this.inventoryChance);
        }
    }
}
