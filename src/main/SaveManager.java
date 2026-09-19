package main;

import java.io.*;

public class SaveManager {

    static final String SAVE_FILE = "save/inventory.txt";

    public static void save(BoxManager boxManager) {
        try {
            File folder = new File("save");
            if (!folder.exists()) folder.mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE));

            for (Box box : boxManager.boxes) {
                for (int i = 0; i < box.slotCount; i++) {
                    ItemSlot slot = box.slots[i];
                    if (slot.filePath != null) {
                        writer.write(box.col + "|" + box.row + "|" + i + "|" + slot.itemName + "|" + slot.filePath);
                        writer.newLine();
                    }
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(BoxManager boxManager) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return; // primeira vez rodando, sem save ainda

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 5);
                if (parts.length < 5) continue;

                int col = Integer.parseInt(parts[0]);
                int row = Integer.parseInt(parts[1]);
                int slotIndex = Integer.parseInt(parts[2]);
                String itemName = parts[3];
                String filePath = parts[4];

                Box box = boxManager.getBoxAt(col, row);
                if (box != null && slotIndex < box.slotCount) {
                    box.slots[slotIndex].itemName = itemName;
                    box.slots[slotIndex].filePath = filePath;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}