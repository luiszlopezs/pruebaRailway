/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


/**
 * Servicio encargado de enviar correos electrónicos desde la aplicación.
 */
@Service
public class EmailService {

    /** Componente para enviar correos electrónicos. */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía un correo de confirmación de cuenta al usuario.
     *
     * @param destinatario correo electrónico del destinatario
     * @param nombreUsuario nombre del usuario registrado
     */
    public void enviarConfirmacionCuenta(String destinatario, String nombreUsuario) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Cuenta creada exitosamente!");
        mensaje.setText("Hola " + nombreUsuario + ",\n\n" +
            "Tu cuenta ha sido creada con éxito.\n\n" +
            "¡Gracias por registrarte!\n\n" +
            "Equipo de soporte \n\n" +
            "VENECOLOMBIA ALLIANZ INC ®");

        mailSender.send(mensaje);
    }
}