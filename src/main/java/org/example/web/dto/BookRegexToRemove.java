package org.example.web.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

public class BookRegexToRemove {

    @Getter
    @Setter
    @NotEmpty
    private String regex;
}
