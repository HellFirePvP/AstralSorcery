/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.linking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LinkContainer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LinkContainer {

    public static final Codec<LinkContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SetCodec.of(BlockLinkConnection.CODEC).fieldOf("linkedTo").forGetter(ct -> ct.linkedTo)
    ).apply(instance, LinkContainer::new));

    protected final Set<BlockLinkConnection> linkedTo;

    public LinkContainer() {
        this(new HashSet<>());
    }

    protected LinkContainer(Set<BlockLinkConnection> linkedTo) {
        this.linkedTo = linkedTo;
    }

    public LinkResult canLink(Level level, BlockPos from, BlockPos to) {
        if (this.containsLinkPosition(to)) {
            return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.duplicate").withStyle(ChatFormatting.RED));
        }
        return LinkResult.success();
    }

    public LinkResult link(Level level, BlockPos from, BlockPos to, Linkable.LinkAction action) {
        if (action.simulate()) {
            if (this.containsLinkPosition(to)) {
                return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.duplicate").withStyle(ChatFormatting.RED));
            }
            return LinkResult.success();
        }
        LinkResult canLink = this.canLink(level, from, to);
        if (!canLink.isSuccess()) {
            return canLink;
        }
        if (!this.linkedTo.add(new BlockLinkConnection(to, from.distSqr(to), true))) {
            return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.duplicate").withStyle(ChatFormatting.RED));
        }
        return LinkResult.success();
    }

    public LinkResult unlink(Level level, BlockPos from, BlockPos to, Linkable.LinkAction action) {
        if (action.simulate()) {
            if (!this.containsLinkPosition(to)) {
                return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.missing").withStyle(ChatFormatting.RED));
            }
            return LinkResult.success();
        }
        if (!this.linkedTo.removeIf(conn -> conn.getTo().equals(to))) {
            return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.missing").withStyle(ChatFormatting.RED));
        }
        return LinkResult.success();
    }

    public boolean containsLinkPosition(BlockPos other) {
        return this.linkedTo.stream().anyMatch(conn -> conn.getTo().equals(other));
    }

    public int getLinkedToCount() {
        return this.linkedTo.size();
    }

    public Collection<BlockLinkConnection> getLinkedTo() {
        return Collections.unmodifiableSet(this.linkedTo);
    }
}
