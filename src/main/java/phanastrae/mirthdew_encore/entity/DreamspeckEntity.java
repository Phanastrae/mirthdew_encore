package phanastrae.mirthdew_encore.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreEntityTypeTags;

public class DreamspeckEntity extends MobEntity {

    public DreamspeckEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
        this.moveControl = new DreamspeckEntity.DreamspeckMoveControl(this);
    }

    public static DefaultAttributeContainer.Builder createDreamspeckAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new FlyRandomlyGoal(this));
    }

    @Override
    public void tick() {
        if (this.getWorld().isClient) {
            if (this.random.nextInt(36) == 0 && !this.isSilent()) {
                this.getWorld()
                        .playSound(
                                this.getX() + 0.5,
                                this.getY() + 0.5,
                                this.getZ() + 0.5,
                                SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                                this.getSoundCategory(),
                                0.5F + this.random.nextFloat() * 0.2F,
                                0.3F + this.random.nextFloat() * 1.5F,
                                false
                        );
            }

            for(int i = 0; i < 2; ++i) {
                this.getWorld().addParticle(ParticleTypes.WITCH, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.0, 0.0);
                this.getWorld().addParticle(ParticleTypes.ENCHANT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.0, 0.0);
            }
        }

        super.tick();
        this.setNoGravity(true);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(!damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.isSourceCreativePlayer()) {
            return true;
        } else {
            return super.isInvulnerableTo(damageSource);
        }
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if(entity.getType().isIn(MirthdewEncoreEntityTypeTags.DREAMSPECK_OPAQUE)) {
            super.pushAwayFrom(entity);
        }
    }

    @Override
    protected void pushAway(Entity entity) {
        if(entity.getType().isIn(MirthdewEncoreEntityTypeTags.DREAMSPECK_OPAQUE)) {
            super.pushAway(entity);
        }
    }

    @Override
    public boolean canAvoidTraps() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.INTENTIONALLY_EMPTY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.INTENTIONALLY_EMPTY;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.INTENTIONALLY_EMPTY;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0F;
    }

    static class FlyRandomlyGoal extends Goal {

        private final MobEntity entity;

        public FlyRandomlyGoal(MobEntity entity) {
            this.entity = entity;
        }

        @Override
        public boolean canStart() {
            return true;
        }

        @Override
        public void tick() {
            Random random = this.entity.getRandom();
            MoveControl moveControl = this.entity.getMoveControl();
            if(!moveControl.isMoving() || random.nextInt(toGoalTicks(10)) == 0) {
                moveControl.moveTo(this.entity.getX() + getRandomFloat(random, 8), this.entity.getY() + getRandomFloat(random, 8) - 1, this.entity.getZ() + getRandomFloat(random, 8), 1);
            }
        }

        public float getRandomFloat(Random random, float distance) {
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
            if (this.state == MoveControl.State.MOVE_TO) {
                Vec3d targetOffset = new Vec3d(this.targetX - this.entity.getX(), this.targetY - this.entity.getY(), this.targetZ - this.entity.getZ());
                double targetOffsetLength = targetOffset.length();
                if (targetOffsetLength < this.entity.getBoundingBox().getAverageSideLength()) {
                    this.state = MoveControl.State.WAIT;
                } else {
                    this.entity.setVelocity(this.entity.getVelocity().add(targetOffset.multiply(this.speed * 0.05 / targetOffsetLength)));

                    Vec3d velocity = this.entity.getVelocity();
                    this.entity.setYaw(-((float) MathHelper.atan2(velocity.x, velocity.z)) * (180.0F / (float)Math.PI));
                    this.entity.bodyYaw = this.entity.getYaw();
                }
            }
        }
    }
}
