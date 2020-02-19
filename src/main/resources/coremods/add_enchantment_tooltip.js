function initializeCoreMod() {
    return {
        'add_enchantment_tooltip': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.item.ItemStack',
                'methodName': 'func_82840_a',
                'methodDesc': '(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List;'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'add_enchantment_tooltip\' ASM patch...');

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
                    var node = methodCall.getNext();

                    method.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0)); //ItemStack
                    method.instructions.insertBefore(node, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'addNewEnchantmentLevelsTag',
                        '(Lnet/minecraft/nbt/ListNBT;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/ListNBT;',
                        ASMAPI.MethodType.STATIC));

                    methodCall = ASMAPI.findFirstMethodCallAfter(method,
                            ASMAPI.MethodType.VIRTUAL,
                            'net/minecraft/item/ItemStack',
                            callGetEnchantmentTagList,
                            '()Lnet/minecraft/nbt/ListNBT;',
                            method.instructions.indexOf(methodCall) + 2);
                }

                ASMAPI.log('INFO', 'Added \'add_enchantment_tooltip\' ASM patch!');
                return method;
            }
        }
    }
}