package com.lucasbrunkhorst.hubspotintegration.service;

import com.lucasbrunkhorst.hubspotintegration.record.ContactRequestDTO;

public interface HubSpotContactService {
    void createContact(ContactRequestDTO dto);
}
