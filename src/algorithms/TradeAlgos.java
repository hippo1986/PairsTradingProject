/*
 * File: TradeAlgos.java
 * ---------------------
 * This file is what we run to actually make trades using
 * the data we've generated.
 */

/* Package Designation */
package algorithms;

/* Imports */
import java.util.Calendar;

import org.rosuda.JRI.Rengine;

import pairtrading.TradePair;

public class TradeAlgos {
	
	static TradeOrder shortA = new TradeOrder("none", "none", 0.0, 0);
	static TradeOrder longA = new TradeOrder("none", "none", 0.0, 0);
	
	/**
	 * 
	 * @param ticker1 first stock
	 * @param ticker2 second stock
	 * @param re instance of REngine. Must have libraries loaded
	 * @return the trade if one was determined, null if no trade
	 */
	public static TradePair evaluateAndInitiateTrades(String ticker1, String ticker2, Calendar day,  Rengine re) {
		
		// Calculate ratios and standard deviation.
		double historicalRatio = RCalls.getRatio(re); // Call R.
		double historicalStdDev = RCalls.getStdDev(re); // Call R.
		double currentRatio = RCalls.getCurrentRatio(re); // Call R.
		double absoluteRatioDifference = Math.abs(historicalRatio - currentRatio);
		
		// Running the algorithm if we're a standard deviation diverged. We've also added safeguards.
		if(absoluteRatioDifference > historicalStdDev && absoluteRatioDifference <= 3*historicalStdDev) {
			
			// If we want to short stock A, because A is over-performing relative to B.
			if(currentRatio > historicalRatio) {
				// Get some of the data we need for the following process.
				double currentOverHistRatio = currentRatio / historicalRatio;
				double oldPercentage = shortA.percentage;	
				// Sets up longAShortB if it's not already there.				
				if(shortA.ticker.equals("none")) {
					shortA.ticker = ticker1;
					shortA.aTradeType = "short";
					shortA.percentage = 10.0 * currentOverHistRatio;
					shortA.OUValue = RCalls.calculateOU(re, ticker1);
				} else {
					// If we're not investing more, just leave this function.
					if(10.0*currentOverHistRatio <= oldPercentage) return null;
					// Set the new percentage if it is more.
					shortA.percentage = 10.0*currentOverHistRatio;
				}	  			
				// Make the trades, investing the same amount in each stock!
				executeAndRecordTrade(ticker1, "short", shortA.percentage - oldPercentage);
				executeAndRecordTrade(ticker2, "long", shortA.percentage - oldPercentage);				
			}

			// If we want to long stock A, because A is under-performing relative to B.
			if(currentRatio < historicalRatio) {
				// Get some of the data we need for the following process.
				double currentOverHistRatio = currentRatio / historicalRatio;
				double oldPercentage = longA.percentage;
				// Sets up longA if it's not already there.	
				if(longA.ticker.equals("none")) {
					longA.ticker = ticker2;
					longA.aTradeType = "long";
					longA.percentage = 10.0 * currentOverHistRatio;
					longA.OUValue = RCalls.calculateOU(re, ticker2);
				} else {
					// If we're not investing more, just leave this function.
					if(10.0*currentOverHistRatio <= oldPercentage) return null;
					// Set the new percentage if it is more.
					longA.percentage = 10.0*currentOverHistRatio;
				}
				// Make the trades, investing the same amount in each stock!
				executeAndRecordTrade(ticker1, "long", longA.percentage - oldPercentage);
				executeAndRecordTrade(ticker2, "short", longA.percentage - oldPercentage);		
			}
			
		}	
		return null;
		
	}
	
	/*
	 * Function: analyzePositions
	 * --------------------------
	 * Called daily. Look at the Ornstein-Uhlenbeck expected values,
	 * update the amount of time that has passed so far.
	 * --------------------------
	 * Unwind a short-long position under two circumstances:
	 * 1. We've reverted to within x of the mean.
	 * 2. The OU expected value has been reached.
	 */
	public static void analyzePositions(String ticker1, String ticker2) {
		
		// Get historical ratio.
		// Get the ratio today.
		
		// Firstly, unwind if the ratio is >= 3*historicalStdDev.
		// If we're within 1/2 of a standard deviation of the mean, divest.
		// Get the Ornstein-Uhlenbeck value, then OU--.
		// If the O-U value is zero, divest.
		
	}
	
	
	/*
	 * Function: executeAndRecordTrade
	 * -------------------------------
	 * Interacts with the API and records
	 * the trade in the ArrayList.
	 */
	private static void executeAndRecordTrade(String ticker, String tradeType, double percentage) {
		
		// Calculate the amount to invest using the StockPair.
		int amount = 0;
		
		
		if(tradeType.equals("short")) {
			APICalls.PlaceShortOrder(ticker, amount); // Call the API
		} else if(tradeType.equals("long")) {
			// The below line will be an API call in the future.
			APICalls.PlaceLongOrder(ticker, amount); // Call the API
		}

	}
}