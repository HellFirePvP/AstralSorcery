/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.datagen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.screen.tome.lumen.data.LumenDisplayPosition;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenDisplayPositionProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class LumenDisplayPositionProvider implements DataProvider {

    private final PackOutput output;
    private final String modid;

    private boolean replace = false;
    private final Map<Lumen, LumenDisplayPosition> positions = new HashMap<>();

    protected LumenDisplayPositionProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    public abstract void registerPositions();

    public void put(Supplier<? extends Lumen> lumen, int x, int y) {
        this.put(lumen.get(), new LumenDisplayPosition(x, y));
    }

    public void put(Lumen lumen, int x, int y) {
        this.put(lumen, new LumenDisplayPosition(x, y));
    }

    public void put(Lumen lumen, LumenDisplayPosition position) {
        this.positions.put(lumen, position);
    }

    public void replace() {
        this.replace = true;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        this.registerPositions();

        PositionFile file = new PositionFile(this.replace, this.positions);
        return DataProvider.saveStable(
                output,
                PositionFile.CODEC.encodeStart(JsonOps.INSTANCE, file).getOrThrow(),
                createPath()
        );
    }

    protected Path createPath() {
        return this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(modid)
                .resolve("lumen_display_positions.json");
    }

    @Override
    public String getName() {
        return "Tome Lumen Display Position Provider";
    }

    public record PositionFile(boolean replace, Map<Lumen, LumenDisplayPosition> positions) {

        public static final Codec<PositionFile> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.BOOL.fieldOf("replace").forGetter(PositionFile::replace),
                Codec.unboundedMap(RegistriesAS.REGISTRY_LUMEN.byNameCodec(), LumenDisplayPosition.CODEC).fieldOf("positions").forGetter(PositionFile::positions)
        ).apply(inst, PositionFile::new));

    }
}
