package org.itech.labSampleTracker.exception;

public class OperationFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public OperationFailedException(String msg) {
		super(msg);
	}

	public OperationFailedException() {
		super("Echec lors de l'opération!");
	}
}
