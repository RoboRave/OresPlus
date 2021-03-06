package tw.oresplus.blocks;

import java.util.LinkedList;

import tw.oresplus.OresPlus;
import tw.oresplus.api.OresPlusAPI;
import tw.oresplus.core.FuelHelper;
import tw.oresplus.triggers.OresTrigger;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.FluidTank;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.transport.IPipeTile;

public class TileEntityCracker 
extends TileEntityMachine 
implements IFluidHandler {
	public FluidTank tank = new FluidTank(1000);
	
	public TileEntityCracker() {
		super(1200);
		this.inventory = new ItemStack[2];
		this.inventoryName = "container:cracker";
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		
		this.burnFuel();
		
		if (!this.worldObj.isRemote) {
			if (this.isBurning() && this.hasWork()) {
				this.doCrack(1);
			}
		}
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return slot == 0 ? item.getItem() == OresPlusAPI.getItem("itemBitumen") : FuelHelper.isItemFuel(item);
	}

	@Override
	public void doWork(PowerHandler workProvider) {
		if (this.powerHandler.useEnergy(this.energyRequired, this.energyRequired, true) != this.energyRequired)
			return;
		doCrack((int)(this.energyRequired * 1.5));
	}

	@Override
	public boolean hasWork() {
		if (this.inventory[0] == null && this.currentItem == null)
			return false;
		
		ItemStack item = this.currentItem != null ? this.currentItem : this.inventory[0];
		return item.getItem() == OresPlusAPI.getItem("itemBitumen")
			&& this.tank.getFluidAmount() < this.tank.getCapacity();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if (resource == null)
			return null;
		if (!resource.isFluidEqual(this.tank.getFluid()))
			return null;
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] {tank.getInfo()};
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound){
		super.readFromNBT(tagCompound);
		tank.readFromNBT(tagCompound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound){
		super.writeToNBT(tagCompound);
		tank.writeToNBT(tagCompound);
	}
	
	private void doCrack(int amount) {
		if (this.machineWorkTime == 0)
			this.startCrack();
		if (this.currentItem != null) {
			this.machineWorkTime += amount;
			if (this.machineWorkTime >= this.workTimeNeeded) {
				this.machineWorkTime -= this.workTimeNeeded;
				this.crackItem();
				this.markDirty();
			}
		}
	}

	private void crackItem() {
		tank.fill(new FluidStack(FluidRegistry.getFluid("oil"), 10), true);
		OresPlus.log.info("Tank: " + this.tank.getFluidAmount());
		if (this.inventory[0] == null) {
			this.currentItem = null;
			this.machineWorkTime = 0;
		}
	}

	private void startCrack() {
		if (this.currentItem == null && this.inventory[0] != null) {
			this.currentItem = this.inventory[0].copy();
			--this.inventory[0].stackSize;
			if (this.inventory[0].stackSize == 0)
				this.inventory[0] = null;
		}
		else if (this.inventory[0].isItemEqual(this.currentItem)) {
			--this.inventory[0].stackSize;
			if (this.inventory[0].stackSize == 0)
				this.inventory[0] = null;
		}
	}

	@Override
	public LinkedList<ITrigger> getPipeTriggers(IPipeTile pipe) {
		return null;
	}

	@Override
	public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile) {
		LinkedList triggers = new LinkedList();
		triggers.add(OresTrigger.hasWork);
		return triggers;
	}
}
