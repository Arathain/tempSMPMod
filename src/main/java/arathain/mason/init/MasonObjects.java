package arathain.mason.init;

import arathain.mason.entity.BoneflyEntity;
import arathain.mason.entity.RavenEntity;
import arathain.mason.entity.RippedSoulEntity;
import arathain.mason.entity.SoulmouldEntity;
import arathain.mason.item.*;

import java.util.LinkedHashMap;
import java.util.Map;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class MasonObjects {
    private static final Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();
    private static final Map<Item, Identifier> ITEMS = new LinkedHashMap<>();
    private static final Map<EntityType<?>, Identifier> ENTITY_TYPES = new LinkedHashMap<>();
    private static final Map<SoundEvent, Identifier> SOUND_EVENTS = new LinkedHashMap<>();
    public static final SoundEvent ENTITY_RAVEN_CAW = createSoundEvent("entity.raven.caw");
    public static final SoundEvent ENTITY_SOULMOULD_AMBIENT = createSoundEvent("entity.soulmould.ambient");
    public static final SoundEvent ENTITY_SOULMOULD_ATTACK = createSoundEvent("entity.soulmould.attack");
    public static final SoundEvent ENTITY_SOULMOULD_HURT = createSoundEvent("entity.soulmould.hurt");
    public static final SoundEvent ENTITY_SOULMOULD_DEATH = createSoundEvent("entity.soulmould.death");
    public static final Block TORCHLIGHT;
    public static final Block SOULLIGHT;
    public static final Item GLAIVE;
    public static final Item SOULMOULD_ITEM;
    public static final Item BONEFLY_SKULL;
    public static final Item SOULTRAP_EFFIGY_ITEM;
    public static final EntityType<RavenEntity> RAVEN;
    public static final EntityType<SoulmouldEntity> SOULMOULD;
    public static final EntityType<BoneflyEntity> BONEFLY;
    public static final EntityType<RippedSoulEntity> RIPPED_SOUL;

    public MasonObjects() {
    }

    private static <T extends Entity> EntityType<T> createEntity(String name, EntityType<T> type) {
        ENTITY_TYPES.put(type, new Identifier("mason", name));
        return type;
    }

    private static <T extends LivingEntity> EntityType<T> createEntity(String name, DefaultAttributeContainer.Builder attributes, EntityType<T> type) {
        FabricDefaultAttributeRegistry.register(type, attributes);
        ENTITY_TYPES.put(type, new Identifier("mason", name));
        return type;
    }

    private static <T extends LivingEntity> EntityType<T> createRaven(String name, DefaultAttributeContainer.Builder attributes, EntityType<T> type) {
        FabricDefaultAttributeRegistry.register(type, attributes);
        ENTITY_TYPES.put(type, new Identifier("tot", name));
        return type;
    }

    private static <T extends Block> T createBlock(String name, T block, boolean createItem) {
        BLOCKS.put(block, new Identifier("mason", name));
        if (createItem) {
            ITEMS.put(new BlockItem(block, new Item.Settings()), (Identifier)BLOCKS.get(block));
        }

        return block;
    }

    private static <T extends Item> T createItem(String name, T item) {
        ITEMS.put(item, new Identifier("mason", name));
        return item;
    }

    private static SoundEvent createSoundEvent(String name) {
        Identifier id = new Identifier("mason", name);
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(id);
        SOUND_EVENTS.put(soundEvent, id);
        return soundEvent;
    }

    public static void init() {
        SOUND_EVENTS.keySet().forEach((effect) -> {
            Registry.register(Registries.SOUND_EVENT, (Identifier)SOUND_EVENTS.get(effect), effect);
        });
        BLOCKS.keySet().forEach((block) -> {
            Registry.register(Registries.BLOCK, (Identifier)BLOCKS.get(block), block);
        });
        ITEMS.keySet().forEach((item) -> {
            Registry.register(Registries.ITEM, (Identifier)ITEMS.get(item), item);
        });
        ENTITY_TYPES.keySet().forEach((entityType) -> {
            Registry.register(Registries.ENTITY_TYPE, (Identifier)ENTITY_TYPES.get(entityType), entityType);
        });


        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((content) -> {
            ITEMS.keySet().forEach((item) -> {
                content.addStack(item.getDefaultStack());
            });
            content.addStack(SOULMOULD_ITEM.getDefaultStack());
            content.addStack(BONEFLY_SKULL.getDefaultStack());
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((content) -> {
            content.addStack(GLAIVE.getDefaultStack());
        });
    }

    static {
        TORCHLIGHT = createBlock("torchlight", new Block(FabricBlockSettings.create().sounds(BlockSoundGroup.METAL).mapColor(MapColor.get(56)).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.LANTERN).luminance((blockState) -> {
            return 15;
        })), true);
        SOULLIGHT = createBlock("soullight", new SoullightBlock(FabricBlockSettings.create().sounds(BlockSoundGroup.METAL).mapColor(MapColor.get(56)).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.LANTERN).luminance((blockState) -> {
            return 11;
        })), true);
        GLAIVE = createItem("glaive", new GlaiveItem(4, -3.1F, (new FabricItemSettings()).fireproof().rarity(Rarity.UNCOMMON).maxCount(1)));
        SOULMOULD_ITEM = createItem("soulmould", new SoulmouldItem((new FabricItemSettings()).fireproof().rarity(Rarity.UNCOMMON).maxCount(16)));
        BONEFLY_SKULL = createItem("bonefly_skull", new BoneflySkullItem((new FabricItemSettings()).fireproof().rarity(Rarity.UNCOMMON).maxCount(16)));
        SOULTRAP_EFFIGY_ITEM = createItem("soultrap_effigy", new SoultrapEffigyItem((new FabricItemSettings()).fireproof().rarity(Rarity.RARE).maxCount(1)));
        RAVEN = createRaven("raven", RavenEntity.createRavenAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RavenEntity::new).dimensions(EntityDimensions.fixed(0.4F, 0.4F)).build());
        SOULMOULD = createEntity("soulmould", SoulmouldEntity.createSoulmouldAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SoulmouldEntity::new).dimensions(EntityDimensions.fixed(0.85F, 2.7F)).fireImmune().build());
        BONEFLY = createEntity("bonefly", BoneflyEntity.createBoneflyAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BoneflyEntity::new).dimensions(EntityDimensions.changing(1.4F, 2.1F)).fireImmune().build());
        RIPPED_SOUL = createEntity("ripped_soul", RippedSoulEntity.createVexAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, RippedSoulEntity::new).dimensions(EntityDimensions.changing(0.9F, 0.9F)).fireImmune().build());
    }
}

