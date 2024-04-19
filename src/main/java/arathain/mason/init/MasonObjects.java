package arathain.mason.init;

import arathain.mason.MasonDecor;
import arathain.mason.entity.*;
import arathain.mason.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

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

    public static final Block TORCHLIGHT = createBlock("torchlight", new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).mapColor(MapColor.get(56)).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.LANTERN).luminance((blockState) -> 15)), true);
    public static final Block SOULLIGHT = createBlock("soullight", new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).mapColor(MapColor.get(56)).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.LANTERN).luminance((blockState) -> 11)), true);
    public static final Block MERCHANT_SIMULACRUM = createBlock("merchant_simulacrum", new MerchantSimulacrumBlock(AbstractBlock.Settings.create().mapColor(MapColor.WARPED_STEM).requiresTool().strength(4F, 16.0F).dropsNothing().sounds(BlockSoundGroup.ANCIENT_DEBRIS).luminance((blockState) -> 1)), true);

    public static final Item GLAIVE = createItem("glaive", new GlaiveItem(-2, -3.4f, new QuiltItemSettings().fireproof().rarity(Rarity.RARE).maxCount(1)));
    public static final Item SOULMOULD_ITEM = createItem("soulmould", new SoulmouldItem(new QuiltItemSettings().fireproof().rarity(Rarity.UNCOMMON).maxCount(16)));
    public static final Item BONEFLY_SKULL = createItem("bonefly_skull", new BoneflySkullItem(new QuiltItemSettings().fireproof().rarity(Rarity.UNCOMMON).maxCount(16)));
    public static final Item SOULTRAP_EFFIGY_ITEM = createItem("soultrap_effigy", new SoultrapEffigyItem(new QuiltItemSettings().fireproof().rarity(Rarity.RARE).maxCount(1)));

    public static final EntityType<ChainsEntity> CHAINS = createEntity("chains", RavenEntity.createRavenAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.MISC, ChainsEntity::new).dimensions(EntityDimensions.fixed(0.4F, 0.8F)).build());
    public static final EntityType<RavenEntity> RAVEN = createRaven("raven", RavenEntity.createRavenAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RavenEntity::new).dimensions(EntityDimensions.fixed(0.4F, 0.4F)).build());
    public static final EntityType<SoulmouldEntity> SOULMOULD = createEntity("soulmould", SoulmouldEntity.createSoulmouldAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SoulmouldEntity::new).dimensions(EntityDimensions.fixed(0.85F, 2.7F)).fireImmune().build());
    public static final EntityType<BoneflyEntity> BONEFLY = createEntity("bonefly", BoneflyEntity.createBoneflyAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BoneflyEntity::new).dimensions(EntityDimensions.changing(1.4F, 2.1F)).fireImmune().build());
    public static final EntityType<SoulExplosionEntity> SOUL_EXPLOSION = createEntity("soul_explosion", FabricEntityTypeBuilder.create(SpawnGroup.MISC, SoulExplosionEntity::new).trackRangeBlocks(10).dimensions(EntityDimensions.fixed(0.9f, 1.8F)).build());
    public static final EntityType<RippedSoulEntity> RIPPED_SOUL = createEntity("ripped_soul", RippedSoulEntity.createVexAttributes(), FabricEntityTypeBuilder.<RippedSoulEntity>create(SpawnGroup.MONSTER, RippedSoulEntity::new).dimensions(EntityDimensions.changing(0.9F, 0.9F)).fireImmune().build());

    public static RegistryKey<DamageType> SOUL_RIP_DMG_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(MasonDecor.MODID, "soul_rip"));


    private static <T extends Entity> EntityType<T> createEntity(String name, EntityType<T> type) {

        ENTITY_TYPES.put(type, new Identifier(MasonDecor.MODID, name));
        return type;
    }

    private static <T extends LivingEntity> EntityType<T> createEntity(String name, DefaultAttributeContainer.Builder attributes, EntityType<T> type) {
        FabricDefaultAttributeRegistry.register(type, attributes);
        ENTITY_TYPES.put(type, new Identifier(MasonDecor.MODID, name));
        return type;
    }
    private static <T extends LivingEntity> EntityType<T> createRaven(String name, DefaultAttributeContainer.Builder attributes, EntityType<T> type) {
        FabricDefaultAttributeRegistry.register(type, attributes);
        ENTITY_TYPES.put(type, new Identifier("tot", name));
        return type;
    }
    private static <T extends Block> T createBlock(String name, T block, boolean createItem) {
        BLOCKS.put(block, new Identifier(MasonDecor.MODID, name));
        if (createItem) {
            ITEMS.put(new BlockItem(block, new Item.Settings()), BLOCKS.get(block));
        }
        return block;
    }
    private static <T extends Item> T createItem(String name, T item) {
        ITEMS.put(item, new Identifier(MasonDecor.MODID, name));
        return item;
    }
    private static SoundEvent createSoundEvent(String name) {
        Identifier id = new Identifier(MasonDecor.MODID, name);
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(id);
        SOUND_EVENTS.put(soundEvent, id);
        return soundEvent;
    }
    public static void init() {
        SOUND_EVENTS.keySet().forEach(effect -> Registry.register(Registries.SOUND_EVENT, SOUND_EVENTS.get(effect), effect));
        BLOCKS.keySet().forEach(block -> Registry.register(Registries.BLOCK, BLOCKS.get(block), block));
        ITEMS.keySet().forEach(item -> Registry.register(Registries.ITEM, ITEMS.get(item), item));
        ENTITY_TYPES.keySet().forEach(entityType -> Registry.register(Registries.ENTITY_TYPE, ENTITY_TYPES.get(entityType), entityType));

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

        /* I have done what I can, but what I can is never enough */
    }
}
