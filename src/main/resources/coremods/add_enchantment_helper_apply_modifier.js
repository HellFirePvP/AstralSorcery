function initializeCoreMod() {
    return {
        'post_process_vanilla': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.enchantment.EnchantmentHelper',
                'methodName': 'applyEnchantmentModifier',
                'methodDesc': '(Lnet/minecraft/enchantment/EnchantmentHelper$IEnchantmentVisitor;Lnet/minecraft/item/ItemStack;)V'
            },
            'transformer': function(method) {
                print('[AstralSorcery] Adding \'add_enchantment_helper_apply_modifier\' ASM patch...');

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var methodCall = ASMAPI.findFirstMethodCallAfter(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/item/ItemStack',
                        'getEnchantmentTagList',
                        '()Lnet/minecraft/nbt/ListNBT;',
                        0);

                while (methodCall !== null) {
                    var next = methodCall.getNext();

                    method.instructions.insertBefore(next, new VarInsnNode(Opcodes.ALOAD, 1)); //ItemStack
                    method.instructions.insertBefore(next, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'addNewEnchantmentLevelsTag',
                        '(Lnet/minecraft/nbt/NBTTagList;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NBTTagList;',
                        ASMAPI.MethodType.STATIC));

                    methodCall = ASMAPI.findFirstMethodCallAfter(method,
                            ASMAPI.MethodType.VIRTUAL,
                            'net/minecraft/item/ItemStack',
                            'getEnchantmentTagList',
                            '()Lnet/minecraft/nbt/ListNBT;',
                            method.instructions.indexOf(methodCall) + 1);
                }

                print('[AstralSorcery] Added \'add_enchantment_helper_apply_modifier\' ASM patch!');
                return method;
            }
        }
    }
}