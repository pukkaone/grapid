package com.github.pukkaone.grapid.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a resource is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message
   *     detail message, which is saved for later retrieval by the {@link #getMessage()}
   *     method.
   * @param cause
   *     cause, which is saved for later retrieval by the {@link #getCause()} method).
   *     A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.
   */
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message
   *     detail message, which is saved for later retrieval by the {@link #getMessage()}
   *     method.
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
