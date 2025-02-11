package com.example.multilab.DTO;

import lombok.Data;
import java.util.List;

@Data
public class OrdreDTO {
    private int id;
    private String organisme;
    private List<ObjetMissionDTO> objetMissions;
    private String status; // Add status
    private String dateDebut; // Add dateDebut
}