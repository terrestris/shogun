package de.terrestris.shogun.boot.dto;

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

    private String userName;

    private List<String> authorities;
}
