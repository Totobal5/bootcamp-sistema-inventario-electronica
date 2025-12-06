package com.bootcamp.inventario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para servir las vistas HTML con Thymeleaf
 */
@Controller
public class ViewController {

    /**
     * Página principal - redirige al login
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    /**
     * Página de login
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Dashboard principal
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    /**
     * Gestión de componentes electrónicos
     */
    @GetMapping("/componentes")
    public String componentes() {
        return "componentes";
    }

    /**
     * Gestión de placas
     */
    @GetMapping("/placas")
    public String placas() {
        return "placas";
    }

    /**
     * Gestión de solicitudes de mecanizado
     */
    @GetMapping("/solicitudes-mecanizado")
    public String solicitudesMecanizado() {
        return "solicitudes-mecanizado";
    }

    /**
     * Gestión de solicitudes de armado
     */
    @GetMapping("/solicitudes-armado")
    public String solicitudesArmado() {
        return "solicitudes-armado";
    }

    /**
     * Gestión de tareas
     */
    @GetMapping("/tareas")
    public String tareas() {
        return "tareas";
    }
}
