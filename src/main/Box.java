package main;

public class Box {

    public int col, row;
    public int slotCount;
    public ItemSlot[] slots;

    public Box(int col, int row, int slotCount) {
        this.col = col;
        this.row = row;
        this.slotCount = slotCount;

        slots = new ItemSlot[slotCount];
        for (int i = 0; i < slotCount; i++) {
            slots[i] = new ItemSlot();
        }
    }
}