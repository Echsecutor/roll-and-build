package de.echsecutables.rollandbuild.models;

import de.echsecutables.rollandbuild.persistence.LongId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(description = "The dice is a fundamental concept in a dice rolling game. It is represented by sides with faces.")
@Entity
public class Dice implements LongId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Primary Key", example = "42")
    private Long id;

    @ApiModelProperty(value = "Multiple sides of the dice may show the same face. The total number of sides of the dice is obtained by summing.")
    @ElementCollection
    private List<DiceFace> diceFaces = new ArrayList<>();

    public Dice(Collection<? extends DiceFace> faces) {
        diceFaces.addAll(faces);
    }
}
