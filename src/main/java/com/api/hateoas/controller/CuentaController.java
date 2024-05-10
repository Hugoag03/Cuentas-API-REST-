package com.api.hateoas.controller;

import com.api.hateoas.exception.CuentaNotFoundException;
import com.api.hateoas.model.Cuenta;
import com.api.hateoas.model.Monto;
import com.api.hateoas.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping
    public ResponseEntity<List<Cuenta>> listarCuentas() {
        List<Cuenta> cuentas = cuentaService.listAll();

        if (cuentas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        for(Cuenta cuenta : cuentas){
            cuenta.add(linkTo(methodOn(CuentaController.class).listarCuenta(cuenta.getId())).withSelfRel());
            cuenta.add(linkTo(methodOn(CuentaController.class).depositarDinero(cuenta.getId(), null)).withRel("depositos"));
            cuenta.add(linkTo(methodOn(CuentaController.class).retirarDinero(cuenta.getId(), null)).withRel("retiros"));
            cuenta.add(linkTo(methodOn(CuentaController.class).listarCuentas()).withRel(IanaLinkRelations.COLLECTION));
        }

        CollectionModel<Cuenta> modelo = CollectionModel.of(cuentas);
        modelo.add(linkTo(methodOn(CuentaController.class).listarCuentas()).withSelfRel());
        return new ResponseEntity<>(cuentas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> listarCuenta(@PathVariable Integer id) {

        try {
            Cuenta cuenta = cuentaService.get(id);
            cuenta.add(linkTo(methodOn(CuentaController.class).listarCuenta(cuenta.getId())).withSelfRel());
            cuenta.add(linkTo(methodOn(CuentaController.class).depositarDinero(cuenta.getId(), null)).withRel("depositos"));
            cuenta.add(linkTo(methodOn(CuentaController.class).retirarDinero(cuenta.getId(), null)).withRel("retiros"));
            cuenta.add(linkTo(methodOn(CuentaController.class).listarCuentas()).withRel(IanaLinkRelations.COLLECTION));

            return new ResponseEntity<>(cuenta, HttpStatus.OK);
        } catch (Exception exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Cuenta> guardarCuenta(@RequestBody Cuenta c) {

        Cuenta cuenta = cuentaService.save(c);
        if (cuenta == null) {
            return ResponseEntity.noContent().build();
        }

        cuenta.add(linkTo(methodOn(CuentaController.class).listarCuenta(cuenta.getId())).withSelfRel());
        cuenta.add(linkTo(methodOn(CuentaController.class).depositarDinero(cuenta.getId(), null)).withRel("depositos"));
        cuenta.add(linkTo(methodOn(CuentaController.class).retirarDinero(cuenta.getId(), null)).withRel("retiros"));
        cuenta.add(linkTo(methodOn(CuentaController.class).listarCuentas()).withRel(IanaLinkRelations.COLLECTION));

        return ResponseEntity.created(linkTo(methodOn(CuentaController.class).listarCuenta(cuenta.getId())).toUri()).body(cuenta);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> actualizarCuenta(@PathVariable Integer id, @RequestBody Cuenta c) {

        Cuenta cuenta = cuentaService.get(id);
        if (cuenta == null) {
            return ResponseEntity.noContent().build();
        }

        cuenta.setNumeroDeCuenta(c.getNumeroDeCuenta());
        cuenta.setMonto(c.getMonto());
        cuentaService.save(cuenta);

        cuenta.add(linkTo(methodOn(CuentaController.class).listarCuenta(cuenta.getId())).withSelfRel());
        cuenta.add(linkTo(methodOn(CuentaController.class).depositarDinero(cuenta.getId(), null)).withRel("depositos"));
        cuenta.add(linkTo(methodOn(CuentaController.class).retirarDinero(cuenta.getId(), null)).withRel("retiros"));
        cuenta.add(linkTo(methodOn(CuentaController.class).listarCuentas()).withRel(IanaLinkRelations.COLLECTION));

        return new ResponseEntity<>(cuenta, HttpStatus.OK);
    }

    @PatchMapping("/{id}/deposito")
    public ResponseEntity<Cuenta> depositarDinero(@PathVariable Integer id, @RequestBody Monto monto){
        Cuenta cuenta = cuentaService.depositar(monto.getDinero(), id);

        if(cuenta == null){
            return ResponseEntity.noContent().build();
        }

        cuenta.add(linkTo(methodOn(CuentaController.class).listarCuenta(cuenta.getId())).withSelfRel());
        cuenta.add(linkTo(methodOn(CuentaController.class).depositarDinero(cuenta.getId(), null)).withRel("depositos"));
        cuenta.add(linkTo(methodOn(CuentaController.class).retirarDinero(cuenta.getId(), null)).withRel("retiros"));
        cuenta.add(linkTo(methodOn(CuentaController.class).listarCuentas()).withRel(IanaLinkRelations.COLLECTION));

        return new ResponseEntity<>(cuenta, HttpStatus.OK);
    }

    @PatchMapping("/{id}/retiro")
    public ResponseEntity<Cuenta> retirarDinero(@PathVariable Integer id, @RequestBody Monto monto){
        Cuenta cuenta = cuentaService.retirar(monto.getDinero(), id);

        if(cuenta == null){
            return ResponseEntity.noContent().build();
        }

        cuenta.add(linkTo(methodOn(CuentaController.class).listarCuenta(cuenta.getId())).withSelfRel());
        cuenta.add(linkTo(methodOn(CuentaController.class).depositarDinero(cuenta.getId(), null)).withRel("depositos"));
        cuenta.add(linkTo(methodOn(CuentaController.class).retirarDinero(cuenta.getId(), null)).withRel("retiros"));
        cuenta.add(linkTo(methodOn(CuentaController.class).listarCuentas()).withRel(IanaLinkRelations.COLLECTION));

        return new ResponseEntity<>(cuenta, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable Integer id) throws CuentaNotFoundException {

            cuentaService.delete(id);
            return ResponseEntity.noContent().build();

    }
}
