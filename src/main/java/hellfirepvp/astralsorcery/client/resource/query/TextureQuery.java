/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource.query;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetLocation;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TextureQuery
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TextureQuery {

    public static final Codec<TextureQuery> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            StringRepresentable.fromEnum(AssetLocation::values).fieldOf("location").forGetter(query -> query.location),
            Codec.STRING.listOf().fieldOf("path").forGetter(TextureQuery::getPath)
    ).apply(inst, TextureQuery::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TextureQuery> STREAM_CODEC =
            StreamCodec.of(TextureQuery::write, TextureQuery::read);

    private final AssetLocation location;

    private final String[] path;
    private final ClientObject<AbstractRenderTexture> resource = new ClientObject<>();

    public TextureQuery(AssetLocation location, String... path) {
        this.location = location;
        this.path = path;
    }

    private TextureQuery(AssetLocation location, List<String> path) {
        this(location, path.toArray(new String[0]));
    }

    private List<String> getPath() {
        return List.of(this.path);
    }

    public SpriteSheetQuery asSpriteSheet(int rows, int columns) {
        return new SpriteSheetQuery(this.location, rows, columns, this.path);
    }

    @OnlyIn(Dist.CLIENT)
    public AbstractRenderTexture resolve() {
        if (this.resource.isNull()) {
            this.resource.set(AssetLibrary.loadTexture(this.location, this.path));
        }
        return this.resource.get();
    }

    private static TextureQuery read(RegistryFriendlyByteBuf buf) {
        AssetLocation location = buf.readEnum(AssetLocation.class);
        String[] path = buf.readArray(String[]::new, FriendlyByteBuf::readUtf);
        return new TextureQuery(location, path);
    }

    private static void write(RegistryFriendlyByteBuf buf, TextureQuery textureQuery) {
        buf.writeEnum(textureQuery.location);
        buf.writeArray(textureQuery.path, FriendlyByteBuf::writeUtf);
    }
}
