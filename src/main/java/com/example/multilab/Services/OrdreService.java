package com.example.multilab.Services;

import com.example.multilab.Entities.Ordre;
import com.example.multilab.Repositories.OrdreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdreService {

    @Autowired
    private OrdreRepo ordreRepository;

    public List<Ordre> getAllOrdres() {
        return ordreRepository.findAll();
    }

    public Optional<Ordre> getOrdreById(int id) {
        return ordreRepository.findById(id);
    }

    public Ordre createOrdre(Ordre ordre) {
        return ordreRepository.save(ordre);
    }

    public Ordre updateOrdre(int id, Ordre ordreDetails) {
        return ordreRepository.findById(id).map(ordre -> {
            ordre.setDateDebut(ordreDetails.getDateDebut());
            ordre.setDateFin(ordreDetails.getDateFin());
            ordre.setOrganisme(ordreDetails.getOrganisme());
            ordre.setStatus(ordreDetails.getStatus());
            return ordreRepository.save(ordre);
        }).orElseThrow(() -> new RuntimeException("Ordre non trouvé"));
    }

    public void deleteOrdre(int id) {
        ordreRepository.deleteById(id);
    }
}
