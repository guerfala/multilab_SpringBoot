package com.example.multilab.DTO;

import lombok.Data;

@Data
public class ObjetMissionDTO {
    private int id;
    private String description;
    private ObjetPredifiniDTO objetPredifini;
}

