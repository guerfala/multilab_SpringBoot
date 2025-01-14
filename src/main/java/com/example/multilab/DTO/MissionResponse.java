package com.example.multilab.DTO;

import com.example.multilab.Entities.Mission;
import com.example.multilab.Entities.User;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MissionResponse {
    private int id;
    private String organisme;
    private String date;
    private User user;
    private List<Integer> objets; // List of IDs

    public MissionResponse(Mission mission) {
        this.id = mission.getId();
        this.organisme = mission.getOrganisme();
        this.date = mission.getDate().toString();
        this.user = mission.getUser();
        this.objets = mission.getObjets().stream()
                .map(objet -> objet.getObjetPredifini().getId()) // Extract IDs
                .collect(Collectors.toList());
    }

    // Getters and setters
}
