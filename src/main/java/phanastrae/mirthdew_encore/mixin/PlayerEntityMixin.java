package phanastrae.mirthdew_encore.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.duck.PlayerEntityDuckInterface;
import phanastrae.mirthdew_encore.entity.MirthdewEncorePlayerEntityAttachment;

import static phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityDuckInterface {

    @Unique
    private MirthdewEncorePlayerEntityAttachment mirthdew_encore$playerEntityAttachment;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthdew_encore$init(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        this.mirthdew_encore$playerEntityAttachment = new MirthdewEncorePlayerEntityAttachment((PlayerEntity)(Object)this);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void mirthdew_encore$tick(CallbackInfo ci) {
        this.mirthdew_encore$playerEntityAttachment.tick();
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void mirthdew_encore$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        if(nbt.contains("MirthdewEncore", NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtCompound = nbt.getCompound("MirthdewEncore");
            this.mirthdew_encore$playerEntityAttachment.writeNbt(nbtCompound);
        } else {
            NbtCompound nbtCompound = new NbtCompound();
            this.mirthdew_encore$playerEntityAttachment.writeNbt(nbtCompound);
            nbt.put("MirthdewEncore", nbtCompound);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void mirthdew_encore$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if(nbt.contains("MirthdewEncore", NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtCompound = nbt.getCompound("MirthdewEncore");
            this.mirthdew_encore$playerEntityAttachment.readNbt(nbtCompound);
        }
    }

    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$canAlwaysConsumeIfDreamyDieting(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasStatusEffect(DREAMY_DIET_ENTRY)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public MirthdewEncorePlayerEntityAttachment mirthdew_encore$getAttachment() {
        return mirthdew_encore$playerEntityAttachment;
    }
}
