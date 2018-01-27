/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core.patch.helper;

import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchModifyEnchantmentLevels
 * Created by HellFirePvP
 * Date: 27.01.2018 / 11:41
 */
public class PatchModifyEnchantmentLevels extends ClassPatch {

    public PatchModifyEnchantmentLevels() {
        super("net.minecraft.enchantment.EnchantmentHelper");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethodLazy(cn, "getEnchantmentLevel", "func_77506_a");
        int peek = peekFirstInstructionAfter(mn, 0, Opcodes.IRETURN);
        while (peek != -1) {
            AbstractInsnNode node = mn.instructions.get(peek);
            //The old value is on the stack right now
            mn.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0)); //Enchantment
            mn.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 1)); //ItemStack
            mn.instructions.insertBefore(node, new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "hellfirepvp/astralsorcery/common/enchantment/amulet/EnchantmentUpgradeHelper",
                    "getNewEnchantmentLevel",
                    "(ILnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I",
                    false));

            peek = peekFirstInstructionAfter(mn, peek + 4, Opcodes.IRETURN);
        }

        mn = getMethodLazy(cn, "getEnchantments", "func_82781_a");
        peek = peekFirstInstructionAfter(mn, 0, Opcodes.ARETURN);
        while (peek != -1) {
            AbstractInsnNode node = mn.instructions.get(peek);
            //The stack currently contains the calculated enchantment->level mapping
            mn.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0)); //ItemStack
            mn.instructions.insertBefore(node, new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "hellfirepvp/astralsorcery/common/enchantment/amulet/EnchantmentUpgradeHelper",
                    "applyNewEnchantmentLevels",
                    "(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)Ljava/util/Map;",
                    false));

            peek = peekFirstInstructionAfter(mn, peek + 3, Opcodes.IRETURN);
        }

        mn = getMethodLazy(cn, "applyEnchantmentModifier", "func_77518_a");
        peek = peekFirstMethodCallAfter(mn,
                "net/minecraft/item/ItemStack",
                "getEnchantmentTagList",
                "func_77986_q",
                "()Lnet/minecraft/nbt/NBTTagList;",
                0);
        while (peek != -1) {
            AbstractInsnNode node = mn.instructions.get(peek).getNext();
            //The actual enchantment-NBT-list is on the stack right now.
            mn.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 1)); //ItemStack
            mn.instructions.insertBefore(node, new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "hellfirepvp/astralsorcery/common/enchantment/amulet/EnchantmentUpgradeHelper",
                    "modifyEnchantmentTags",
                    "(Lnet/minecraft/nbt/NBTTagList;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NBTTagList;",
                    false));

            peek = peekFirstMethodCallAfter(mn,
                    "net/minecraft/item/ItemStack",
                    "getEnchantmentTagList",
                    "func_77986_q",
                    "()Lnet/minecraft/nbt/NBTTagList;",
                    peek + 3);
        }
    }

}
