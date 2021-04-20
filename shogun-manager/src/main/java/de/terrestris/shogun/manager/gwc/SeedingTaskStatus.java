package de.terrestris.shogun.manager.gwc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeedingTaskStatus {

    private int tilesProcessed;
    private int totalTiles;
    private int noRemainingTiles;
    private int taskId;
    private int taskStatus;

    @Override
    public String toString() {
        String status;
        switch (taskStatus) {
            case 0:
                status = "PENDING";
                break;
            case 1:
                status = "RUNNING";
                break;
            case 2:
                status = "DONE";
                break;
            default:
                status = "ABORTED";
        }
        return String.format("Seeding-Task %d (%s): %d of %d processed.", taskId, status, tilesProcessed, totalTiles);
    }
}
