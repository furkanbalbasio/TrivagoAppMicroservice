package com.balbasio.controller;

import com.balbasio.repository.entity.Yetki;
import com.balbasio.service.YetkiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/yetki")
@RequiredArgsConstructor
public class YetkiController {

    private final YetkiService yetkiService;

    @PostMapping("/save")
    public ResponseEntity<Yetki> save(@RequestBody Yetki yetki){
        yetkiService.save(yetki);
        return  ResponseEntity.ok(yetki);
    }

    @GetMapping("/find-all")
    public ResponseEntity<Iterable<Yetki>> findAll(){
        return ResponseEntity.ok(yetkiService.findAll());
    }


}
