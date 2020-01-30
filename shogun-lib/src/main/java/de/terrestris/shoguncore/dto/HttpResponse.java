package de.terrestris.shoguncore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HttpResponse {
    private HttpStatus statusCode;

    private HttpHeaders headers;

    private byte[] body;
}
