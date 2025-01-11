package com.example.multilab.DTO;

import lombok.Data;

import java.util.List;

@Data
public class OrdreAdminDTO {
    private int id;
    private String organisme;
    private List<ObjetMissionDTO> objetMissions;
    private String status; // Add status
    private String dateDebut; // Add dateDebut
    private String username;
}
