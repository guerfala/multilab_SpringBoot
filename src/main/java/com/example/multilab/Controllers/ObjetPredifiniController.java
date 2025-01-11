package com.example.multilab.Controllers;

import com.example.multilab.Entities.ObjetPredifini;
import com.example.multilab.Repositories.ObjetPredifiniRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objets")
@CrossOrigin(origins = "*")
public class ObjetPredifiniController {

    @Autowired
    private ObjetPredifiniRepo objetPredifiniRepo;

    @PostMapping("/admin/add-objet-predifini")
    public ResponseEntity<ObjetPredifini> addObjetPredifini(@RequestBody ObjetPredifini objetPredifini) {
        ObjetPredifini savedObjet = objetPredifiniRepo.save(objetPredifini);
        return ResponseEntity.ok(savedObjet);
    }

    @GetMapping
    public List<ObjetPredifini> getAll() {
        return objetPredifiniRepo.findAll();
    }
}
