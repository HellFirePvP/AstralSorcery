/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.types.LiquidInteractionResultTypesAS;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.phys.Vec3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidInteractionResultSpawnEntity
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class LiquidInteractionResultSpawnEntity extends LiquidInteractionResult {

    public static final MapCodec<LiquidInteractionResultSpawnEntity> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType").forGetter(LiquidInteractionResultSpawnEntity::getEntityType)
    ).apply(inst, LiquidInteractionResultSpawnEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidInteractionResultSpawnEntity> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(BuiltInRegistries.ENTITY_TYPE.key()),
            LiquidInteractionResultSpawnEntity::getEntityType,
            LiquidInteractionResultSpawnEntity::new);

    public static final Type<LiquidInteractionResultSpawnEntity> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final EntityType<?> entityType;

    public LiquidInteractionResultSpawnEntity(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    @Override
    public ItemStack getDisplayOutput() {
        SpawnEggItem egg = SpawnEggItem.byId(this.getEntityType());
        if (egg == null) return ItemStack.EMPTY;
        return new ItemStack(egg);
    }

    @Override
    public Type<?> getType() {
        return LiquidInteractionResultTypesAS.SPAWN_ENTITY.get();
    }

    @Override
    public void apply(ServerLevel level, Vec3 at) {
        if (!this.entityType.canSummon()) {
            AstralSorcery.LOG.warn("Tried to spawn non-summonable entity from liquid interaction: {}", BuiltInRegistries.ENTITY_TYPE.getKey(this.entityType));
            return;
        }
        Entity spawned = this.entityType.spawn(level, net.minecraft.core.BlockPos.containing(at), MobSpawnType.TRIGGERED);
        if (spawned != null) {
            spawned.setPos(at.x, at.y, at.z);
        }
    }
}
