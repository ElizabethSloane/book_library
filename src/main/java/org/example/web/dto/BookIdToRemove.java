package org.example.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;



public class BookIdToRemove {

    @Setter
    @Getter
    @NotNull
    private Integer id;


}
