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
import hellfirepvp.astralsorcery.common.lib.types.LiquidInteractionResultTypesAS;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidInteractionResultDropItem
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class LiquidInteractionResultDropItem extends LiquidInteractionResult {

    public static final MapCodec<LiquidInteractionResultDropItem> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemStack.CODEC.fieldOf("output").forGetter(LiquidInteractionResultDropItem::getOutput)
    ).apply(inst, LiquidInteractionResultDropItem::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidInteractionResultDropItem> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, LiquidInteractionResultDropItem::getOutput,
            LiquidInteractionResultDropItem::new);

    public static final Type<LiquidInteractionResultDropItem> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final ItemStack output;

    public LiquidInteractionResultDropItem(ItemStack output) {
        this.output = output;
    }

    public ItemStack getOutput() {
        return this.output.copy();
    }

    @Override
    public ItemStack getDisplayOutput() {
        return this.getOutput();
    }

    @Override
    public Type<?> getType() {
        return LiquidInteractionResultTypesAS.DROP_ITEM.get();
    }

    @Override
    public void apply(ServerLevel level, Vec3 at) {
        ItemStack toDrop = this.output.copy();
        if (toDrop.isEmpty()) {
            return;
        }
        ItemUtil.dropItem(level, at, toDrop);
    }
}
