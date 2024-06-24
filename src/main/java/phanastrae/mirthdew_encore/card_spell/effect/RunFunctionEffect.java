package phanastrae.mirthdew_encore.card_spell.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.util.Optional;

public record RunFunctionEffect(Identifier function) {
    public static final MapCodec<RunFunctionEffect> CODEC =
            RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                                    Identifier.CODEC.fieldOf("function").forGetter(RunFunctionEffect::function)
                            )
                            .apply(instance, RunFunctionEffect::new)
    );

    public void castSpell(ServerWorld world, Entity user) {
        MinecraftServer minecraftServer = world.getServer();
        CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
        Optional<CommandFunction<ServerCommandSource>> optional = commandFunctionManager.getFunction(this.function);
        if (optional.isPresent()) {
            ServerCommandSource serverCommandSource = minecraftServer.getCommandSource()
                    .withLevel(2)
                    .withSilent()
                    .withEntity(user)
                    .withWorld(world)
                    .withPosition(user.getPos())
                    .withRotation(user.getRotationClient());
            commandFunctionManager.execute((CommandFunction<ServerCommandSource>)optional.get(), serverCommandSource);
        } else {
            MirthdewEncore.LOGGER.error("Card Spell run_function effect failed for non-existent function {}", this.function);
        }
    }
}
