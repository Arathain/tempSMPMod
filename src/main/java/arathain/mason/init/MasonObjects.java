package arathain.mason.init;

import arathain.mason.MasonDecor;
import arathain.mason.entity.*;
import arathain.mason.item.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class MasonObjects {
    private static final Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();
    private static final Map<Item, Identifier> ITEMS = new LinkedHashMap<>();
    private static final Map<EntityType<?>, Identifier> ENTITY_TYPES = new LinkedHashMap<>();

    public static final Block TORCHLIGHT = createBlock("torchlight", new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.DARK_AQUA).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.LANTERN).luminance((blockState) -> 15)), true);
    public static final Block SOULLIGHT = createBlock("soullight", new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.DARK_AQUA).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.LANTERN).luminance((blockState) -> 11)), true);

    public static final Item GLAIVE = createItem("glaive", new GlaiveItem(2, -2.5f, new FabricItemSettings().fireproof().rarity(Rarity.UNCOMMON).group(ItemGroup.COMBAT).maxCount(1)));
    public static final Item SOULMOULD_ITEM = createItem("soulmould", new SoulmouldItem(new FabricItemSettings().fireproof().rarity(Rarity.UNCOMMON).group(ItemGroup.DECORATIONS).maxCount(16)));
    public static final Item BONEFLY_SKULL = createItem("bonefly_skull", new BoneflySkullItem(new FabricItemSettings().fireproof().rarity(Rarity.UNCOMMON).group(ItemGroup.DECORATIONS).maxCount(16)));
    public static final Item SOULTRAP_EFFIGY_ITEM = createItem("soultrap_effigy", new SoultrapEffigyItem(new FabricItemSettings().fireproof().rarity(Rarity.RARE).maxCount(1)));

    public static final EntityType<ChainsEntity> CHAINS = createEntity("chains", RavenEntity.createRavenAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.MISC, ChainsEntity::new).dimensions(EntityDimensions.fixed(0.4F, 0.8F)).build());
    public static final EntityType<RavenEntity> RAVEN = createEntity("raven", RavenEntity.createRavenAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RavenEntity::new).dimensions(EntityDimensions.fixed(0.4F, 0.4F)).build());
    public static final EntityType<SoulmouldEntity> SOULMOULD = createEntity("soulmould", SoulmouldEntity.createSoulmouldAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SoulmouldEntity::new).dimensions(EntityDimensions.fixed(0.85F, 2.7F)).fireImmune().build());
    public static final EntityType<BoneflyEntity> BONEFLY = createEntity("bonefly", BoneflyEntity.createBoneflyAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BoneflyEntity::new).dimensions(EntityDimensions.changing(1.4F, 2.1F)).fireImmune().build());
    public static final EntityType<AnimatedStatueEntity> STATUE = createEntity("statue", AnimatedStatueEntity.createStatueAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, AnimatedStatueEntity::new).dimensions(EntityDimensions.fixed(0.5F, 2.0F)).fireImmune().build());
    public static final EntityType<SoulExplosionEntity> SOUL_EXPLOSION = createEntity("soul_explosion", FabricEntityTypeBuilder.create(SpawnGroup.MISC, SoulExplosionEntity::new).trackRangeBlocks(10).dimensions(EntityDimensions.fixed(0.9f, 1.8F)).build());

    private static <T extends Entity> EntityType<T> createEntity(String name, EntityType<T> type) {

        ENTITY_TYPES.put(type, new Identifier(MasonDecor.MODID, name));
        return type;
    }

    private static <T extends LivingEntity> EntityType<T> createEntity(String name, DefaultAttributeContainer.Builder attributes, EntityType<T> type) {
        FabricDefaultAttributeRegistry.register(type, attributes);
        ENTITY_TYPES.put(type, new Identifier("tot", name));
        return type;
    }
    private static <T extends Block> T createBlock(String name, T block, boolean createItem) {
        BLOCKS.put(block, new Identifier(MasonDecor.MODID, name));
        if (createItem) {
            ITEMS.put(new BlockItem(block, new Item.Settings().group(ItemGroup.DECORATIONS)), BLOCKS.get(block));
        }
        return block;
    }
    private static <T extends Item> T createItem(String name, T item) {
        ITEMS.put(item, new Identifier(MasonDecor.MODID, name));
        return item;
    }
    public static void init() {
        BLOCKS.keySet().forEach(block -> Registry.register(Registry.BLOCK, BLOCKS.get(block), block));
        ITEMS.keySet().forEach(item -> Registry.register(Registry.ITEM, ITEMS.get(item), item));
        ENTITY_TYPES.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENTITY_TYPES.get(entityType), entityType));
    }
}
