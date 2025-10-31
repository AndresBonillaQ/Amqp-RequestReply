package it.nova.logger.model;

import lombok.Data;
import lombok.Getter;

import java.util.Map;

/**
 * Rappresenta un messaggio fallito ricevuto dalla coda DLQ, con campi estratti per la visualizzazione.
 */

@Getter
public class FailedMessage {

    private final String timestamp; // Quando è stato registrato l'errore
    private final String bodySnippet; // Estratto del corpo del messaggio
    private final Map<String, Object> headers; // Tutti gli headers per il debug
    private final String originalRoutingKey; // Routing Key originale
    private final String reason; // Causa del fallimento (es. "rejected", "expired")
    private final String consumerQueue;
    private final String receivedExchange;

    public FailedMessage(String timestamp, String bodySnippet, Map<String, Object> headers, String originalRoutingKey, String receivedExchange, String consumerQueue, String reason) {
        this.timestamp = timestamp;
        this.bodySnippet = bodySnippet.substring(0, Math.min(bodySnippet.length(), 100));
        this.headers = headers;
        this.originalRoutingKey = originalRoutingKey;
        this.consumerQueue = consumerQueue;
        this.receivedExchange = receivedExchange;
        this.reason = reason;
    }
}
