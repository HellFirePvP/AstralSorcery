/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary.link;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LinkHandler
 * Created by HellFirePvP
 * Date: 30.06.2019 / 20:57
 */
public class LinkHandler implements ITickHandler {

    private static final LinkHandler instance = new LinkHandler();

    private static final Map<UUID, LinkSession> players = new HashMap<>();

    private LinkHandler() {}

    public static LinkHandler getInstance() {
        return instance;
    }

    @Nullable
    public static LinkSession getActiveSession(PlayerEntity player) {
        return players.get(player.getUniqueID());
    }

    @Nonnull
    public static RightClickResult onInteractEntity(PlayerEntity clicked, LivingEntity entity) {
        LinkSession session = LinkSession.entity(entity);
        players.put(clicked.getUniqueID(), session);
        return new RightClickResult(RightClickResultType.SELECT_START, session);
    }

    @Nonnull
    public static RightClickResult onInteractBlock(PlayerEntity clicked, World world, BlockPos pos, boolean sneak) {
        UUID playerUUID = clicked.getUniqueID();
        if (!players.containsKey(playerUUID)) {
            LinkableTileEntity tile = MiscUtils.getTileAt(world, pos, LinkableTileEntity.class, true);
            if (tile == null) {
                return new RightClickResult(RightClickResultType.NONE, null);
            }

            LinkSession session = LinkSession.tile(tile);
            players.put(playerUUID, session);
            return new RightClickResult(RightClickResultType.SELECT_START, session);
        } else {
            LinkSession session = players.get(playerUUID);
            if (session.getType() == LinkType.ENTITY) {
                LinkableTileEntity tile = MiscUtils.getTileAt(world, pos, LinkableTileEntity.class, true);
                if (tile == null) {
                    players.remove(playerUUID);
                    return new RightClickResult(RightClickResultType.NONE, null);
                } else {
                    session.setSelected(tile);
                    return new RightClickResult(RightClickResultType.TRY_LINK, session);
                }
            } else if (sneak) {
                return new RightClickResult(RightClickResultType.TRY_UNLINK, session);
            } else {
                return new RightClickResult(RightClickResultType.TRY_LINK, session);
            }
        }
    }

    public static void processInteraction(RightClickResult result, PlayerEntity playerIn, World world, BlockPos pos) {
        LinkSession session = result.getLinkingSession();
        LinkableTileEntity tile = session.getSelectedTile();
        String linkedToName;
        switch (result.getType()) {
            case SELECT_START:
                if (session.getType() == LinkType.ENTITY) {
                    playerIn.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.start",
                            result.getLinkingSession().getSelectedEntity().getDisplayName()).mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
                } else {
                    String name = tile.getUnLocalizedDisplayName();
                    if (tile.onSelect(playerIn)) {
                        if (name != null) {
                            playerIn.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.start",
                                    new TranslationTextComponent(name)).mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
                        }
                    }
                }
                break;
            case TRY_LINK:
                TileEntity te = MiscUtils.getTileAt(world, pos, TileEntity.class, true);
                linkedToName = "astralsorcery.misc.link.link.block";
                if (te instanceof LinkableTileEntity) {
                    if (!((LinkableTileEntity) te).doesAcceptLinks()) {
                        return;
                    }
                    String unloc = ((LinkableTileEntity) te).getUnLocalizedDisplayName();
                    if (unloc != null) {
                        linkedToName = unloc;
                    }
                }

                if (session.getType() == LinkType.ENTITY && te instanceof LinkableTileEntity) {
                    LinkableTileEntity linkTarget = (LinkableTileEntity) te;
                    LivingEntity linked = session.getSelectedEntity();
                    if (linkTarget.tryLinkEntity(playerIn, linked)) {
                        linkTarget.onEntityLinkCreate(playerIn, linked);
                    }
                } else {
                    if (tile.tryLinkBlock(playerIn, pos)) {
                        tile.onBlockLinkCreate(playerIn, pos);
                        String linkedFrom = tile.getUnLocalizedDisplayName();
                        if (linkedFrom != null) {
                            playerIn.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.link",
                                    new TranslationTextComponent(linkedFrom),
                                    new TranslationTextComponent(linkedToName))
                                    .mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
                        }
                    }
                }
                break;
            case TRY_UNLINK:
                if (tile.tryUnlink(playerIn, pos)) {
                    linkedToName = "astralsorcery.misc.link.link.block";
                    te = MiscUtils.getTileAt(world, pos, TileEntity.class, true);
                    if (te instanceof LinkableTileEntity) {
                        String unloc = ((LinkableTileEntity) te).getUnLocalizedDisplayName();
                        if (unloc != null) {
                            linkedToName = unloc;
                        }
                    }
                    String linkedFrom = tile.getUnLocalizedDisplayName();
                    if (linkedFrom != null) {
                        playerIn.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.unlink",
                                new TranslationTextComponent(linkedFrom),
                                new TranslationTextComponent(linkedToName))
                                .mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
                    }
                }
                break;
            case NONE:
                break;
            default:
                break;
        }
    }
    @Override
    public void tick(TickEvent.Type type, Object... context) {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if (server == null) {
            return;
        }

        Iterator<UUID> iterator = players.keySet().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            LinkSession session = players.get(uuid);
            PlayerEntity player = server.getPlayerList().getPlayerByUUID(uuid);
            if (player == null) {
                iterator.remove();
                continue;
            }

            boolean needsRemoval = MiscUtils.getMainOrOffHand(player, stack -> stack.getItem() instanceof IItemLinkingTool) == null;

            switch (session.getType()) {
                case ENTITY:
                    LivingEntity entity = session.getSelectedEntity();
                    if (!entity.isAlive() || !entity.getEntityWorld().getDimensionKey().equals(player.getEntityWorld().getDimensionKey())) {
                        needsRemoval = true;
                    }
                    break;
                case BLOCK:
                    if (!session.getSelectedTile().getLinkWorld().getDimensionKey().equals(player.getEntityWorld().getDimensionKey())) {
                        needsRemoval = true;
                    }
                    break;
            }
            if (needsRemoval) {
                iterator.remove();
                player.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.stop")
                        .mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "LinkHandler";
    }

    public static class LinkSession {

        private final LinkType type;
        private LinkableTileEntity selected;
        private final LivingEntity entity;

        private LinkSession(LinkType type, LinkableTileEntity selected, LivingEntity entity) {
            this.type = type;
            this.selected = selected;
            this.entity = entity;
        }

        public static LinkSession tile(LinkableTileEntity selected) {
            return new LinkSession(LinkType.BLOCK, selected, null);
        }

        public static LinkSession entity(LivingEntity entity) {
            return new LinkSession(LinkType.ENTITY, null, entity);
        }

        public LinkType getType() {
            return type;
        }

        @Nullable
        public LinkableTileEntity getSelectedTile() {
            return selected;
        }

        public void setSelected(LinkableTileEntity selected) {
            this.selected = selected;
        }

        @Nullable
        public LivingEntity getSelectedEntity() {
            return entity;
        }
    }

    public static class RightClickResult {

        private final RightClickResultType type;
        private final LinkSession linkingSession;

        RightClickResult(RightClickResultType type, LinkSession linkingSession) {
            this.type = type;
            this.linkingSession = linkingSession;
        }

        public RightClickResultType getType() {
            return type;
        }

        public LinkSession getLinkingSession() {
            return linkingSession;
        }

        public boolean shouldProcess() {
            return this.getType() != RightClickResultType.NONE;
        }
    }

    public static enum LinkType {

        ENTITY,
        BLOCK

    }

    public static enum RightClickResultType {

        SELECT_START,
        TRY_LINK,
        TRY_UNLINK,
        NONE

    }

}