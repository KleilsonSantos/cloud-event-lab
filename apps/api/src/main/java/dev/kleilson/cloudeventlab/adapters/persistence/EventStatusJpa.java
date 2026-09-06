package dev.kleilson.cloudeventlab.adapters.persistence;

public enum EventStatusJpa {
  ACCEPTED,
  QUEUED,
  PROCESSING,
  PROCESSED,
  FAILED
}
