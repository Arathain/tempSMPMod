package arathain.mason.mixin;

import arathain.mason.init.MasonObjects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(StrongholdGenerator.PortalRoom.class)
public abstract class StrongholdPortalRoomMixin extends StructurePiece {
    protected StrongholdPortalRoomMixin(StructurePieceType type, int length, BlockBox boundingBox) {
        super(type, length, boundingBox);
    }

    @Inject(method = "generate", at = @At("TAIL"))
    public void generatePortalRoom(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos, CallbackInfo ci) {
        BlockPos blockPos = this.offsetPos(5, 3, 6);
        addCorners(world, Blocks.DEEPSLATE_TILES, chunkBox, 1);
        addCorners(world, Blocks.DEEPSLATE_TILE_WALL, chunkBox, 2);
        addCorners(world, Blocks.END_STONE_BRICKS, chunkBox, 3);
        addCorners(world, Blocks.DEEPSLATE_BRICK_WALL, chunkBox, 4);
        addCorners(world, Blocks.CHAIN, chunkBox, 5);
        addCorners(world, Blocks.CHAIN, chunkBox, 6);
        addCorners(world, Blocks.DEEPSLATE_BRICKS, chunkBox, 7);
        BlockState blockState4 = Blocks.DEEPSLATE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH).with(StairsBlock.HALF, BlockHalf.TOP);
        BlockState blockState5 = Blocks.DEEPSLATE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH).with(StairsBlock.HALF, BlockHalf.TOP);
        BlockState blockState6 = Blocks.DEEPSLATE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.HALF, BlockHalf.TOP);
        BlockState blockState7 = Blocks.DEEPSLATE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.HALF, BlockHalf.TOP);
        for(int j = 3; j < 14; j += 2) {
            this.addBlock(world, Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST), 0, 2, j, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST), 10, 2, j, chunkBox);
        }
        for(int j = 2; j < 9; j += 2) {
            this.addBlock(world, Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH), j, 2, 15, chunkBox);
        }
        this.fillWithOutline(world, chunkBox, 1, 1, 1, 2, 1, 4, Blocks.POLISHED_DEEPSLATE.getDefaultState(), Blocks.POLISHED_DEEPSLATE.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 8, 1, 1, 9, 1, 4, Blocks.POLISHED_DEEPSLATE.getDefaultState(), Blocks.POLISHED_DEEPSLATE.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 1, 1, 1, 1, 3, Blocks.SOUL_FIRE.getDefaultState(), Blocks.SOUL_FIRE.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 0, 1, 1, 0, 3, Blocks.SOUL_SOIL.getDefaultState(), Blocks.SOUL_SOIL.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 9, 1, 1, 9, 1, 3, Blocks.SOUL_FIRE.getDefaultState(), Blocks.SOUL_FIRE.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 0, 1, 1, 0, 3, Blocks.SOUL_SOIL.getDefaultState(), Blocks.SOUL_SOIL.getDefaultState(), false);
        this.addBlock(world, Blocks.POLISHED_DEEPSLATE_WALL.getDefaultState(), 2, 2, 4, chunkBox);
        this.addBlock(world, Blocks.POLISHED_DEEPSLATE_WALL.getDefaultState(), 8, 2, 4, chunkBox);
        this.addBlock(world, Blocks.SOUL_LANTERN.getDefaultState(), 2, 3, 4, chunkBox);
        this.addBlock(world, Blocks.SOUL_LANTERN.getDefaultState(), 8, 3, 4, chunkBox);
        for(int l = 0; l<=2; ++l) {
            this.addBlock(world, blockState4, 4 + l, 1, 8, chunkBox);
            this.addBlock(world, blockState5, 4 + l, 1, 12, chunkBox);;
            this.addBlock(world, blockState6, 3, 1, 9 + l, chunkBox);
            this.addBlock(world, blockState7, 7, 1, 9 + l, chunkBox);
        }
        for(int k = 4; k <= 6; ++k) {
            this.addBlock(world, Blocks.DEEPSLATE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH), k, 1, 4, chunkBox);
            this.addBlock(world, Blocks.DEEPSLATE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH), k, 2, 5, chunkBox);
            this.addBlock(world, Blocks.DEEPSLATE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH), k, 3, 6, chunkBox);
        }
        this.fillWithOutline(world, chunkBox, 4, 1, 5, 6, 1, 7, Blocks.DEEPSLATE_BRICKS.getDefaultState(), Blocks.DEEPSLATE_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 4, 2, 6, 6, 2, 7, Blocks.DEEPSLATE_BRICKS.getDefaultState(), Blocks.DEEPSLATE_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 4, 3, 7, 6, 3, 7, Blocks.DEEPSLATE_BRICKS.getDefaultState(), Blocks.DEEPSLATE_BRICKS.getDefaultState(), false);
        world.setBlockState(blockPos, MasonObjects.MOULDTEMP.getDefaultState().with(Properties.HORIZONTAL_FACING, this.getFacing()), 2);
        world.createAndScheduleBlockTick(blockPos, MasonObjects.MOULDTEMP, 0);

    }
    private void addCorners(StructureWorldAccess world, Block block, BlockBox chunkBox, int height) {
        addBlock(world, block.getDefaultState(), 3, height, 8, chunkBox);
        addBlock(world, block.getDefaultState(), 7, height, 8, chunkBox);
        addBlock(world, block.getDefaultState(), 3, height, 12, chunkBox);
        addBlock(world, block.getDefaultState(), 7, height, 12, chunkBox);
    }

}
