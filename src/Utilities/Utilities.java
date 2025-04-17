package Utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utilities {
	
	public static BigDecimal truncateBigDecimal(BigDecimal value, int decimals) {
	    return value.setScale(decimals, RoundingMode.DOWN);
	}




}
