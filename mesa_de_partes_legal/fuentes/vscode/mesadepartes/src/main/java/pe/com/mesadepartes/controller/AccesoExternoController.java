package pe.com.mesadepartes.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.com.mesadepartes.dto.AccesoExternoRequest;
import pe.com.mesadepartes.dto.AccesoExternoResponse;
import pe.com.mesadepartes.entity.TokenAccesoTemporal;
import pe.com.mesadepartes.service.AccesoExternoService;

import java.util.Optional;

@RestController
@RequestMapping("/api/externo")
@CrossOrigin(origins = "*")
public class AccesoExternoController {

    @Autowired
    private AccesoExternoService accesoExternoService;

    @PostMapping("/generar-acceso")
    public ResponseEntity<AccesoExternoResponse> generarAcceso(
            @RequestBody AccesoExternoRequest request,
            HttpServletRequest httpRequest) {

        // Registrar información de la solicitud
        String direccionIp = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        try {
            AccesoExternoResponse response = accesoExternoService.generarAccesoExterno(request);

            if (response.isExitoso()) {
                // Si hay éxito, podríamos guardar información adicional del acceso
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(AccesoExternoResponse.error("Error interno del servidor"));
        }
    }

    @PostMapping("/validar-acceso")
    public ResponseEntity<AccesoExternoResponse> validarAcceso(
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Extraer el token del header (formato: "Bearer token")
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(AccesoExternoResponse.error("Formato de token inválido"));
            }

            String token = authorizationHeader.substring(7); // Remove "Bearer "
            Optional<TokenAccesoTemporal> tokenAccesoOpt = accesoExternoService.validarAccesoToken(token);

            if (tokenAccesoOpt.isPresent()) {
                TokenAccesoTemporal tokenAcceso = tokenAccesoOpt.get();
                accesoExternoService.incrementarAcceso(tokenAcceso);

                return ResponseEntity.ok(AccesoExternoResponse.exitoso(
                        token,
                        tokenAcceso.getFechaVencimiento(),
                        Long.valueOf(tokenAcceso.getExpediente().getIdExpediente()),
                        tokenAcceso.getExpediente().getCodigoSeguimiento(),
                        tokenAcceso.getExpediente().getEstadoExpediente()
                ));
            } else {
                return ResponseEntity.badRequest()
                        .body(AccesoExternoResponse.error("Token inválido o expirado"));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(AccesoExternoResponse.error("Error al validar token"));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API de acceso externo funcionando correctamente");
    }

    @GetMapping("/descargar-expediente")
    public ResponseEntity<InputStreamResource> descargarExpediente(
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validar el token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().build();
            }

            String token = authorizationHeader.substring(7);
            var tokenAccesoOpt = accesoExternoService.validarAccesoToken(token);

            if (tokenAccesoOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Generar PDF del expediente
            InputStreamResource pdfResource = accesoExternoService.generarPdfExpediente(
                tokenAccesoOpt.get().getExpediente().getIdExpediente()
            );

            if (pdfResource == null) {
                return ResponseEntity.internalServerError().build();
            }

            var tokenAcceso = tokenAccesoOpt.get();
            String filename = "expediente_" + tokenAcceso.getExpediente().getCodigoSeguimiento() + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfResource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            return xForwardedForHeader.split(",")[0].trim();
        }

        String xRealIpHeader = request.getHeader("X-Real-IP");
        if (xRealIpHeader != null && !xRealIpHeader.isEmpty()) {
            return xRealIpHeader;
        }

        return request.getRemoteAddr();
    }
}