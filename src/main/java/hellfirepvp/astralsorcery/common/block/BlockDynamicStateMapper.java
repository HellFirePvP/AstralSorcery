/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDynamicStateMapper
 * Created by HellFirePvP
 * Date: 04.12.2017 / 19:27
 */
public interface BlockDynamicStateMapper {

    default public boolean handleRegisterStateMapper() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    default public void registerStateMapper() {
        ModelLoader.setCustomStateMapper(getBlock(), this::getModelLocations);
    }

    default public Block getBlock() {
        return (Block) this;
    }

    public Map<IBlockState, ModelResourceLocation> getModelLocations(Block blockIn);

    public static interface Festive extends BlockDynamicStateMapper {

        @Override
        default boolean handleRegisterStateMapper() {
            LocalDateTime now = LocalDateTime.now();
            return (now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 21 && now.getDayOfMonth() <= 31);
        }

    }

    default public String getPropertyString(Map<IProperty<?>, Comparable<?>> values) {
        StringBuilder stringbuilder = new StringBuilder();

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : values.entrySet()) {
            if (stringbuilder.length() != 0) {
                stringbuilder.append(",");
            }
            IProperty<?> iproperty = entry.getKey();
            stringbuilder.append(iproperty.getName());
            stringbuilder.append("=");
            stringbuilder.append(this.getPropertyName(iproperty, entry.getValue()));
        }

        if (stringbuilder.length() == 0) {
            stringbuilder.append("normal");
        }

        return stringbuilder.toString();
    }

    default public <T extends Comparable<T>> String getPropertyName(IProperty<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }

}
