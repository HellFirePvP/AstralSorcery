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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LinkContainer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityLinkContainer {

    public static final Codec<EntityLinkContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SetCodec.of(EntityLinkConnection.CODEC).fieldOf("linkedTo").forGetter(ct -> ct.linkedTo)
    ).apply(instance, EntityLinkContainer::new));

    protected final Set<EntityLinkConnection> linkedTo;

    public EntityLinkContainer() {
        this(new HashSet<>());
    }

    protected EntityLinkContainer(Set<EntityLinkConnection> linkedTo) {
        this.linkedTo = linkedTo;
    }

    public LinkResult canLink(Level level, BlockPos from, Entity to) {
        return this.canLink(level, from, to.getUUID());
    }

    public LinkResult canLink(Level level, BlockPos from, UUID toId) {
        if (this.containsLink(toId)) {
            return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.duplicate").withStyle(ChatFormatting.RED));
        }
        return LinkResult.success();
    }

    public LinkResult link(Level level, BlockPos from, Entity to, Linkable.LinkAction action) {
        return this.link(level, from, to.getUUID(), action);
    }

    public LinkResult link(Level level, BlockPos from, UUID toId, Linkable.LinkAction action) {
        if (action.simulate()) {
            if (this.containsLink(toId)) {
                return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.duplicate").withStyle(ChatFormatting.RED));
            }
            return LinkResult.success();
        }
        LinkResult canLink = this.canLink(level, from, toId);
        if (!canLink.isSuccess()) {
            return canLink;
        }
        if (!this.linkedTo.add(new EntityLinkConnection(toId))) {
            return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.duplicate").withStyle(ChatFormatting.RED));
        }
        return LinkResult.success();
    }

    public LinkResult unlink(Level level, BlockPos from, Entity to, Linkable.LinkAction action) {
        return this.unlink(level, from, to.getUUID(), action);
    }

    public LinkResult unlink(Level level, BlockPos from, UUID toId, Linkable.LinkAction action) {
        if (action.simulate()) {
            if (!this.containsLink(toId)) {
                return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.missing").withStyle(ChatFormatting.RED));
            }
            return LinkResult.success();
        }
        if (!this.linkedTo.removeIf(conn -> conn.entityUid().equals(toId))) {
            return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.missing").withStyle(ChatFormatting.RED));
        }
        return LinkResult.success();
    }

    public boolean containsLink(Entity other) {
        return this.containsLink(other.getUUID());
    }

    public boolean containsLink(UUID otherId) {
        return this.linkedTo.stream().anyMatch(conn -> conn.entityUid().equals(otherId));
    }

    public int getLinkedToCount() {
        return this.linkedTo.size();
    }

    public Collection<EntityLinkConnection> getLinkedTo() {
        return Collections.unmodifiableSet(this.linkedTo);
    }
}
