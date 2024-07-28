package phanastrae.mirthdew_encore.card_spell.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.util.Optional;

public record RunFunctionEffect(ResourceLocation function) {
    public static final MapCodec<RunFunctionEffect> CODEC =
            RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                                    ResourceLocation.CODEC.fieldOf("function").forGetter(RunFunctionEffect::function)
                            )
                            .apply(instance, RunFunctionEffect::new)
    );

    public void castSpell(ServerLevel world, Entity user) {
        MinecraftServer minecraftServer = world.getServer();
        ServerFunctionManager commandFunctionManager = minecraftServer.getFunctions();
        Optional<CommandFunction<CommandSourceStack>> optional = commandFunctionManager.get(this.function);
        if (optional.isPresent()) {
            CommandSourceStack serverCommandSource = minecraftServer.createCommandSourceStack()
                    .withPermission(2)
                    .withSuppressedOutput()
                    .withEntity(user)
                    .withLevel(world)
                    .withPosition(user.position())
                    .withRotation(user.getRotationVector());
            commandFunctionManager.execute((CommandFunction<CommandSourceStack>)optional.get(), serverCommandSource);
        } else {
            MirthdewEncore.LOGGER.error("Card Spell run_function effect failed for non-existent function {}", this.function);
        }
    }
}
