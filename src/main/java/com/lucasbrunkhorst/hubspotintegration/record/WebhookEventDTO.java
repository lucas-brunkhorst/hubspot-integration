package com.lucasbrunkhorst.hubspotintegration.record;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WebhookEventDTO(
        @JsonProperty("appId") Long appId,
        @JsonProperty("eventId") Long eventId,
        @JsonProperty("subscriptionId") Long subscriptionId,
        @JsonProperty("portalId") Long portalId,
        @JsonProperty("occurredAt") Long occurredAt,
        @JsonProperty("subscriptionType") String subscriptionType,
        @JsonProperty("attemptNumber") int attemptNumber,
        @JsonProperty("objectId") Long objectId,
        @JsonProperty("changeSource") String changeSource,
        @JsonProperty("changeFlag") String changeFlag
) { }
