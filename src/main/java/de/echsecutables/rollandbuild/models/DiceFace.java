package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@Data
@NoArgsConstructor
@ApiModel(description = "A Face of a dice is made up of a combination of symbols. This class represents a (part od a ) face in a tree structure")
public class DiceFace {

    @ApiModelProperty(value = "How faces are combined.", example = "AND")
    private FaceCombinationType faceCombinationType = FaceCombinationType.SYMBOL;

    @ApiModelProperty(value = "if faceCombinationType = SYMBOL, this is the symbol shown. Otherwise ignored.", example = "SWORD")
    private DiceSymbol diceSymbol;

    @ApiModelProperty(value = "if faceCombinationType != SYMBOL, these two faces are combined in the defined way. Otherwise ignored.")
    private Pair<DiceFace, DiceFace> combination;
}
