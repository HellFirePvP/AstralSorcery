package hellfirepvp.astralsorcery.common.auxiliary.link;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
 * Date: 03.08.2016 / 18:32
 */
public class LinkHandler implements ITickHandler {

    private static Map<EntityPlayer, LinkSession> players = new HashMap<>();

    @Nonnull
    public static RightClickResult onRightClick(EntityPlayer clicked, World world, BlockPos pos, boolean sneak) {
        if(!players.containsKey(clicked)) {
            TileEntity te = world.getTileEntity(pos);
            if(te == null || !(te instanceof ILinkableTile)) {
                return new RightClickResult(RightClickResultType.NONE, null);
            }
            ILinkableTile tile = (ILinkableTile) te;

            players.put(clicked, new LinkSession(tile));
            return new RightClickResult(RightClickResultType.SELECT, tile);
        } else {
            LinkSession l = players.get(clicked);
            if(sneak) {
                return new RightClickResult(RightClickResultType.TRY_UNLINK, l.selected);
            } else {
                return new RightClickResult(RightClickResultType.TRY_LINK, l.selected);
            }
        }
    }

    public static void propagateClick(RightClickResult result, EntityPlayer playerIn, World worldIn, BlockPos pos) {
        ILinkableTile tile = result.getInteracted();
        switch (result.getType()) {
            case SELECT:
                String name = tile.getUnlocalizedDisplayName();
                if(name != null) {
                    playerIn.addChatMessage(new TextComponentString("§a" + I18n.translateToLocalFormatted("misc.link.start", I18n.translateToLocal(name))));
                }
                tile.onSelect(playerIn);
                break;
            case TRY_LINK:
                TileEntity te = worldIn.getTileEntity(pos);
                if(te != null && te instanceof ILinkableTile) {
                    if(!((ILinkableTile) te).doesAcceptLinks()) return;
                }
                if(tile.tryLink(playerIn, pos)) {
                    tile.onLinkCreate(playerIn, pos);
                    String linkedTo = I18n.translateToLocal("misc.link.link.block");
                    if(te != null && te instanceof ILinkableTile) {
                        String unloc = ((ILinkableTile) te).getUnlocalizedDisplayName();
                        if(unloc != null) {
                            linkedTo = I18n.translateToLocal(unloc);
                        }
                    }
                    String linkedFrom = tile.getUnlocalizedDisplayName();
                    if(linkedFrom != null) {
                        playerIn.addChatMessage(new TextComponentString("§a" + I18n.translateToLocalFormatted("misc.link.link", I18n.translateToLocal(linkedFrom), linkedTo)));
                    }
                }
                break;
            case TRY_UNLINK:
                if(tile.tryUnlink(playerIn, pos)) {
                    String linkedTo = I18n.translateToLocal("misc.link.link.block");
                    te = worldIn.getTileEntity(pos);
                    if(te != null && te instanceof ILinkableTile) {
                        String unloc = ((ILinkableTile) te).getUnlocalizedDisplayName();
                        if(unloc != null) {
                            linkedTo = I18n.translateToLocal(unloc);
                        }
                    }
                    String linkedFrom = tile.getUnlocalizedDisplayName();
                    if(linkedFrom != null) {
                        playerIn.addChatMessage(new TextComponentString("§a" + I18n.translateToLocalFormatted("misc.link.unlink", I18n.translateToLocal(linkedFrom), linkedTo)));
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
        Iterator<EntityPlayer> iterator = players.keySet().iterator();
        while (iterator.hasNext()) {
            EntityPlayer pl = iterator.next();
            LinkSession session = players.get(pl);

            boolean needsRemoval = true;
            ItemStack inhand = pl.getHeldItemMainhand();
            if (inhand != null && inhand.getItem() != null && inhand.getItem() instanceof IItemLinkingTool)
                needsRemoval = false;
            inhand = pl.getHeldItemOffhand();
            if (inhand != null && inhand.getItem() != null && inhand.getItem() instanceof IItemLinkingTool)
                needsRemoval = false;
            int dimId = session.selected.getWorld().provider.getDimension();
            if(dimId != pl.dimension) needsRemoval = true;
            if (needsRemoval) {
                iterator.remove();
                pl.addChatMessage(new TextComponentString("§c" + I18n.translateToLocal("misc.link.stop")));
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

        private final ILinkableTile selected;

        public LinkSession(ILinkableTile selected) {
            this.selected = selected;
        }

    }

    public static class RightClickResult {

        private final RightClickResultType type;
        private final ILinkableTile interacted;

        public RightClickResult(RightClickResultType type, ILinkableTile interacted) {
            this.type = type;
            this.interacted = interacted;
        }

        public RightClickResultType getType() {
            return type;
        }

        public ILinkableTile getInteracted() {
            return interacted;
        }
    }

    public static enum RightClickResultType {

        SELECT,
        TRY_LINK,
        TRY_UNLINK,
        NONE

    }

    public static interface IItemLinkingTool {}

}
