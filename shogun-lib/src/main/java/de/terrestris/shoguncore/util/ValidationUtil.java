package de.terrestris.shoguncore.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

public class ValidationUtil {

    protected static final Logger LOG = LogManager.getLogger(ValidationUtil.class);

    /**
     * Validates a BindingResult.
     *
     * @param bindingResult The BindingResult to be validated.
     * @return A ResponseEntity with status 422 and an array of validation issues
     * @throws ResponseStatusException If there are validation errors a ResponseStatusException is thrown. These are handled
     *                                 by the Spring ResponseStatusExceptionResolver which allows the validation errors to be included in the HTTP response.
     */
    public static ResponseEntity<Map<String, Object>> validateBindingResult(BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", String.format("Invalid %s", bindingResult.getObjectName()));
        List<String> errors = new ArrayList<>();

        StringBuilder message = new StringBuilder(String.format("Invalid %s input. Errors:", bindingResult.getObjectName()));
        for (ObjectError err : bindingResult.getAllErrors()) {
            message.append(String.format(" %s", err.getDefaultMessage()));
            errors.add(err.getDefaultMessage());
        }
        response.put("message", errors);
        response.put("status", 422);
        response.put("timestamp", new Date().toString());

        LOG.error("Found validation errors in bindingResult for entity {}: {}", bindingResult.getObjectName(),
            message.toString());

        return ResponseEntity.unprocessableEntity().body(response);
    }

}
