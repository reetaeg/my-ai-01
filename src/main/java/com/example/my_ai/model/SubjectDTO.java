package com.example.my_ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SubjectDTO {

    private Long id;

    private Integer priority;

    private Integer depth;

    @Size(max = 200)
    private String subject;

    @Size(max = 255)
    private String description;

    @JsonProperty("isDone")
    private Boolean isDone;

    @JsonProperty("isDivide")
    private Boolean isDivide;

    @JsonProperty("isFavorite")
    private Boolean isFavorite;

    @Size(max = 255)
    private String member;

    private Long parent;

}
