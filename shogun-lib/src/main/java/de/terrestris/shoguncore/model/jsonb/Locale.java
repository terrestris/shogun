package de.terrestris.shoguncore.model.jsonb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// TODO Find a way to localize and persist localized database
// entries, e.g. the name of an application. The list of potential
// languages has to be extensible.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Locale implements Serializable {
    private String de;
}
