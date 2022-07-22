package org.teacon.exhibitiontoolkit.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teacon.exhibitiontoolkit.ExhibitionToolkit;
import org.teacon.exhibitiontoolkit.menu.PowerSupplyMenu;

public class PowerSupplyBlock extends BaseEntityBlock {
    public PowerSupplyBlock(Properties prop) {
        super(prop);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player p, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (level.getBlockEntity(pos) instanceof Entity be) {
                NetworkHooks.openGui((ServerPlayer) p, new PowerSupplyMenu.Provider(be.data),
                        buf -> buf.writeVarInt(be.data.status).writeVarInt(be.data.power));
            }
            return InteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new Entity(pPos, pState);
    }

    public static final class Data {
        // Working status flag. 0 represents OFF; 1 represents ON.
        public int status = 1;
        // Amount of energy output per tick
        public int power = 100;
        public Runnable markDirty;
    }

    public static final class Entity extends BlockEntity {

        private final LazyOptional<IEnergyStorage> energyStore = LazyOptional.of(() -> new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return 0;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return Entity.this.data.status == 1 ? Entity.this.data.power : 0;
            }

            @Override
            public int getEnergyStored() {
                return Integer.MAX_VALUE;
            }

            @Override
            public int getMaxEnergyStored() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public boolean canReceive() {
                return false;
            }
        });

        public final Data data = new Data();

        public Entity(BlockPos pWorldPosition, BlockState pBlockState) {
            super(ExhibitionToolkit.POWER_SUPPLY_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
            this.data.markDirty = this::setChanged;
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            tag.putInt("status", this.data.status);
            tag.putInt("power", this.data.power);
            super.saveAdditional(tag);
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            this.data.status = tag.getInt("status");
            this.data.power = tag.getInt("power");
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction d) {
            return cap == CapabilityEnergy.ENERGY ? this.energyStore.cast() : super.getCapability(cap, d);
        }

        @Override
        public void invalidateCaps() {
            this.energyStore.invalidate();
            super.invalidateCaps();
        }
    }
}
