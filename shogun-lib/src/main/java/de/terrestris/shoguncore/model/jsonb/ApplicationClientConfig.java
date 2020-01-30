package de.terrestris.shoguncore.model.jsonb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationClientConfig implements Serializable {
    private String logoPath;
}
