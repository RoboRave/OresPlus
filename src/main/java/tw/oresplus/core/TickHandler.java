package tw.oresplus.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.world.chunk.Chunk;
import tw.oresplus.OresPlus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TickHandler {
	public static HashMap<Integer, ArrayList<Chunk>> regenList = new HashMap();
	
	@SubscribeEvent
	public boolean onWorldTick(TickEvent.WorldTickEvent event) {
		int dim = event.world.provider.dimensionId;
		ArrayList chunks = regenList.get(Integer.valueOf(dim));
		if (chunks == null)
			return false;
		if (!chunks.isEmpty()) {
			Chunk chunk = (Chunk) chunks.get(0);
			//OresPlus.log.info("Regenerating ores in chunk x" + chunk.xPosition + ", z" + chunk.zPosition);
			OresPlus.worldGen.doWorldGen(event.world.rand, event.world, chunk.xPosition, chunk.zPosition, false);
			chunks.remove(0);
			regenList.put(Integer.valueOf(dim), chunks);
		}
		return true;
	}

}
