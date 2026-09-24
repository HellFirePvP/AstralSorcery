/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.key;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tick.TickablePerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.fml.LogicalSide;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkReducedFood
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkReducedFood extends KeyPerk implements TickablePerk {

    public static final MapCodec<KeyPerkReducedFood> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkReducedFood::new));
    public static final PerkType<KeyPerkReducedFood> TYPE =
            PerkType.of(KeyPerkReducedFood.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkReducedFood::new);

    private KeyPerkReducedFood(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkReducedFood(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    public void tick(Player player, LogicalSide side) {
        if (side.isServer()) {
            FoodData data = player.getFoodData();

            float exhaustion = data.getExhaustionLevel();
            if (exhaustion > 0) {
                data.addExhaustion(-Math.min(exhaustion, 0.005F));
            }
        }
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }
}
