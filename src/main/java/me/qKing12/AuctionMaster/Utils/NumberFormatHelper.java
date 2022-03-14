package me.qKing12.AuctionMaster.Utils;

import me.qKing12.AuctionMaster.AuctionMaster;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberFormatHelper {

    private final DecimalFormat numberFormat;
    public boolean useDecimals;

    public NumberFormatHelper(){
        this.numberFormat = new DecimalFormat();
        this.numberFormat.setRoundingMode(RoundingMode.DOWN);
        this.numberFormat.setMinimumFractionDigits(0);

        if(AuctionMaster.plugin.getConfig().getBoolean("number-format.use-decimals")) {
            numberFormat.setMaximumFractionDigits(2);
            useDecimals = true;
        }
        else {
            numberFormat.setRoundingMode(RoundingMode.FLOOR);
            numberFormat.setMaximumFractionDigits(0);
            useDecimals = false;
        }
    }

    public String fix(String number) {
        boolean remove = AuctionMaster.plugin.getConfig().getBoolean("number-format.remove-decimal");
        if (!remove)
            return number;

        return number.endsWith(".0") ? number.replace(".0", "") : number;
    }

    public String formatNumber(Double number) {
        if (this.numberFormat == null)
            return fix(number.toString());

        String formatted = this.numberFormat.format(number);
        return fix(formatted);
    }
}
