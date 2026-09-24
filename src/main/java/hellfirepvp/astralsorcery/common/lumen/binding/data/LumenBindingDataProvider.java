/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.data;

import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingDataProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class LumenBindingDataProvider implements DataProvider {

    private final String modId;
    protected final PackOutput.PathProvider pathProvider;
    protected final CompletableFuture<HolderLookup.Provider> registries;

    private final Map<ResourceKey<Lumen>, ResourceLocation> lumenMapping = new HashMap<>();
    private final List<BuiltBindingType> registeredBindingTypes = new ArrayList<>();

    protected LumenBindingDataProvider(String modId, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.modId = modId;
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "lumen_binding");
        this.registries = registries;
    }

    public abstract void registerBindingTypes();

    protected void registerBinding(LumenAS.DeferredLumen<Lumen> lumen, BuiltBindingType bindingType) {
        this.registerBinding(lumen.getKey(), bindingType.id);
    }

    protected void registerBinding(ResourceKey<Lumen> lumen, ResourceLocation bindingTypeId) {
        this.lumenMapping.put(lumen, bindingTypeId);
    }

    protected LumenBindingTypeBuilder newBindingType(String id) {
        return this.newBindingType(ResourceLocation.fromNamespaceAndPath(this.modId, id));
    }

    protected LumenBindingTypeBuilder newBindingType(ResourceLocation id) {
        return new LumenBindingTypeBuilder(this, id);
    }

    protected LumenBindingTypeBuilder newBindingType(DeferredHolder<?, ?> reference) {
        return this.newBindingType(reference.getKey().location());
    }

    BuiltBindingType build(ResourceLocation id, LumenBindingType bindingType) {
        BuiltBindingType built = new BuiltBindingType(id, bindingType);
        this.registeredBindingTypes.add(built);
        return built;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            List<CompletableFuture<?>> registers = new ArrayList<>();
            List<BuiltBindingType> builtBindingTypes = new ArrayList<>();

            this.registeredBindingTypes.clear();
            this.registerBindingTypes();

            if (!this.lumenMapping.isEmpty()) {
                registers.add(DataProvider.saveStable(output,
                        provider,
                        LumenBindingTypeMapping.CODEC,
                        LumenBindingTypeMapping.of(this.lumenMapping),
                        this.pathProvider.json(ResourceLocation.fromNamespaceAndPath(this.modId, "_lumen_mapping"))));
            }

            this.registeredBindingTypes.forEach(bindingType -> {
                if (builtBindingTypes.contains(bindingType)) {
                    throw new IllegalArgumentException("Duplicate lumen binding type: " + bindingType.id);
                }
                builtBindingTypes.add(bindingType);

                Path path = this.pathProvider.json(bindingType.id);
                registers.add(DataProvider.saveStable(output, provider, LumenBindingType.CODEC, bindingType.bindingType, path));
            });

            return CompletableFuture.allOf(registers.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Lumen Binding Data Provider";
    }

    public class BuiltBindingType {

        private final ResourceLocation id;
        private final LumenBindingType bindingType;

        public BuiltBindingType(ResourceLocation id, LumenBindingType bindingType) {
            this.id = id;
            this.bindingType = bindingType;
        }

        public void registerBinding(LumenAS.DeferredLumen<? extends Lumen> lumen) {
            this.registerBinding(lumen.getKey());
        }

        public void registerBinding(ResourceKey<Lumen> lumen) {
            LumenBindingDataProvider.this.registerBinding(lumen, this.id);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            BuiltBindingType that = (BuiltBindingType) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
