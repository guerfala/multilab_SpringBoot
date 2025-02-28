package com.example.multilab.Controllers;

import com.example.multilab.Entities.Organisme;
import com.example.multilab.Repositories.OrganismeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organismes")
@CrossOrigin(origins = "*")
public class OrganismeController {

    @Autowired
    private OrganismeRepo organismeRepo;

    @GetMapping
    public ResponseEntity<List<Organisme>> getAllOrganismes() {
        return ResponseEntity.ok(organismeRepo.findAll());
    }

    // CREATE a new organisme
    @PostMapping
    public Organisme createOrganisme(@RequestBody Organisme organisme) {
        return organismeRepo.save(organisme);
    }

    // UPDATE an organisme
    @PutMapping("/{id}")
    public Organisme updateOrganisme(@PathVariable int id, @RequestBody Organisme organismeDetails) {
        Organisme organisme = organismeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisme not found with id: " + id));

        organisme.setLibelle(organismeDetails.getLibelle());
        return organismeRepo.save(organisme);
    }

    // DELETE an organisme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganisme(@PathVariable int id) {
        organismeRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
