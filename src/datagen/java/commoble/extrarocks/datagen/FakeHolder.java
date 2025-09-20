package commoble.extrarocks.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;

// dummy holder for datagen which can always serialize as its key and doesn't need to exist in a registry
public class FakeHolder<T> extends Holder.Reference<T>
{
	private FakeHolder(ResourceKey<T> key)
	{
		super(Holder.Reference.Type.STAND_ALONE, null, key, null);
	}
	
	public static <T> FakeHolder<T> of(ResourceKey<T> key)
	{
		return new FakeHolder<>(key);
	}

	@Override
	public boolean canSerializeIn(HolderOwner<T> owner)
	{
		return true;
	}
}
