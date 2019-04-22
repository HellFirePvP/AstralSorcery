/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import com.google.common.base.Splitter;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStateHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 09:25
 */
public class BlockStateHelper {

    private static final Splitter PROP_SPLITTER = Splitter.on(';');
    private static final Splitter PROP_ELEMENT_SPLITTER = Splitter.on('=');

    @Nonnull
    public static String serialize(@Nonnull IBlockState state) {
        StringBuilder name = new StringBuilder(state.getBlock().getRegistryName().toString());
        List<IProperty<?>> props = new ArrayList<>(state.getProperties());
        if (!props.isEmpty()) {
            name.append('[');
            for (int i = 0; i < props.size(); i++) {
                IProperty<?> prop = props.get(i);
                if (i > 0) {
                    name.append(';');
                }
                name.append(prop.getName());
                name.append('=');
                name.append(state.get(prop));
            }
            name.append(']');
        }
        return name.toString();
    }

    @Nullable
    public static <T extends Comparable<T>, V> IBlockState deserialize(@Nonnull String serialized) {
        int propIndex = serialized.indexOf('[');
        boolean hasProperties = propIndex != -1;
        ResourceLocation key;
        if (!hasProperties) {
            key = new ResourceLocation(serialized.toLowerCase());
        } else {
            key = new ResourceLocation(serialized.substring(0, propIndex).toLowerCase());
        }
        Block block = ForgeRegistries.BLOCKS.getValue(key);
        IBlockState state = block.getDefaultState();
        if (!block.equals(Blocks.AIR) && hasProperties) {
            List<String> strProps = PROP_SPLITTER.splitToList(serialized.substring(propIndex, serialized.length() - 1));
            for (String serializedProperty : strProps) {
                List<String> propertyValues = PROP_ELEMENT_SPLITTER.splitToList(serializedProperty);
                String name = propertyValues.get(0);
                String strValue = propertyValues.get(1);
                IProperty<T> property = (IProperty<T>)  MiscUtils.iterativeSearch(state.getProperties(), prop -> prop.getName().equalsIgnoreCase(name));
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

}
