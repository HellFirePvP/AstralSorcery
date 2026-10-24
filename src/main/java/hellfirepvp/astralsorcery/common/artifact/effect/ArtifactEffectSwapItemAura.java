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
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.types.ArtifactEffectTypesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectSwapItemAura
 * Created by HellFirePvP
 * Date: 24.10.2026 / 20:40
 */
public class ArtifactEffectSwapItemAura extends ArtifactEffect implements ContinuousArtifactEffect {

    public static final MapCodec<ArtifactEffectSwapItemAura> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            Codec.INT.fieldOf("range").forGetter(ArtifactEffectSwapItemAura::getRange),
            Codec.INT.fieldOf("interval").forGetter(ArtifactEffectSwapItemAura::getInterval),
            Codec.FLOAT.fieldOf("chance").forGetter(ArtifactEffectSwapItemAura::getChance)
    ).apply(inst, ArtifactEffectSwapItemAura::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactEffectSwapItemAura> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ArtifactEffect::getId,
            ByteBufCodecs.INT, ArtifactEffectSwapItemAura::getRange,
            ByteBufCodecs.INT, ArtifactEffectSwapItemAura::getInterval,
            ByteBufCodecs.FLOAT, ArtifactEffectSwapItemAura::getChance,
            ArtifactEffectSwapItemAura::new);

    private final int range;
    private final int interval;
    private final float chance;

    protected ArtifactEffectSwapItemAura(ResourceLocation id, int range, int interval, float chance) {
        super(id);
        this.range = range;
        this.interval = Math.max(1, interval);
        this.chance = chance;
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

    public static Provider of(int range, int interval, float chance) {
        return id -> new ArtifactEffectSwapItemAura(id, range, interval, chance);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.SWAP_ITEM_AURA;
    }

    @Override
    public void tickEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        if (artifact.tickCount % this.getInterval() != 0) return;

        this.getPlayersInRange(sLevel, artifact).forEach(player -> {
            if (rand.nextFloat() < this.getChance()) {
                this.swapHeldItem(rand, player);
            }
        });
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        this.getPlayersInRange(sLevel, artifact).forEach(player -> this.swapHeldItem(rand, player));
    }

    private List<Player> getPlayersInRange(ServerLevel sLevel, ItemEntityArtifact artifact) {
        AABB bounds = artifact.getBoundingBox().inflate(this.getRange());
        return sLevel.getEntities(EntityTypeTest.forClass(Player.class), bounds, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(Entity::isAlive));
    }

    private void swapHeldItem(RandomSource rand, Player player) {
        Inventory inventory = player.getInventory();
        if (inventory.getSelected().isEmpty()) return;

        int heldSlot = inventory.selected;

        List<Integer> swapSlots = new ArrayList<>();
        for (int slot = 0; slot < Inventory.INVENTORY_SIZE; slot++) {
            if (slot != heldSlot && !inventory.getItem(slot).isEmpty()) {
                swapSlots.add(slot);
            }
        }

        if (swapSlots.isEmpty()) {
            if (inventory.getSelected().isEmpty()) return;

            swapSlots = IntStream.range(0, Inventory.INVENTORY_SIZE)
                    .filter(slot -> slot != heldSlot)
                    .boxed()
                    .toList();
        }

        MiscUtil.getRandomEntry(swapSlots, rand).ifPresent(swapSlot -> {
            ItemStack held = inventory.getSelected();
            inventory.setItem(heldSlot, inventory.getItem(swapSlot));
            inventory.setItem(swapSlot, held);
        });
    }
}
