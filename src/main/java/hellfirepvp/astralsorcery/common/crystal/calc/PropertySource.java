/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal.calc;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertySource
 * Created by HellFirePvP
 * Date: 30.01.2019 / 08:20
 */
public abstract class PropertySource<T, I extends PropertySource.SourceInstance> {

    private final ResourceLocation registryName;

    public PropertySource(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public final ResourceLocation getRegistryName() {
        return registryName;
    }

    public abstract I createInstance(T obj);

    public ITextComponent getName() {
        return new TranslationTextComponent(String.format("crystal.source.%s.%s.name",
                getRegistryName().getNamespace(), getRegistryName().getPath()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertySource that = (PropertySource) o;
        return Objects.equals(registryName, that.registryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registryName);
    }

    public abstract static class SourceInstance {

        private final PropertySource<?, ?> source;

        protected SourceInstance(PropertySource<?, ?> source) {
            this.source = source;
        }

        public PropertySource<?, ?> getSource() {
            return source;
        }
    }

}
