package de.terrestris.shogunboot.dto;

import de.terrestris.shoguncore.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ApplicationInfo {

    private String version;

    private String buildTime;

    private User user;

    private List<String> authorities;
}
