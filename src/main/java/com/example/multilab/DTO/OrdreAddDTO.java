package com.example.multilab.DTO;

import lombok.Data;
import java.util.List;

@Data
public class OrdreAddDTO {
    private int id;
    private String organisme;
    private int missionId;
    private int userId;
    private List<Integer> objetMissionIds;
}