package sagi.neta.CurrencyExchanger;

public class CurrencyException extends Exception {
    CurrencyException(String msg, Throwable e){
        super(msg,e);
    }
}
