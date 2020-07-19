package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "A Face of a dice is made up of a combination of symbols. " +
        "A simple face only has a number of symbols which are rolled simultaneously. " +
        "A 'choice face' has two sets of symbols rolled where the player can later choose one of the two.")
@Embeddable
public class DiceFace {

    @ApiModelProperty(value = "The primary List of symbols shown on the dice.")
    private List<DiceSymbol> rolled = new ArrayList<>();

    @ApiModelProperty(value = "If non-null, the player can choose thi list as the outcome.")
    private List<DiceSymbol> choice = null;

    public DiceFace(List<DiceSymbol> rolled) {
        this.rolled = rolled;
    }
}
