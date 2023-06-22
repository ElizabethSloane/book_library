package org.example.web.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

public class Book {
    @Getter
    @Setter
    private Integer id;
    @NotEmpty
    @Pattern(regexp = "[a-zA-Z]*\s*[a-zA-Z]*\s*[a-zA-Z]*\s*[a-zA-Z]*")
    @Getter
    @Setter
    private String author;
    @NotEmpty
    @Pattern(regexp = "[a-zA-Z]*\s*[a-zA-Z]*\s*[a-zA-Z]*\s*[a-zA-Z]*")
    @Getter
    @Setter
    private String title;
    @Digits(integer = 4, fraction = 0)
    @NotNull
    @Getter
    @Setter
    private Integer size;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", size=" + size +
                '}';
    }
}
