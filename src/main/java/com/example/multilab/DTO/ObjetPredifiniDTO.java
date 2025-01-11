package com.example.multilab.DTO;

import lombok.Data;

@Data
public class ObjetPredifiniDTO {
    private int id;
    private String nom;

    public ObjetPredifiniDTO() {
    }
    public ObjetPredifiniDTO(int id, String nom) {
    }
}

