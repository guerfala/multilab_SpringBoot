package com.example.multilab.DTO;

import com.example.multilab.Entities.Etat;
import lombok.Data;

@Data
public class ObjetMissionDTO {
    private int id;
    private String description;
    private ObjetPredifiniDTO objetPredifini;
    private Etat etat;
}

