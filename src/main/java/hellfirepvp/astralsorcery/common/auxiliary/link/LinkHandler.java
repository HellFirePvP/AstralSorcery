/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary.link;

import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LinkHandler
 * Created by HellFirePvP
 * Date: 30.06.2019 / 20:57
 */
public class LinkHandler implements ITickHandler {

    private static final LinkHandler instance = new LinkHandler();

    private static Map<PlayerEntity, LinkSession> players = new HashMap<>();

    private LinkHandler() {}

    public static LinkHandler getInstance() {
        return instance;
    }

    @Nonnull
    public static RightClickResult onRightClick(PlayerEntity clicked, World world, BlockPos pos, boolean sneak) {
        if (!players.containsKey(clicked)) {
            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof LinkableTileEntity)) {
                return new RightClickResult(RightClickResultType.NONE, null);
            }
            LinkableTileEntity tile = (LinkableTileEntity) te;

            players.put(clicked, new LinkSession(tile));
            return new RightClickResult(RightClickResultType.SELECT, tile);
        } else {
            LinkSession l = players.get(clicked);
            if (sneak) {
                return new RightClickResult(RightClickResultType.TRY_UNLINK, l.selected);
            } else {
                return new RightClickResult(RightClickResultType.TRY_LINK, l.selected);
            }
        }
    }

    public static void propagateClick(RightClickResult result, PlayerEntity playerIn, World worldIn, BlockPos pos) {
        LinkableTileEntity tile = result.getInteracted();
        Style green = new Style().setColor(TextFormatting.GREEN);
        switch (result.getType()) {
            case SELECT:
                String name = tile.getUnLocalizedDisplayName();
                if (tile.onSelect(playerIn)) {
                    if (name != null) {
                        playerIn.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.start", new TranslationTextComponent(name)).setStyle(green));
                    }
                }
                break;
            case TRY_LINK:
                TileEntity te = worldIn.getTileEntity(pos);
                if (te instanceof LinkableTileEntity) {
                    if (!((LinkableTileEntity) te).doesAcceptLinks()) return;
                }
                if (tile.tryLink(playerIn, pos)) {
                    tile.onLinkCreate(playerIn, pos);
                    String linkedTo = "astralsorcery.misc.link.link.block";
                    if (te instanceof LinkableTileEntity) {
                        String unloc = ((LinkableTileEntity) te).getUnLocalizedDisplayName();
                        if (unloc != null) {
                            linkedTo = unloc;
                        }
                    }
                    String linkedFrom = tile.getUnLocalizedDisplayName();
                    if (linkedFrom != null) {
                        playerIn.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.link", new TranslationTextComponent(linkedFrom), new TranslationTextComponent(linkedTo)).setStyle(green));
                    }
                }
                break;
            case TRY_UNLINK:
                if (tile.tryUnlink(playerIn, pos)) {
                    String linkedTo = "astralsorcery.misc.link.link.block";
                    te = worldIn.getTileEntity(pos);
                    if (te instanceof LinkableTileEntity) {
                        String unloc = ((LinkableTileEntity) te).getUnLocalizedDisplayName();
                        if (unloc != null) {
                            linkedTo = unloc;
                        }
                    }
                    String linkedFrom = tile.getUnLocalizedDisplayName();
                    if (linkedFrom != null) {
                        playerIn.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.unlink", new TranslationTextComponent(linkedFrom), new TranslationTextComponent(linkedTo)).setStyle(green));
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
        Iterator<PlayerEntity> iterator = players.keySet().iterator();
        while (iterator.hasNext()) {
            PlayerEntity pl = iterator.next();
            LinkSession session = players.get(pl);

            boolean needsRemoval = true;
            ItemStack inhand = pl.getHeldItemMainhand();
            if (!inhand.isEmpty() && inhand.getItem() instanceof IItemLinkingTool)
                needsRemoval = false;
            inhand = pl.getHeldItemOffhand();
            if (!inhand.isEmpty() && inhand.getItem() instanceof IItemLinkingTool)
                needsRemoval = false;
            int dimId = session.selected.getLinkWorld().getDimension().getType().getId();
            if (dimId != pl.dimension.getId()) needsRemoval = true;
            if (needsRemoval) {
                iterator.remove();
                pl.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.stop").setStyle(new Style().setColor(TextFormatting.RED)));
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

        private final LinkableTileEntity selected;

        public LinkSession(LinkableTileEntity selected) {
            this.selected = selected;
        }

    }

    public static class RightClickResult {

        private final RightClickResultType type;
        private final LinkableTileEntity interacted;

        RightClickResult(RightClickResultType type, LinkableTileEntity interacted) {
            this.type = type;
            this.interacted = interacted;
        }

        public RightClickResultType getType() {
            return type;
        }

        LinkableTileEntity getInteracted() {
            return interacted;
        }
    }

    public static enum RightClickResultType {

        SELECT,
        TRY_LINK,
        TRY_UNLINK,
        NONE

    }

}