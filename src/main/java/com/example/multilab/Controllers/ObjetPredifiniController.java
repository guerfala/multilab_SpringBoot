package com.example.multilab.Controllers;

import com.example.multilab.Entities.ObjetPredifini;
import com.example.multilab.Repositories.ObjetPredifiniRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objets")
@CrossOrigin(origins = "*")
public class ObjetPredifiniController {

    @Autowired
    private ObjetPredifiniRepo repository;

    @GetMapping
    public List<ObjetPredifini> getAll() {
        return repository.findAll();
    }
}
