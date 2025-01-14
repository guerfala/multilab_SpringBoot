package com.example.multilab.Controllers;

import com.example.multilab.Entities.Organisme;
import com.example.multilab.Repositories.OrganismeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
