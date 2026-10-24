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
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectEatFoodAura
 * Created by HellFirePvP
 * Date: 24.10.2026 / 20:05
 */
public class ArtifactEffectEatFoodAura extends ArtifactEffect implements ContinuousArtifactEffect {

    public static final MapCodec<ArtifactEffectEatFoodAura> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            Codec.INT.fieldOf("range").forGetter(ArtifactEffectEatFoodAura::getRange),
            Codec.INT.fieldOf("interval").forGetter(ArtifactEffectEatFoodAura::getInterval),
            Codec.FLOAT.fieldOf("chance").forGetter(ArtifactEffectEatFoodAura::getChance)
    ).apply(inst, ArtifactEffectEatFoodAura::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactEffectEatFoodAura> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ArtifactEffect::getId,
            ByteBufCodecs.INT, ArtifactEffectEatFoodAura::getRange,
            ByteBufCodecs.INT, ArtifactEffectEatFoodAura::getInterval,
            ByteBufCodecs.FLOAT, ArtifactEffectEatFoodAura::getChance,
            ArtifactEffectEatFoodAura::new);

    private final int range;
    private final int interval;
    private final float chance;

    protected ArtifactEffectEatFoodAura(ResourceLocation id, int range, int interval, float chance) {
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
        return id -> new ArtifactEffectEatFoodAura(id, range, interval, chance);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.EAT_FOOD_AURA;
    }

    @Override
    public void tickEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        if (artifact.tickCount % this.getInterval() != 0) return;

        this.getPlayersInRange(sLevel, artifact).forEach(player -> {
            if (rand.nextFloat() < this.getChance()) {
                this.eatRandomFood(rand, sLevel, player);
            }
        });
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        this.getPlayersInRange(sLevel, artifact).forEach(player -> this.eatRandomFood(rand, sLevel, player));
    }

    private List<Player> getPlayersInRange(ServerLevel sLevel, ItemEntityArtifact artifact) {
        AABB bounds = artifact.getBoundingBox().inflate(this.getRange());
        return sLevel.getEntities(EntityTypeTest.forClass(Player.class), bounds, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(Entity::isAlive));
    }

    private void eatRandomFood(RandomSource rand, ServerLevel sLevel, Player player) {
        Inventory inventory = player.getInventory();
        List<Integer> foodSlots = new ArrayList<>();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot).getFoodProperties(player) != null) {
                foodSlots.add(slot);
            }
        }
        if (foodSlots.isEmpty()) return;

        int foodSlot = foodSlots.get(rand.nextInt(foodSlots.size()));
        ItemStack food = inventory.getItem(foodSlot);
        ItemStack foodCopy = food.copy();
        if (foodCopy.getUseAnimation() == UseAnim.EAT) {
            Vec3 at = player.getEyePosition().add(player.getLookAngle().scale(0.4));
            sLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, foodCopy),
                    at.x, at.y - 0.15, at.z, 16,
                    0.1, 0.1, 0.1, 0.05);
        }

        ItemStack result = EventHooks.onItemUseFinish(player, foodCopy, 0, food.finishUsingItem(sLevel, player));
        if (result != food) {
            inventory.setItem(foodSlot, result);
        }
    }
}
