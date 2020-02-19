function initializeCoreMod() {
    return {
        'add_enchantment_helper_apply_modifier': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.enchantment.EnchantmentHelper',
                'methodName': 'func_77518_a',
                'methodDesc': '(Lnet/minecraft/enchantment/EnchantmentHelper$IEnchantmentVisitor;Lnet/minecraft/item/ItemStack;)V'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'add_enchantment_helper_apply_modifier\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var callGetEnchantmentTagList = ASMAPI.mapMethod('func_77986_q');

                var methodCall = ASMAPI.findFirstMethodCallAfter(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/item/ItemStack',
                        callGetEnchantmentTagList,
                        '()Lnet/minecraft/nbt/ListNBT;',
                        0);

                while (methodCall !== null) {
                    var next = methodCall.getNext();

                    method.instructions.insertBefore(next, new VarInsnNode(Opcodes.ALOAD, 1)); //ItemStack
                    method.instructions.insertBefore(next, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'addNewEnchantmentLevelsTag',
                        '(Lnet/minecraft/nbt/ListNBT;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/ListNBT;',
                        ASMAPI.MethodType.STATIC));

                    methodCall = ASMAPI.findFirstMethodCallAfter(method,
                            ASMAPI.MethodType.VIRTUAL,
                            'net/minecraft/item/ItemStack',
                            callGetEnchantmentTagList,
                            '()Lnet/minecraft/nbt/ListNBT;',
                            method.instructions.indexOf(methodCall) + 1);
                }

                ASMAPI.log('INFO', 'Added \'add_enchantment_helper_apply_modifier\' ASM patch!');
                return method;
            }
        }
    }
}