package com.rbc.exchange.model;

/**
 * Buy Sell indicator enum.
 */
public enum Side {
    BUY, SELL;

    public Side getOppositeSide(){
        if(this == BUY){
            return SELL;
        }else{
            return BUY;
        }
    }
}
