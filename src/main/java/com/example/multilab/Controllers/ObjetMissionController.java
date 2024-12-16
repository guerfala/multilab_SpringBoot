package com.example.multilab.Controllers;

import com.example.multilab.Entities.ObjetMission;
import com.example.multilab.Services.ObjetMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/objet-missions")
public class ObjetMissionController {

    @Autowired
    private ObjetMissionService objetMissionService;

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
