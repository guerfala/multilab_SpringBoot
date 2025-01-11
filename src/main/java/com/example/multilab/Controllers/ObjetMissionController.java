package com.example.multilab.Controllers;

import com.example.multilab.DTO.ObjetMissionDTO;
import com.example.multilab.DTO.ObjetPredifiniDTO;
import com.example.multilab.Entities.ObjetMission;
import com.example.multilab.Entities.Ordre;
import com.example.multilab.Repositories.ObjetMissionRepo;
import com.example.multilab.Repositories.OrdreRepo;
import com.example.multilab.Services.ObjetMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/objet-missions")
public class ObjetMissionController {

    @Autowired
    private ObjetMissionService objetMissionService;

    @Autowired
    private ObjetMissionRepo objetMissionRepo;

    @Autowired
    private OrdreRepo ordreRepo;

    @GetMapping("/api/ordres/{ordreId}/missions")
    public ResponseEntity<List<ObjetMissionDTO>> getObjetMissions(@PathVariable int ordreId) {
        Ordre ordre = ordreRepo.findById(ordreId).orElseThrow(() -> new RuntimeException("Ordre not found"));
        List<ObjetMissionDTO> missions = ordre.getObjets().stream().map(objetMission -> {
            ObjetMissionDTO dto = new ObjetMissionDTO();
            dto.setId(objetMission.getId());
            dto.setDescription(objetMission.getCause());
            dto.setEtat(objetMission.getEtat());
            dto.setObjetPredifini(new ObjetPredifiniDTO(
                    objetMission.getObjetPredifini().getId(),
                    objetMission.getObjetPredifini().getNom()
            ));
            return dto;
        }).toList();
        return ResponseEntity.ok(missions);
    }


    @GetMapping("/getAll")
    public List<ObjetMission> getAllObjetMissions() {
        return objetMissionService.getAllObjetMissions();
    }

    @GetMapping("/get/{id}")
    public Optional<ObjetMission> getObjetMissionById(@PathVariable int id) {
        return objetMissionService.getObjetMissionById(id);
    }

    @PostMapping("/add")
    public ObjetMission createObjetMission(@RequestBody ObjetMission objetMission) {
        return objetMissionService.createObjetMission(objetMission);
    }

    @PutMapping("/update/{id}")
    public ObjetMission updateObjetMission(@PathVariable int id, @RequestBody ObjetMission details) {
        return objetMissionService.updateObjetMission(id, details);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteObjetMission(@PathVariable int id) {
        objetMissionService.deleteObjetMission(id);
        return "ObjetMission supprimé avec succès";
    }
}
