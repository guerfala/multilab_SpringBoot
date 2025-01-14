package com.example.multilab.Services;

import com.example.multilab.Repositories.MissionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MissionService {

    @Autowired
    private MissionRepo missionRepository;

    public boolean deleteMissionById(int id) {
        if (missionRepository.existsById(id)) {
            missionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
