package com.example.multilab.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String organisme;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "mission-objetMission")
    private List<ObjetMission> Objets = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ManyToOne
    private User user;

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference(value = "ordre-mission")
    private Ordre ordre; // ✅ One-to-one relation with Ordre

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", organisme='" + organisme + '\'' +
                ", date='" + date + '\'' +
                '}'; // 🚫 Removed objetMissions to avoid recursion
    }

}
