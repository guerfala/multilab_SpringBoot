package com.example.multilab.DTO;

import lombok.Data;

import java.util.List;

@Data
public class OrdreAddDTO {
    private int id;
    private String organisme;
    private List<ObjetMissionDTO> objetMissions;
}
