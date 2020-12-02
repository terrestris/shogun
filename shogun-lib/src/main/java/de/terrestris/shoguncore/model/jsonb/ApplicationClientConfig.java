package de.terrestris.shoguncore.model.jsonb;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationClientConfig implements Serializable {
    private String logoPath;
}
