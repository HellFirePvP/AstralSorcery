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
import hellfirepvp.astralsorcery.client.sky.constellation.SkyConstellationPosition;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConstellationPositionProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ConstellationPositionProvider implements DataProvider {

    private final PackOutput output;
    private final String modid;

    private boolean replace = false;
    private final List<SkyConstellationPosition> positions = new ArrayList<>();

    protected ConstellationPositionProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    public abstract void registerPositions();

    public void add(float yaw, float pitch) {
        this.positions.add(new SkyConstellationPosition(yaw, pitch));
    }

    public void add(SkyConstellationPosition position) {
        position.validatePosition();
        this.positions.add(position);
    }

    //Replace existing/previous resource pack constellation positions with these
    public void replace() {
        this.replace = true;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        this.registerPositions();

        PositionFile file = new PositionFile(this.replace, false, this.positions);
        return DataProvider.saveStable(
                output,
                PositionFile.CODEC.encodeStart(JsonOps.INSTANCE, file).getOrThrow(),
                createPath()
        );
    }

    protected Path createPath() {
        return this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(modid)
                .resolve("constellation_positions.json");
    }

    @Override
    public String getName() {
        return "Constellation Position Provider";
    }

    public record PositionFile(boolean replace, boolean renderDebug, List<SkyConstellationPosition> positions) {

        public static final Codec<PositionFile> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.BOOL.fieldOf("replace").forGetter(PositionFile::replace),
                Codec.BOOL.optionalFieldOf("render_debug", false).forGetter(PositionFile::renderDebug),
                SkyConstellationPosition.CODEC.listOf().fieldOf("positions").forGetter(PositionFile::positions)
        ).apply(inst, PositionFile::new));

    }
}
