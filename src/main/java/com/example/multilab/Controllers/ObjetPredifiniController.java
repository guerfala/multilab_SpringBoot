package com.example.multilab.Controllers;

import com.example.multilab.Entities.ObjetPredifini;
import com.example.multilab.Repositories.ObjetPredifiniRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/objets")
@CrossOrigin(origins = "*")
public class ObjetPredifiniController {
    @Autowired
    private ObjetPredifiniRepo objetPredifiniRepo;

    @PostMapping("/admin/add-objet-predifini")
    public ResponseEntity<ObjetPredifini> addObjetPredifini(@RequestBody ObjetPredifini objetPredifini) {
        return ResponseEntity.ok(objetPredifiniRepo.save(objetPredifini));
    }

    @GetMapping
    public List<ObjetPredifini> getAll() {
        return objetPredifiniRepo.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjet(@PathVariable int id) {
        objetPredifiniRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjetPredifini> updateObjet(@PathVariable int id, @RequestBody ObjetPredifini objetDetails) {
        Optional<ObjetPredifini> optionalObjet = objetPredifiniRepo.findById(id);
        if (optionalObjet.isPresent()) {
            ObjetPredifini existingObjet = optionalObjet.get();
            existingObjet.setNom(objetDetails.getNom());
            ObjetPredifini updatedObjet = objetPredifiniRepo.save(existingObjet);
            return ResponseEntity.ok(updatedObjet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
