package com.example.multilab.Controllers;

import com.example.multilab.Entities.Ordre;
import com.example.multilab.Services.OrdreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ordre")
public class OrdreController {

    @Autowired
    private OrdreService ordreService;

    @GetMapping("/getAll")
    public List<Ordre> getAllOrdres() {
        return ordreService.getAllOrdres();
    }

    @GetMapping("/get/{id}")
    public Optional<Ordre> getOrdreById(@PathVariable int id) {
        return ordreService.getOrdreById(id);
    }

    @PostMapping("/add")
    public Ordre createOrdre(@RequestBody Ordre ordre) {
        return ordreService.createOrdre(ordre);
    }

    @PutMapping("/update/{id}")
    public Ordre updateOrdre(@PathVariable int id, @RequestBody Ordre ordreDetails) {
        return ordreService.updateOrdre(id, ordreDetails);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteOrdre(@PathVariable int id) {
        ordreService.deleteOrdre(id);
        return "Ordre supprimé avec succès";
    }
}
