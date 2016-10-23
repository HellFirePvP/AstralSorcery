package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.client.gui.journal.page.IJournalPage;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchNode
 * Created by HellFirePvP
 * Date: 12.08.2016 / 20:51
 */
public class ResearchNode {

    private static int counter = 0;

    private final int id;
    private final RenderType renderType;
    public final int renderPosX, renderPosZ;
    private String unlocName;
    //private boolean special = false;

    private ItemStack[] renderItemStacks;
    private BindableResource texture;

    private List<ResearchNode> connectionsTo = new LinkedList<>();
    private List<IJournalPage> pages = new LinkedList<>();

    private ResearchNode(RenderType type, String unlocName, int rPosX, int rPosZ) {
        this.id = counter;
        counter++;
        this.renderType = type;
        this.renderPosX = rPosX;
        this.renderPosZ = rPosZ;
        this.unlocName = unlocName;
    }

    public ResearchNode(ItemStack itemStack, String unlocName, int renderPosX, int renderPosZ) {
        this(RenderType.ITEMSTACK, unlocName, renderPosX, renderPosZ);
        this.renderItemStacks = new ItemStack[] { itemStack };
    }

    public ResearchNode(ItemStack[] stacks, String unlocName, int renderPosX, int renderPosZ) {
        this(RenderType.ITEMSTACK, unlocName, renderPosX, renderPosZ);
        this.renderItemStacks = stacks;
    }

    public ResearchNode(BindableResource textureResource, String unlocName, int renderPosX, int renderPosZ) {
        this(RenderType.TEXTURE, unlocName, renderPosX, renderPosZ);
        this.texture = textureResource;
    }

    public ResearchNode addConnectionTo(ResearchNode node) {
        this.connectionsTo.add(node);
        return this;
    }
    public ResearchNode addConnectionsTo(Collection<ResearchNode> node) {
        this.connectionsTo.addAll(node);
        return this;
    }

    public List<ResearchNode> getConnectionsTo() {
        return connectionsTo;
    }

    public ResearchNode addPage(IJournalPage page) {
        pages.add(page);
        return this;
    }

    /*public ResearchNode setSpecial() {
        this.special = true;
        return this;
    }

    public boolean isSpecial() {
        return special;
    }*/

    public RenderType getRenderType() {
        return renderType;
    }

    public ItemStack getRenderItemStack() {
        return getRenderItemStack(0);
    }

    public ItemStack getRenderItemStack(int tick) {
        return renderItemStacks[(tick / 60) % renderItemStacks.length];
    }

    public BindableResource getTexture() {
        return texture;
    }

    public List<IJournalPage> getPages() {
        return pages;
    }

    public String getUnLocalizedName() {
        return String.format("research.%s.name", unlocName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResearchNode that = (ResearchNode) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static enum RenderType {

        ITEMSTACK, TEXTURE

    }

}
