/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.research.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputUpdateResearchTier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputUpdateResearchTier extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputUpdateResearchTier> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResearchTier.CODEC.fieldOf("tier").forGetter(AltarOutputUpdateResearchTier::getTier)
    ).apply(inst, AltarOutputUpdateResearchTier::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputUpdateResearchTier> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(ResearchTier.class),
            AltarOutputUpdateResearchTier::getTier,
            AltarOutputUpdateResearchTier::new);
    public static final Type<AltarOutputUpdateResearchTier> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final ResearchTier tier;

    private AltarOutputUpdateResearchTier(ResearchTier tier) {
        this.tier = tier;
    }

    public ResearchTier getTier() {
        return this.tier;
    }

    public static AltarOutputUpdateResearchTier create(ResearchTier tier) {
        return new AltarOutputUpdateResearchTier(tier);
    }

    @Override
    public Type<AltarOutputUpdateResearchTier> getType() {
        return TYPE;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        input.getCraftingServerPlayer().ifPresent(sPlayer -> {
            PlayerProgress prog = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
            ResearchTier current = prog.getTierReached();
            if (prog.isValid() && this.tier.isThisLater(current)) {
                if (ResearchHelper.setResearchProgress(sPlayer, this.tier)) {
                    ResearchMessageHelper.sendResearchTierDiscovery(sPlayer, current, prog.getTierReached());
                }
            }
        });
        return output;
    }

    @Override
    public ItemStack modifyOutputForDisplay(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        return output;
    }
}
