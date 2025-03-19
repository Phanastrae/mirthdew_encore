package phanastrae.mirthdew_encore.item;

import phanastrae.mirthdew_encore.card_spell.SpellCast;
import phanastrae.mirthdew_encore.card_spell.SpellCastHelper;
import phanastrae.mirthdew_encore.component.type.SpellChargeComponent;
import phanastrae.mirthdew_encore.component.type.SpellDeckContentsComponent;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.SPELL_CHARGE;
import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.SPELL_DECK_CONTENTS;
import static phanastrae.mirthdew_encore.item.MirthdewEncoreItems.SPELL_CARD;
import static phanastrae.mirthdew_encore.item.MirthdewEncoreItems.SPELL_DECK;

// TODO remove any excess .copy() calls
public abstract class SpellCardAbstractItem extends Item {
    public static final int MAX_CARD_STACK_SIZE = 32;

    public SpellCardAbstractItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        user.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return true;
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        RandomSource random = user.getRandom();
        if(random.nextInt(3) == 0) {
            SoundEvent soundEvent = random.nextBoolean() ? SoundEvents.BUNDLE_INSERT : SoundEvents.BUNDLE_REMOVE_ONE;
            Player player = (user instanceof Player playerEntity) ? playerEntity : null;
            world.playSound(player, user.blockPosition(), soundEvent, SoundSource.NEUTRAL, 0.8F, 1.4F + 0.4F * random.nextFloat());
        }

        long worldTime = world.getGameTime();
        SpellChargeComponent spellChargeComponent = stack.get(SPELL_CHARGE);
        if(spellChargeComponent == null || !spellChargeComponent.isDisabled(worldTime)) {
            if(tryToCastNext(world, user, stack)) {
                return;
            }
        }

        for(ItemStack stack1 : user.getHandSlots()) {
            if(stack1 != stack) {
                if(tryToCastNext(world, user, stack1)) {
                    return;
                }
            }
        }
    }

    public boolean tryToCastNext(Level world, Entity user, ItemStack itemStack) {
        long worldTime = world.getGameTime();
        SpellChargeComponent spellChargeComponent = itemStack.get(SPELL_CHARGE);

        if(spellChargeComponent != null && spellChargeComponent.isDisabled(worldTime)) {
            return false;
        }

        if(!world.isClientSide && world instanceof ServerLevel serverWorld) {
            SpellChargeComponent.Builder builder;
            if (spellChargeComponent == null) {
                List<SpellCast> spellCastList = SpellCastHelper.castListFromStack(itemStack);
                builder = new SpellChargeComponent.Builder(spellCastList);
            } else {
                builder = new SpellChargeComponent.Builder(spellChargeComponent);
            }

            int totalDelayMs = 0;

            SpellCast nextCast = builder.removeFirst();
            if (nextCast != null) {
                SpellCast.SpellInfoCollector spellInfoCollector = nextCast.castSpell(serverWorld, user);

                totalDelayMs += spellInfoCollector.getCastDelayMs();
                builder.addRechargeDelay(spellInfoCollector.getRechargeDelayMs());

                boolean hadSuccess = spellInfoCollector.getHadSuccess();
                boolean hadFailure = spellInfoCollector.getHadFailure();
                if(hadFailure || !hadSuccess) {
                    Vec3 pos = user.getEyePosition();
                    double spread = hadSuccess ? 0.02 : 0.1;
                    serverWorld.sendParticles(ParticleTypes.SMOKE,
                            pos.x,
                            pos.y - (hadSuccess ? 0.3 : 0.0),
                            pos.z,
                            hadSuccess ? 20 : 200,
                            spread,
                            spread,
                            spread,
                            hadSuccess ? 0.01 : 0.1);
                    serverWorld.playSound(null,
                            pos.x,
                            pos.y,
                            pos.z,
                            SoundEvents.FIRE_EXTINGUISH,
                            SoundSource.PLAYERS,
                            0.6F,
                            hadSuccess ? 1.0F : 0.3F);
                    if(!hadSuccess) {
                        serverWorld.playSound(null,
                                pos.x,
                                pos.y,
                                pos.z,
                                SoundEvents.GENERIC_EXPLODE,
                                SoundSource.PLAYERS,
                                0.2F,
                                1.0F);
                    }
                }
            }

            if (builder.isEmpty()) {
                totalDelayMs += builder.removeRechargeDelay();
                List<SpellCast> spellCastList = SpellCastHelper.castListFromStack(itemStack);
                builder.addAll(spellCastList);
            }

            if (totalDelayMs > 0) {
                int tickDelay = Mth.ceil(totalDelayMs / 50F);
                builder.setCooldown(worldTime, tickDelay);
            } else {
                builder.setCooldown(0, 0);
            }
            itemStack.set(SPELL_CHARGE, builder.build());
        }
        return true;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack heldStack, Slot slot, ClickAction clickType, Player player) {
        ItemStack baseStack = slot.getItem();
        if(baseStack.isEmpty()) {
            if(clickType == ClickAction.SECONDARY) {
                // Right click over an empty slot to remove the bottom-most card from the stack
                SpellDeckContentsComponent spellDeckContentsComponent = heldStack.get(SPELL_DECK_CONTENTS);
                if(spellDeckContentsComponent != null) {
                    SpellDeckContentsComponent.Builder builder = new SpellDeckContentsComponent.Builder(spellDeckContentsComponent);

                    ItemStack removedStack = builder.removeStackFromBase();
                    if(removedStack != null) {
                        if(builder.isEmpty()) {
                            heldStack.set(SPELL_DECK_CONTENTS, null);
                        } else {
                            heldStack.set(SPELL_DECK_CONTENTS, builder.build());
                        }
                        slot.setByPlayer(removedStack);

                        heldStack.remove(SPELL_CHARGE);
                        if(builder.isEmpty()) {
                            // if the held item is no longer a deck, try to convert it to a card
                            AbstractContainerMenu screenHandler = player.containerMenu;
                            if (screenHandler != null) {
                                ItemStack screenHandlerCursorStack = screenHandler.getCarried();
                                if (screenHandlerCursorStack == heldStack) {
                                    screenHandler.setCarried(getStackWithCorrectItem(heldStack));
                                }
                            }
                        }

                        this.playRemoveOneSound(player);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack baseStack, ItemStack heldStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
        if(!slot.allowModification(player)) {
            return false;
        }

        if(heldStack.isEmpty()) {
            if (clickType == ClickAction.SECONDARY) {
                // Right click with an empty cursor to remove the top-most card from the stack
                SpellDeckContentsComponent spellDeckContentsComponent = baseStack.get(SPELL_DECK_CONTENTS);
                if (spellDeckContentsComponent != null) {
                    List<ItemStack> stackList = deckStackToStackList(baseStack);
                    if(!stackList.isEmpty()) {
                        ItemStack first = stackList.removeFirst();
                        first.remove(SPELL_CHARGE);
                        cursorStackReference.set(first);

                        ItemStack remainingDeck = stackListToDeckStack(stackList);
                        slot.setByPlayer(remainingDeck);
                        this.playRemoveOneSound(player);
                        return true;
                    }
                }
            }
        } else if(heldStack.getItem() instanceof SpellCardAbstractItem) {
            if(clickType == ClickAction.PRIMARY) {
                int maxAcceptedCards = getMaxAcceptedCards(baseStack);
                if(maxAcceptedCards < 1) {
                    playInsertFailSound(player);
                    return true;
                } else {
                    heldStack.remove(SPELL_CHARGE);
                    baseStack.remove(SPELL_CHARGE);

                    List<ItemStack> heldStackList = deckStackToStackList(heldStack);
                    List<ItemStack> baseStackList = deckStackToStackList(baseStack);

                    int heldSize = heldStackList.size();
                    int removedCards = Math.min(maxAcceptedCards, heldSize);

                    List<ItemStack> newHeldStackList = heldStackList.subList(0, heldSize - removedCards);
                    List<ItemStack> newBaseStackList;
                    newBaseStackList = new ArrayList<>(heldStackList.subList(heldSize - removedCards, heldSize));
                    newBaseStackList.addAll(baseStackList);

                    cursorStackReference.set(stackListToDeckStack(newHeldStackList));
                    slot.setByPlayer(stackListToDeckStack(newBaseStackList));

                    this.playRemoveOneSound(player);
                    this.playInsertSound(player);
                    return true;
                }
            } else if(clickType == ClickAction.SECONDARY) {
                // Right click with a Card/Deck in cursor to remove the bottom item of the Card/Deck and place it on top of the stack
                if(getMaxAcceptedCards(baseStack) < 1) {
                    playInsertFailSound(player);
                    return true;
                }

                heldStack.remove(SPELL_CHARGE);
                baseStack.remove(SPELL_CHARGE);

                SpellDeckContentsComponent spellDeckContentsComponent = heldStack.get(SPELL_DECK_CONTENTS);
                ItemStack removedStack;
                if(spellDeckContentsComponent == null || spellDeckContentsComponent.isEmpty()) {
                    removedStack = getStackWithCorrectItem(heldStack);
                    cursorStackReference.set(ItemStack.EMPTY.copy());
                } else {
                    SpellDeckContentsComponent.Builder builder = new SpellDeckContentsComponent.Builder(spellDeckContentsComponent);
                    removedStack = builder.removeStackFromBase();
                    heldStack.set(SPELL_DECK_CONTENTS, builder.build());
                    cursorStackReference.set(getStackWithCorrectItem(heldStack));
                }

                if(removedStack != null) {
                    removedStack = removedStack.copy();
                    slot.setByPlayer(getMerged(removedStack, baseStack));
                }

                this.playRemoveOneSound(player);
                this.playInsertSound(player);
                return true;
            }
        }
        return false;
    }

    public static List<ItemStack> deckStackToStackList(ItemStack deckStack) {
        SpellDeckContentsComponent deckContents = deckStack.get(SPELL_DECK_CONTENTS);
        List<ItemStack> stackList = new ArrayList<>();
        if(deckContents == null) {
            stackList.add(deckStack.copy());
        } else {
            ItemStack topStack = deckStack.copy();
            topStack.remove(SPELL_DECK_CONTENTS);
            stackList.add(getStackWithCorrectItem(topStack));
            for(ItemStack stack : deckContents.iterate()) {
                stackList.add(stack.copy());
            }
        }
        return stackList;
    }

    public static ItemStack stackListToDeckStack(List<ItemStack> stackList) {
        if(stackList == null || stackList.isEmpty()) return ItemStack.EMPTY.copy();

        if(stackList.size() == 1) {
            return stackList.getFirst();
        } else {
            ItemStack topStack = stackList.getFirst().copy();
            SpellDeckContentsComponent deckContents = topStack.get(SPELL_DECK_CONTENTS);
            SpellDeckContentsComponent.Builder builder;
            if(deckContents == null) {
                builder = new SpellDeckContentsComponent.Builder();
            } else {
                builder = new SpellDeckContentsComponent.Builder(deckContents);
            }

            for(int i = 1; i < stackList.size(); i++) {
                builder.addStackToBase(stackList.get(i));
            }

            topStack.set(SPELL_DECK_CONTENTS, builder.build());
            return getStackWithCorrectItem(topStack);
        }
    }

    public static int getMaxAcceptedCards(ItemStack stack) {
        if(stack.isEmpty()) {
            return 0;
        } else if(stack.getItem() instanceof SpellCardAbstractItem) {
            SpellDeckContentsComponent deckContents = stack.get(SPELL_DECK_CONTENTS);
            if(deckContents == null) {
                return MAX_CARD_STACK_SIZE - 1;
            } else {
                int accepted = MAX_CARD_STACK_SIZE - 1 - deckContents.size();
                if(accepted < 0) accepted = 0;
                return accepted;
            }
        } else {
            return 0;
        }
    }

    public static ItemStack getMerged(ItemStack top, ItemStack bottom) {
        SpellDeckContentsComponent spellDeckContentsComponent = top.get(SPELL_DECK_CONTENTS);

        SpellDeckContentsComponent.Builder builder;
        if(spellDeckContentsComponent == null) {
            builder = new SpellDeckContentsComponent.Builder();
        } else {
            builder = new SpellDeckContentsComponent.Builder(spellDeckContentsComponent);
        }

        SpellDeckContentsComponent baseStackSpellDeckContentsComponent = bottom.get(SPELL_DECK_CONTENTS);
        if(baseStackSpellDeckContentsComponent == null) {
            builder.addStackToBase(bottom.copy());
        } else {
            ItemStack newBottom = bottom.copy();
            newBottom.set(SPELL_DECK_CONTENTS, null);
            builder.addStackToBase(getStackWithCorrectItem(newBottom));
            for(ItemStack itemStack : baseStackSpellDeckContentsComponent.iterate()) {
                builder.addStackToBase(itemStack.copy());
            }
        }

        ItemStack newTop = top.copy();
        newTop.set(SPELL_DECK_CONTENTS, builder.build());
        return getStackWithCorrectItem(newTop);
    }

    public static ItemStack getStackWithCorrectItem(ItemStack stack) {
        SpellDeckContentsComponent spellDeckContentsComponent = stack.get(SPELL_DECK_CONTENTS);
        if(stack.is(SPELL_CARD)) {
            if(spellDeckContentsComponent != null) {
                if(spellDeckContentsComponent.isEmpty()) {
                    ItemStack newStack = stack.copy();
                    newStack.set(SPELL_DECK_CONTENTS, null);
                    return newStack;
                } else {
                    ItemStack newStack = SPELL_DECK.getDefaultInstance();
                    newStack.applyComponentsAndValidate(stack.getComponentsPatch());
                    return newStack;
                }
            }
        } else if(stack.is(SPELL_DECK)) {
            if(spellDeckContentsComponent == null || spellDeckContentsComponent.isEmpty()) {

                ItemStack newStack = SPELL_CARD.getDefaultInstance();
                newStack.applyComponentsAndValidate(stack.getComponentsPatch());
                if(spellDeckContentsComponent != null) {
                    newStack.set(SPELL_DECK_CONTENTS, null);
                }
                return newStack;
            } else {
                return stack.copy();
            }
        }

        return stack.copy();
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 1.0F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 1.0F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertFailSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.3F + entity.level().getRandom().nextFloat() * 0.3F);
    }
}
