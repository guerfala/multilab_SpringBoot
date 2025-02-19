package com.example.multilab.Services;

import com.example.multilab.Entities.Mission;
import com.example.multilab.Repositories.MissionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Mission> getMissionsByUser(int userId) {
        return missionRepository.findByUserId(userId);
    }
}
