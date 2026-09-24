/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.resource.AssetLocation;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeLoader;
import hellfirepvp.astralsorcery.common.util.data.IntRectangle;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchProgression
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum ResearchTier implements StringRepresentable {

    SHIMMER(-2, -2),
    ILLUMINATION(0, 1),
    RESONANCE(2, -2),
    LUMINANCE(4, 0),
    RADIANCE(5, -3);

    public static final Codec<ResearchTier> CODEC = StringRepresentable.fromEnum(ResearchTier::values);
    public static final StreamCodec<FriendlyByteBuf, ResearchTier> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(ResearchTier.class);

    private final String descriptionId;
    private final TextureQuery cloudTexture;
    private final IntRectangle cloudArea;

    ResearchTier(int x, int y) {
        this(new IntRectangle(x, y, 2, 2));
    }

    ResearchTier(IntRectangle cloudArea) {
        this.descriptionId = Util.makeDescriptionId("tome.research.tier", AstralSorcery.key(this.getSerializedName()));
        this.cloudTexture = new TextureQuery(AssetLocation.SCREEN, "tome", "cluster_" + this.getSerializedName());
        this.cloudArea = cloudArea;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public Component getName() {
        return Component.translatable(this.descriptionId);
    }

    public Component getDiscoveryMessage() {
        return Component.translatable(String.format("message.astralsorcery.research.tier.%s", this.name().toLowerCase(Locale.ROOT)));
    }

    public TextureQuery getCloudTexture() {
        return this.cloudTexture;
    }

    public IntRectangle getCloudArea() {
        return this.cloudArea;
    }

    public boolean canSee(PlayerProgress progress) {
        return progress.getTierReached().isThisLaterOrEqual(this);
    }

    public boolean hasNextTier() {
        return ordinal() < ResearchTier.values().length - 1;
    }

    public ResearchTier next() {
        return values()[Math.min(values().length - 1, ordinal() + 1)];
    }

    public boolean isThisLaterOrEqual(ResearchTier other) {
        return ordinal() >= other.ordinal();
    }

    public boolean isThisLater(ResearchTier other) {
        return ordinal() > other.ordinal();
    }

    public static ResearchTier first() {
        return values()[0];
    }

    public static ResearchTier last() {
        return values()[values().length - 1];
    }
}
