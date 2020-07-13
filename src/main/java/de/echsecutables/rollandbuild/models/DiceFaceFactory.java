package de.echsecutables.rollandbuild.models;

import org.springframework.data.util.Pair;

import java.util.List;

public class DiceFaceFactory {

    private DiceFaceFactory() {
    }

    // Dice Face showing the choice of a or b
    public static DiceFace or(DiceFace a, DiceFace b) {
        DiceFace re = new DiceFace();
        re.setFaceCombinationType(FaceCombinationType.OR);
        re.setCombination(Pair.of(a, b));

        return re;
    }

    // Dice Face showing all given symbols
    public static DiceFace multiSymbol(
            List<DiceSymbol> symbols
    ) {
        assert symbols.size() > 0;
        if (symbols.size() == 1) {
            return singleSymbol(symbols.get(0));
        }
        List<DiceSymbol> symbolsA = symbols.subList(0, symbols.size() / 2);
        List<DiceSymbol> symbolsB = symbols.subList(symbols.size() / 2, symbols.size());
        DiceFace a = multiSymbol(symbolsA);
        DiceFace b = multiSymbol(symbolsB);

        DiceFace re = new DiceFace();
        re.setFaceCombinationType(FaceCombinationType.AND);
        re.setCombination(Pair.of(a, b));

        return re;
    }

    // Dice Face showing a single symbol
    public static DiceFace singleSymbol(DiceSymbol diceSymbol) {
        DiceFace re = new DiceFace();
        re.setFaceCombinationType(FaceCombinationType.SYMBOL);
        re.setDiceSymbol(diceSymbol);

        return re;
    }
}
