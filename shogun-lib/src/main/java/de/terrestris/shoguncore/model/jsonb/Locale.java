package de.terrestris.shoguncore.model.jsonb;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO Find a way to localize and persist localized database
// entries, e.g. the name of an application. The list of potential
// languages has to be extensible.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Locale implements Serializable {
    private String de;
}
