/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2021
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMatchInformation
 * Created by HellFirePvP
 * Date: 13.10.2019 / 10:07
 */
public class BlockMatchInformation implements Predicate<BlockState> {

    private final ItemStack display;

    private BlockState matchState;
    private boolean matchExact;

    private ITag<Block> matchTag;
    private ResourceLocation matchTagKey;

    public BlockMatchInformation(ITag<Block> matchTag) {
        this(matchTag, createDisplayStack(matchTag));
    }

    public BlockMatchInformation(ITag<Block> matchTag, ItemStack display) {
        this.matchTag = matchTag;
        this.matchTagKey = TagCollectionManager.getManager().getBlockTags().getDirectIdFromTag(matchTag);
        this.display = display;

        if (this.matchTagKey == null) {
            throw new IllegalArgumentException("Unknown block tag name!");
        }
        if (this.display.isEmpty()) {
            throw new IllegalArgumentException("No display ItemStack passed, and unable to create valid itemstack from block tag " + this.matchTagKey.toString() + "!");
        }
    }

    public BlockMatchInformation(BlockState matchState, boolean matchExact) {
        this(matchState, ItemUtils.createBlockStack(matchState), matchExact);
    }

    public BlockMatchInformation(BlockState matchState, ItemStack display, boolean matchExact) {
        this.matchState = matchState;
        this.display = display;
        this.matchExact = matchExact;

        if (this.display.isEmpty()) {
            throw new IllegalArgumentException("No display ItemStack passed, and " + matchState.getBlock().getRegistryName() + " has no associated ItemBlock!");
        }
    }

    private static ItemStack createDisplayStack(ITag<Block> blockTag) {
        for (Block block : blockTag.getAllElements()) {
            ItemStack blockStack = ItemUtils.createBlockStack(block.getDefaultState());
            if (!blockStack.isEmpty()) {
                return blockStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isValid() {
        if (this.matchState != null && this.matchState.getBlock() instanceof AirBlock) {
            return false;
        }
        return true;
    }

    @Nonnull
    public ItemStack getDisplayStack() {
        return this.display.copy();
    }

    @Override
    public boolean test(BlockState state) {
        if (this.matchState != null) {
            return this.matchExact ? BlockUtils.matchStateExact(state, this.matchState) : state.getBlock().equals(this.matchState.getBlock());
        }
        if (this.matchTag != null) {
            return this.matchTag.contains(state.getBlock());
        }
        return false;
    }

    public static BlockMatchInformation read(JsonObject object) {
        if (object.has("block")) {
            BlockState state = BlockStateHelper.deserializeObject(object);
            boolean fullyDefined = !BlockStateHelper.isMissingStateInformation(object);
            ItemStack display = new ItemStack(state.getBlock());
            if (object.has("display")) {
                display = JsonHelper.getItemStack(object, "display");
            }
            return new BlockMatchInformation(state, display, fullyDefined);
        } else if (object.has("tag")) {
            ITag<Block> blockTag = TagCollectionManager.getManager().getBlockTags().get(new ResourceLocation(object.get("tag").getAsString()));
            if (object.has("display")) {
                ItemStack display = JsonHelper.getItemStack(object, "display");
                return new BlockMatchInformation(blockTag, display);
            }
            return new BlockMatchInformation(blockTag);
        }
        throw new JsonSyntaxException("Neither block nor tag defined for block transmutation match information!");
    }

    public JsonObject serializeJson() {
        JsonObject out = new JsonObject();
        if (this.matchState != null) {
            BlockStateHelper.serializeObject(out, this.matchState, this.matchExact);
            out.add("display", JsonHelper.serializeItemStack(this.getDisplayStack()));
        } else if (this.matchTag != null) {
            out.add("tag", new JsonPrimitive(this.matchTagKey.toString()));
        }
        return out;
    }

    public static BlockMatchInformation read(PacketBuffer buf) {
        int type = buf.readInt();
        ItemStack display = ByteBufUtils.readItemStack(buf);
        switch (type) {
            case 0:
                BlockState state = ByteBufUtils.readBlockState(buf);
                boolean exactMatch = buf.readBoolean();
                return new BlockMatchInformation(state, display, exactMatch);
            case 1:
                String tagName = ByteBufUtils.readString(buf);
                ITag<Block> blockTag = TagCollectionManager.getManager().getBlockTags().get(new ResourceLocation(tagName));
                return new BlockMatchInformation(blockTag, display);
        }
        throw new IllegalArgumentException("Unknown block transmutation match type: " + type);
    }

    public void serialize(PacketBuffer buf) {
        int type = this.matchState != null ? 0 /*state*/ : 1 /*type*/;
        buf.writeInt(type);
        ByteBufUtils.writeItemStack(buf, this.display);

        switch (type) {
            case 0:
                ByteBufUtils.writeBlockState(buf, this.matchState);
                buf.writeBoolean(this.matchExact);
                break;
            case 1:
                ByteBufUtils.writeString(buf, this.matchTagKey.toString());
                break;
        }
    }
}
