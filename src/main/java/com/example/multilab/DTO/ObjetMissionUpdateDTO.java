package com.example.multilab.DTO;

import com.example.multilab.Entities.Etat;
import lombok.Data;

@Data
public class ObjetMissionUpdateDTO {
    private int id;
    private Etat etat;
    private String cause;
}
