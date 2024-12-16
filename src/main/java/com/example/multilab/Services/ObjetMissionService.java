package com.example.multilab.Services;

import com.example.multilab.Entities.ObjetMission;
import com.example.multilab.Repositories.ObjetMissionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObjetMissionService {

    @Autowired
    private ObjetMissionRepo objetMissionRepository;

    public List<ObjetMission> getAllObjetMissions() {
        return objetMissionRepository.findAll();
    }

    public Optional<ObjetMission> getObjetMissionById(int id) {
        return objetMissionRepository.findById(id);
    }

    public ObjetMission createObjetMission(ObjetMission objetMission) {
        return objetMissionRepository.save(objetMission);
    }

    public ObjetMission updateObjetMission(int id, ObjetMission details) {
        return objetMissionRepository.findById(id).map(objet -> {
            objet.setNom(details.getNom());
            objet.setEtat(details.getEtat());
            objet.setCause(details.getCause());
            return objetMissionRepository.save(objet);
        }).orElseThrow(() -> new RuntimeException("ObjetMission non trouvé"));
    }

    public void deleteObjetMission(int id) {
        objetMissionRepository.deleteById(id);
    }
}
