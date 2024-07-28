package phanastrae.mirthdew_encore.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.block.entity.VericDreamsnareBlockEntity;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreEntityTypeTags;

public class DreamspeckEntity extends Mob {

    private boolean snared = false;
    @Nullable
    private BlockPos snarePos = null;
    private int decayTimer = 0;

    public DreamspeckEntity(EntityType<? extends Mob> type, Level world) {
        super(type, world);
        this.moveControl = new DreamspeckEntity.DreamspeckMoveControl(this);
    }

    public static AttributeSupplier.Builder createDreamspeckAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.15);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FlyRandomlyGoal(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("decay_timer", this.decayTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if(nbt.contains("decay_timer", Tag.TAG_INT)) {
            this.decayTimer = nbt.getInt("decay_timer");
        }
    }

    @Override
    public void tick() {
        if (this.random.nextInt(36) == 0 && !this.isSilent()) {
            this.level()
                    .playLocalSound(
                            this.getX() + 0.5,
                            this.getY() + 0.5,
                            this.getZ() + 0.5,
                            SoundEvents.AMETHYST_BLOCK_RESONATE,
                            this.getSoundSource(),
                            0.5F + this.random.nextFloat() * 0.2F,
                            0.3F + this.random.nextFloat() * 1.5F,
                            false
                    );
            this.level().gameEvent(this, GameEvent.STEP, this.position());
        }

        if (this.level().isClientSide) {
            for(int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.WITCH, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
                this.level().addParticle(ParticleTypes.ENCHANT, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
        }

        if(!this.level().isClientSide && this.level() instanceof ServerLevel serverWorld) {
            if(this.snared && this.tickCount % 20 == 0) {
                BlockEntity blockEntity = this.level().getBlockEntity(snarePos);
                if(!(blockEntity instanceof VericDreamsnareBlockEntity dreamsnareBlockEntity) || dreamsnareBlockEntity.getSnaredEntity() != this) {
                    this.setSnare(null);
                }
            }

            if(!this.snared) {
                if(this.decayTimer < Integer.MAX_VALUE) {
                    this.decayTimer++;
                }
            } else {
                if(this.decayTimer > 0) {
                    this.decayTimer--;
                }
            }

            if(this.decayTimer > 4200 && !this.isPersistenceRequired()) {
                serverWorld.sendParticles(
                        ParticleTypes.PORTAL,
                        this.getX(),
                        this.getY() + this.getBbHeight() * 0.5,
                        this.getZ(),
                        80,
                        0.1,
                        0.1,
                        0.1,
                        0.02
                );

                this.discard();
                return;
            }
        }

        super.tick();
        this.setNoGravity(true);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.isCreativePlayer()) {
            return true;
        } else {
            return super.isInvulnerableTo(damageSource);
        }
    }

    @Override
    public void push(Entity entity) {
        if(entity.getType().is(MirthdewEncoreEntityTypeTags.DREAMSPECK_OPAQUE)) {
            super.push(entity);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        if(entity.getType().is(MirthdewEncoreEntityTypeTags.DREAMSPECK_OPAQUE)) {
            super.doPush(entity);
        }
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.EMPTY;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public boolean isSnared() {
        return this.snared;
    }

    public void setSnare(@Nullable BlockPos pos) {
        this.snared = pos != null;
        this.snarePos = pos;
    }

    static class FlyRandomlyGoal extends Goal {

        private final Mob entity;

        public FlyRandomlyGoal(Mob entity) {
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            RandomSource random = this.entity.getRandom();
            MoveControl moveControl = this.entity.getMoveControl();
            if(!moveControl.hasWanted() || random.nextInt(reducedTickDelay(10)) == 0) {
                moveControl.setWantedPosition(this.entity.getX() + getRandomFloat(random, 8), this.entity.getY() + getRandomFloat(random, 8) - 1, this.entity.getZ() + getRandomFloat(random, 8), 1);
            }
        }

        public float getRandomFloat(RandomSource random, float distance) {
            return (random.nextFloat() - random.nextFloat()) * distance;
        }
    }

    static class DreamspeckMoveControl extends MoveControl {
        private final DreamspeckEntity entity;

        public DreamspeckMoveControl(DreamspeckEntity entity) {
            super(entity);
            this.entity = entity;
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 targetOffset = new Vec3(this.wantedX - this.entity.getX(), this.wantedY - this.entity.getY(), this.wantedZ - this.entity.getZ());
                double targetOffsetLength = targetOffset.length();
                if (targetOffsetLength < this.entity.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                } else {
                    this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(targetOffset.scale(this.speedModifier * 0.05 / targetOffsetLength)));

                    Vec3 velocity = this.entity.getDeltaMovement();
                    this.entity.setYRot(-((float) Mth.atan2(velocity.x, velocity.z)) * (180.0F / (float)Math.PI));
                    this.entity.yBodyRot = this.entity.getYRot();
                }
            }
        }
    }
}
