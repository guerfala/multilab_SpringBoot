package com.example.multilab.DTO;

import com.example.multilab.Entities.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MissionRequest {
    private int id;
    private String organisme;
    private LocalDate date;
    private List<Integer> objets; // List of ObjetMission IDs
    private User user;
}
