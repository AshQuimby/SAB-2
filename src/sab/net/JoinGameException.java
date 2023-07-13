package sab.net;

import sab.error.SabError;

public class JoinGameException extends Exception {
    public final SabError error;
    public JoinGameException(SabError error) {
        super();
        this.error = error;
    }
}
