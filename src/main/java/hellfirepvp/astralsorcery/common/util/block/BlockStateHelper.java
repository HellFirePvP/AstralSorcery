/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import com.google.common.base.Splitter;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStateHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 09:25
 */
public class BlockStateHelper {

    private static final Splitter PROP_SPLITTER = Splitter.on(',');
    private static final Splitter PROP_ELEMENT_SPLITTER = Splitter.on('=');

    @Nonnull
    public static <V extends Comparable<V>> String serialize(@Nonnull BlockState state) {
        StringBuilder name = new StringBuilder(state.getBlock().getRegistryName().toString());
        List<IProperty<?>> props = new ArrayList<>(state.getProperties());
        if (!props.isEmpty()) {
            name.append('[');
            for (int i = 0; i < props.size(); i++) {
                IProperty<V> prop = (IProperty<V>) props.get(i);
                if (i > 0) {
                    name.append(',');
                }
                name.append(prop.getName());
                name.append('=');
                name.append(prop.getName(state.get(prop)));
            }
            name.append(']');
        }
        return name.toString();
    }

    @Nonnull
    public static BlockMatchInformation deserializeMatcher(@Nonnull String serialized) {
        return new BlockMatchInformation(deserialize(serialized), !isMissingStateInformation(serialized));
    }

    @Nonnull
    public static <T extends Comparable<T>> BlockState deserialize(@Nonnull String serialized) {
        int propIndex = serialized.indexOf('[');
        boolean hasProperties = propIndex != -1;
        ResourceLocation key;
        if (!hasProperties) {
            key = new ResourceLocation(serialized.toLowerCase());
        } else {
            key = new ResourceLocation(serialized.substring(0, propIndex).toLowerCase());
        }
        Block block = ForgeRegistries.BLOCKS.getValue(key);
        BlockState state = block.getDefaultState();
        if (!block.equals(Blocks.AIR) && hasProperties) {
            List<String> strProps = PROP_SPLITTER.splitToList(serialized.substring(propIndex, serialized.length() - 1));
            for (String serializedProperty : strProps) {
                List<String> propertyValues = PROP_ELEMENT_SPLITTER.splitToList(serializedProperty);
                String name = propertyValues.get(0);
                String strValue = propertyValues.get(1);
                IProperty<T> property = (IProperty<T>) MiscUtils.iterativeSearch(state.getProperties(), prop -> prop.getName().equalsIgnoreCase(name));
                if (property != null) {
                    Optional<T> value = property.parseValue(strValue);
                    if (value.isPresent()) {
                        state = state.with(property, value.get());
                    }
                }
            }
        }
        return state;
    }

    public static boolean isMissingStateInformation(@Nonnull String serialized) {
        return serialized.indexOf('[') == -1;
    }

}
