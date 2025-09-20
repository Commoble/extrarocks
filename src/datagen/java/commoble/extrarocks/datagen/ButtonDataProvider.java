package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import com.mojang.math.Quadrant;

import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ButtonDataProvider extends BlockDataProvider
{
	/** The cubic block this variant is based on **/
	protected final Block baseBlock;
	protected final ResourceLocation baseBlockID;
	protected final ResourceLocation buttonModel;
	protected final ResourceLocation buttonPressedModel;
	protected final ResourceLocation itemModel;
	protected final ResourceLocation textureLocation;

	public ButtonDataProvider(Block buttonBlock, Block baseBlock, ResourceLocation textureLocation)
	{
		super(buttonBlock);
		this.baseBlock = baseBlock;
		this.baseBlockID = BuiltInRegistries.BLOCK.getKey(baseBlock);
		this.buttonModel = this.reformatBlockID("block/%s");
		this.buttonPressedModel = this.reformatBlockID("block/%s_pressed");
		this.itemModel = this.reformatBlockID("block/%s_inventory");
		this.textureLocation = textureLocation;
	}

	@Override
	protected void generateBlockStates(Map<ResourceLocation, BlockModelDefinition> blockStates)
	{
		blockStates.put(this.blockId(), BlockStateBuilder.variants(builder -> {
			for (AttachFace face : AttachFace.values())
			{
				// x rotation depends on attachment face alone
				Quadrant x = face == AttachFace.CEILING ? Quadrant.R180
					: face == AttachFace.WALL ? Quadrant.R90
					: Quadrant.R0; // floor
				
				// uv is only locked for wall buttons
				boolean uvlock = face == AttachFace.WALL;
				for (Direction facing : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues())
				{
					
					// y rotation depends on face and facing
					// if face is ceiling, yrotation is 0 for south, continuing clockwise
					// otherwise, yrotation is 0 for north, continuing clockwise
					// the canonical y-rotation for Directions is 0 for south (continuing clockwise) so we will leverage that
					int baseYRotations = face == AttachFace.CEILING
						? facing.get2DDataValue()
						: (facing.get2DDataValue() + 2) % 4;
					Quadrant y = Quadrant.values()[baseYRotations];
					
					for (boolean powered : new boolean[] {false,true})
					{
						ResourceLocation modelFile = powered ? this.buttonPressedModel : this.buttonModel;
						builder.addMultiPropertyVariant(properties -> properties
							.addPropertyValue(BlockStateProperties.ATTACH_FACE, face)
							.addPropertyValue(BlockStateProperties.HORIZONTAL_FACING, facing)
							.addPropertyValue(BlockStateProperties.POWERED, powered),
							BlockStateBuilder.model(modelFile,x,y,uvlock));
					}
				}
			}
		}));
	}

	@Override
	protected void generateBlockModels(Map<ResourceLocation, SimpleModel> blockModels)
	{
		blockModels.put(this.buttonModel, SimpleModel.create("block/button").addTexture("texture", this.textureLocation));
		blockModels.put(this.buttonPressedModel, SimpleModel.create("block/button_pressed").addTexture("texture", this.textureLocation));
		blockModels.put(this.itemModel, SimpleModel.create("block/button_inventory").addTexture("texture", this.textureLocation));
	}

	@Override
	protected void generateClientItems(Map<ResourceLocation, ClientItem> items)
	{
		items.put(this.blockId(), SimpleModel.blockModelWrapper(this.itemModel));
	}
	
	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.blockId(), RecipeHelpers.shapeless(this.block, 1, CraftingBookCategory.REDSTONE, List.of(Ingredient.of(this.baseBlock))));
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		// don't super generate tags, we add ourself to stone button which is already mineable
		blocks.tag(BlockTags.STONE_BUTTONS).add(this.key());
		items.tag(ItemTags.STONE_BUTTONS).add(this.itemKey());
	}

}
